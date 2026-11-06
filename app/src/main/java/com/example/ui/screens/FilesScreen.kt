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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.CyberNeonPurple
import com.example.ui.theme.CyberObsidian
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

data class FileItem(
    val name: String,
    val path: String,
    val size: String,
    val type: String,
    val color: Color
)

@Composable
fun FilesScreen(
    onOpenFileInEditor: (String) -> Unit
) {
    val projectFiles = listOf(
        FileItem("script.js", "src/script.js", "2.4 KB", "JS", CyberNeonGreen),
        FileItem("MainActivity.kt", "app/src/main/MainActivity.kt", "8.6 KB", "KT", CyberNeonCyan),
        FileItem("style.css", "src/style.css", "1.8 KB", "CSS", CyberNeonPink),
        FileItem("index.html", "public/index.html", "3.2 KB", "HTML", Color(0xFFFF9900)),
        FileItem("storyboard_ep1.json", "assets/storyboard.json", "5.1 KB", "JSON", CyberNeonPurple),
        FileItem("subtitles_bn.srt", "assets/subtitles_bn.srt", "1.2 KB", "SRT", CyberNeonCyan)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberObsidian)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PROJECT FILES & WORKSPACE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = CyberNeonCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Active project: autonomous-ide/workspace",
                    fontSize = 11.sp,
                    color = CyberTextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = CyberNeonCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Folder Path Breadcrumb
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF0A1122))
                .border(0.8.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = CyberNeonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "/nova/autonomous-core/workspace/",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CyberTextPrimary
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(projectFiles) { file ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenFileInEditor(file.name) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1E293B)),
                        width = 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(file.color.copy(alpha = 0.15f))
                                    .border(0.8.dp, file.color.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = file.type,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = file.color,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Column {
                                Text(
                                    text = file.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "${file.path} • ${file.size}",
                                    fontSize = 11.sp,
                                    color = CyberTextSecondary
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open",
                            tint = CyberTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
