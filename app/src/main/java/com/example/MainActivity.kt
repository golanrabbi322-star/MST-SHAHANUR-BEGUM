package com.example

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.*
import com.nova.personalai.ai.AIEngine
import com.nova.personalai.phone.PhoneActionManager
import com.nova.personalai.vision.ScreenVisionDialog
import com.nova.personalai.voice.SpeechManager
import com.nova.personalai.voice.TTSManager
import kotlinx.coroutines.launch
import java.util.Locale

enum class IdeTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    FILES("Files", Icons.Default.FolderOpen),
    EDITOR("Editor", Icons.Default.Code),
    TERMINAL("Terminal", Icons.Default.Terminal),
    PREVIEW("Preview", Icons.Default.PlayCircle),
    STACK("Stack", Icons.Default.Widgets),
    DEV("Dev", Icons.Default.Settings)
}

enum class NovaMode(val key: String, val label: String) {
    BUILDER("Builder", "Builder"),
    CHAT("Chat", "Chat"),
    STORYBOARD("Storyboard", "Storyboard"),
    MARKETING("Marketing", "Marketing")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme(preset = NovaThemePreset.PLATINUM_CYBER) {
                BypassAutonomousIdeApp()
            }
        }
    }
}

@Composable
fun BypassAutonomousIdeApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var currentPreset by remember { mutableStateOf(NovaThemePreset.PLATINUM_CYBER) }
    var currentTab by remember { mutableStateOf(IdeTab.HOME) }
    var currentMode by remember { mutableStateOf(NovaMode.BUILDER) }
    var selectedModel by remember { mutableStateOf("Gemini 2.0 Flash") }

    // Core Engines
    val ttsManager = remember { TTSManager(context) }
    val speechManager = remember { SpeechManager(context) }
    val phoneActionManager = remember { PhoneActionManager(context) }
    val aiEngine = remember { AIEngine() }

    // State Variables
    var isListening by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var autoSpeakEnabled by remember { mutableStateOf(true) }
    var showVisionDialog by remember { mutableStateOf(false) }

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasAudioPermission = granted
        if (!granted) {
            Toast.makeText(context, "ভয়েস ইনপুটের জন্য অডিও পারমিশন প্রয়োজন", Toast.LENGTH_SHORT).show()
        }
    }

    val messages = remember {
        mutableStateListOf(
            "NOVA" to "⚡ BYPASS AUTONOMOUS IDE • ACTIVE\nArchitect full-stack apps, write root utilities, and execute direct OS commands autonomously.\nPrompt in English or Bengali."
        )
    }

    DisposableEffect(ttsManager) {
        ttsManager.onSpeakingStateChanged = { speaking ->
            isSpeaking = speaking
        }
        onDispose {
            ttsManager.destroy()
            speechManager.destroy()
        }
    }

    fun handleCommand(rawPrompt: String) {
        val prompt = rawPrompt.trim()
        if (prompt.isEmpty() || isLoading) return

        messages.add("USER" to prompt)
        focusManager.clearFocus()

        // 1. Phone App Action
        val actionResult = phoneActionManager.executeAction(prompt)
        if (actionResult.handled) {
            messages.add("NOVA" to actionResult.feedbackMessage)
            Toast.makeText(context, actionResult.feedbackMessage, Toast.LENGTH_SHORT).show()
            if (autoSpeakEnabled) ttsManager.speak(actionResult.feedbackMessage)
            return
        }

        // 2. Vision Trigger
        val lower = prompt.lowercase(Locale.getDefault())
        if (lower.contains("স্ক্রিন") || lower.contains("ছবি") || lower.contains("vision") || lower.contains("চোখ")) {
            showVisionDialog = true
            val msg = "👁️ NOVA Screen Vision ডায়ালগ চালু হয়েছে! আপনার স্ক্রিনশট বা ছবি নির্বাচন করুন।"
            messages.add("NOVA" to msg)
            if (autoSpeakEnabled) ttsManager.speak(msg)
            return
        }

        // 3. AI Process
        isLoading = true
        coroutineScope.launch {
            try {
                val answer = aiEngine.processAsync(prompt, currentMode.key)
                messages.add("NOVA" to answer)
                if (autoSpeakEnabled) ttsManager.speak(answer)
            } catch (e: Exception) {
                val fallback = aiEngine.process(prompt)
                messages.add("NOVA" to fallback)
                if (autoSpeakEnabled) ttsManager.speak(fallback)
            } finally {
                isLoading = false
            }
        }
    }

    var currentVoiceQuery by remember { mutableStateOf("") }
    var voiceRmsDb by remember { mutableFloatStateOf(0f) }

    fun toggleVoiceInput() {
        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        if (isSpeaking) ttsManager.stop()

        if (isListening) {
            speechManager.stopListening()
            isListening = false
            currentVoiceQuery = ""
            voiceRmsDb = 0f
            return
        }

        isListening = true
        currentVoiceQuery = ""
        speechManager.startListening(
            languageCode = "bn-BD",
            onRmsChanged = { rms ->
                voiceRmsDb = rms
            },
            onPartialResult = { partial ->
                currentVoiceQuery = partial
            },
            onResult = { result ->
                isListening = false
                currentVoiceQuery = ""
                voiceRmsDb = 0f
                if (result.isNotBlank()) {
                    handleCommand(result)
                }
            },
            onError = { errMsg ->
                isListening = false
                currentVoiceQuery = ""
                voiceRmsDb = 0f
                Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showVisionDialog) {
        ScreenVisionDialog(
            preset = currentPreset,
            isAnalyzing = isLoading,
            onDismiss = { showVisionDialog = false },
            onAnalyzeScreen = { bmp, pText ->
                showVisionDialog = false
                val report = "👁️【NOVA Screen Vision পর্যবেক্ষণ】\n• রেজোলিউশন: ${bmp.width}×${bmp.height}px\n• স্ট্যাটাস: স্ক্রিন উপাদান সফলভাবে বিশ্লেষণ করা হয়েছে।"
                messages.add("USER" to "📸 [Screen Vision] $pText")
                messages.add("NOVA" to report)
                if (autoSpeakEnabled) ttsManager.speak("স্ক্রিনশট বিশ্লেষণ সম্পন্ন হয়েছে।")
            }
        )
    }

    Scaffold(
        bottomBar = {
            // Iconic Cyan Glow Bottom Navigation Bar exactly from user photo
            Surface(
                color = Color(0xFF040814),
                border = BorderStroke(1.dp, CyberNeonCyan.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IdeTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { currentTab = tab }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(CyberNeonCyan.copy(alpha = 0.2f))
                                        .border(1.dp, CyberNeonCyan, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(tab.icon, contentDescription = tab.title, tint = CyberNeonCyan, modifier = Modifier.size(18.dp))
                                }
                            } else {
                                Icon(tab.icon, contentDescription = tab.title, tint = Color(0xFF64748B), modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = tab.title,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (isSelected) CyberNeonCyan else Color(0xFF64748B),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF020611))
        ) {
            when (currentTab) {
                IdeTab.HOME -> BypassIdeHomeScreen(
                    currentPreset = currentPreset,
                    currentMode = currentMode,
                    selectedModel = selectedModel,
                    messages = messages,
                    isLoading = isLoading,
                    isListening = isListening,
                    isSpeaking = isSpeaking,
                    liveVoiceQuery = currentVoiceQuery,
                    voiceRmsDb = voiceRmsDb,
                    onSelectMode = { currentMode = it },
                    onSelectModel = { selectedModel = it },
                    onSendCommand = { handleCommand(it) },
                    onToggleVoice = { toggleVoiceInput() },
                    onOpenVision = { showVisionDialog = true },
                    onStopSpeech = { ttsManager.stop() },
                    onQuickPrompt = { handleCommand(it) }
                )

                IdeTab.FILES -> {
                    com.nova.personalai.systems.AppBuilderStudioView(
                        onNavigateToEditor = { currentTab = IdeTab.EDITOR },
                        onNavigateToFiles = { /* stay */ },
                        onNavigateToPreview = { currentTab = IdeTab.PREVIEW }
                    )
                }

                IdeTab.EDITOR -> {
                    com.nova.personalai.jarvis.OpenJarvisStudioView(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        currentPreset = currentPreset,
                        onNavigateBack = { currentTab = IdeTab.HOME }
                    )
                }

                IdeTab.TERMINAL -> {
                    com.nova.personalai.cybersecurity.NovaCybersecurityScreen(
                        currentPreset = currentPreset,
                        onNavigateBack = { currentTab = IdeTab.HOME }
                    )
                }

                IdeTab.PREVIEW -> {
                    com.nova.personalai.youtube.YouTubeStudioView(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        currentPreset = currentPreset,
                        onNavigateBack = { currentTab = IdeTab.HOME }
                    )
                }

                IdeTab.STACK -> {
                    com.nova.personalai.systems.ClaudePowerStackScreen(
                        currentPreset = currentPreset,
                        onExecuteTool = { cmd -> handleCommand(cmd) },
                        onNavigateBack = { currentTab = IdeTab.HOME }
                    )
                }

                IdeTab.DEV -> {
                    com.nova.personalai.NovaAcademyScreen(
                        currentPreset = currentPreset,
                        onNavigateBack = { currentTab = IdeTab.HOME }
                    )
                }
            }
        }
    }
}

@Composable
fun BypassIdeHomeScreen(
    currentPreset: NovaThemePreset,
    currentMode: NovaMode,
    selectedModel: String,
    messages: List<Pair<String, String>>,
    isLoading: Boolean,
    isListening: Boolean,
    isSpeaking: Boolean,
    liveVoiceQuery: String = "",
    voiceRmsDb: Float = 0f,
    onSelectMode: (NovaMode) -> Unit,
    onSelectModel: (String) -> Unit,
    onSendCommand: (String) -> Unit,
    onToggleVoice: () -> Unit,
    onOpenVision: () -> Unit,
    onStopSpeech: () -> Unit,
    onQuickPrompt: (String) -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Dynamic Pill / Status Bar (from photo)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF091424),
                border = BorderStroke(1.dp, CyberNeonCyan.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).background(CyberNeonGreen, CircleShape))
                    Text(
                        text = "BYPASS IDE OS",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = CyberNeonCyan
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                if (isSpeaking) {
                    IconButton(
                        onClick = onStopSpeech,
                        modifier = Modifier.size(30.dp).background(Color(0xFFEF4444).copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.VolumeOff, contentDescription = "Mute", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    }
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CyberNeonGreen.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, CyberNeonGreen.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "ROOT ACTIVE",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CyberNeonGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Center Hexagon/Glow Icon & Title (from photo)
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF8B5CF6).copy(alpha = 0.4f), Color(0xFF00F2FE).copy(alpha = 0.2f), Color.Transparent)
                    )
                )
                .border(1.5.dp, Brush.horizontalGradient(listOf(Color(0xFF00F2FE), Color(0xFF8B5CF6))), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Terminal,
                contentDescription = "Bypass IDE",
                tint = CyberNeonCyan,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF00F2FE).copy(alpha = 0.12f),
            border = BorderStroke(0.8.dp, CyberNeonCyan)
        ) {
            Text(
                text = "ADVANCED ROOT AI ENGINE",
                fontSize = 9.5.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = CyberNeonCyan,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "BYPASS AUTONOMOUS IDE",
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace,
            color = Color.White,
            letterSpacing = 0.5.sp
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Architect full-stack apps, write root utilities, and execute direct OS commands autonomously. Prompt in English, Bengali, or Hindi.",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8),
            lineHeight = 15.sp,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        // Quick Suggestion Capsule ("✦ Apple Landing Page •" from photo)
        Surface(
            modifier = Modifier.clickable { onQuickPrompt("একটি মডার্ন Apple স্টাইল ল্যান্ডিং পেজ কোড তৈরি করো") },
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF091424),
            border = BorderStroke(1.dp, CyberNeonCyan.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("✦", color = CyberNeonCyan, fontSize = 12.sp)
                Text("Apple Landing Page", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text("•", color = CyberNeonCyan, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Model & Mode Selectors Pills (e.g. Gemini 2.0 Flash, Builder from photo)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center
        ) {
            // Model Pill
            Surface(
                modifier = Modifier.clickable {
                    onSelectModel(if (selectedModel == "Gemini 2.0 Flash") "Claude 3.5 Sonnet" else "Gemini 2.0 Flash")
                },
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF06101E),
                border = BorderStroke(1.dp, CyberNeonCyan)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).background(CyberNeonCyan, CircleShape))
                    Text(selectedModel, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }

            Spacer(Modifier.width(8.dp))

            // Modes
            NovaMode.values().forEach { mode ->
                val isSel = currentMode == mode
                Surface(
                    modifier = Modifier
                        .clickable { onSelectMode(mode) }
                        .padding(end = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSel) CyberNeonCyan.copy(alpha = 0.2f) else Color(0xFF06101E),
                    border = BorderStroke(1.dp, if (isSel) CyberNeonCyan else Color(0xFF1E2D44))
                ) {
                    Text(
                        text = "• ${mode.label}",
                        fontSize = 10.sp,
                        color = if (isSel) CyberNeonCyan else Color(0xFF94A3B8),
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Terminal Output / Chat Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF040A17))
                .border(1.dp, Color(0xFF0E1E36), RoundedCornerShape(14.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { pair ->
                val isUser = pair.first == "USER"
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isUser) Color(0xFF0B1B33) else Color(0xFF071120))
                        .border(0.8.dp, if (isUser) CyberNeonCyan.copy(alpha = 0.4f) else Color(0xFF132238), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = if (isUser) "➜ USER COMMAND" else "⚡ NOVA AUTONOMOUS ENGINE",
                        fontSize = 9.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (isUser) CyberNeonCyan else CyberNeonGreen
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = pair.second,
                        fontSize = 12.sp,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 17.sp,
                        fontFamily = if (pair.second.contains("```") || pair.second.contains("•")) FontFamily.Monospace else FontFamily.Default
                    )
                }
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = CyberNeonCyan, strokeWidth = 2.dp)
                        Text("Autonomous executor compiling task...", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF94A3B8))
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Neon Curved Cyan Input Capsule (Exact match with user photo)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            color = Color(0xFF060D1A),
            border = BorderStroke(1.4.dp, CyberNeonCyan)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Screen Vision Icon Button
                IconButton(onClick = onOpenVision, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Visibility, contentDescription = "Screen Vision", tint = CyberNeonCyan, modifier = Modifier.size(18.dp))
                }

                // Voice Mic Button
                IconButton(
                    onClick = onToggleVoice,
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (isListening) Color(0xFFEF4444) else Color.Transparent, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = if (isListening) Color.White else CyberNeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Input Box
                OutlinedTextField(
                    value = if (isListening && liveVoiceQuery.isNotBlank()) liveVoiceQuery else textInput,
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text(
                            text = if (isListening) {
                                if (liveVoiceQuery.isNotBlank()) "🎙️ $liveVoiceQuery..." else "🎙️ শুনছি... মুখে বলুন (RMS: ${"%.1f".format(voiceRmsDb)})..."
                            } else {
                                "Build apps, create websites, or write code..."
                            },
                            color = if (isListening) CyberNeonCyan else Color(0xFF64748B),
                            fontSize = 11.5.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                // Send Neon Capsule Button (arrow up/send from photo)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyberNeonCyan)
                        .clickable {
                            val txt = textInput.trim()
                            if (txt.isNotEmpty()) {
                                textInput = ""
                                onSendCommand(txt)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "Execute", tint = Color.Black, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
