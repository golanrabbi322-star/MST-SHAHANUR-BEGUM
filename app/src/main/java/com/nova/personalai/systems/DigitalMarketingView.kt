package com.nova.personalai.systems

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.ui.theme.CyberNeonAmber
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonRed
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalNovaTheme

@Composable
fun DigitalMarketingView() {
    val theme = LocalNovaTheme.current
    val context = LocalContext.current

    var campaignTopic by remember { mutableStateOf("বাংলা AI & টেকনোলজি গ্রোথ") }

    val viralTitles = remember(campaignTopic) {
        listOf(
            "🔥 $campaignTopic | কেউ আপনাকে এই গোপন ট্রিক বলবে না!",
            "⚡ ২০২৬ সালে $campaignTopic দিয়ে কীভাবে সফল হবেন? (A to Z গাইড)",
            "😱 এই ৩টি এআই টুল আপনার ঘণ্টার কাজ করবে মিনিটে! #Shorts"
        )
    }

    val tags = remember {
        listOf("#Shorts", "#BanglaTech", "#NOVA_AI", "#ViralShorts", "#TechHacks", "#AIProductivity", "#DigitalMarketing")
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        border = BorderStroke(1.dp, CyberNeonGreen.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyberNeonGreen.copy(alpha = 0.2f))
                        .border(1.dp, CyberNeonGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Marketing",
                        tint = CyberNeonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "DIGITAL MARKETING & SEO ENGINE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberNeonGreen,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "YouTube SEO, ভাইরাল ট্যাগ, ক্যালেন্ডার ও গ্রোথ অ্যানালিটিক্স",
                        fontSize = 11.sp,
                        color = CyberTextSecondary
                    )
                }
            }

            var showYouTubeAgentStudio by remember { mutableStateOf(false) }

            // YouTube Agent Skills Launcher Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF8B0000).copy(alpha = 0.35f), Color(0xFF030712))))
                    .border(1.dp, Color(0xFFFF0000).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable { showYouTubeAgentStudio = !showYouTubeAgentStudio }
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = Color(0xFFFF0000),
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "YouTube Agent Skills Engine (11 Skills)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "২১ হুক ফর্মুলা, স্কোরার, টাইটেল+থাম্বনেইল লিন্টার ও এডিট লিস্ট",
                                fontSize = 10.sp,
                                color = CyberTextSecondary
                            )
                        }
                    }
                    Text(
                        text = if (showYouTubeAgentStudio) "মিনিমাইজ" else "খুলুন ▾",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF4444)
                    )
                }
            }

            AnimatedVisibility(visible = showYouTubeAgentStudio) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    com.nova.personalai.youtube.YouTubeStudioView(
                        currentPreset = theme
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Topic Input
            OutlinedTextField(
                value = campaignTopic,
                onValueChange = { campaignTopic = it },
                label = { Text("মার্কেটিং নিশ বা বিষয়ের নাম", fontSize = 11.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("marketing_topic_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberNeonGreen,
                    unfocusedBorderColor = theme.borderGlow.copy(alpha = 0.5f),
                    focusedTextColor = CyberTextPrimary,
                    unfocusedTextColor = CyberTextPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Viral Title Hooks
            Text(text = "১. হাই-সিটিআর (High CTR) ভাইরাল টাইটেল ফর্মুলা:", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = CyberTextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                viralTitles.forEach { title ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.cardBg)
                            .border(0.8.dp, CyberNeonGreen.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Title", title))
                                Toast.makeText(context, "শিরোনাম কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                            }
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = title, fontSize = 11.5.sp, color = CyberTextPrimary, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = CyberNeonGreen, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SEO Tags
            Text(text = "২. অ্যালগরিদম র‍্যাংকিং এসইও ট্যাগ (#Tags):", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = CyberTextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tags.take(4).forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberNeonCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = tag, fontSize = 10.5.sp, color = CyberNeonCyan, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Analytics Report Projection
            Text(text = "৩. প্রজেক্টেড অ্যানালিটিক্স ও গ্রোথ মেট্রিক্স:", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = CyberTextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GrowthStatCard("EST. VIEWS", "185K+", CyberNeonGreen, Modifier.weight(1f))
                GrowthStatCard("EST. CTR", "12.8%", CyberNeonAmber, Modifier.weight(1f))
                GrowthStatCard("RETENTION", "84.2%", CyberNeonCyan, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Platform Launch Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://studio.youtube.com/"))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, "YouTube Studio ব্রাউজারে চালু হচ্ছে...", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("YouTube Studio", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://trends.google.com/trends/"))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, "Google Trends চালু হচ্ছে...", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Google Trends", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun GrowthStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .border(0.8.dp, color.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column {
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color, fontFamily = FontFamily.Monospace)
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = CyberTextPrimary)
        }
    }
}
