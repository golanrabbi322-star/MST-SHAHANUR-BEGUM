package com.nova.personalai.systems

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.CyberNeonAmber
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.CyberNeonPurple
import com.example.ui.theme.CyberNeonRed

/**
 * The 7 Canonical Autonomous Systems in NOVA AI Suite.
 */
enum class NovaSystemCategory(
    val title: String,
    val titleBn: String,
    val subtitleBn: String,
    val icon: ImageVector,
    val accentColor: Color
) {
    VOICE_ASSISTANT(
        title = "Voice Assistant",
        titleBn = "বাংলা Voice Assistant",
        subtitleBn = "Speech Recognizer ও Text-to-Speech পরীক্ষা",
        icon = Icons.Default.Mic,
        accentColor = CyberNeonCyan
    ),
    IMAGE_STUDIO(
        title = "AI Image Studio",
        titleBn = "AI Image Studio",
        subtitleBn = "বাস্তব Image Generation API ও প্রম্পট ইঞ্জিন",
        icon = Icons.Default.Palette,
        accentColor = CyberNeonPink
    ),
    STORY_TO_VIDEO(
        title = "Story to Video",
        titleBn = "Story-to-Video Studio",
        subtitleBn = "গল্পকে দৃশ্য, প্রম্পট ও Narration-এ ভাগ করা",
        icon = Icons.Default.Movie,
        accentColor = CyberNeonRed
    ),
    SUBTITLES_SRT(
        title = "Bangla Subtitles",
        titleBn = "বাংলা Subtitles (SRT)",
        subtitleBn = "SRT সাবটাইটেল তৈরি, টাইমিং ও সিঙ্ক যাচাই",
        icon = Icons.Default.Subtitles,
        accentColor = CyberNeonAmber
    ),
    VIDEO_EDITING(
        title = "Video & Audio Edit",
        titleBn = "Video Editing & Audio Mixing",
        subtitleBn = "FFmpeg স্টাইল অডিও মিক্সিং ও ট্রানজিশন রেন্ডারিং",
        icon = Icons.Default.Tune,
        accentColor = CyberNeonPurple
    ),
    MARKETING(
        title = "Digital Marketing",
        titleBn = "Digital Marketing Hub",
        subtitleBn = "YouTube SEO, ভাইরাল ট্যাগ ও গ্রোথ অ্যানালিটিক্স",
        icon = Icons.Default.Campaign,
        accentColor = CyberNeonGreen
    ),
    APP_BUILDER(
        title = "App Builder",
        titleBn = "App Builder & Dev Engine",
        subtitleBn = "কোড তৈরি, ফাইল ম্যানেজমেন্ট ও কম্পাইলার টেস্ট",
        icon = Icons.Default.Code,
        accentColor = CyberNeonCyan
    )
}
