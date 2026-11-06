package com.nova.personalai

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

enum class TaskStatus(val titleBn: String) {
    QUEUED("অপেক্ষমান"),
    RUNNING("চলমান"),
    WAITING_FOR_APPROVAL("অনুমতি প্রয়োজন"),
    COMPLETED("সম্পন্ন"),
    FAILED("ব্যর্থ"),
    CANCELLED("বাতিল")
}

enum class StepStatus(val titleBn: String) {
    PENDING("অপেক্ষমান"),
    RUNNING("চলছে"),
    WAITING_FOR_APPROVAL("অনুমতি চাই"),
    COMPLETED("সম্পন্ন"),
    FAILED("ব্যর্থ"),
    SKIPPED("এড়িয়ে যাওয়া")
}

data class StepResult(
    val stepId: String,
    val success: Boolean,
    val output: String = "",
    val error: String? = null,
    val attempts: Int = 0
)

typealias ToolResult = StepResult

data class WorkflowStep(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val tool: String,
    val input: String,
    val requiresApproval: Boolean = false,
    val approvalPrompt: String? = null,
    val status: StepStatus = StepStatus.PENDING,
    val output: String? = null,
    val error: String? = null,
    val currentAttempt: Int = 1,
    val maxAttempts: Int = 3
) {
    // Backwards compatibility getters
    val name: String get() = title
    val toolName: String get() = tool
}

data class WorkflowTask(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val category: String = "Workflow",
    val steps: List<WorkflowStep>,
    val status: TaskStatus = TaskStatus.QUEUED,
    val currentStep: Int = 0,
    val results: List<StepResult> = emptyList(),
    val approvedStepIds: Set<String> = emptySet(),
    val error: String? = null,
    val finalSummary: String? = null,
    val videoProject: com.nova.personalai.video.AIVideoProject? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val currentStepIndex: Int get() = currentStep
    val errorMessage: String? get() = error
    val approvalReason: String? get() = steps.getOrNull(currentStep)?.approvalPrompt ?: "ইউজারের অনুমোদন প্রয়োজন"
}

fun interface NovaTool {
    suspend fun execute(input: String): String
}

class WorkflowEngine(
    initialTools: Map<String, NovaTool> = emptyMap(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val onUpdate: (WorkflowTask) -> Unit = {}
) {
    constructor(tools: Map<String, NovaTool>, onUpdate: (WorkflowTask) -> Unit) : this(
        initialTools = tools,
        scope = CoroutineScope(Dispatchers.Default),
        onUpdate = onUpdate
    )

    private val tools = ConcurrentHashMap<String, NovaTool>(initialTools)
    private val cancelledTasks = ConcurrentHashMap.newKeySet<String>()
    private val approvedTasks = ConcurrentHashMap.newKeySet<String>()
    private val activeJobs = ConcurrentHashMap<String, Job>()

    private val _tasks = MutableStateFlow<List<WorkflowTask>>(emptyList())
    val tasks: StateFlow<List<WorkflowTask>> = _tasks.asStateFlow()

    private val _selectedTaskId = MutableStateFlow<String?>(null)
    val selectedTaskId: StateFlow<String?> = _selectedTaskId.asStateFlow()

    fun selectTask(taskId: String?) {
        _selectedTaskId.value = taskId
    }

    fun registerTool(toolName: String, tool: NovaTool) {
        tools[toolName] = tool
    }

    fun enqueueTask(task: WorkflowTask, autoStart: Boolean = true): String {
        _tasks.update { listOf(task) + it.filter { t -> t.id != task.id } }
        _selectedTaskId.value = task.id
        onUpdate(task)
        if (autoStart) {
            runTask(task.id)
        }
        return task.id
    }

    fun runTask(taskId: String) {
        val existingJob = activeJobs[taskId]
        if (existingJob != null && existingJob.isActive) return

        cancelledTasks.remove(taskId)
        val job = scope.launch {
            val task = _tasks.value.find { it.id == taskId } ?: return@launch
            run(task)
        }
        activeJobs[taskId] = job
    }

    /**
     * Approves a paused task and resumes execution.
     */
    fun approve(taskId: String) {
        approvedTasks.add(taskId)
        val task = _tasks.value.find { it.id == taskId } ?: return
        if (task.status != TaskStatus.WAITING_FOR_APPROVAL) return

        val step = task.steps.getOrNull(task.currentStep)
        val updatedApprovedIds = if (step != null) task.approvedStepIds + step.id else task.approvedStepIds

        val updatedTask = task.copy(
            approvedStepIds = updatedApprovedIds,
            status = TaskStatus.RUNNING
        )
        updateTaskState(updatedTask)
        runTask(taskId)
    }

    fun approveStep(taskId: String) = approve(taskId)

    fun cancel(taskId: String) {
        cancelledTasks.add(taskId)
        activeJobs[taskId]?.cancel()
        activeJobs.remove(taskId)

        val task = _tasks.value.find { it.id == taskId }
        if (task != null) {
            val updatedSteps = task.steps.mapIndexed { idx, s ->
                if (idx == task.currentStep && (s.status == StepStatus.RUNNING || s.status == StepStatus.WAITING_FOR_APPROVAL)) {
                    s.copy(status = StepStatus.FAILED, error = "ইউজার দ্বারা বাতিল করা হয়েছে")
                } else s
            }
            val cancelledTask = task.copy(
                status = TaskStatus.CANCELLED,
                steps = updatedSteps,
                error = "টাস্ক বাতিল করা হয়েছে।"
            )
            updateTaskState(cancelledTask)
        }
    }

    fun cancelTask(taskId: String) = cancel(taskId)

    fun retryTask(taskId: String) {
        val task = _tasks.value.find { it.id == taskId } ?: return
        cancel(taskId)

        val resetSteps = task.steps.map {
            it.copy(
                status = StepStatus.PENDING,
                output = null,
                error = null,
                currentAttempt = 1
            )
        }

        val resetTask = task.copy(
            steps = resetSteps,
            currentStep = 0,
            results = emptyList(),
            approvedStepIds = emptySet(),
            status = TaskStatus.QUEUED,
            error = null,
            finalSummary = null
        )

        updateTaskState(resetTask)
        runTask(taskId)
    }

    fun clearCompletedTasks() {
        _tasks.update { list ->
            list.filter { it.status == TaskStatus.RUNNING || it.status == TaskStatus.WAITING_FOR_APPROVAL }
        }
        if (_selectedTaskId.value != null && _tasks.value.none { it.id == _selectedTaskId.value }) {
            _selectedTaskId.value = _tasks.value.firstOrNull()?.id
        }
    }

    private fun publish(task: WorkflowTask): WorkflowTask {
        updateTaskState(task)
        return task
    }

    private fun updateTaskState(updatedTask: WorkflowTask) {
        _tasks.update { list ->
            if (list.none { it.id == updatedTask.id }) {
                listOf(updatedTask) + list
            } else {
                list.map { if (it.id == updatedTask.id) updatedTask else it }
            }
        }
        onUpdate(updatedTask)
    }

    /**
     * Core multi-step workflow runner with:
     * - Status tracking
     * - Approval gate
     * - 3 retry attempts per step
     * - Cancellation support
     */
    suspend fun run(initialTask: WorkflowTask): WorkflowTask = withContext(Dispatchers.IO) {
        var task = initialTask

        if (task.status in setOf(TaskStatus.COMPLETED, TaskStatus.CANCELLED)) {
            return@withContext task
        }

        if (task.steps.isEmpty()) {
            return@withContext publish(
                task.copy(
                    status = TaskStatus.FAILED,
                    error = "এই workflow-তে কোনো ধাপ নেই।"
                )
            )
        }

        if (task.currentStep !in task.steps.indices) {
            return@withContext publish(
                task.copy(
                    status = TaskStatus.COMPLETED,
                    error = null,
                    finalSummary = buildFinalSummary(task)
                )
            )
        }

        task = publish(
            task.copy(status = TaskStatus.RUNNING, error = null)
        )

        while (task.currentStep < task.steps.size) {
            currentCoroutineContext().ensureActive()

            if (task.id in cancelledTasks) {
                cancelledTasks.remove(task.id)
                return@withContext publish(
                    task.copy(status = TaskStatus.CANCELLED, error = "টাস্ক বাতিল করা হয়েছে।")
                )
            }

            val step = task.steps[task.currentStep]

            if (step.requiresApproval && step.id !in task.approvedStepIds && !approvedTasks.remove(task.id)) {
                val updatedSteps = task.steps.toMutableList()
                updatedSteps[task.currentStep] = step.copy(status = StepStatus.WAITING_FOR_APPROVAL)
                return@withContext publish(
                    task.copy(
                        status = TaskStatus.WAITING_FOR_APPROVAL,
                        steps = updatedSteps
                    )
                )
            }

            val tool = tools[step.tool]
                ?: return@withContext publish(
                    task.copy(
                        status = TaskStatus.FAILED,
                        error = "Tool পাওয়া যায়নি: ${step.tool}"
                    )
                )

            // Mark step as running
            val stepsRunning = task.steps.toMutableList()
            stepsRunning[task.currentStep] = step.copy(status = StepStatus.RUNNING)
            task = publish(task.copy(steps = stepsRunning))

            var result: StepResult? = null

            for (attempt in 1..step.maxAttempts) {
                currentCoroutineContext().ensureActive()

                if (task.id in cancelledTasks) {
                    cancelledTasks.remove(task.id)
                    return@withContext publish(
                        task.copy(status = TaskStatus.CANCELLED, error = "টাস্ক বাতিল করা হয়েছে।")
                    )
                }

                // Update attempt
                val stepsAttempt = task.steps.toMutableList()
                stepsAttempt[task.currentStep] = stepsAttempt[task.currentStep].copy(currentAttempt = attempt)
                task = publish(task.copy(steps = stepsAttempt))

                try {
                    val output = tool.execute(step.input)
                    result = StepResult(
                        stepId = step.id,
                        success = true,
                        output = output,
                        attempts = attempt
                    )
                    break
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    result = StepResult(
                        stepId = step.id,
                        success = false,
                        error = e.message ?: "অজানা ত্রুটি",
                        attempts = attempt
                    )
                    if (attempt < step.maxAttempts) {
                        delay(600L * attempt)
                    }
                }
            }

            val completedResult = result
                ?: StepResult(
                    stepId = step.id,
                    success = false,
                    error = "কোনো ফলাফল পাওয়া যায়নি।",
                    attempts = step.maxAttempts
                )

            val updatedStepState = task.steps[task.currentStep].copy(
                status = if (completedResult.success) StepStatus.COMPLETED else StepStatus.FAILED,
                output = completedResult.output,
                error = completedResult.error
            )
            val updatedSteps = task.steps.toMutableList()
            updatedSteps[task.currentStep] = updatedStepState

            task = task.copy(
                steps = updatedSteps,
                results = task.results + completedResult
            )

            if (!completedResult.success) {
                return@withContext publish(
                    task.copy(
                        status = TaskStatus.FAILED,
                        error = "${step.title}: ${completedResult.error}"
                    )
                )
            }

            task = task.copy(currentStep = task.currentStep + 1)
            task = publish(task)
        }

        publish(
            task.copy(
                status = TaskStatus.COMPLETED,
                error = null,
                finalSummary = buildFinalSummary(task)
            )
        )
    }

    suspend fun approveAndResume(task: WorkflowTask): WorkflowTask {
        val currentStep = task.steps.getOrNull(task.currentStep)
            ?: return task

        val updatedTask = task.copy(
            approvedStepIds = task.approvedStepIds + currentStep.id
        )

        return run(updatedTask)
    }

    private fun buildFinalSummary(task: WorkflowTask): String {
        val completedSteps = task.steps.filter { it.status == StepStatus.COMPLETED }
        val sb = StringBuilder()
        sb.append("🎉 স্বয়ংক্রিয় Workflow সফলভাবে সম্পন্ন হয়েছে!\n\n")
        sb.append("📋 টাস্ক: ${task.title}\n")
        sb.append("📂 সিস্টেম: ${task.category}\n")
        sb.append("⚡ সম্পন্ন ধাপ: ${completedSteps.size} / ${task.steps.size}\n\n")
        task.steps.forEachIndexed { i, step ->
            val icon = if (step.status == StepStatus.COMPLETED) "✓" else "•"
            sb.append("$icon ${i + 1}. ${step.title} [${step.status.titleBn}]\n")
        }
        return sb.toString().trim()
    }

    // ==========================================
    // Pre-built Automation Workflows
    // ==========================================

    fun createVideoStudioWorkflow(topic: String, isShorts: Boolean = true): WorkflowTask {
        val cleanTopic = topic.ifBlank { "রহস্যময় বিজ্ঞান ও মহাকাশ" }
        val format = if (isShorts) "YouTube Shorts (৬০ সেকেন্ড)" else "পূর্ণাঙ্গ সিনেমাটিক ভিডিও"
        val generatedVideo = com.nova.personalai.video.AIVideoGenerator.createProject(cleanTopic, isShorts)
        return WorkflowTask(
            title = "Video Studio: $cleanTopic",
            description = "$format তৈরির স্বয়ংক্রিয় স্টুডিও পাইপলাইন",
            category = "Video Studio",
            videoProject = generatedVideo,
            steps = listOf(
                WorkflowStep(
                    title = "১. গল্প ও চিত্রনাট্য তৈরি (Script)",
                    tool = "story_generator",
                    input = "Create a viral Bengali narrative script on '$cleanTopic' ($format). Include title, emotional hook, timing, and narration."
                ),
                WorkflowStep(
                    title = "২. দৃশ্য ও স্টোরিবোর্ড ম্যাপিং (Scene Breakdown)",
                    tool = "storyboard_generator",
                    input = "Break down '$cleanTopic' into 6 cinematic scenes with visual prompt, camera movement, narration, and duration."
                ),
                WorkflowStep(
                    title = "৩. স্ক্রিপ্ট ও দৃশ্য অনুমোদন (User Approval)",
                    tool = "plan_reviewer",
                    input = "Review script & scene breakdown for safety and production cost.",
                    requiresApproval = true,
                    approvalPrompt = "স্ক্রিপ্ট ও দৃশ্যসমূহ তৈরি হয়েছে। ছবি জেনারেশন, ভয়েসওভার ও রেন্ডারিং শুরু করতে অনুমোদন দিন।"
                ),
                WorkflowStep(
                    title = "৪. AI ছবি ও আর্টওয়ার্ক জেনারেশন (AI Images)",
                    tool = "image_generator",
                    input = "Generate 9:16 high-resolution keyframes for all scenes of '$cleanTopic'."
                ),
                WorkflowStep(
                    title = "৫. বাংলা ভয়েসওভার তৈরি (Voiceover)",
                    tool = "voice_generator",
                    input = "Synthesize neural Bengali voiceover (bn-BD) for narration."
                ),
                WorkflowStep(
                    title = "৬. সাবটাইটেল তৈরি ও সিঙ্ক (Subtitles)",
                    tool = "subtitle_generator",
                    input = "Generate millisecond-precise Bengali .SRT subtitle file."
                ),
                WorkflowStep(
                    title = "৭. চূড়ান্ত ভিডিও প্যাকেজ রেন্ডার (Video Rendering)",
                    tool = "video_renderer",
                    input = "Compose visuals, audio voiceover, subtitles, and export 1080x1920 MP4 package."
                ),
                WorkflowStep(
                    title = "৮. YouTube পাবলিশিং প্রস্তুতি (Publisher)",
                    tool = "youtube_publisher",
                    input = "Generate upload metadata, tags, description, and export manifest."
                )
            )
        )
    }

    fun createShortsWorkflow(topic: String): WorkflowTask = createVideoStudioWorkflow(topic, isShorts = true)

    fun createMarketingAutomationWorkflow(brandOrTopic: String): WorkflowTask {
        val cleanTopic = brandOrTopic.ifBlank { "AI Tech & Digital Growth" }
        return WorkflowTask(
            title = "Digital Marketing: $cleanTopic",
            description = "YouTube SEO, কনটেন্ট ক্যালেন্ডার, সোশ্যাল পোস্ট ও অ্যানালিটিক্স",
            category = "Digital Marketing",
            steps = listOf(
                WorkflowStep(
                    title = "১. YouTube SEO ও কিওয়ার্ড রিসার্চ (YouTube SEO)",
                    tool = "seo_generator",
                    input = "Research top ranking YouTube keywords, hashtags, and click-worthy titles for '$cleanTopic'."
                ),
                WorkflowStep(
                    title = "২. ৭ দিনের কনটেন্ট ক্যালেন্ডার (Content Calendar)",
                    tool = "content_calendar",
                    input = "Design a 7-day multi-platform social media calendar (YouTube, Facebook, LinkedIn) for '$cleanTopic'."
                ),
                WorkflowStep(
                    title = "৩. মার্কেটিং প্ল্যান অনুমোদন (Marketing Approval)",
                    tool = "plan_reviewer",
                    input = "Review content calendar and marketing strategy.",
                    requiresApproval = true,
                    approvalPrompt = "SEO কীওয়ার্ড ও ৭ দিনের কনটেন্ট ক্যালেন্ডার প্রস্তুত। সোশ্যাল ড্রাফট ও অ্যানালিটিক্স তৈরি করতে অনুমোদন দিন।"
                ),
                WorkflowStep(
                    title = "৪. সোশ্যাল পোস্ট ও কপি ড্রাফটিং (Social Drafts)",
                    tool = "social_draft_generator",
                    input = "Write viral Bengali and English post copy with calls-to-action for '$cleanTopic'."
                ),
                WorkflowStep(
                    title = "৫. গ্রোথ ও অ্যানালিটিক্স রিপোর্ট (Analytics Report)",
                    tool = "analytics_report",
                    input = "Generate expected reach, audience demographics, and performance tracking KPIs."
                )
            )
        )
    }

    fun createPhoneAutomationWorkflow(taskInstruction: String): WorkflowTask {
        val cleanInstruction = taskInstruction.ifBlank { "YouTube ও ক্যামেরা ওপেন করে অটোমেশন চেক" }
        return WorkflowTask(
            title = "Phone Automation: $cleanInstruction",
            description = "অনুমোদিত অ্যাপ ও সিস্টেম অ্যাকশন স্বয়ংক্রিয় পরিচালনা",
            category = "Phone Automation",
            steps = listOf(
                WorkflowStep(
                    title = "১. টাস্ক প্যারামিটার ও পারমিশন ভ্যালিডেশন",
                    tool = "permission_checker",
                    input = "Check permissions for: $cleanInstruction"
                ),
                WorkflowStep(
                    title = "২. সিস্টেম অ্যাকশন অনুমোদন (Security Gate)",
                    tool = "plan_reviewer",
                    input = "Validate phone command intent: $cleanInstruction",
                    requiresApproval = true,
                    approvalPrompt = "সিস্টেম ও অ্যাপ অ্যাকশন পরিচালনার জন্য অনুমতি প্রয়োজন। এক্সিকিউট করতে অনুমোদন দিন।"
                ),
                WorkflowStep(
                    title = "৩. অ্যাপ ও সিস্টেম অ্যাকশন এক্সিকিউশন",
                    tool = "phone_automation",
                    input = cleanInstruction
                ),
                WorkflowStep(
                    title = "৪. অ্যাকশন রেজাল্ট ও ফিডব্যাক লগ",
                    tool = "action_logger",
                    input = "Generate completion report for phone automation."
                )
            )
        )
    }

    fun createAppBuilderWorkflow(appIdea: String): WorkflowTask {
        val cleanIdea = appIdea.ifBlank { "Personal AI Assistant with Jetpack Compose" }
        return WorkflowTask(
            title = "App Builder: $cleanIdea",
            description = "আর্কিটেকচার, কোড তৈরি ও বিল্ড ভ্যালিডেশন স্বয়ংক্রিয় পাইপলাইন",
            category = "App Builder",
            steps = listOf(
                WorkflowStep(
                    title = "১. সিস্টেম আর্কিটেকচার ও ডেটা স্কিমা",
                    tool = "code_architect",
                    input = "Design Clean Architecture, MVVM packages and Room schema for '$cleanIdea'."
                ),
                WorkflowStep(
                    title = "২. Jetpack Compose UI ও কম্পোনেন্ট কোড",
                    tool = "code_generator",
                    input = "Generate production Jetpack Compose screens with Material 3 for '$cleanIdea'."
                ),
                WorkflowStep(
                    title = "৩. আর্কিটেকচার ও পারমিশন রিভিউ (Approval Gate)",
                    tool = "plan_reviewer",
                    input = "Review code architecture and safety rules.",
                    requiresApproval = true,
                    approvalPrompt = "অ্যাপের আর্কিটেকচার ও কম্পোনেন্ট তৈরি হয়েছে। কম্পাইলার টেস্ট ও প্যাকেজিংয়ে যেতে অনুমোদন দিন।"
                ),
                WorkflowStep(
                    title = "৪. স্ট্যাটিক অ্যানালাইসিস ও কম্পাইলার টেস্ট",
                    tool = "build_tester",
                    input = "Validate syntax, imports, and Kotlin 2.0+ compatibility."
                ),
                WorkflowStep(
                    title = "৫. রিলিজ ডকুমেন্টেশন ও ফাইল প্যাকেজিং",
                    tool = "package_builder",
                    input = "Build distribution instructions, manifest, and APK validation report."
                )
            )
        )
    }
}
