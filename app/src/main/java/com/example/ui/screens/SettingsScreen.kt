package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.CyberNeonPurple
import com.example.ui.theme.CyberObsidian
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalNovaTheme
import com.example.ui.theme.NovaThemePreset
import com.nova.personalai.BackendType
import com.nova.personalai.NovaApi
import com.nova.personalai.NovaBackend
import kotlinx.coroutines.launch

/**
 * Diagnostics & Voice System Settings matching Screenshot 2
 */
@Composable
fun SettingsScreen(
    currentPreset: NovaThemePreset = NovaThemePreset.PLATINUM_CYBER,
    onSelectPreset: (NovaThemePreset) -> Unit = {},
    dynamicIslandEnabled: Boolean,
    onToggleDynamicIsland: (Boolean) -> Unit,
    backgroundAgentEnabled: Boolean,
    onToggleBackgroundAgent: (Boolean) -> Unit,
    liveWebSearchEnabled: Boolean,
    onToggleLiveWebSearch: (Boolean) -> Unit,
    speechRate: Float,
    speechPitch: Float,
    selectedLanguage: String,
    onUpdateVoiceSettings: (rate: Float, pitch: Float, lang: String) -> Unit,
    onTestVoice: (String) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var rate by remember { mutableFloatStateOf(speechRate) }
    var pitch by remember { mutableFloatStateOf(speechPitch) }
    var currentLang by remember { mutableStateOf(selectedLanguage) }

    var backendType by remember { mutableStateOf(NovaApi.currentBackendType) }
    var phpUrl by remember { mutableStateOf(NovaApi.phpUrl.ifBlank { "http://127.0.0.1:8080/api.php" }) }
    var phpToken by remember { mutableStateOf(NovaApi.phpToken.ifBlank { "NOVA_SUPER_SECRET_2026" }) }
    var backendUrl by remember { mutableStateOf(NovaApi.webAppUrl) }
    var backendToken by remember { mutableStateOf(NovaApi.accessToken) }

    var isTestingConnection by remember { mutableStateOf(false) }
    var testResultText by remember { mutableStateOf("") }
    var testResultSuccess by remember { mutableStateOf<Boolean?>(null) }
    var showPhpScriptSection by remember { mutableStateOf(false) }
    var savedConfirmationText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(currentPreset.background)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Premium Theme Suite Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = currentPreset.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "PREMIUM LUXURY THEME SUITE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = currentPreset.primary,
                    letterSpacing = 1.sp
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(currentPreset.primary.copy(alpha = 0.15f))
                    .border(0.8.dp, currentPreset.primary, RoundedCornerShape(6.dp))
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text(
                    text = currentPreset.badge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = currentPreset.primary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Theme Selector Horizontal Cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = currentPreset.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.Brush.linearGradient(currentPreset.gradientColors),
                width = 1.2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Select your luxury visual vibe:",
                    fontSize = 12.sp,
                    color = CyberTextSecondary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NovaThemePreset.values().forEach { preset ->
                        val isSelected = preset == currentPreset
                        Box(
                            modifier = Modifier
                                .width(135.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(preset.cardBg)
                                .border(
                                    width = if (isSelected) 1.8.dp else 1.dp,
                                    color = if (isSelected) preset.primary else Color(0xFF22304A),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onSelectPreset(preset) }
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Gradient Color Dots
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(preset.primary)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(preset.secondary)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(preset.accent)
                                        )
                                    }

                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(preset.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(11.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = preset.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) preset.primary else CyberTextPrimary
                                )

                                Text(
                                    text = preset.badge,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = FontFamily.Monospace,
                                    color = preset.accent
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Header
        Text(
            text = "VOICE DIAGNOSTICS & SYSTEM",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = currentPreset.primary,
            letterSpacing = 1.sp
        )

        // System Toggles Card (from Screenshot 2)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1E293B)),
                width = 1.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Dynamic Island Toggle
                ToggleItem(
                    title = "DYNAMIC ISLAND TOP CAPSULE",
                    subtitle = "অডিও ওয়েবফর্ম ও লাইভ ক্যাপসুল স্ট্যাটাস প্রদর্শন",
                    checked = dynamicIslandEnabled,
                    onCheckedChange = onToggleDynamicIsland,
                    accentColor = CyberNeonPink
                )

                Box(modifier = Modifier.fillMaxWidth().height(0.6.dp).background(Color(0xFF1E293B)))

                // Background Agent Service Toggle
                ToggleItem(
                    title = "BACKGROUND AGENT SERVICE",
                    subtitle = "ব্যাকগ্রাউন্ডে স্বয়ংক্রিয় এআই এজেন্ট চালু রাখা",
                    checked = backgroundAgentEnabled,
                    onCheckedChange = onToggleBackgroundAgent,
                    accentColor = CyberNeonCyan
                )

                Box(modifier = Modifier.fillMaxWidth().height(0.6.dp).background(Color(0xFF1E293B)))

                // Live Web Search Browsing Toggle
                ToggleItem(
                    title = "LIVE WEB SEARCH BROWSING",
                    subtitle = "বাস্তব সময়ে ওয়েব সার্চ ও ফোন টুলস এক্সিকিউশন",
                    checked = liveWebSearchEnabled,
                    onCheckedChange = onToggleLiveWebSearch,
                    accentColor = CyberNeonGreen
                )
            }
        }

        // Voice Engine Tuning Card
        Text(
            text = "VOICE ENGINE & SPEECH SYNTHESIS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = CyberNeonPurple,
            letterSpacing = 1.sp
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1E293B)),
                width = 1.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Language Selection
                Text(
                    text = "ভয়েস ভাষা নির্বাচন (Speech Language)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CyberTextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val langs = listOf(
                        Triple("বাংলা", "bn-BD", "🇧🇩"),
                        Triple("English", "en-US", "🇺🇸"),
                        Triple("हिंदी", "hi-IN", "🇮🇳")
                    )

                    langs.forEach { (name, code, flag) ->
                        val isSelected = currentLang == code
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFF182A4A) else CyberSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) CyberNeonCyan else Color(0xFF1E293B),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    currentLang = code
                                    onUpdateVoiceSettings(rate, pitch, currentLang)
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$flag $name",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) CyberNeonCyan else CyberTextSecondary
                            )
                        }
                    }
                }

                // Speech Rate Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "কথা বলার গতি (Speed Rate)",
                            fontSize = 12.sp,
                            color = CyberTextSecondary
                        )
                        Text(
                            text = "${String.format("%.2f", rate)}x",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberNeonCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Slider(
                        value = rate,
                        onValueChange = {
                            rate = it
                            onUpdateVoiceSettings(rate, pitch, currentLang)
                        },
                        valueRange = 0.6f..1.6f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberNeonCyan,
                            activeTrackColor = CyberNeonCyan,
                            inactiveTrackColor = Color(0xFF1E293B)
                        )
                    )
                }

                // Pitch Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ভয়েস পিচ (Tone Pitch)",
                            fontSize = 12.sp,
                            color = CyberTextSecondary
                        )
                        Text(
                            text = "${String.format("%.2f", pitch)}x",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberNeonPurple,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Slider(
                        value = pitch,
                        onValueChange = {
                            pitch = it
                            onUpdateVoiceSettings(rate, pitch, currentLang)
                        },
                        valueRange = 0.6f..1.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberNeonPurple,
                            activeTrackColor = CyberNeonPurple,
                            inactiveTrackColor = Color(0xFF1E293B)
                        )
                    )
                }

                // Test Voice Button
                Button(
                    onClick = {
                        val testText = when (currentLang) {
                            "hi-IN" -> "नमस्ते! मैं नोवा एआई हूँ, आपका पर्सनल असिस्टेंट।"
                            "en-US" -> "Hello! I am NOVA AI, your autonomous assistant."
                            else -> "হ্যালো! আমি নোভা এআই, আপনার পার্সোনাল ভয়েস অ্যাসিস্ট্যান্ট।"
                        }
                        onTestVoice(testText)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16213B)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        tint = CyberNeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ভয়েস টেস্ট শুনুন",
                        color = CyberNeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Backend & AI Model Configuration
        Text(
            text = "AI MODEL & BACKEND PIPELINE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = CyberNeonGreen,
            letterSpacing = 1.sp
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1E293B)),
                width = 1.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "AI ইঞ্জিন ব্যাকএন্ড নির্বাচন করুন:",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextPrimary
                )

                // Backend Type Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BackendType.values().forEach { type ->
                        val isSelected = backendType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                backendType = type
                                testResultText = ""
                                testResultSuccess = null
                            },
                            label = { Text(type.titleBn, fontSize = 10.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberNeonGreen.copy(alpha = 0.25f),
                                selectedLabelColor = CyberNeonGreen
                            )
                        )
                    }
                }

                // 1. Direct Gemini Mode Info
                if (backendType == BackendType.DIRECT) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF031A0F))
                            .border(1.dp, CyberNeonGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = CyberNeonGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Model: Gemini 3.8 Flash • বিল্ট-ইন এক্টিভ",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberNeonGreen,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "কোনো সার্ভার বা হোস্টিং ছাড়াই সরাসরি ফোন থেকে Gemini AI কাজ করবে। চ্যাট, স্টোরি, বিল্ডার ও অটোমেশন অবিলম্বে রেডি।",
                                fontSize = 11.sp,
                                color = CyberTextSecondary
                            )
                        }
                    }
                }

                // 2. PHP Backend Mode Inputs
                if (backendType == BackendType.PHP) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF101C33))
                            .border(1.dp, CyberNeonCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Dns, contentDescription = null, tint = CyberNeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PHP API (Termux / cPanel / InfinityFree / VPS)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberNeonCyan
                            )
                        }
                    }

                    OutlinedTextField(
                        value = phpUrl,
                        onValueChange = { phpUrl = it },
                        label = { Text("PHP Server URL (api.php)") },
                        placeholder = { Text("https://your-domain.example/api.php বা http://127.0.0.1:8080/api.php") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberNeonCyan,
                            unfocusedBorderColor = Color(0xFF1E293B),
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = phpToken,
                        onValueChange = { phpToken = it },
                        label = { Text("X-NOVA-TOKEN সিক্রেট") },
                        placeholder = { Text("যেমন: NOVA_SUPER_SECRET_2026") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberNeonCyan,
                            unfocusedBorderColor = Color(0xFF1E293B),
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        )
                    )

                    // Quick URL Preset Chips for easy testing
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = phpUrl == "http://127.0.0.1:8080/api.php",
                            onClick = {
                                phpUrl = "http://127.0.0.1:8080/api.php"
                                if (phpToken.isBlank()) phpToken = "NOVA_SUPER_SECRET_2026"
                            },
                            label = { Text("📱 Termux Localhost", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberNeonCyan.copy(alpha = 0.2f))
                        )
                        FilterChip(
                            selected = phpUrl.startsWith("https://"),
                            onClick = {
                                if (!phpUrl.startsWith("https://")) phpUrl = "https://"
                            },
                            label = { Text("🌐 Web Hosting", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberNeonCyan.copy(alpha = 0.2f))
                        )
                    }

                    // Test PHP connection button
                    OutlinedButton(
                        enabled = !isTestingConnection,
                        onClick = {
                            if (phpUrl.isBlank()) {
                                testResultSuccess = false
                                testResultText = "⚠️ অনুগ্রহ করে আগে আপনার PHP সার্ভারের URL লিখুন (যেমন: http://127.0.0.1:8080/api.php বা https://your-server.com/api.php)।"
                                return@OutlinedButton
                            }
                            isTestingConnection = true
                            testResultText = ""
                            testResultSuccess = null
                            scope.launch {
                                val backend = NovaBackend(phpUrl, phpToken)
                                val (ok, msg) = backend.testConnection()
                                isTestingConnection = false
                                testResultSuccess = ok
                                testResultText = msg
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_php_connection_button"),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNeonCyan)
                    ) {
                        if (isTestingConnection) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CyberNeonCyan, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("সার্ভারে পিং করা হচ্ছে...", fontSize = 11.5.sp, color = CyberNeonCyan)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = CyberNeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PHP সার্ভার সংযোগ টেস্ট করুন", fontSize = 11.5.sp, color = CyberNeonCyan, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Test result box
                    if (testResultText.isNotBlank()) {
                        val isOk = testResultSuccess == true
                        val color = if (isOk) CyberNeonGreen else CyberNeonPink
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(color.copy(alpha = 0.1f))
                                .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isOk) Icons.Default.Check else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = color,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = testResultText,
                                        fontSize = 11.sp,
                                        color = color,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (!isOk) {
                                    Button(
                                        onClick = {
                                            backendType = BackendType.DIRECT
                                            NovaApi.saveConfig(context, BackendType.DIRECT, phpUrl, phpToken, backendUrl, backendToken)
                                            testResultSuccess = true
                                            testResultText = "✓ বিল্ট-ইন Direct AI ইঞ্জিন সক্রিয় করা হয়েছে! কোনো সার্ভার ছাড়াই NOVA ১০০% কাজ করবে।"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyberNeonGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("⚡ কোনো সার্ভার ছাড়াই Direct AI সক্রিয় করুন", fontSize = 11.sp, color = Color(0xFF03141F), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // View / Copy api.php code toggle
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(0.8.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .clickable { showPhpScriptSection = !showPhpScriptSection }
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Code, contentDescription = null, tint = CyberNeonCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "সম্পূর্ণ api.php কোড ও টারমাক্স গাইড",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTextPrimary
                                )
                            }
                            Icon(
                                imageVector = if (showPhpScriptSection) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = CyberTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (showPhpScriptSection) {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0F1D)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "📱 Android ফোনে Termux দিয়ে চালাতে চাইলে:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberNeonGreen
                                )
                                Text(
                                    text = "1. pkg install php\n2. export GEMINI_API_KEY=\"AI_Studio_Key\"\n3. export NOVA_API_TOKEN=\"${phpToken.ifBlank { "NOVA_SUPER_SECRET_2026" }}\"\n4. php -S 0.0.0.0:8080",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = CyberTextSecondary
                                )

                                Button(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                        val clip = ClipData.newPlainText("api.php", NovaApi.getSamplePhpCode())
                                        clipboard?.setPrimaryClip(clip)
                                        Toast.makeText(context, "✓ api.php কোড ক্লিপবোর্ডে কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonCyan),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("সম্পূর্ণ api.php কোড কপি করুন", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // 3. Google Apps Script Mode Inputs
                if (backendType == BackendType.APPS_SCRIPT) {
                    OutlinedTextField(
                        value = backendUrl,
                        onValueChange = { backendUrl = it },
                        label = { Text("Google Apps Script URL") },
                        placeholder = { Text("https://script.google.com/macros/s/...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberNeonGreen,
                            unfocusedBorderColor = Color(0xFF1E293B),
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = backendToken,
                        onValueChange = { backendToken = it },
                        label = { Text("NOVA Access Token") },
                        placeholder = { Text("NOVA_ACCESS_TOKEN") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberNeonGreen,
                            unfocusedBorderColor = Color(0xFF1E293B),
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        )
                    )
                }

                // Save All Settings Button
                Button(
                    onClick = {
                        NovaApi.saveConfig(
                            context = context,
                            backendType = backendType,
                            phpUrlInput = phpUrl,
                            phpTokenInput = phpToken,
                            appsScriptUrlInput = backendUrl,
                            appsScriptTokenInput = backendToken
                        )
                        savedConfirmationText = "✓ সেটিংস সফলভাবে সংরক্ষিত হয়েছে! (সক্রিয় ইঞ্জিন: ${backendType.titleBn})"
                        Toast.makeText(context, "✓ AI ব্যাকএন্ড সেটিংস সফলভাবে সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_settings_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "সেটিংস সংরক্ষণ করুন",
                        color = Color(0xFF021E0F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                if (savedConfirmationText.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberNeonGreen.copy(alpha = 0.12f))
                            .border(1.dp, CyberNeonGreen.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = CyberNeonGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = savedConfirmationText,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyberNeonGreen
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (checked) accentColor else CyberTextSecondary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = CyberTextMuted
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = accentColor,
                checkedTrackColor = accentColor.copy(alpha = 0.25f),
                uncheckedThumbColor = Color(0xFF64748B),
                uncheckedTrackColor = Color(0xFF1E293B)
            )
        )
    }
}
