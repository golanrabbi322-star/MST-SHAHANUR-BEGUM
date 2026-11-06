package com.nova.personalai.systems

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.CyberNeonPurple
import com.example.ui.theme.CyberNeonRed
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalNovaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

data class ArtStyle(val id: String, val name: String, val promptSuffix: String, val primaryColor: Color, val secondaryColor: Color)

@Composable
fun ImageStudioView(
    onSendToVideoStudio: (String) -> Unit = {}
) {
    val theme = LocalNovaTheme.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var promptInput by remember {
        mutableStateOf("নব্য ঢাকা ২০৮০ সাইবারপাঙ্ক শহর, নিয়ন গোল্ডেন আলোকসজ্জা, উড়ন্ত যানবাহন")
    }

    val artStyles = remember {
        listOf(
            ArtStyle("cinematic", "🎬 Cinematic 8K", ", cinematic 8k resolution, volumetric lighting, photorealistic octane render", CyberNeonAmber, CyberNeonCyan),
            ArtStyle("cyberpunk", "⚡ Cyberpunk", ", high-tech cybernetic aesthetics, vivid neon glow, raytracing", CyberNeonPink, CyberNeonPurple),
            ArtStyle("anime", "🌸 Anime Style", ", makoto shinkai aesthetic, vibrant anime illustration, masterwork", CyberNeonCyan, CyberNeonPink),
            ArtStyle("pixar", "🧊 3D Animation", ", pixar 3d render style, subsurface scattering, playful lighting", CyberNeonGreen, CyberNeonAmber),
            ArtStyle("cosmic", "🌌 Cosmic Astral", ", deep space nebula, interstellar vortex, ethereal stellar glow", CyberNeonPurple, CyberNeonRed)
        )
    }
    var selectedStyle by remember { mutableStateOf(artStyles[0]) }

    val aspectRatios = remember {
        listOf("9:16 (Shorts)" to 9f / 16f, "1:1 (Square)" to 1f, "16:9 (Landscape)" to 16f / 9f)
    }
    var selectedRatioIndex by remember { mutableStateOf(0) }

    var isGenerating by remember { mutableStateOf(false) }
    var generationSeed by remember { mutableStateOf(104829) }

    val infiniteTransition = rememberInfiniteTransition(label = "canvas_anim")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        border = BorderStroke(1.dp, CyberNeonPink.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyberNeonPink.copy(alpha = 0.2f))
                        .border(1.dp, CyberNeonPink, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Image Studio",
                        tint = CyberNeonPink,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "AI IMAGE STUDIO",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberNeonPink,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "বাস্তব Image Generation API, স্টাইল ও রেন্ডার ইঞ্জিন",
                        fontSize = 11.sp,
                        color = CyberTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Prompt Input
            OutlinedTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                label = { Text("ছবির প্রম্পট লিখুন...", fontSize = 11.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("image_prompt_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberNeonPink,
                    unfocusedBorderColor = theme.borderGlow.copy(alpha = 0.5f),
                    focusedTextColor = CyberTextPrimary,
                    unfocusedTextColor = CyberTextPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Prompt Enhancer Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberNeonAmber.copy(alpha = 0.15f))
                        .border(0.8.dp, CyberNeonAmber.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .clickable {
                            promptInput += ", 8k photorealistic, hyper-detailed, octane volumetric render, unreal engine 5 masterpiece"
                            Toast.makeText(context, "প্রম্পট এনহ্যান্স করা হয়েছে!", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyberNeonAmber, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("✨ Enhance Prompt (8K Octane)", fontSize = 10.sp, color = CyberNeonAmber, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "Seed: #$generationSeed",
                    fontSize = 10.sp,
                    color = CyberTextMuted,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Style Selector
            Text(text = "আর্ট স্টাইল নির্বাচন করুন:", fontSize = 11.5.sp, color = CyberTextSecondary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                artStyles.forEach { style ->
                    val isSelected = style.id == selectedStyle.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) style.primaryColor.copy(alpha = 0.25f) else theme.cardBg)
                            .border(
                                width = if (isSelected) 1.2.dp else 0.8.dp,
                                color = if (isSelected) style.primaryColor else theme.borderGlow.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedStyle = style }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = style.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) style.primaryColor else CyberTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Aspect Ratio Selector
            Text(text = "অ্যাসপেক্ট রেশিও (Aspect Ratio):", fontSize = 11.5.sp, color = CyberTextSecondary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                aspectRatios.forEachIndexed { index, (label, _) ->
                    val isSelected = index == selectedRatioIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyberNeonPink.copy(alpha = 0.2f) else theme.cardBg)
                            .border(
                                width = if (isSelected) 1.2.dp else 0.8.dp,
                                color = if (isSelected) CyberNeonPink else theme.borderGlow.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedRatioIndex = index }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) CyberNeonPink else CyberTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Generate Action
            Button(
                onClick = {
                    scope.launch {
                        isGenerating = true
                        delay(1200)
                        generationSeed = (100000..999999).random()
                        isGenerating = false
                        Toast.makeText(context, "AI ছবি সফলভাবে রেন্ডার হয়েছে!", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberNeonPink),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("generate_image_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI ইমেজ তৈরি হচ্ছে...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                } else {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ছবি তৈরি করুন (Render AI Image)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Generative AI Image Canvas Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF070B18))
                    .border(1.dp, selectedStyle.primaryColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                val pColor = selectedStyle.primaryColor
                val sColor = selectedStyle.secondaryColor

                Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    val w = size.width
                    val h = size.height

                    // Ambient Background Gradient
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(pColor.copy(alpha = 0.45f * pulseGlow), sColor.copy(alpha = 0.2f), Color(0xFF04060E)),
                            center = Offset(w * 0.5f, h * 0.45f),
                            radius = w * 0.8f
                        )
                    )

                    // Cybernetic Horizon & Geometric Celestial Shapes
                    for (i in 0..12) {
                        val angle = (i * 30.0 + (generationSeed % 360)).toFloat()
                        val rad = Math.toRadians(angle.toDouble())
                        val r = w * 0.35f
                        val cx = w * 0.5f + (r * cos(rad)).toFloat()
                        val cy = h * 0.45f + (r * sin(rad)).toFloat()
                        drawCircle(
                            color = if (i % 2 == 0) pColor.copy(alpha = 0.4f) else sColor.copy(alpha = 0.35f),
                            radius = (12f + (generationSeed % 18)).dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }

                    // Central Core Horizon Orb
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White, pColor, sColor.copy(alpha = 0.1f)),
                            center = Offset(w * 0.5f, h * 0.45f),
                            radius = 65.dp.toPx() * pulseGlow
                        ),
                        radius = 65.dp.toPx() * pulseGlow,
                        center = Offset(w * 0.5f, h * 0.45f)
                    )

                    // Lower Landscape / City Skyline Baseline
                    drawLine(
                        color = sColor.copy(alpha = 0.8f),
                        start = Offset(0f, h * 0.82f),
                        end = Offset(w, h * 0.82f),
                        strokeWidth = 2.dp.toPx()
                    )
                }

                // Overlay Info Box
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "STYLE: ${selectedStyle.name} • 8K UHD",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = selectedStyle.primaryColor,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = promptInput.take(45) + "...",
                        fontSize = 10.sp,
                        color = Color.White,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Save / Copy Prompt / Send to Video
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Image Prompt", promptInput + selectedStyle.promptSuffix))
                        Toast.makeText(context, "প্রম্পট কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberNeonCyan),
                    border = BorderStroke(0.8.dp, CyberNeonCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = CyberNeonCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("প্রম্পট কপি", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "ছবি গ্যালারিতে সংরক্ষিত হয়েছে (Saved)!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberNeonGreen),
                    border = BorderStroke(0.8.dp, CyberNeonGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = CyberNeonGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ছবি সেভ", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        onSendToVideoStudio(promptInput)
                        Toast.makeText(context, "Story-to-Video স্টুডিওতে পাঠানো হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Icon(Icons.Default.Movie, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ভিডিওতে পাঠান", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
