package com.nova.personalai.systems

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.CyberNeonPurple
import com.example.ui.theme.NovaThemePreset

data class PowerStackTool(
    val id: String,
    val name: String,
    val repoName: String,
    val category: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val actionCommand: String
)

val ClaudePowerStackTools = listOf(
    PowerStackTool(
        id = "hyperframes",
        name = "HyperFrames",
        repoName = "heygen-com/hyperframes",
        category = "VIDEO & MOTION",
        description = "HTML/CSS থেকে সরাসরি সিনেমাটিক ভিডিও ও মোশন কনটেন্ট তৈরির অটোমেশন পাইপলাইন।",
        icon = Icons.Default.Movie,
        accentColor = CyberNeonPink,
        actionCommand = "/hyperframes --render html-motion"
    ),
    PowerStackTool(
        id = "impeccable",
        name = "Impeccable",
        repoName = "impeccable-ai/design-engine",
        category = "UI/UX & DESIGN",
        description = "প্রিমিয়াম ডার্ক থিম, নিখুঁত টাইপোগ্রাফি ও আধুনিক Vibe Coding ওয়েব লেআউট আর্কিটেক্ট।",
        icon = Icons.Default.Palette,
        accentColor = CyberNeonCyan,
        actionCommand = "/impeccable --scaffold portfolio-theme"
    ),
    PowerStackTool(
        id = "voicestudio",
        name = "VoiceStudio",
        repoName = "voice-ai/voicestudio-android",
        category = "SPEECH & DUBBING",
        description = "Android SpeechRecognizer ও রিয়েল-টাইম বাংলা ভয়েস সিন্থেসিস (TTS ডাবিং ইঞ্জিন)।",
        icon = Icons.Default.Mic,
        accentColor = CyberNeonGreen,
        actionCommand = "/voicestudio --listen bn-BD"
    ),
    PowerStackTool(
        id = "agent_reach",
        name = "Agent Reach",
        repoName = "agentreach/web-crawler-citations",
        category = "WEB RESEARCH",
        description = "মাল্টি-হপ রিসার্চ, লোকাল ডকুমেন্ট ইনডেক্সিং এবং পেপার সাইটেশন এক্সট্রাক্টর।",
        icon = Icons.Default.Search,
        accentColor = Color(0xFF38BDF8),
        actionCommand = "/reach --query deep-research"
    ),
    PowerStackTool(
        id = "claude_mem",
        name = "Claude Mem",
        repoName = "memory-layer/claude-mem-persistent",
        category = "LONG-TERM MEMORY",
        description = "ইউজারের পছন্দ, ওয়ার্কফ্লো স্মৃতি ও প্রজেক্ট কনটেক্সট আজীবনের জন্য ক্লাউড/লোকালে সংরক্ষণ।",
        icon = Icons.Default.Storage,
        accentColor = CyberNeonPurple,
        actionCommand = "/memory --sync-state"
    ),
    PowerStackTool(
        id = "ponytail",
        name = "Ponytail",
        repoName = "ponytail/code-review-optimizer",
        category = "CODE REVIEW & LINT",
        description = "কম লাইনে কার্যকর ক্লিন কোড লেখা, বাগ ডিটেকশন ও রিয়েল-টাইম কোড লিন্টিং।",
        icon = Icons.Default.Code,
        accentColor = Color(0xFFFFB800),
        actionCommand = "/ponytail --lint clean-code"
    ),
    PowerStackTool(
        id = "plugins",
        name = "Knowledge Work Plugins",
        repoName = "anthropic/knowledge-work-plugins",
        category = "PRO WORKFLOW",
        description = "ইউটিউব ভাইরাল হুক অ্যালগরিদম, ডিজিটাল মার্কেটিং ও এসইও অপ্টিমাইজার প্লাগইন।",
        icon = Icons.Default.Extension,
        accentColor = Color(0xFFFF0055),
        actionCommand = "/plugins --load yt-viral-hooks"
    ),
    PowerStackTool(
        id = "context_mode",
        name = "Context Mode",
        repoName = "context-ai/context-mode-orchestrator",
        category = "CONTEXT MANAGEMENT",
        description = "বড় প্রজেক্টের মাল্টি-ফাইল কনটেক্সট ম্যানেজমেন্ট ও স্টেপ-বাই-স্টেপ রিজনিং ট্র্যাকার।",
        icon = Icons.Default.AccountTree,
        accentColor = CyberNeonCyan,
        actionCommand = "/context --manage active-tokens"
    ),
    PowerStackTool(
        id = "hallmark",
        name = "Hallmark",
        repoName = "hallmark-docs/ai-presentation-gen",
        category = "DOCS & PRESENTATION",
        description = "স্বয়ংক্রিয় প্রফেশনাল রিপোর্ট, সাইবার অডিট লগ ও প্রেজেন্টেশন স্লাইড জেনারেটর।",
        icon = Icons.Default.Description,
        accentColor = CyberNeonGreen,
        actionCommand = "/hallmark --generate audit-report"
    ),
    PowerStackTool(
        id = "cloudflare_audit",
        name = "Cloudflare Security Audit",
        repoName = "security-audit/cloudflare-fastapi-shield",
        category = "SECURITY & SHIELD",
        description = "FastAPI Rate Limiting, JWT Bearer Token ভেরিফিকেশন ও এপিআই ডস ডিফেন্স শিল্ড।",
        icon = Icons.Default.Security,
        accentColor = CyberNeonPink,
        actionCommand = "/shield --status-check"
    )
)

@Composable
fun ClaudePowerStackScreen(
    currentPreset: NovaThemePreset = NovaThemePreset.PLATINUM_CYBER,
    onExecuteTool: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    var selectedTool by remember { mutableStateOf<PowerStackTool?>(null) }
    var actionOutput by remember { mutableStateOf("টুল নির্বাচন করে 'RUN' বাটনে চাপ দিন।") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020611))
            .padding(14.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyberNeonCyan.copy(alpha = 0.2f))
                        .border(1.dp, CyberNeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Widgets, contentDescription = "Stack", tint = CyberNeonCyan, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text("CLAUDE POWER STACK", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, fontFamily = FontFamily.Monospace)
                    Text("10-IN-1 AUTONOMOUS AI AGENT REPOSITORIES", fontSize = 9.sp, color = Color(0xFF64748B), fontFamily = FontFamily.Monospace)
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CyberNeonGreen.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, CyberNeonGreen.copy(alpha = 0.4f))
            ) {
                Text(
                    text = "10/10 READY",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyberNeonGreen,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Info Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF071120),
            border = BorderStroke(1.dp, Color(0xFF132238)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "⚡ এই ১০টি পাওয়ার রিপোজিটরি সরাসরি আপনার ফোনে কন্ট্রোল সেন্টার হিসেবে কাজ করছে এবং ব্যাকএন্ডে অটোনোমাসলি কোড, ডিজাইন, ভিডিও ও মেমোরি হ্যান্ডেল করছে।",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 16.sp,
                modifier = Modifier.padding(10.dp)
            )
        }

        Spacer(Modifier.height(10.dp))

        // 10-Tool Lazy List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ClaudePowerStackTools) { tool ->
                val isSelected = selectedTool?.id == tool.id

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) tool.accentColor.copy(alpha = 0.15f) else Color(0xFF040A17),
                    border = BorderStroke(
                        if (isSelected) 1.5.dp else 0.8.dp,
                        if (isSelected) tool.accentColor else Color(0xFF0E1E36)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedTool = tool
                            actionOutput = "⚡ [${tool.name} Activated]\nRepository: ${tool.repoName}\nCommand: ${tool.actionCommand}\nStatus: Module initialized on-device."
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(tool.accentColor.copy(alpha = 0.2f))
                                .border(1.dp, tool.accentColor, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(tool.icon, contentDescription = tool.name, tint = tool.accentColor, modifier = Modifier.size(20.dp))
                        }

                        Spacer(Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(tool.name, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = tool.accentColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = tool.category,
                                        fontSize = 8.sp,
                                        color = tool.accentColor,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(3.dp))
                            Text(tool.description, fontSize = 10.5.sp, color = Color(0xFF94A3B8), lineHeight = 14.sp)
                        }

                        Spacer(Modifier.width(6.dp))

                        Button(
                            onClick = {
                                selectedTool = tool
                                onExecuteTool(tool.actionCommand)
                                actionOutput = "🚀 [Executing ${tool.name}]:\nCommand dispatched -> ${tool.actionCommand}\nProcess ID: #STK-${tool.id.uppercase()}-OK"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = tool.accentColor),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("RUN", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Console Log Output
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF030812),
            border = BorderStroke(1.dp, CyberNeonCyan.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("➜ STACK CONSOLE", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = CyberNeonCyan, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(actionOutput, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF00FFCC), lineHeight = 15.sp)
            }
        }
    }
}
