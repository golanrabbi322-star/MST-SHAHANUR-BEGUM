package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LocalNovaTheme = staticCompositionLocalOf { NovaThemePreset.PLATINUM_CYBER }

fun buildColorSchemeForPreset(preset: NovaThemePreset) = darkColorScheme(
    primary = preset.primary,
    onPrimary = Color(0xFF001524),
    primaryContainer = preset.surface,
    onPrimaryContainer = preset.primary,
    secondary = preset.secondary,
    onSecondary = Color.White,
    secondaryContainer = preset.cardBg,
    onSecondaryContainer = preset.accent,
    tertiary = preset.accent,
    onTertiary = Color.White,
    background = preset.background,
    onBackground = CyberTextPrimary,
    surface = preset.surface,
    onSurface = CyberTextPrimary,
    surfaceVariant = preset.cardBg,
    onSurfaceVariant = CyberTextSecondary,
    outline = preset.borderGlow.copy(alpha = 0.35f),
    outlineVariant = Color(0xFF1E293B)
)

@Composable
fun MyApplicationTheme(
    preset: NovaThemePreset = NovaThemePreset.PLATINUM_CYBER,
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> buildColorSchemeForPreset(preset)
    }

    CompositionLocalProvider(LocalNovaTheme provides preset) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

