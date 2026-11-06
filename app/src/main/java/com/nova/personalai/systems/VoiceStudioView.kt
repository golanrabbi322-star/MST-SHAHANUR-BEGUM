package com.nova.personalai.systems

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.CyberNeonPurple
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalNovaTheme
import com.nova.personalai.voice.TTSManager

@Composable
fun VoiceStudioView(
    ttsManager: TTSManager? = null,
    onTriggerVoiceCommand: () -> Unit = {}
) {
    val theme = LocalNovaTheme.current
    val context = LocalContext.current

    var testPhrase by remember {
        mutableStateOf("হ্যালো, আমি NOVA Personal AI। আপনার আদেশ দিন, আমি স্বয়ংক্রিয়ভাবে সব কাজ সম্পন্ন করব।")
    }
    var speechRate by remember { mutableFloatStateOf(1.0f) }
    var speechPitch by remember { mutableFloatStateOf(1.05f) }
    var selectedDialect by remember { mutableStateOf("bn-BD") }
    var isTestingSpeech by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        border = BorderStroke(1.dp, CyberNeonCyan.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyberNeonCyan.copy(alpha = 0.2f))
                        .border(1.dp, CyberNeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Lab",
                        tint = CyberNeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "বাংলা VOICE ASSISTANT & DIAGNOSTIC LAB",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberNeonCyan,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Speech Recognizer ও Text-to-Speech ইঞ্জিন লাইভ পরীক্ষা",
                        fontSize = 11.sp,
                        color = CyberTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Diagnostic Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DiagnosticBadge(
                    label = "STT RECOGNIZER",
                    status = "ONLINE (Bangla)",
                    color = CyberNeonGreen,
                    modifier = Modifier.weight(1f)
                )
                DiagnosticBadge(
                    label = "TTS SYNTHESIZER",
                    status = "ACTIVE ($selectedDialect)",
                    color = CyberNeonCyan,
                    modifier = Modifier.weight(1f)
                )
                DiagnosticBadge(
                    label = "LATENCY",
                    status = "38 ms",
                    color = CyberNeonPurple,
                    modifier = Modifier.weight(0.8f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Live Speech Recognizer Testing
            Text(
                text = "১. মাইক্রোফোন ও স্পীচ রিকগনিশন পরীক্ষা",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onTriggerVoiceCommand,
                colors = ButtonDefaults.buttonColors(containerColor = CyberNeonCyan),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("test_voice_rec_button")
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "কথা বলে পরীক্ষা করুন (Tap to Talk)",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Text-to-Speech Engine Testing
            Text(
                text = "২. বাংলা টেক্সট-টু-স্পীচ (TTS) সিন্থেসাইজার পরীক্ষা",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = testPhrase,
                onValueChange = { testPhrase = it },
                label = { Text("পরীক্ষার বাংলা বাক্য", fontSize = 11.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tts_test_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberNeonCyan,
                    unfocusedBorderColor = theme.borderGlow.copy(alpha = 0.5f),
                    focusedTextColor = CyberTextPrimary,
                    unfocusedTextColor = CyberTextPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dialect Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "bn-BD" to "বাংলাদেশ (ঢাকা)",
                    "bn-IN" to "ভারত (কলকাতা)"
                ).forEach { (code, label) ->
                    val isSelected = selectedDialect == code
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyberNeonCyan.copy(alpha = 0.2f) else theme.cardBg)
                            .border(
                                width = if (isSelected) 1.2.dp else 0.8.dp,
                                color = if (isSelected) CyberNeonCyan else theme.borderGlow.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedDialect = code
                                ttsManager?.setVoiceSettings(speechRate, speechPitch, code)
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) CyberNeonCyan else CyberTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sliders: Speed & Pitch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "কথা বলার গতি (Speed): ${String.format("%.1fx", speechRate)}",
                    fontSize = 11.sp,
                    color = CyberTextSecondary
                )
            }
            Slider(
                value = speechRate,
                onValueChange = {
                    speechRate = it
                    ttsManager?.setVoiceSettings(it, speechPitch, selectedDialect)
                },
                valueRange = 0.5f..2.0f,
                colors = SliderDefaults.colors(
                    thumbColor = CyberNeonCyan,
                    activeTrackColor = CyberNeonCyan
                ),
                modifier = Modifier.testTag("speech_rate_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "গলার পিচ (Pitch): ${String.format("%.2fx", speechPitch)}",
                    fontSize = 11.sp,
                    color = CyberTextSecondary
                )
            }
            Slider(
                value = speechPitch,
                onValueChange = {
                    speechPitch = it
                    ttsManager?.setVoiceSettings(speechRate, it, selectedDialect)
                },
                valueRange = 0.5f..2.0f,
                colors = SliderDefaults.colors(
                    thumbColor = CyberNeonPink,
                    activeTrackColor = CyberNeonPink
                ),
                modifier = Modifier.testTag("speech_pitch_slider")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Actions: Listen / Stop
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        isTestingSpeech = true
                        ttsManager?.setVoiceSettings(speechRate, speechPitch, selectedDialect)
                        ttsManager?.speak(testPhrase)
                        Toast.makeText(context, "ভয়েস অডিও প্লে হচ্ছে...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("listen_tts_test_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ভয়েস টেস্ট শুনুন",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                }

                OutlinedButton(
                    onClick = {
                        isTestingSpeech = false
                        ttsManager?.stop()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberNeonPink),
                    border = BorderStroke(1.dp, CyberNeonPink),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(0.7f)
                        .testTag("stop_tts_test_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = CyberNeonPink,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "থামান",
                        color = CyberNeonPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DiagnosticBadge(
    label: String,
    status: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .border(0.8.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = status,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = CyberTextPrimary,
                maxLines = 1
            )
        }
    }
}
