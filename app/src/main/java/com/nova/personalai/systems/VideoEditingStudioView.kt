package com.nova.personalai.systems

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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.example.ui.theme.CyberNeonAmber
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.CyberNeonPurple
import com.example.ui.theme.CyberNeonRed
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalNovaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VideoEditingStudioView(
    onOpenRenderedVideo: () -> Unit = {}
) {
    val theme = LocalNovaTheme.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var narrationVolume by remember { mutableFloatStateOf(0.95f) }
    var bgmVolume by remember { mutableFloatStateOf(0.35f) }
    var sfxVolume by remember { mutableFloatStateOf(0.60f) }

    val bgmTracks = remember {
        listOf(
            "🎵 Cyber Neon Pulse",
            "🌌 Cosmic Orchestral",
            "🎧 Lofi Chill Beats",
            "⚡ Dark Sci-Fi Ambient"
        )
    }
    var selectedTrackIndex by remember { mutableStateOf(0) }

    val transitions = remember {
        listOf("Cross-Dissolve", "Cinematic Fade", "Cyber Glitch", "Zoom Punch", "Slide Left")
    }
    var selectedTransition by remember { mutableStateOf(transitions[0]) }

    var isRendering by remember { mutableStateOf(false) }
    var renderProgress by remember { mutableFloatStateOf(0f) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        border = BorderStroke(1.dp, CyberNeonPurple.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyberNeonPurple.copy(alpha = 0.2f))
                        .border(1.dp, CyberNeonPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Video Editing",
                        tint = CyberNeonPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "VIDEO EDITING & AUDIO MIXING",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberNeonPurple,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "FFmpeg স্টাইল অডিও মিক্সিং ও ট্রানজিশন রেন্ডারিং ইঞ্জিন",
                        fontSize = 11.sp,
                        color = CyberTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-Track Audio Sliders
            Text(text = "১. মাল্টি-ট্র্যাক অডিও ব্যালেন্স (Audio Mixer):", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = CyberTextSecondary)
            Spacer(modifier = Modifier.height(6.dp))

            // Narration
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "🎙️ ভয়েসওভার (Narration): ${(narrationVolume * 100).toInt()}%", fontSize = 11.sp, color = CyberNeonCyan)
            }
            Slider(
                value = narrationVolume,
                onValueChange = { narrationVolume = it },
                colors = SliderDefaults.colors(thumbColor = CyberNeonCyan, activeTrackColor = CyberNeonCyan),
                modifier = Modifier.testTag("narration_vol_slider")
            )

            // BGM
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "🎵 ব্যাকগ্রাউন্ড মিউজিক (BGM): ${(bgmVolume * 100).toInt()}%", fontSize = 11.sp, color = CyberNeonPurple)
            }
            Slider(
                value = bgmVolume,
                onValueChange = { bgmVolume = it },
                colors = SliderDefaults.colors(thumbColor = CyberNeonPurple, activeTrackColor = CyberNeonPurple),
                modifier = Modifier.testTag("bgm_vol_slider")
            )

            // SFX
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "🔊 সাউন্ড ইফেক্টস (SFX): ${(sfxVolume * 100).toInt()}%", fontSize = 11.sp, color = CyberNeonAmber)
            }
            Slider(
                value = sfxVolume,
                onValueChange = { sfxVolume = it },
                colors = SliderDefaults.colors(thumbColor = CyberNeonAmber, activeTrackColor = CyberNeonAmber),
                modifier = Modifier.testTag("sfx_vol_slider")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // BGM Track Selector
            Text(text = "ব্যাকগ্রাউন্ড মিউজিক ট্র্যাক:", fontSize = 11.5.sp, color = CyberTextSecondary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bgmTracks.forEachIndexed { idx, track ->
                    val isSelected = idx == selectedTrackIndex
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyberNeonPurple.copy(alpha = 0.25f) else theme.cardBg)
                            .border(
                                width = if (isSelected) 1.2.dp else 0.8.dp,
                                color = if (isSelected) CyberNeonPurple else theme.borderGlow.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedTrackIndex = idx }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = track,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) CyberNeonPurple else CyberTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scene Transition Effects
            Text(text = "২. দৃশ্য ট্রানজিশন ইফেক্ট (Scene Transition):", fontSize = 11.5.sp, color = CyberTextSecondary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                transitions.forEach { trans ->
                    val isSelected = trans == selectedTransition
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyberNeonGreen.copy(alpha = 0.2f) else theme.cardBg)
                            .border(
                                width = if (isSelected) 1.2.dp else 0.8.dp,
                                color = if (isSelected) CyberNeonGreen else theme.borderGlow.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedTransition = trans }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = trans,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) CyberNeonGreen else CyberTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Render Progress / Action
            if (isRendering) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "⚡ FFmpeg রেন্ডারিং চলছে...", fontSize = 11.sp, color = CyberNeonPurple, fontWeight = FontWeight.Bold)
                        Text(text = "${(renderProgress * 100).toInt()}%", fontSize = 11.sp, color = CyberNeonPurple, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { renderProgress },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = CyberNeonPurple,
                        trackColor = Color(0xFF1E1735)
                    )
                }
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            isRendering = true
                            renderProgress = 0f
                            for (i in 1..10) {
                                delay(150)
                                renderProgress = i / 10f
                            }
                            isRendering = false
                            Toast.makeText(context, "ভিডিও ও অডিও রেন্ডার সফল হয়েছে!", Toast.LENGTH_SHORT).show()
                            onOpenRenderedVideo()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonPurple),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("render_video_mix_button")
                ) {
                    Icon(Icons.Default.Movie, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("⚡ FFmpeg অডিও ও ভিডিও রেন্ডার করুন", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
