package com.nova.personalai.video

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberNeonAmber
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.CyberNeonPurple
import com.example.ui.theme.CyberNeonRed
import com.example.ui.theme.CyberObsidian
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalNovaTheme
import com.nova.personalai.voice.TTSManager
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AIVideoStudioPlayer(
    project: AIVideoProject,
    ttsManager: TTSManager? = null,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null
) {
    val theme = LocalNovaTheme.current
    val context = LocalContext.current

    var currentSceneIndex by remember(project.id) { mutableIntStateOf(0) }
    var isPlaying by remember(project.id) { mutableStateOf(false) }
    var currentPlaybackSec by remember(project.id) { mutableFloatStateOf(0.0f) }
    var voiceNarrationEnabled by remember { mutableStateOf(true) }
    var isLooping by remember { mutableStateOf(true) }

    val scenes = project.scenes
    val currentScene = scenes.getOrNull(currentSceneIndex) ?: scenes.firstOrNull()

    // Real-time animation transitions for Ken Burns camera motion & visual pulsing
    val infiniteTransition = rememberInfiniteTransition(label = "video_motion")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val cosmicRotate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cosmic_rotate"
    )
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset"
    )

    // Trigger voice narration speech when scene changes while playing
    LaunchedEffect(currentSceneIndex, isPlaying, voiceNarrationEnabled) {
        if (isPlaying && voiceNarrationEnabled && currentScene != null && ttsManager != null) {
            ttsManager.speak(currentScene.narrationText)
        }
    }

    // Playback loop ticker
    LaunchedEffect(isPlaying, project.totalDurationSeconds) {
        while (isPlaying) {
            delay(100L)
            currentPlaybackSec += 0.1f

            val sceneIdx = scenes.indexOfFirst {
                currentPlaybackSec >= it.startSeconds && currentPlaybackSec < (it.startSeconds + it.durationSeconds)
            }
            if (sceneIdx != -1 && sceneIdx != currentSceneIndex) {
                currentSceneIndex = sceneIdx
            }

            if (currentPlaybackSec >= project.totalDurationSeconds) {
                if (isLooping) {
                    currentPlaybackSec = 0.0f
                    currentSceneIndex = 0
                } else {
                    isPlaying = false
                    currentPlaybackSec = project.totalDurationSeconds
                }
            }
        }
    }

    // Stop TTS when leaving the player
    DisposableEffect(Unit) {
        onDispose {
            try {
                ttsManager?.stop()
            } catch (_: Exception) {}
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
        border = BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(CyberNeonCyan, CyberNeonPurple, CyberNeonAmber))),
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_video_studio_player_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Header: Title & Format Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = "Video Studio",
                        tint = CyberNeonPink,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "NOVA AI VIDEO STUDIO",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = CyberNeonCyan
                        )
                        Text(
                            text = project.title,
                            fontSize = 11.sp,
                            color = CyberTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberNeonPink.copy(alpha = 0.15f))
                            .border(0.8.dp, CyberNeonPink, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "9:16 SHORTS",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberNeonPink,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // 9:16 Cinematic Video Frame Viewport
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9f / 16f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberObsidian)
                    .border(1.5.dp, Brush.verticalGradient(listOf(theme.primary, CyberNeonPurple)), RoundedCornerShape(12.dp))
                    .clickable { isPlaying = !isPlaying }
                    .testTag("video_canvas_viewport"),
                contentAlignment = Alignment.Center
            ) {
                val primaryColor = Color(currentScene?.primaryColorHex ?: 0xFF0D1B2A)
                val secondaryColor = Color(currentScene?.secondaryColorHex ?: 0xFF00E5FF)

                // 1. Dynamic Canvas with Visual Effects, Nebulae, and Motion Simulation
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val w = size.width
                    val h = size.height

                    // Background Gradient
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                secondaryColor.copy(alpha = 0.35f),
                                primaryColor,
                                Color(0xFF030712)
                            ),
                            center = Offset(w * 0.5f, h * 0.45f),
                            radius = w * 0.9f * pulseScale
                        )
                    )

                    // Draw cosmic orbital rings and geometry
                    val center = Offset(w * 0.5f, h * 0.45f)
                    val baseRadius = w * 0.32f * pulseScale

                    drawCircle(
                        color = secondaryColor.copy(alpha = 0.25f),
                        radius = baseRadius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.4f),
                        radius = baseRadius * 1.35f,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // Orbiting stars / energy particles
                    for (i in 0..7) {
                        val angle = Math.toRadians((cosmicRotate + (i * 45)).toDouble())
                        val px = center.x + (baseRadius * cos(angle)).toFloat()
                        val py = center.y + (baseRadius * sin(angle)).toFloat()

                        drawCircle(
                            color = if (i % 2 == 0) secondaryColor else CyberNeonAmber,
                            radius = 3.5.dp.toPx(),
                            center = Offset(px, py)
                        )
                    }

                    // Pulsing audio waveform visualizer at bottom when playing
                    if (isPlaying) {
                        val barCount = 20
                        val barWidth = (w - 40.dp.toPx()) / barCount
                        for (b in 0 until barCount) {
                            val barHeight = (12 + (sin((b * 0.6f + waveOffset * 6.28f)) + 1.0f) * 16.dp.toPx()).toFloat()
                            val bx = 20.dp.toPx() + (b * barWidth)
                            drawRoundRect(
                                color = if (b % 2 == 0) secondaryColor.copy(alpha = 0.7f) else CyberNeonPink.copy(alpha = 0.7f),
                                topLeft = Offset(bx, h - 85.dp.toPx() - barHeight),
                                size = androidx.compose.ui.geometry.Size(barWidth * 0.7f, barHeight),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                            )
                        }
                    }
                }

                // 2. Scene Top Info Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Scene number pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.65f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "সিন ${(currentSceneIndex + 1)}/${scenes.size}: ${currentScene?.title ?: ""}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Camera Effect Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(secondaryColor.copy(alpha = 0.2f))
                                .border(0.5.dp, secondaryColor, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "🎥 ${currentScene?.cameraEffect?.name ?: "MOTION"}",
                                fontSize = 9.sp,
                                color = secondaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 3. Central AI Artwork & Concept Graphic
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(secondaryColor.copy(alpha = 0.15f))
                            .border(1.5.dp, secondaryColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = secondaryColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentScene?.visualTag ?: "Visual Keyframe",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentScene?.visualPrompt ?: "",
                        fontSize = 9.5.sp,
                        color = Color.White.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // 4. Play / Pause Overlay Icon when tapped or paused
                if (!isPlaying) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .border(1.dp, CyberNeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = CyberNeonCyan,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // 5. Synchronized Dynamic Karaoke Subtitle Overlay (Bottom of Video)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .border(1.dp, secondaryColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = currentScene?.subtitleText ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberNeonAmber,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // Video Timeline Scrubber & Timing
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format(Locale.US, "%02d:%02d", (currentPlaybackSec / 60).toInt(), (currentPlaybackSec % 60).toInt()),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyberNeonCyan
                )

                Slider(
                    value = currentPlaybackSec,
                    onValueChange = { newSec ->
                        currentPlaybackSec = newSec
                        val idx = scenes.indexOfFirst {
                            newSec >= it.startSeconds && newSec < (it.startSeconds + it.durationSeconds)
                        }
                        if (idx != -1) currentSceneIndex = idx
                    },
                    valueRange = 0f..project.totalDurationSeconds.coerceAtLeast(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = CyberNeonPink,
                        activeTrackColor = CyberNeonCyan,
                        inactiveTrackColor = theme.cardBg
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )

                Text(
                    text = String.format(Locale.US, "%02d:%02d", (project.totalDurationSeconds / 60).toInt(), (project.totalDurationSeconds % 60).toInt()),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CyberTextSecondary
                )
            }

            // ==========================================
            // Media Controls: Prev, Play/Pause, Next, Voice, Loop
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Loop toggle
                IconButton(
                    onClick = { isLooping = !isLooping },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Loop",
                        tint = if (isLooping) CyberNeonCyan else CyberTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Prev Scene
                IconButton(
                    onClick = {
                        if (currentSceneIndex > 0) {
                            currentSceneIndex--
                            currentPlaybackSec = scenes[currentSceneIndex].startSeconds
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Previous Scene",
                        tint = CyberTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Main Play/Pause Button
                Button(
                    onClick = { isPlaying = !isPlaying },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonCyan),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("video_player_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Next Scene
                IconButton(
                    onClick = {
                        if (currentSceneIndex < scenes.size - 1) {
                            currentSceneIndex++
                            currentPlaybackSec = scenes[currentSceneIndex].startSeconds
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Next Scene",
                        tint = CyberTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Voice Narration Toggle (Speaks via TTS)
                IconButton(
                    onClick = {
                        voiceNarrationEnabled = !voiceNarrationEnabled
                        if (!voiceNarrationEnabled) {
                            try { ttsManager?.stop() } catch (_: Exception) {}
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (voiceNarrationEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Voiceover",
                        tint = if (voiceNarrationEnabled) CyberNeonGreen else CyberTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // Scene Timeline Selector Strip
            // ==========================================
            Text(
                text = "ভিডিওর দৃশ্যসমূহ (SCENE TIMELINE)",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = theme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scenes.forEachIndexed { idx, scene ->
                    val isSelected = idx == currentSceneIndex
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) CyberNeonCyan.copy(alpha = 0.18f) else theme.surface
                        ),
                        border = BorderStroke(
                            if (isSelected) 1.5.dp else 0.8.dp,
                            if (isSelected) CyberNeonCyan else theme.borderGlow.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .width(115.dp)
                            .clickable {
                                currentSceneIndex = idx
                                currentPlaybackSec = scene.startSeconds
                            }
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "সিন ${scene.sceneNumber}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) CyberNeonCyan else CyberTextPrimary
                                )
                                Text(
                                    text = "${scene.durationSeconds.toInt()}s",
                                    fontSize = 9.sp,
                                    color = CyberTextMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = scene.title,
                                fontSize = 9.sp,
                                color = CyberTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // Real Export & Publishing Actions (No fake buttons!)
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // One-tap Direct YouTube Shorts Upload
                Button(
                    onClick = {
                        AIVideoGenerator.openYouTubeShortsUpload(context, project)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("publish_youtube_shorts_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Publish",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "YouTube Shorts পাবলিশ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Share / Export Full Video Package to WhatsApp, Drive, Telegram
                OutlinedButton(
                    onClick = {
                        AIVideoGenerator.shareVideoProject(context, project)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberNeonGreen),
                    border = BorderStroke(1.dp, CyberNeonGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_video_package_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = CyberNeonGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ভিডিও শেয়ার ও এক্সপোর্ট",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Copy .SRT Subtitles
            OutlinedButton(
                onClick = {
                    val srt = AIVideoGenerator.generateSrtContent(project)
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("SRT Subtitles", srt))
                    Toast.makeText(context, "বাংলা .SRT সাবটাইটেল ক্লিপবোর্ডে কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberNeonAmber),
                border = BorderStroke(0.8.dp, CyberNeonAmber),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("copy_srt_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Subtitles,
                    contentDescription = "SRT",
                    tint = CyberNeonAmber,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "বাংলা .SRT সাবটাইটেল ফাইল কপি করুন",
                    fontSize = 11.sp,
                    color = CyberNeonAmber
                )
            }
        }
    }
}
