package com.nova.personalai.jarvis

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.NovaThemePreset

enum class JarvisViewTab(val label: String, val subtitle: String) {
    AGENTS("8 Agents", "Local Execution Modes"),
    SKILLS("Skills Catalog", "Hermes & OpenClaw"),
    TELEMETRY("Intelligence/Watt", "Zero Cloud Cost Benchmark")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenJarvisStudioView(
    modifier: Modifier = Modifier,
    currentPreset: NovaThemePreset = NovaThemePreset.IMPERIAL_GOLD,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(JarvisViewTab.AGENTS) }
    var selectedAgent by remember { mutableStateOf(OpenJarvisEngine.builtInAgents[0]) }
    var taskInput by remember { mutableStateOf("আজকের দৈনিক নিউজ ও সকালের এজেন্ডা ব্রিফিং প্রস্তুত করো") }
    var executionOutput by remember {
        mutableStateOf(OpenJarvisEngine.executePreset(selectedAgent.defaultPreset, taskInput))
    }

    val primaryColor = currentPreset.primary
    val cardBg = currentPreset.cardBg

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label কপি হয়েছে!", Toast.LENGTH_SHORT).show()
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(CyberNeonCyan.copy(alpha = 0.6f), primaryColor.copy(alpha = 0.5f)))
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CyberNeonCyan.copy(alpha = 0.15f))
                            .border(1.dp, CyberNeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "OpenJarvis",
                            tint = CyberNeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "OPENJARVIS ENGINE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = CyberNeonCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Personal AI, On Personal Devices • Stanford SAIL",
                            fontSize = 10.sp,
                            color = CyberTextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyberNeonCyan.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "LOCAL-FIRST",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = CyberNeonCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Tab Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                JarvisViewTab.values().forEach { tab ->
                    val isSelected = tab == currentTab
                    Surface(
                        modifier = Modifier
                            .clickable { currentTab = tab }
                            .testTag("jarvis_tab_${tab.name}"),
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) CyberNeonCyan.copy(alpha = 0.2f) else currentPreset.surface,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(
                                if (isSelected) listOf(CyberNeonCyan, CyberNeonCyan)
                                else listOf(CyberNeonCyan.copy(alpha = 0.2f), CyberNeonCyan.copy(alpha = 0.2f))
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = tab.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CyberNeonCyan else CyberTextPrimary
                            )
                        }
                    }
                }
            }

            // TAB 1: 8 BUILT-IN AGENTS
            if (currentTab == JarvisViewTab.AGENTS) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Agent Selector Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OpenJarvisEngine.builtInAgents.forEach { agent ->
                            val isSel = agent == selectedAgent
                            Surface(
                                modifier = Modifier.clickable {
                                    selectedAgent = agent
                                    executionOutput = OpenJarvisEngine.executePreset(agent.defaultPreset, taskInput)
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) primaryColor.copy(alpha = 0.2f) else currentPreset.surface,
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.horizontalGradient(
                                        if (isSel) listOf(primaryColor, primaryColor)
                                        else listOf(primaryColor.copy(alpha = 0.2f), primaryColor.copy(alpha = 0.2f))
                                    )
                                )
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                    Text(
                                        text = agent.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) primaryColor else CyberTextPrimary
                                    )
                                    Text(
                                        text = agent.executionMode,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (isSel) CyberNeonGreen else CyberTextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Active Agent Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = currentPreset.surface),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(CyberNeonCyan.copy(alpha = 0.4f), primaryColor.copy(alpha = 0.3f)))
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🤖 ${selectedAgent.title.uppercase()} [${selectedAgent.executionMode}]",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = CyberNeonCyan
                                )
                                Text(
                                    text = "Preset: ${selectedAgent.defaultPreset}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = primaryColor
                                )
                            }
                            Text(
                                text = selectedAgent.description,
                                fontSize = 11.5.sp,
                                color = CyberTextPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Task Input
                    OutlinedTextField(
                        value = taskInput,
                        onValueChange = { taskInput = it },
                        label = { Text("এজেন্ট টাস্ক বা নির্দেশ দিন", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberNeonCyan,
                            unfocusedBorderColor = CyberNeonCyan.copy(alpha = 0.3f),
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        )
                    )

                    Button(
                        onClick = {
                            executionOutput = OpenJarvisEngine.executePreset(selectedAgent.defaultPreset, taskInput)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberNeonCyan)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("লোকাল-ফার্স্ট এক্সিকিউশন রান করুন", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                    }

                    // Output Display
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF030712))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚡ EXECUTION LOG (ZERO CLOUD LATENCY)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = CyberNeonGreen
                                )
                                IconButton(
                                    onClick = { copyToClipboard("Jarvis Output", executionOutput) },
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyberNeonGreen, modifier = Modifier.size(14.dp))
                                }
                            }
                            Text(
                                text = executionOutput,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF00FFCC),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // TAB 2: SKILLS CATALOG (Hermes & OpenClaw)
            if (currentTab == JarvisViewTab.SKILLS) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "📦 ওপেনজারভিস স্কিল ক্যাটালগ (Hermes & OpenClaw Standard):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberNeonCyan
                    )

                    OpenJarvisEngine.installedSkills.forEach { skill ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(currentPreset.surface)
                                .border(0.8.dp, CyberNeonCyan.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = skill.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                    Text(text = skill.source, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = CyberNeonGreen)
                                }
                                Text(text = skill.description, fontSize = 11.sp, color = CyberTextPrimary)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF030712)
                                ) {
                                    Text(
                                        text = "$ ${skill.command}",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = CyberNeonCyan,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // TAB 3: TELEMETRY & INTELLIGENCE PER WATT
            if (currentTab == JarvisViewTab.TELEMETRY) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "📊 Intelligence Per Watt রিসার্চ বেঞ্চমার্ক (Stanford SAIL):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberNeonGreen
                    )

                    val telemetry = remember { LocalTelemetry() }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = currentPreset.surface
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("LOCAL QUERIES", fontSize = 9.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                                Text(telemetry.localQueryRatio, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyberNeonGreen)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = currentPreset.surface
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("EFFICIENCY", fontSize = 9.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                                Text("5.3× Boost", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyberNeonCyan)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = currentPreset.surface
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("LATENCY", fontSize = 9.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                                Text("${telemetry.latencyMs} ms", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = currentPreset.surface
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("CLOUD COST", fontSize = 9.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                                Text("$0.00 Free", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyberNeonGreen)
                            }
                        }
                    }

                    Text(
                        text = "💡 OpenJarvis নিশ্চিত করে যে ৮৮.৭% সিঙ্গেল-টার্ন চ্যাট ও রিজনিং সরাসরি আপনার ফোনেই সম্পন্ন হয়, যাতে কোনো বাহ্যিক সার্ভারের ওপর নির্ভর করতে না হয়।",
                        fontSize = 11.5.sp,
                        color = CyberTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
