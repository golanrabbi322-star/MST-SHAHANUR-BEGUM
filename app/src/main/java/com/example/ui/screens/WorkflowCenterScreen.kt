package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberNeonAmber
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.CyberNeonPurple
import com.example.ui.theme.CyberNeonRed
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalNovaTheme
import com.nova.personalai.StepStatus
import com.nova.personalai.TaskStatus
import com.nova.personalai.WorkflowEngine
import com.nova.personalai.WorkflowStep
import com.nova.personalai.WorkflowTask
import com.nova.personalai.video.AIVideoGenerator
import com.nova.personalai.video.AIVideoStudioPlayer
import com.nova.personalai.voice.TTSManager

import com.nova.personalai.systems.NovaSystemCategory
import com.nova.personalai.systems.VoiceStudioView
import com.nova.personalai.systems.ImageStudioView
import com.nova.personalai.systems.StoryToVideoView
import com.nova.personalai.systems.SubtitlesStudioView
import com.nova.personalai.systems.VideoEditingStudioView
import com.nova.personalai.systems.DigitalMarketingView
import com.nova.personalai.systems.AppBuilderStudioView
import com.nova.personalai.ui.NovaWorkflowPanel

@Composable
fun WorkflowCenterScreen(
    workflowEngine: WorkflowEngine,
    ttsManager: TTSManager? = null,
    onTriggerVoiceCommand: () -> Unit = {},
    initialSystem: NovaSystemCategory = NovaSystemCategory.VOICE_ASSISTANT,
    onNavigateToEditor: () -> Unit = {},
    onNavigateToFiles: () -> Unit = {},
    onNavigateToPreview: () -> Unit = {}
) {
    val theme = LocalNovaTheme.current
    val context = LocalContext.current
    val tasks by workflowEngine.tasks.collectAsState()
    val selectedTaskId by workflowEngine.selectedTaskId.collectAsState()

    var selectedSystem by remember { mutableStateOf(initialSystem) }
    var activePreviewProject by remember { mutableStateOf<com.nova.personalai.video.AIVideoProject?>(null) }
    var customTopicInput by remember { mutableStateOf("") }
    val activeTask = tasks.find { it.id == selectedTaskId } ?: tasks.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Top Header Banner with 7-System Navigation
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = theme.surface),
                border = BorderStroke(1.2.dp, Brush.horizontalGradient(theme.gradientColors)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                    contentDescription = "Autonomous Engine",
                                    tint = theme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "NOVA 7-SYSTEM SUITE",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = theme.primary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "৭টি সমন্বিত অটোনোমাস সিস্টেম হাব",
                                    fontSize = 11.sp,
                                    color = CyberTextSecondary
                                )
                            }
                        }

                        if (tasks.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(theme.primary.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${tasks.size} TASKS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.primary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 7 Core Canonical Systems Horizontal Selector Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NovaSystemCategory.values().forEach { cat ->
                            val isSelected = cat == selectedSystem
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) cat.accentColor.copy(alpha = 0.22f) else theme.cardBg)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.8.dp,
                                        color = if (isSelected) cat.accentColor else theme.borderGlow.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .clickable { selectedSystem = cat }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(cat.icon, contentDescription = null, tint = cat.accentColor, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cat.titleBn,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) cat.accentColor else CyberTextSecondary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Dedicated Interactive Studio View for Currently Selected System
        item {
            when (selectedSystem) {
                NovaSystemCategory.VOICE_ASSISTANT -> {
                    VoiceStudioView(
                        ttsManager = ttsManager,
                        onTriggerVoiceCommand = onTriggerVoiceCommand
                    )
                }
                NovaSystemCategory.IMAGE_STUDIO -> {
                    ImageStudioView(
                        onSendToVideoStudio = { prompt ->
                            customTopicInput = prompt
                            selectedSystem = NovaSystemCategory.STORY_TO_VIDEO
                        }
                    )
                }
                NovaSystemCategory.STORY_TO_VIDEO -> {
                    StoryToVideoView(
                        onPlayProject = { project ->
                            activePreviewProject = project
                        }
                    )
                }
                NovaSystemCategory.SUBTITLES_SRT -> {
                    SubtitlesStudioView()
                }
                NovaSystemCategory.VIDEO_EDITING -> {
                    VideoEditingStudioView(
                        onOpenRenderedVideo = {
                            activePreviewProject = AIVideoGenerator.createProject("FFmpeg রেন্ডার করা ভিডিও", isShorts = true)
                        }
                    )
                }
                NovaSystemCategory.MARKETING -> {
                    DigitalMarketingView()
                }
                NovaSystemCategory.APP_BUILDER -> {
                    AppBuilderStudioView(
                        onNavigateToEditor = onNavigateToEditor,
                        onNavigateToFiles = onNavigateToFiles,
                        onNavigateToPreview = onNavigateToPreview
                    )
                }
            }
        }

        // 3. Optional Direct Video Player (when launched from Story-to-Video or Video Editing)
        if (activePreviewProject != null) {
            item {
                AIVideoStudioPlayer(
                    project = activePreviewProject!!,
                    ttsManager = ttsManager,
                    onClose = { activePreviewProject = null }
                )
            }
        }

        // 3.5. Interactive NOVA Workflow Engine Panel
        item {
            NovaWorkflowPanel(workflowEngine = workflowEngine)
        }

        // 4. Autonomous Workflow Pipeline Generator
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = theme.surface),
                border = BorderStroke(1.dp, theme.primary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚡ অটোনোমাস পাইপলাইন রানার (Autonomous Automation)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "এক ক্লিকে মাল্টি-স্টেপ ওয়ার্কফ্লো টাস্ক তৈরি ও পরিচালনা",
                        fontSize = 11.sp,
                        color = CyberTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetWorkflowChip(
                            icon = Icons.Default.VideoLibrary,
                            title = "YouTube Shorts (60s)",
                            subtitle = "গল্প → সিন → অডিও → ভিডিও",
                            color = CyberNeonRed,
                            onClick = {
                                val topic = customTopicInput.ifBlank { "রহস্যময় বিজ্ঞান ও মহাকাশ" }
                                val task = workflowEngine.createVideoStudioWorkflow(topic, isShorts = true)
                                workflowEngine.enqueueTask(task)
                                customTopicInput = ""
                                Toast.makeText(context, "YouTube Shorts Workflow সক্রিয় হয়েছে!", Toast.LENGTH_SHORT).show()
                            }
                        )

                        PresetWorkflowChip(
                            icon = Icons.Default.Campaign,
                            title = "YouTube SEO & Growth",
                            subtitle = "টপ কীওয়ার্ড, ট্যাগ ও শিরোনাম",
                            color = CyberNeonCyan,
                            onClick = {
                                val topic = customTopicInput.ifBlank { "AI Tech & Productivity" }
                                val task = workflowEngine.createMarketingAutomationWorkflow(topic)
                                workflowEngine.enqueueTask(task)
                                customTopicInput = ""
                                Toast.makeText(context, "Marketing Automation সক্রিয় হয়েছে!", Toast.LENGTH_SHORT).show()
                            }
                        )

                        PresetWorkflowChip(
                            icon = Icons.Default.Code,
                            title = "Full App Builder",
                            subtitle = "Clean Architecture + UI Code",
                            color = CyberNeonPurple,
                            onClick = {
                                val idea = customTopicInput.ifBlank { "Personal AI Assistant with Jetpack Compose" }
                                val task = workflowEngine.createAppBuilderWorkflow(idea)
                                workflowEngine.enqueueTask(task)
                                customTopicInput = ""
                                Toast.makeText(context, "App Builder চালু হয়েছে!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Custom Topic Prompt Box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customTopicInput,
                            onValueChange = { customTopicInput = it },
                            placeholder = {
                                Text(
                                    "কাস্টম বিষয় বা অটোমেশন নির্দেশ লিখুন...",
                                    fontSize = 11.5.sp,
                                    color = CyberTextMuted
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("workflow_topic_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = theme.primary,
                                unfocusedBorderColor = theme.borderGlow.copy(alpha = 0.5f),
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary,
                                cursorColor = theme.primary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                val topic = customTopicInput.ifBlank { "রহস্যময় বিজ্ঞান ও মহাকাশ" }
                                val task = workflowEngine.createVideoStudioWorkflow(topic, isShorts = true)
                                workflowEngine.enqueueTask(task)
                                customTopicInput = ""
                                Toast.makeText(context, "Workflow সক্রিয় হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("launch_custom_workflow")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = Color.Black, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // 2. Active Tasks Switcher
        if (tasks.size > 1) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tasks.forEach { t ->
                        val isSelected = t.id == activeTask?.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) theme.primary.copy(alpha = 0.2f) else theme.surface)
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.8.dp,
                                    color = if (isSelected) theme.primary else theme.borderGlow.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { workflowEngine.selectTask(t.id) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusDot(status = t.status)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = t.category,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) theme.primary else CyberTextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = { workflowEngine.clearCompletedTasks() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Clear completed", tint = CyberTextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // 3. Active Task Detailed View
        if (activeTask != null) {
            item {
                TaskDetailCard(
                    task = activeTask,
                    onApprove = { workflowEngine.approve(activeTask.id) },
                    onCancel = { workflowEngine.cancel(activeTask.id) },
                    onRetry = { workflowEngine.retryTask(activeTask.id) }
                )
            }

            val videoProject = activeTask.videoProject ?: if (activeTask.category == "Video Studio") AIVideoGenerator.createProject(activeTask.title) else null
            if (videoProject != null) {
                item {
                    AIVideoStudioPlayer(
                        project = videoProject,
                        ttsManager = ttsManager
                    )
                }
            }

            // 4. Stepper Steps Timeline
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WORKFLOW PIPELINE (${activeTask.steps.size} ধাপ)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${activeTask.steps.count { it.status == StepStatus.COMPLETED }}/${activeTask.steps.size}",
                        fontSize = 11.sp,
                        color = CyberNeonGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            itemsIndexed(activeTask.steps) { index, step ->
                WorkflowStepRow(
                    stepNumber = index + 1,
                    step = step,
                    isCurrentStep = index == activeTask.currentStep && activeTask.status == TaskStatus.RUNNING
                )
            }
        } else {
            // Empty State
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = theme.surface),
                    border = BorderStroke(1.dp, theme.borderGlow.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = theme.primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "কোনো সক্রিয় স্বয়ংক্রিয় Workflow নেই",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "উপরের বাটনে ক্লিক করুন অথবা ভয়েস কমান্ড দিন:\n\"আমার জন্য একটি YouTube Shorts তৈরি করো\"",
                            fontSize = 12.sp,
                            color = CyberTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskDetailCard(
    task: WorkflowTask,
    onApprove: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit
) {
    val theme = LocalNovaTheme.current
    val context = LocalContext.current

    val borderBrush = when (task.status) {
        TaskStatus.RUNNING -> Brush.horizontalGradient(listOf(theme.primary, theme.accent))
        TaskStatus.WAITING_FOR_APPROVAL -> Brush.horizontalGradient(listOf(CyberNeonAmber, CyberNeonRed))
        TaskStatus.COMPLETED -> Brush.horizontalGradient(listOf(CyberNeonGreen, theme.primary))
        TaskStatus.FAILED -> Brush.horizontalGradient(listOf(CyberNeonRed, CyberNeonPink))
        TaskStatus.CANCELLED -> Brush.horizontalGradient(listOf(CyberTextMuted, CyberTextMuted))
        else -> Brush.horizontalGradient(listOf(theme.borderGlow.copy(alpha = 0.5f), theme.borderGlow.copy(alpha = 0.5f)))
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
        border = BorderStroke(1.2.dp, borderBrush),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = task.description.ifBlank { "স্বয়ংক্রিয় মাল্টি-স্টেপ প্রসেস" },
                        fontSize = 11.sp,
                        color = CyberTextSecondary,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                TaskStatusPill(status = task.status)
            }

            // Progress bar
            val completedCount = task.steps.count { it.status == StepStatus.COMPLETED }
            val progressFraction = if (task.steps.isNotEmpty()) completedCount.toFloat() / task.steps.size else 0f

            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(CircleShape),
                color = when (task.status) {
                    TaskStatus.COMPLETED -> CyberNeonGreen
                    TaskStatus.FAILED -> CyberNeonRed
                    TaskStatus.WAITING_FOR_APPROVAL -> CyberNeonAmber
                    else -> theme.primary
                },
                trackColor = theme.cardBg
            )

            // Approval Banner (User Gate with Resumption)
            if (task.status == TaskStatus.WAITING_FOR_APPROVAL) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberNeonAmber.copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, CyberNeonAmber),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = "Approval",
                                tint = CyberNeonAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ব্যবহারকারীর অনুমোদন প্রয়োজন (Approval Gate)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberNeonAmber,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = task.approvalReason ?: "পরবর্তী ধাপে যাওয়ার আগে অনুগ্রহ করে তৈরি করা স্ক্রিপ্ট ও দৃশ্যসমূহ যাচাই করুন।",
                            fontSize = 12.sp,
                            color = CyberTextPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onApprove,
                                colors = ButtonDefaults.buttonColors(containerColor = CyberNeonGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("workflow_approve_btn")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("অনুমোদন দিন ও চালু রাখুন", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = onCancel,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberNeonRed),
                                border = BorderStroke(1.dp, CyberNeonRed),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("বাতিল", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Error banner with retry
            if (task.status == TaskStatus.FAILED && task.error != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberNeonRed.copy(alpha = 0.15f))
                        .border(1.dp, CyberNeonRed, RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = CyberNeonRed, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = task.error,
                        fontSize = 11.sp,
                        color = CyberNeonRed,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onRetry, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = CyberNeonRed, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Completion Final Summary
            if (task.status == TaskStatus.COMPLETED && task.finalSummary != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberNeonGreen.copy(alpha = 0.12f))
                        .border(1.dp, CyberNeonGreen, RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberNeonGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = task.finalSummary,
                        fontSize = 11.sp,
                        color = CyberTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Summary", task.finalSummary))
                            Toast.makeText(context, "রিপোর্ট কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyberNeonGreen, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Action row
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (task.status == TaskStatus.RUNNING) {
                    OutlinedButton(
                        onClick = onCancel,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberNeonRed),
                        border = BorderStroke(0.8.dp, CyberNeonRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("টাস্ক থামান", fontSize = 11.sp)
                    }
                } else if (task.status == TaskStatus.COMPLETED || task.status == TaskStatus.CANCELLED || task.status == TaskStatus.FAILED) {
                    OutlinedButton(
                        onClick = onRetry,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = theme.primary),
                        border = BorderStroke(0.8.dp, theme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("পুনরায় চালান", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun WorkflowStepRow(
    stepNumber: Int,
    step: WorkflowStep,
    isCurrentStep: Boolean
) {
    val theme = LocalNovaTheme.current
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(step.status == StepStatus.RUNNING || step.status == StepStatus.WAITING_FOR_APPROVAL) }

    val stepBorderBrush = when (step.status) {
        StepStatus.RUNNING -> Brush.horizontalGradient(listOf(theme.primary, theme.accent))
        StepStatus.COMPLETED -> Brush.horizontalGradient(listOf(CyberNeonGreen.copy(alpha = 0.6f), CyberNeonGreen.copy(alpha = 0.3f)))
        StepStatus.WAITING_FOR_APPROVAL -> Brush.horizontalGradient(listOf(CyberNeonAmber, CyberNeonAmber))
        StepStatus.FAILED -> Brush.horizontalGradient(listOf(CyberNeonRed, CyberNeonRed))
        else -> Brush.horizontalGradient(listOf(theme.borderGlow.copy(alpha = 0.3f), theme.borderGlow.copy(alpha = 0.3f)))
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        border = BorderStroke(if (isCurrentStep) 1.5.dp else 0.8.dp, stepBorderBrush),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (!step.output.isNullOrBlank()) {
                    isExpanded = !isExpanded
                }
            }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Step number circle
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                when (step.status) {
                                    StepStatus.COMPLETED -> CyberNeonGreen.copy(alpha = 0.2f)
                                    StepStatus.RUNNING -> theme.primary.copy(alpha = 0.2f)
                                    StepStatus.WAITING_FOR_APPROVAL -> CyberNeonAmber.copy(alpha = 0.2f)
                                    StepStatus.FAILED -> CyberNeonRed.copy(alpha = 0.2f)
                                    else -> theme.cardBg
                                }
                            )
                            .border(
                                1.dp,
                                when (step.status) {
                                    StepStatus.COMPLETED -> CyberNeonGreen
                                    StepStatus.RUNNING -> theme.primary
                                    StepStatus.WAITING_FOR_APPROVAL -> CyberNeonAmber
                                    StepStatus.FAILED -> CyberNeonRed
                                    else -> theme.borderGlow.copy(alpha = 0.4f)
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$stepNumber",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (step.status) {
                                StepStatus.COMPLETED -> CyberNeonGreen
                                StepStatus.RUNNING -> theme.primary
                                StepStatus.WAITING_FOR_APPROVAL -> CyberNeonAmber
                                StepStatus.FAILED -> CyberNeonRed
                                else -> CyberTextMuted
                            },
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = step.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyberTextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Tool: ${step.tool}",
                                fontSize = 10.sp,
                                color = CyberTextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                            if (step.status == StepStatus.RUNNING || step.currentAttempt > 1) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(theme.primary.copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "Attempt ${step.currentAttempt}/${step.maxAttempts}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = theme.primary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                StepStatusPill(status = step.status)
            }

            // Running indicator
            if (step.status == StepStatus.RUNNING) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = theme.primary
                    )
                    Text(
                        text = "স্বয়ংক্রিয় প্রসেস চলমান (চেষ্টা ${step.currentAttempt}/${step.maxAttempts})…",
                        fontSize = 11.sp,
                        color = theme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Expandable Output
            AnimatedVisibility(visible = isExpanded && !step.output.isNullOrBlank()) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = theme.borderGlow.copy(alpha = 0.3f), thickness = 0.6.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "আউটপুট রেজাল্ট:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.primary,
                            fontFamily = FontFamily.Monospace
                        )

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText(step.title, step.output))
                                Toast.makeText(context, "কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy step output",
                                tint = CyberTextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.cardBg)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = step.output ?: "",
                            fontSize = 11.sp,
                            color = CyberTextSecondary,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PresetWorkflowChip(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    val theme = LocalNovaTheme.current
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = theme.cardBg),
        border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(color.copy(alpha = 0.6f), color.copy(alpha = 0.2f)))),
        modifier = Modifier
            .clickable(onClick = onClick)
            .width(170.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = CyberTextMuted,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
fun TaskStatusPill(status: TaskStatus) {
    val (color, text) = when (status) {
        TaskStatus.QUEUED -> Pair(CyberTextMuted, status.titleBn)
        TaskStatus.RUNNING -> Pair(CyberNeonCyan, status.titleBn)
        TaskStatus.WAITING_FOR_APPROVAL -> Pair(CyberNeonAmber, status.titleBn)
        TaskStatus.COMPLETED -> Pair(CyberNeonGreen, status.titleBn)
        TaskStatus.FAILED -> Pair(CyberNeonRed, status.titleBn)
        TaskStatus.CANCELLED -> Pair(CyberTextMuted, status.titleBn)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .border(0.8.dp, color, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun StepStatusPill(status: StepStatus) {
    val (color, text) = when (status) {
        StepStatus.PENDING -> Pair(CyberTextMuted, status.titleBn)
        StepStatus.RUNNING -> Pair(CyberNeonCyan, status.titleBn)
        StepStatus.WAITING_FOR_APPROVAL -> Pair(CyberNeonAmber, status.titleBn)
        StepStatus.COMPLETED -> Pair(CyberNeonGreen, status.titleBn)
        StepStatus.FAILED -> Pair(CyberNeonRed, status.titleBn)
        StepStatus.SKIPPED -> Pair(CyberTextMuted, status.titleBn)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .border(0.6.dp, color, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun StatusDot(status: TaskStatus) {
    val color = when (status) {
        TaskStatus.QUEUED -> CyberTextMuted
        TaskStatus.RUNNING -> CyberNeonCyan
        TaskStatus.WAITING_FOR_APPROVAL -> CyberNeonAmber
        TaskStatus.COMPLETED -> CyberNeonGreen
        TaskStatus.FAILED -> CyberNeonRed
        TaskStatus.CANCELLED -> CyberTextMuted
    }
    Box(
        modifier = Modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(color)
    )
}
