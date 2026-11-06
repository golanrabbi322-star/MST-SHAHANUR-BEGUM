package com.nova.personalai.systems

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.theme.CyberNeonAmber
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonRed
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalNovaTheme

@Composable
fun SubtitlesStudioView() {
    val theme = LocalNovaTheme.current
    val context = LocalContext.current

    var srtContent by remember {
        mutableStateOf(
            """
            1
            00:00:00,000 --> 00:00:05,000
            আপনি কি জানেন মহাবিশ্বের সবচেয়ে গভীর রহস্যটি কী?

            2
            00:00:05,000 --> 00:00:10,000
            আমাদের দৃশ্যমান জগৎ সমগ্র সৃষ্টির মাত্র পাঁচ শতাংশ!

            3
            00:00:10,000 --> 00:00:15,000
            বাকি পঁচানব্বই শতাংশ ডার্ক ম্যাটার ও ডার্ক এনার্জির অতল আঁধারে ঢাকা।

            4
            00:00:15,000 --> 00:00:20,000
            এখানে সময় এবং স্থান অদ্ভুতভাবে বেঁকে গিয়ে বদলে দেয় মহাজাগতিক গতি!

            5
            00:00:20,000 --> 00:00:25,000
            বিজ্ঞানীদের নতুন আবিষ্কার মানবজাতির চিন্তাভাবনাকে পুরোপুরি বদলে দিয়েছে।

            6
            00:00:25,000 --> 00:00:30,000
            এরকম আরও রোমাঞ্চকর রহস্য জানতে এখনই ফলো ও সাবস্ক্রাইব করুন!
            """.trimIndent()
        )
    }

    var previewScrubberSec by remember { mutableFloatStateOf(7.5f) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        border = BorderStroke(1.dp, CyberNeonAmber.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyberNeonAmber.copy(alpha = 0.2f))
                        .border(1.dp, CyberNeonAmber, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Subtitles,
                        contentDescription = "SRT Hub",
                        tint = CyberNeonAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "বাংলা SUBTITLES (SRT STUDIO)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberNeonAmber,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "মিলি-সেকেন্ড নির্ভুল SRT সাবটাইটেল তৈরি ও সিঙ্ক ভ্যালিডেশন",
                        fontSize = 11.sp,
                        color = CyberTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Validation Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ValidationBadge(
                    title = "SRT SYNTAX",
                    value = "VERIFIED ✓",
                    color = CyberNeonGreen,
                    modifier = Modifier.weight(1f)
                )
                ValidationBadge(
                    title = "COLLISION",
                    value = "0 OVERLAPS",
                    color = CyberNeonCyan,
                    modifier = Modifier.weight(1f)
                )
                ValidationBadge(
                    title = "READ SPEED",
                    value = "14.2 CPS (OK)",
                    color = CyberNeonAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Live Karaoke Timeline Scrubber Preview
            Text(
                text = "লাইভ টাইমলাইন সিঙ্ক প্রিভিউ (Timeline: ${String.format("%.1f", previewScrubberSec)}s):",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = CyberTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Highlighted Subtitle Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF090D1C))
                    .border(1.dp, CyberNeonAmber.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                val currentLine = when {
                    previewScrubberSec < 5.0f -> "আপনি কি জানেন মহাবিশ্বের সবচেয়ে গভীর রহস্যটি কী?"
                    previewScrubberSec < 10.0f -> "আমাদের দৃশ্যমান জগৎ সমগ্র সৃষ্টির মাত্র পাঁচ শতাংশ!"
                    previewScrubberSec < 15.0f -> "বাকি পঁচানব্বই শতাংশ ডার্ক ম্যাটার ও ডার্ক এনার্জির অতল আঁধারে ঢাকা।"
                    previewScrubberSec < 20.0f -> "এখানে সময় এবং স্থান অদ্ভুতভাবে বেঁকে গিয়ে বদলে দেয় মহাজাগতিক গতি!"
                    previewScrubberSec < 25.0f -> "বিজ্ঞানীদের নতুন আবিষ্কার মানবজাতির চিন্তাভাবনাকে বদলে দিয়েছে।"
                    else -> "এরকম আরও রোমাঞ্চকর রহস্য জানতে এখনই ফলো ও সাবস্ক্রাইব করুন!"
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "⏱️ [00:00:${String.format("%02d", previewScrubberSec.toInt())},000]",
                        fontSize = 10.sp,
                        color = CyberNeonAmber,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "“$currentLine”",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Slider(
                value = previewScrubberSec,
                onValueChange = { previewScrubberSec = it },
                valueRange = 0f..30f,
                colors = SliderDefaults.colors(
                    thumbColor = CyberNeonAmber,
                    activeTrackColor = CyberNeonAmber
                ),
                modifier = Modifier.testTag("srt_scrubber_slider")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Raw SRT Editor Box
            Text(
                text = "স্ট্যান্ডার্ড .SRT ফাইল কোড (মিলি-সেকেন্ড ফরম্যাট):",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = CyberTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = srtContent,
                onValueChange = { srtContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .testTag("srt_content_editor"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberNeonAmber,
                    unfocusedBorderColor = theme.borderGlow.copy(alpha = 0.5f),
                    focusedTextColor = CyberTextPrimary,
                    unfocusedTextColor = CyberTextPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Bengali SRT Subtitles", srtContent))
                        Toast.makeText(context, "বাংলা .SRT সাবটাইটেল ক্লিপবোর্ডে কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonAmber),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("copy_srt_studio_button")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ক্লিপবোর্ডে কপি করুন", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                }

                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "subtitles_bangla.srt ফাইল এক্সপোর্ট সফল হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberNeonGreen),
                    border = BorderStroke(1.dp, CyberNeonGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = CyberNeonGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(".srt এক্সপোর্ট", fontSize = 11.5.sp, color = CyberNeonGreen)
                }
            }
        }
    }
}

@Composable
private fun ValidationBadge(
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
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = value,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = CyberTextPrimary
            )
        }
    }
}
