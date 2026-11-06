package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberNeonAmber
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.CyberNeonPurple
import com.example.ui.theme.CyberNeonRed
import com.example.ui.theme.CyberObsidian
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

data class PhoneTool(
    val title: String,
    val description: String,
    val command: String,
    val icon: ImageVector,
    val color: Color
)

/**
 * Phone & Terminal Control screen
 */
@Composable
fun ActionsScreen(
    onRunAction: (String) -> Unit
) {
    val tools = listOf(
        PhoneTool(
            title = "YouTube Player",
            description = "ভিডিও বা পছন্দের মিউজিক চালান",
            command = "YouTube খোলো",
            icon = Icons.Default.PlayCircle,
            color = CyberNeonRed
        ),
        PhoneTool(
            title = "Chrome Search",
            description = "বাস্তব সময়ে গুগল বা ওয়েব ব্রাউজ",
            command = "Chrome খোলো",
            icon = Icons.Default.Language,
            color = CyberNeonCyan
        ),
        PhoneTool(
            title = "Camera Capture",
            description = "ছবি বা ভিডিও রেকর্ডার অন করুন",
            command = "ক্যামেরা খোলো",
            icon = Icons.Default.CameraAlt,
            color = CyberNeonGreen
        ),
        PhoneTool(
            title = "Phone Dialer",
            description = "কল করতে ডায়ালার ওপেন করুন",
            command = "ডায়ালার খোলো",
            icon = Icons.Default.Call,
            color = CyberNeonAmber
        ),
        PhoneTool(
            title = "Smart Calculator",
            description = "হিসাব বা অংক সমাধান করতে ক্যালকুলেটর",
            command = "ক্যালকুলেটর",
            icon = Icons.Default.Calculate,
            color = CyberNeonPurple
        ),
        PhoneTool(
            title = "Clock & Alarms",
            description = "বর্তমান সময় ও অ্যালার্ম শিডিউল",
            command = "অ্যালার্ম খোলো",
            icon = Icons.Default.Alarm,
            color = CyberNeonPink
        ),
        PhoneTool(
            title = "Quick Notes",
            description = "নোটবুক বা টেক্সট সেভার",
            command = "নোট তৈরি করো",
            icon = Icons.Default.EditNote,
            color = CyberNeonCyan
        ),
        PhoneTool(
            title = "Maps & Location",
            description = "লোকেশন ও ম্যাপস নেভিগেশন",
            command = "ম্যাপ খোলো",
            icon = Icons.Default.Map,
            color = CyberNeonGreen
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberObsidian)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        Column {
            Text(
                text = "AUTONOMOUS SYSTEM ACTIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = CyberNeonCyan,
                letterSpacing = 1.sp
            )
            Text(
                text = "NOVA সরাসরি আপনার অ্যান্ড্রয়েড ডিভাইসের অ্যাপ ও টুলস নিয়ন্ত্রণ করতে পারে।",
                fontSize = 12.sp,
                color = CyberTextSecondary
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(tools) { tool ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRunAction(tool.command) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1E293B)),
                        width = 1.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(tool.color.copy(alpha = 0.15f))
                                .border(1.dp, tool.color.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = tool.icon,
                                contentDescription = tool.title,
                                tint = tool.color,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = tool.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary
                        )

                        Text(
                            text = tool.description,
                            fontSize = 11.sp,
                            color = CyberTextSecondary,
                            lineHeight = 15.sp,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
