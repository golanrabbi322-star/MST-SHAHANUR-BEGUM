package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.CyberNeonRed
import com.example.ui.theme.CyberObsidian
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

data class CodeFile(
    val name: String,
    val language: String,
    var content: String
)

/**
 * Autonomous Code Studio matching Screenshot 1 & 3
 */
@Composable
fun EditorScreen(
    onTriggerAutoFix: (String) -> Unit
) {
    val files = remember {
        mutableListOf(
            CodeFile(
                name = "script.js",
                language = "javascript",
                content = """
const menuItems = [
  {
    name: "Classic Sourdough Loaf",
    category: "breads",
    price: "৳240",
    desc: "Slow-fermented for 36 hours",
    img: "https://images.unsplash.com/photo-1589367920969"
  }
];

function renderGrid() {
  const grid = document.getElementById('grid');
  grid.innerHTML = menuItems.map(item => `
    <div class="menu-card">
      <img src="${'$'}{item.img}" alt="${'$'}{item.name}" />
      <h3>${'$'}{item.name}</h3>
      <p>${'$'}{item.desc}</p>
      <span>${'$'}{item.price}</span>
    </div>
  `).join('');
}
                """.trimIndent()
            ),
            CodeFile(
                name = "MainActivity.kt",
                language = "kotlin",
                content = """
package com.nova.personalai

class AutonomousAgent {
    fun execute(command: String) {
        println("Agent executing: " + command)
    }
}
                """.trimIndent()
            ),
            CodeFile(
                name = "style.css",
                language = "css",
                content = """
:root {
  --neon-cyan: #00f2fe;
  --neon-pink: #ff007a;
  --bg-dark: #040711;
}

body {
  background: var(--bg-dark);
  color: #fff;
  font-family: system-ui;
}
                """.trimIndent()
            )
        )
    }

    var selectedFileIndex by remember { mutableStateOf(0) }
    var codeText by remember(selectedFileIndex) { mutableStateOf(files[selectedFileIndex].content) }
    var showAutoFixBanner by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberObsidian)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top banner: AUTO-FIX APPLIED IN-PLACE (from Screenshot 1)
        AnimatedVisibility(visible = showAutoFixBanner) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF031A0F)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CyberNeonGreen),
                    width = 1.2.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = CyberNeonGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "AUTO-FIX APPLIED IN-PLACE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberNeonGreen,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Repaired syntax in ${files[selectedFileIndex].name}",
                                fontSize = 11.sp,
                                color = CyberTextSecondary
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = {
                                // Trigger auto-fix action
                                onTriggerAutoFix("script.js auto-fix")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberNeonGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "▶ Return to Preview",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF021E0F)
                            )
                        }

                        IconButton(
                            onClick = { showAutoFixBanner = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = CyberTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // File Tabs (like screenshot 1: script.js, style.css, etc.)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            files.forEachIndexed { index, file ->
                val isSelected = index == selectedFileIndex
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CyberSurfaceElevated else CyberSurface)
                        .border(
                            width = if (isSelected) 1.dp else 0.5.dp,
                            color = if (isSelected) CyberNeonCyan else Color(0xFF1E293B),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            files[selectedFileIndex].content = codeText
                            selectedFileIndex = index
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isSelected) "${file.name} *" else file.name,
                        fontSize = 12.sp,
                        color = if (isSelected) CyberNeonCyan else CyberTextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Code Editor Canvas with line numbers
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF070B16)),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1E293B)),
                width = 1.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // Line numbers
                val lineCount = codeText.lines().size.coerceAtLeast(10)
                Column(
                    modifier = Modifier
                        .padding(end = 10.dp, top = 12.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    for (i in 1..lineCount) {
                        Text(
                            text = i.toString().padStart(2, ' '),
                            color = Color(0xFF334155),
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Code Input Area
                OutlinedTextField(
                    value = codeText,
                    onValueChange = {
                        codeText = it
                        files[selectedFileIndex].content = it
                    },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = CyberTextPrimary
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }
        }

        // Quick Syntax Helper Toolbar (like bottom of Screenshot 1)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val keys = listOf("TAB", "( )", "{ }", "[ ]", "< >", "const", "function", "=>", ";", "\" \"")
            keys.forEach { k ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberSurfaceElevated)
                        .border(0.8.dp, Color(0xFF334155), RoundedCornerShape(6.dp))
                        .clickable {
                            codeText += if (k == "TAB") "  " else k
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = k,
                        fontSize = 11.sp,
                        color = CyberNeonCyan,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Ln ${codeText.lines().size}, Col 1",
                fontSize = 10.sp,
                color = CyberTextMuted,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
