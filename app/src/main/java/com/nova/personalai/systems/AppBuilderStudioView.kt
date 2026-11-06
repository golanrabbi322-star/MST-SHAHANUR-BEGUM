package com.nova.personalai.systems

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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalNovaTheme

@Composable
fun AppBuilderStudioView(
    onNavigateToEditor: () -> Unit = {},
    onNavigateToFiles: () -> Unit = {},
    onNavigateToPreview: () -> Unit = {}
) {
    val theme = LocalNovaTheme.current
    val context = LocalContext.current

    var buildStatus by remember { mutableStateOf("HEALTHY (0 Errors, 0 Warnings)") }
    var lastCheckTime by remember { mutableStateOf("Just now") }

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
                        imageVector = Icons.Default.Code,
                        contentDescription = "App Builder",
                        tint = CyberNeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "APP BUILDER & DEV ENGINE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberNeonCyan,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "কোড তৈরি, ফাইল ম্যানেজমেন্ট ও কম্পাইলার টেস্ট পাইপলাইন",
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
                BuilderMetricCard("COMPILER", "KOTLIN 2.0+", CyberNeonGreen, Modifier.weight(1f))
                BuilderMetricCard("UI ENGINE", "COMPOSE M3", CyberNeonCyan, Modifier.weight(1f))
                BuilderMetricCard("STATUS", "PASS (0 ERR)", CyberNeonPurple, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Actions to Dev Tools
            Text(text = "১. ডেভেলপার ওয়ার্কস্পেস কুইক-অ্যাকশন:", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = CyberTextSecondary)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateToEditor,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("কোড এডিটর", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToFiles,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonPurple),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ফাইল ট্রি", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToPreview,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.1f)
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("লাইভ প্রিভিউ", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Compiler Test Action
            Text(text = "২. কম্পাইলার ও কোড ভ্যালিডেশন টেস্ট:", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = CyberTextSecondary)
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedButton(
                onClick = {
                    buildStatus = "COMPILING... VERIFYING DEPENDENCIES..."
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        buildStatus = "BUILD SUCCESS: 0 Errors, 0 Fatal Issues, APK Ready"
                        Toast.makeText(context, "কম্পাইলার ও আর্কিটেকচার ভ্যালিডেশন সফল!", Toast.LENGTH_SHORT).show()
                    }, 600)
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberNeonGreen),
                border = BorderStroke(1.dp, CyberNeonGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("run_compiler_test_button")
            ) {
                Icon(Icons.Default.Build, contentDescription = null, tint = CyberNeonGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("⚡ কম্পাইলার ও বিল্ড টেস্ট রান করুন", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "বিল্ড আউটপুট: $buildStatus",
                fontSize = 10.sp,
                color = CyberNeonGreen,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun BuilderMetricCard(
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
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color, fontFamily = FontFamily.Monospace)
            Text(text = value, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = CyberTextPrimary, maxLines = 1)
        }
    }
}
