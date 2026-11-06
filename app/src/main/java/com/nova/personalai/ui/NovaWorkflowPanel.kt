package com.nova.personalai.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberNeonAmber
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonPurple
import com.example.ui.theme.CyberNeonRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalNovaTheme
import com.nova.personalai.StepResult
import com.nova.personalai.StepStatus
import com.nova.personalai.TaskStatus
import com.nova.personalai.WorkflowEngine
import com.nova.personalai.WorkflowStep
import com.nova.personalai.WorkflowTask
import kotlinx.coroutines.launch

/**
 * Interactive Workflow Automation Panel for NOVA AI.
 * Demonstrates:
 * 1. Multi-step workflows (YouTube Script -> Scene breakdown -> Approval -> Voiceover -> Subtitles)
 * 2. User Approval Checkpoint gate (Waiting for approval)
 * 3. 3-attempt automated retry per step
 * 4. Real-time Status and Task Cancellation
 */
@Composable
fun NovaWorkflowPanel(
    workflowEngine: WorkflowEngine,
    modifier: Modifier = Modifier
) {
    val theme = LocalNovaTheme.current
    val coroutineScope = rememberCoroutineScope()
    val tasks by workflowEngine.tasks.collectAsState()
    val selectedTaskId by workflowEngine.selectedTaskId.collectAsState()

    val currentTask = tasks.find { it.id == selectedTaskId } ?: tasks.firstOrNull()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        border = BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(theme.primary, theme.accent))),
        modifier = modifier
            .fillMaxWidth()
            .testTag("nova_workflow_panel")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(theme.primary.copy(alpha = 0.2f))
                            .border(1.dp, theme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Workflow Engine",
                            tint = theme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "NOVA WORKFLOW ENGINE",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = theme.primary,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "মাল্টি-স্টেপ ওয়ার্কফ্লো ও অটো-রিট্রাই",
                            fontSize = 11.sp,
                            color = CyberTextSecondary
                        )
                    }
                }

                // Quick Launch New Workflow button
                Button(
                    onClick = {
                        val newTask = createSampleAutomationTask()
                        workflowEngine.enqueueTask(newTask, autoStart = true)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("run_workflow_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF001524)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "নতুন Workflow চালাও",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF001524)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (currentTask == null) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(theme.cardBg)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = CyberTextSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "বর্তমানে কোনো সক্রিয় Workflow নেই",
                            fontSize = 13.sp,
                            color = CyberTextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "উপরের 'নতুন Workflow চালাও' বাটনে ক্লিক করে স্বয়ংক্রিয় প্রসেস শুরু করুন।",
                            fontSize = 11.sp,
                            color = CyberTextSecondary
                        )
                    }
                }
            } else {
                // Active Workflow Status Card
                ActiveTaskDetailsCard(
                    task = currentTask,
                    onApprove = {
                        coroutineScope.launch {
                            workflowEngine.approve(currentTask.id)
                        }
                    },
                    onCancel = {
                        workflowEngine.cancel(currentTask.id)
                    },
                    onRetry = {
                        workflowEngine.retryTask(currentTask.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun ActiveTaskDetailsCard(
    task: WorkflowTask,
    onApprove: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit
) {
    val theme = LocalNovaTheme.current
    val statusColor = when (task.status) {
        TaskStatus.QUEUED -> CyberTextSecondary
        TaskStatus.RUNNING -> CyberNeonCyan
        TaskStatus.WAITING_FOR_APPROVAL -> CyberNeonAmber
        TaskStatus.COMPLETED -> CyberNeonGreen
        TaskStatus.FAILED -> CyberNeonRed
        TaskStatus.CANCELLED -> Color.Gray
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = theme.cardBg),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Title & Status Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = "ক্যাটাগরি: ${task.category}",
                        fontSize = 10.5.sp,
                        color = CyberTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.18f))
                        .border(0.8.dp, statusColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = task.status.titleBn,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            val totalSteps = task.steps.size.coerceAtLeast(1)
            val completedCount = task.results.count { it.success }
            val progress = (completedCount.toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f)

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "অগ্রগতি: ধাপ ${task.currentStep.coerceAtMost(totalSteps)} / $totalSteps",
                        fontSize = 10.5.sp,
                        color = CyberTextSecondary
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = theme.primary,
                    trackColor = theme.surface
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Steps List
            Text(
                text = "WORKFLOW STEPS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = theme.primary,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(6.dp))

            task.steps.forEachIndexed { index, step ->
                val result = task.results.find { it.stepId == step.id }
                val isCurrent = index == task.currentStep && (task.status == TaskStatus.RUNNING || task.status == TaskStatus.WAITING_FOR_APPROVAL)
                val isApproved = step.id in task.approvedStepIds

                StepItemRow(
                    stepIndex = index + 1,
                    step = step,
                    result = result,
                    isCurrent = isCurrent,
                    isApproved = isApproved
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Error display if failed
            if (!task.error.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberNeonRed.copy(alpha = 0.15f))
                        .border(1.dp, CyberNeonRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = CyberNeonRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ত্রুটি: ${task.error}",
                            fontSize = 11.sp,
                            color = CyberNeonRed
                        )
                    }
                }
            }

            // Action Buttons
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // If Waiting for Approval -> Show Approval Button
                if (task.status == TaskStatus.WAITING_FOR_APPROVAL) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberNeonAmber),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("approve_step_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "অনুমোদন করুন (Approve)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                // If Running or Waiting for approval -> Show Cancel button
                if (task.status == TaskStatus.RUNNING || task.status == TaskStatus.WAITING_FOR_APPROVAL) {
                    OutlinedButton(
                        onClick = onCancel,
                        border = BorderStroke(1.dp, CyberNeonRed),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberNeonRed),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cancel_workflow_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = CyberNeonRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "বাতিল করুন (Cancel)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // If Failed or Cancelled -> Show Retry Button
                if (task.status == TaskStatus.FAILED || task.status == TaskStatus.CANCELLED) {
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("retry_workflow_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color(0xFF001524),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "আবার চেষ্টা করুন (Retry)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF001524)
                        )
                    }
                }

                // If Completed -> Show Done badge
                if (task.status == TaskStatus.COMPLETED) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberNeonGreen.copy(alpha = 0.15f))
                            .border(1.dp, CyberNeonGreen.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CyberNeonGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🎉 সমস্ত ধাপ সফলভাবে সম্পন্ন হয়েছে!",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberNeonGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepItemRow(
    stepIndex: Int,
    step: WorkflowStep,
    result: StepResult?,
    isCurrent: Boolean,
    isApproved: Boolean
) {
    val theme = LocalNovaTheme.current
    var showOutput by remember { mutableStateOf(false) }

    val isSuccess = result?.success == true
    val isFailed = result != null && !result.success

    val borderColor = when {
        isSuccess -> CyberNeonGreen.copy(alpha = 0.5f)
        isFailed -> CyberNeonRed.copy(alpha = 0.6f)
        isCurrent -> CyberNeonCyan
        else -> theme.borderGlow.copy(alpha = 0.2f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isCurrent) theme.surface.copy(alpha = 0.9f) else theme.surface.copy(alpha = 0.5f))
            .border(if (isCurrent) 1.2.dp else 0.8.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable {
                if (result != null) showOutput = !showOutput
            }
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    // Status Icon: ✓, ✗, running spinner, or lock
                    when {
                        isSuccess -> {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(CyberNeonGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "✓",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = CyberNeonGreen
                                )
                            }
                        }
                        isFailed -> {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(CyberNeonRed.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "✗",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = CyberNeonRed
                                )
                            }
                        }
                        isCurrent -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = theme.primary
                            )
                        }
                        step.requiresApproval && !isApproved -> {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Approval required",
                                tint = CyberNeonAmber,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(CyberTextSecondary.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$stepIndex",
                                    fontSize = 9.sp,
                                    color = CyberTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = step.title,
                            fontSize = 11.5.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) theme.primary else CyberTextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Tool: ${step.tool}",
                                fontSize = 9.5.sp,
                                color = CyberTextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                            if (step.requiresApproval) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isApproved) "✓ অনুমোদিত" else "🔒 অনুমোদন প্রয়োজন",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isApproved) CyberNeonGreen else CyberNeonAmber
                                )
                            }
                        }
                    }
                }

                // Attempts indicator
                if (result != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(theme.cardBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${result.attempts}/3 attempt",
                            fontSize = 9.5.sp,
                            color = if (isSuccess) CyberNeonGreen else CyberNeonRed,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Expandable output preview
            AnimatedVisibility(visible = showOutput && result != null) {
                Column(modifier = Modifier.padding(top = 6.dp)) {
                    val text = if (isSuccess) result?.output.orEmpty() else result?.error.orEmpty()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = text.take(200) + if (text.length > 200) "..." else "",
                            fontSize = 10.sp,
                            color = if (isSuccess) CyberTextSecondary else CyberNeonRed,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

/**
 * Creates a sample multi-step automation workflow for testing.
 */
private fun createSampleAutomationTask(): WorkflowTask {
    return WorkflowTask(
        title = "YouTube Shorts: মহাকাশ ও ভবিষ্যৎ বিজ্ঞান",
        description = "গল্প → স্টোরিবোর্ড → অনুমোদন → ভয়েসওভার → সাবটাইটেল",
        category = "Video Studio",
        steps = listOf(
            WorkflowStep(
                title = "১. গল্প ও চিত্রনাট্য তৈরি (Script)",
                tool = "story_generator",
                input = "Generate a viral narrative script for a 60s YouTube Shorts about space mystery."
            ),
            WorkflowStep(
                title = "২. দৃশ্য ও স্টোরিবোর্ড তালিকা (Scene Breakdown)",
                tool = "storyboard_generator",
                input = "Break down into 6 visual cinematic scenes with audio cues."
            ),
            WorkflowStep(
                title = "৩. স্ক্রিপ্ট ও দৃশ্য অনুমোদন (Security & Review)",
                tool = "plan_reviewer",
                input = "Review content safety and production feasibility.",
                requiresApproval = true,
                approvalPrompt = "স্ক্রিপ্ট ও দৃশ্যসমূহ প্রস্তুত। ভয়েসওভার ও সাবটাইটেল জেনারেশন শুরু করতে অনুমোদন দিন।"
            ),
            WorkflowStep(
                title = "৪. বাংলা ভয়েসওভার অডিও (Voiceover)",
                tool = "voice_generator",
                input = "Synthesize neural Bengali voiceover for narration."
            ),
            WorkflowStep(
                title = "৫. বাংলা সাবটাইটেল সিঙ্ক (Subtitles .SRT)",
                tool = "subtitle_generator",
                input = "Generate timing synchronized Bengali .SRT subtitle file."
            )
        )
    )
}
