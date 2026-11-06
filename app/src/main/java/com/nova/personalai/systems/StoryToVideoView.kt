package com.nova.personalai.systems

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Splitscreen
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
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.CyberNeonRed
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalNovaTheme
import com.nova.personalai.video.AIVideoGenerator
import com.nova.personalai.video.AIVideoProject
import com.nova.personalai.video.VideoScene

@Composable
fun StoryToVideoView(
    onPlayProject: (AIVideoProject) -> Unit = {}
) {
    val theme = LocalNovaTheme.current
    val context = LocalContext.current

    var storyTopic by remember {
        mutableStateOf("মহাকাশের অতল রহস্য ও কৃষ্ণগহ্বর")
    }

    var storyScript by remember {
        mutableStateOf(
            "মহাবিশ্বের সবচেয়ে গভীর রহস্য হলো কৃষ্ণগহ্বর। এর তীব্র মহাকর্ষ বলের কারণে আলো পর্যন্ত ফিরে আসতে পারে না। এর কাছাকাছি সময় এবং স্থান অদ্ভুতভাবে বেঁকে যায়। বিজ্ঞানীদের নতুন আবিষ্কার উন্মোচন করেছে অনন্য মহাজাগতিক দিগন্ত।"
        )
    }

    var currentProject by remember {
        mutableStateOf(AIVideoGenerator.createProject(storyTopic, isShorts = true))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        border = BorderStroke(1.dp, CyberNeonRed.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyberNeonRed.copy(alpha = 0.2f))
                        .border(1.dp, CyberNeonRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = "Story to Video",
                        tint = CyberNeonRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "STORY-TO-VIDEO STUDIO",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberNeonRed,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "গল্পকে দৃশ্য, প্রম্পট ও Narration-এ স্বয়ংক্রিয় ভাগ করা",
                        fontSize = 11.sp,
                        color = CyberTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Preset Topic Chips
            Text(text = "জনপ্রিয় গল্প থিম নির্বাচন করুন:", fontSize = 11.5.sp, color = CyberTextSecondary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "মহাকাশ ও ব্ল্যাকহোল" to "মহাবিশ্বের সবচেয়ে গভীর রহস্য হলো কৃষ্ণগহ্বর। এর তীব্র মহাকর্ষের কারণে আলোও ফিরে আসতে পারে না। এর আশেপাশে স্থান ও সময় বেঁকে গিয়ে অন্য মাত্রা তৈরি করে।",
                    "সুন্দরবনের রয়েল বেঙ্গল টাইগার" to "সুন্দরবনের শ্বাসমূলের গভীরে লুকিয়ে আছে রয়েল বেঙ্গল টাইগার। নিস্তব্ধ ম্যানগ্রোভ অরণ্যে শিকারীর পায়ের আওয়াজ প্রকৃতিকে রহস্যময় ও জীবন্ত করে তোলে।",
                    "২০৮০ সালের সাইবার এআই পৃথিবী" to "২০৮০ সালের মেগাসিটিতে আকাশে উড়ছে সাইবার যানবাহন। মানুষের সাথে কাঁধে কাঁধ মিলিয়ে কাজ করছে সচেতন এআই এজেন্ট, পৃথিবী প্রবেশ করেছে নতুন সভ্যতায়।"
                ).forEach { (title, script) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.cardBg)
                            .border(0.8.dp, theme.borderGlow.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable {
                                storyTopic = title
                                storyScript = script
                                currentProject = AIVideoGenerator.createProject(title, isShorts = true)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = title, fontSize = 11.sp, color = CyberNeonCyan)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Story Input
            OutlinedTextField(
                value = storyScript,
                onValueChange = { storyScript = it },
                label = { Text("আপনার সম্পূর্ণ গল্পের বিবরণ দিন...", fontSize = 11.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("story_script_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberNeonRed,
                    unfocusedBorderColor = theme.borderGlow.copy(alpha = 0.5f),
                    focusedTextColor = CyberTextPrimary,
                    unfocusedTextColor = CyberTextPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Split Action Button
            Button(
                onClick = {
                    currentProject = AIVideoGenerator.createProject(storyTopic.ifBlank { "সিনেমাটিক গল্প" }, isShorts = true)
                    Toast.makeText(context, "${currentProject.scenes.size}টি দৃশ্যে ভাগ করা সম্পন্ন হয়েছে!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberNeonRed),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("split_story_scenes_button")
            ) {
                Icon(Icons.Default.Splitscreen, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("✂️ দৃশ্য ও Narration-এ ভাগ করুন (${currentProject.scenes.size} Scenes)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Split Scenes Showcase List
            Text(
                text = "বিভাজিত দৃশ্য তালিকা (Scene Timeline & Camera Cues):",
                fontSize = 11.5.sp,
                color = CyberTextSecondary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                currentProject.scenes.take(4).forEach { scene ->
                    SceneCard(scene = scene)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Launch Player Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onPlayProject(currentProject)
                        Toast.makeText(context, "৯:১৬ ভিডিও প্লেয়ারে চালু হচ্ছে...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("launch_video_player_from_story")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🎬 ৯:১৬ প্লেয়ারে দেখুন", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        val text = currentProject.scenes.joinToString("\n\n") {
                            "দৃশ্য ${it.sceneNumber}: [${it.title}]\nভয়েসওভার: ${it.narrationText}\nপ্রম্পট: ${it.visualPrompt}\nক্যামেরা: ${it.cameraEffect.name}"
                        }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Story Script", text))
                        Toast.makeText(context, "চিত্রনাট্য কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberNeonAmber),
                    border = BorderStroke(1.dp, CyberNeonAmber),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = CyberNeonAmber, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("স্ক্রিপ্ট কপি", fontSize = 11.sp, color = CyberNeonAmber)
                }
            }
        }
    }
}

@Composable
private fun SceneCard(scene: VideoScene) {
    val theme = LocalNovaTheme.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(theme.cardBg)
            .border(0.8.dp, CyberNeonRed.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SCENE ${scene.sceneNumber}: ${scene.title}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberNeonRed,
                    fontFamily = FontFamily.Monospace
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberNeonAmber.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🎥 ${scene.cameraEffect.name}",
                        fontSize = 9.sp,
                        color = CyberNeonAmber,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "🎙️ Narration: ${scene.narrationText}",
                fontSize = 10.5.sp,
                color = CyberTextPrimary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "📸 Prompt: ${scene.visualPrompt}",
                fontSize = 9.5.sp,
                color = CyberTextMuted,
                maxLines = 1
            )
        }
    }
}
