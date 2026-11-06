package com.nova.personalai.youtube

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.CyberNeonPurple
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.NovaThemePreset

enum class YTSkillTab(val title: String, val icon: ImageVector, val code: String) {
    SCRIPT("Script & Hooks", Icons.Default.Edit, "/yt-script"),
    PACKAGE("Title & Thumb", Icons.Default.Image, "/yt-package"),
    EDIT_EDL("Edit & Retention", Icons.Default.ContentCut, "/yt-edit"),
    VIRAL_FORMULAS("21 Hook Matrix", Icons.AutoMirrored.Filled.TrendingUp, "/yt-viral")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YouTubeStudioView(
    modifier: Modifier = Modifier,
    currentPreset: NovaThemePreset = NovaThemePreset.IMPERIAL_GOLD,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(YTSkillTab.SCRIPT) }
    var topicInput by remember { mutableStateOf("২০২৬ সালে YouTube চ্যানেল গ্রোথ ও অ্যালগরিদম") }
    var currentScriptPackage by remember { mutableStateOf(YouTubeAgentEngine.generateFullScriptPackage(topicInput)) }

    // Package linter state
    var titleInput by remember { mutableStateOf("YouTube এ বেশি ভিউ পাওয়ার ৩টি গোপন নিয়ম (২০২৬ গাইড)") }
    var thumbnailTextInput by remember { mutableStateOf("৯০% ক্রিয়েটর এই ভুল করে!") }
    var packageAudit by remember { mutableStateOf(YouTubeAgentEngine.auditPackaging(titleInput, thumbnailTextInput)) }

    // Custom hook score test
    var customHookInput by remember { mutableStateOf("অধিকাংশ মানুষ ভাবে বেশি ভিডিও বানালেই ভিউ বাড়ে, কিন্তু বাস্তবতা হলো প্রথম ৫ সেকেন্ডই সবকিছু!") }
    var customHookScore by remember { mutableStateOf(YouTubeAgentEngine.scoreHook(customHookInput)) }

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
            brush = Brush.horizontalGradient(listOf(Color(0xFFFF0000).copy(alpha = 0.6f), primaryColor.copy(alpha = 0.5f)))
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
                            .background(Color(0xFFFF0000).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFFFF0000), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "YouTube",
                            tint = Color(0xFFFF0000),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "YOUTUBE AGENT STUDIO",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = primaryColor,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "11 Claude Skills • 21 Hook Formulas • Packaging Linter",
                            fontSize = 10.sp,
                            color = CyberTextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = primaryColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = selectedTab.code,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = primaryColor,
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
                YTSkillTab.values().forEach { tab ->
                    val isSelected = tab == selectedTab
                    Surface(
                        modifier = Modifier
                            .clickable { selectedTab = tab }
                            .testTag("yt_tab_${tab.name}"),
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) primaryColor.copy(alpha = 0.2f) else currentPreset.surface,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(
                                if (isSelected) listOf(primaryColor, primaryColor)
                                else listOf(primaryColor.copy(alpha = 0.2f), primaryColor.copy(alpha = 0.2f))
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = if (isSelected) primaryColor else CyberTextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) primaryColor else CyberTextPrimary
                            )
                        }
                    }
                }
            }

            // TAB 1: SCRIPT & HOOK SCORER
            if (selectedTab == YTSkillTab.SCRIPT) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = topicInput,
                        onValueChange = { topicInput = it },
                        label = { Text("ভিডিওর বিষয় বা আইডিয়া", fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("yt_topic_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = primaryColor.copy(alpha = 0.3f),
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        )
                    )

                    Button(
                        onClick = {
                            currentScriptPackage = YouTubeAgentEngine.generateFullScriptPackage(topicInput)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("generate_yt_script_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("২১ ফর্মুলায় স্ক্রিপ্ট ও হুক স্কোর তৈরি করুন", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                    }

                    // Scored Hook Badge Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = currentPreset.surface),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(CyberNeonGreen.copy(alpha = 0.5f), primaryColor.copy(alpha = 0.3f)))
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🎯 GENERATED HOOK & SCORE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = CyberNeonGreen
                                )
                                Text(
                                    text = "${currentScriptPackage.hookScore.totalScore}/100 [${currentScriptPackage.hookScore.grade}]",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    color = CyberNeonGreen
                                )
                            }

                            Text(
                                text = "\"${currentScriptPackage.generatedHook}\"",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyberTextPrimary,
                                lineHeight = 18.sp
                            )

                            Text(
                                text = "💡 ${currentScriptPackage.hookScore.feedback}",
                                fontSize = 11.sp,
                                color = CyberTextSecondary
                            )
                        }
                    }

                    // Retention Spoken Script
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
                                    text = "📜 SPOKEN SCRIPT (RETENTION BEATS)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = primaryColor
                                )
                                IconButton(
                                    onClick = { copyToClipboard("Script", currentScriptPackage.retentionScript) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = primaryColor, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(
                                text = currentScriptPackage.retentionScript,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF00FFCC),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // TAB 2: PACKAGING LINTER (/yt-package)
            if (selectedTab == YTSkillTab.PACKAGE) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "টাইটেল ও থাম্বনেইল একসাথে লিন্ট করুন (ডুপ্লিকেশন ও মোবাইল ট্রাংকেশন চেক):",
                        fontSize = 12.sp,
                        color = CyberTextSecondary
                    )

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = {
                            titleInput = it
                            packageAudit = YouTubeAgentEngine.auditPackaging(titleInput, thumbnailTextInput)
                        },
                        label = { Text("ভিডিও টাইটেল (Title - max 55 chars)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = thumbnailTextInput,
                        onValueChange = {
                            thumbnailTextInput = it
                            packageAudit = YouTubeAgentEngine.auditPackaging(titleInput, thumbnailTextInput)
                        },
                        label = { Text("থাম্বনেইল টেক্সট (Thumbnail Visual Text)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Packaging Score & Lint Output
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = currentPreset.surface),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(
                                if (packageAudit.score >= 80) listOf(CyberNeonGreen, CyberNeonGreen)
                                else listOf(Color(0xFFFF3366), primaryColor)
                            )
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📦 PACKAGING LINTER RESULT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (packageAudit.score >= 80) CyberNeonGreen else Color(0xFFFF3366)
                                )
                                Text(
                                    text = "Score: ${packageAudit.score}/100",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (packageAudit.score >= 80) CyberNeonGreen else Color(0xFFFF3366)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text(
                                    text = "Title: ${packageAudit.titleLength} chars ${if (packageAudit.isTruncatedMobile) "❌ (Mobile Truncated)" else "✅"}",
                                    fontSize = 11.sp,
                                    color = CyberTextSecondary
                                )
                                Text(
                                    text = "Duplication: ${if (packageAudit.hasDuplication) "❌ হ্যাঁ (খারাপ)" else "✅ নেই (ভালো)"}",
                                    fontSize = 11.sp,
                                    color = CyberTextSecondary
                                )
                            }

                            Text(
                                text = packageAudit.recommendation,
                                fontSize = 12.sp,
                                color = CyberTextPrimary,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }

            // TAB 3: EDIT DECISION LIST & CHAPTERS (/yt-edit, /yt-chapters)
            if (selectedTab == YTSkillTab.EDIT_EDL) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "✂️ এডিট ডিসিশন লিস্ট (EDL) ও ইউটিউব চ্যাপ্টারস:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )

                    currentScriptPackage.editDecisionList.forEach { edlItem ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(currentPreset.surface)
                                .padding(10.dp)
                        ) {
                            Text(text = edlItem, fontSize = 11.sp, color = CyberTextPrimary)
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "📑 ইউটিউব চ্যাপ্টারস (Timestamp Validated):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF030712))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            currentScriptPackage.chapters.forEach { (time, name) ->
                                Text(
                                    text = "$time - $name",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF00FFCC)
                                )
                            }
                        }
                    }
                }
            }

            // TAB 4: 21 HOOK FORMULAS MATRIX
            if (selectedTab == YTSkillTab.VIRAL_FORMULAS) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "🔥 ২১টি পরীক্ষিত ইউটিউব হুক ফর্মুলা (ক্লিক করে কপি করুন):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )

                    hookFormulas21.forEachIndexed { idx, f ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(currentPreset.surface)
                                .border(0.8.dp, primaryColor.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .clickable {
                                    copyToClipboard(f.name, f.example)
                                }
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "${idx + 1}. ${f.name}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                    Text(text = f.category, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = CyberTextSecondary)
                                }
                                Text(text = f.example, fontSize = 11.sp, color = CyberTextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}
