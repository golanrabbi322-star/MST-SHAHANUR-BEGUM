package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

/**
 * Interactive Cyber Voice Orb & Call Bar as seen in User Screenshot 4
 */
@Composable
fun CyberVoiceCallWidget(
    isListening: Boolean,
    isSpeaking: Boolean,
    isLoading: Boolean,
    speechRms: Float,
    currentTranscript: String,
    autoSpeakEnabled: Boolean,
    onToggleSpeak: () -> Unit,
    onMicClick: () -> Unit,
    onStopAudio: () -> Unit
) {
    val theme = LocalNovaTheme.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = theme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(theme.gradientColors),
            width = 1.2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Holographic Sphere Orb
            CyberHolographicSphere(
                isListening = isListening,
                isSpeaking = isSpeaking,
                isLoading = isLoading,
                speechRms = speechRms,
                onClick = onMicClick
            )

            // Center details & transcription
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val statusColor = when {
                        isSpeaking -> CyberNeonGreen
                        isListening -> CyberNeonPink
                        isLoading -> CyberNeonCyan
                        else -> CyberNeonCyan
                    }
                    val statusText = when {
                        isSpeaking -> "● SPEAKING"
                        isListening -> "● LISTENING..."
                        isLoading -> "● THINKING"
                        else -> "● READY"
                    }

                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.6.sp
                    )
                }

                Text(
                    text = "NOVA Autonomous Core",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextPrimary
                )

                Text(
                    text = if (currentTranscript.isNotBlank())
                        currentTranscript
                    else if (isListening)
                        "আপনার কথা শুনছি..."
                    else if (isSpeaking)
                        "উত্তর প্রদান করছি..."
                    else
                        "ভয়েস দিয়ে প্রশ্ন করুন বা নির্দেশ দিন",
                    fontSize = 11.5.sp,
                    color = CyberTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Quick Call Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic button
                IconButton(
                    onClick = onMicClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isListening) CyberNeonPink else Color(0xFF16213B))
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mic",
                        tint = if (isListening) Color.White else CyberNeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Volume / Mute
                IconButton(
                    onClick = onToggleSpeak,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF16213B))
                ) {
                    Icon(
                        imageVector = if (autoSpeakEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeMute,
                        contentDescription = "Audio output",
                        tint = if (autoSpeakEnabled) CyberNeonCyan else CyberTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Stop / End Button (Red)
                if (isSpeaking || isListening) {
                    IconButton(
                        onClick = onStopAudio,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CyberNeonRed)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CyberHolographicSphere(
    isListening: Boolean,
    isSpeaking: Boolean,
    isLoading: Boolean,
    speechRms: Float,
    onClick: () -> Unit
) {
    val infinite = rememberInfiniteTransition(label = "sphere_anim")
    val pulse by infinite.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sphere_pulse"
    )

    val rotate by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sphere_rot"
    )

    val theme = LocalNovaTheme.current
    val primaryGlow = when {
        isListening -> theme.accent
        isSpeaking -> theme.primary
        isLoading -> theme.secondary
        else -> theme.primary
    }

    Box(
        modifier = Modifier
            .size(52.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2.3f

            // Outer energy ring
            val outerRadius = if (isListening) {
                radius + (speechRms.coerceIn(0f, 10f) * 1.5f)
            } else if (isSpeaking) {
                radius * pulse
            } else {
                radius
            }

            drawCircle(
                color = primaryGlow.copy(alpha = if (isListening || isSpeaking) 0.35f else 0.15f),
                radius = outerRadius,
                center = center
            )

            drawCircle(
                color = primaryGlow.copy(alpha = 0.6f),
                radius = outerRadius * 0.88f,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Inner holographic gradient sphere
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CyberNeonCyan,
                        CyberNeonPink,
                        CyberNeonPurple,
                        Color(0xFF030712)
                    ),
                    center = center,
                    radius = radius * 0.75f
                ),
                radius = radius * 0.75f,
                center = center
            )
        }
    }
}
