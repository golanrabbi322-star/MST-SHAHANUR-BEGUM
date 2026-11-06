package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.LocalNovaTheme

/**
 * Top Dynamic Island / Capsule as seen in User Screenshot 5 & 2
 * Shows live audio waveforms when listening or speaking
 */
@Composable
fun DynamicIslandCapsule(
    isVisible: Boolean,
    isListening: Boolean,
    isSpeaking: Boolean,
    isLoading: Boolean,
    speechRms: Float,
    onClick: () -> Unit
) {
    val theme = LocalNovaTheme.current
    AnimatedVisibility(
        visible = isVisible && (isListening || isSpeaking || isLoading),
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut()
    ) {
        val accentColor = when {
            isListening -> theme.accent
            isSpeaking -> theme.primary
            isLoading -> theme.secondary
            else -> theme.primary
        }

        val statusText = when {
            isListening -> "● LISTENING..."
            isSpeaking -> "● SPEAKING"
            isLoading -> "● PROCESSING..."
            else -> "● NOVA ACTIVE"
        }

        Box(
            modifier = Modifier
                .padding(top = 4.dp, bottom = 6.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFF02040A))
                .border(1.5.dp, accentColor, RoundedCornerShape(26.dp))
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 7.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Status dot & text
                Text(
                    text = statusText,
                    color = accentColor,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Waveform bars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(14.dp)
                ) {
                    val infinite = rememberInfiniteTransition(label = "wave_bars")

                    val b1 by infinite.animateFloat(0.3f, 1f, infiniteRepeatable(tween(350), RepeatMode.Reverse), label = "b1")
                    val b2 by infinite.animateFloat(0.5f, 0.95f, infiniteRepeatable(tween(260), RepeatMode.Reverse), label = "b2")
                    val b3 by infinite.animateFloat(0.2f, 1f, infiniteRepeatable(tween(420), RepeatMode.Reverse), label = "b3")
                    val b4 by infinite.animateFloat(0.4f, 0.85f, infiniteRepeatable(tween(300), RepeatMode.Reverse), label = "b4")
                    val b5 by infinite.animateFloat(0.3f, 0.9f, infiniteRepeatable(tween(380), RepeatMode.Reverse), label = "b5")

                    val rmsMultiplier = if (isListening) (speechRms.coerceIn(0f, 10f) / 10f).coerceAtLeast(0.4f) else 1f

                    WaveBar(heightFactor = b1 * rmsMultiplier, color = accentColor)
                    WaveBar(heightFactor = b2 * rmsMultiplier, color = accentColor)
                    WaveBar(heightFactor = b3 * rmsMultiplier, color = accentColor)
                    WaveBar(heightFactor = b4 * rmsMultiplier, color = accentColor)
                    WaveBar(heightFactor = b5 * rmsMultiplier, color = accentColor)
                }
            }
        }
    }
}

@Composable
private fun WaveBar(heightFactor: Float, color: Color) {
    Box(
        modifier = Modifier
            .width(2.5.dp)
            .fillMaxHeight(heightFactor.coerceIn(0.2f, 1f))
            .clip(CircleShape)
            .background(color)
    )
}
