package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// NOVA Cyberpunk Autonomous IDE Palette (from user screenshots)
val CyberObsidian = Color(0xFF040711)
val CyberSurface = Color(0xFF090D1A)
val CyberSurfaceElevated = Color(0xFF0E1528)
val CyberCardBg = Color(0xFF111A30)

// Vibrant Cyberpunk & Luxury Accents
val CyberNeonCyan = Color(0xFF00F2FE)
val CyberCyanMuted = Color(0xFF0284C7)
val CyberNeonPink = Color(0xFFFF007A)
val CyberNeonPurple = Color(0xFF8B5CF6)
val CyberNeonGreen = Color(0xFF00FF66)
val CyberNeonAmber = Color(0xFFFFB800)
val CyberNeonRed = Color(0xFFFF3366)
val CyberLuxuryGold = Color(0xFFFFD700)
val CyberGoldAmber = Color(0xFFF59E0B)
val CyberChampagne = Color(0xFFFDE68A)

// Text Hierarchy
val CyberTextPrimary = Color(0xFFF8FAFC)
val CyberTextSecondary = Color(0xFF94A3B8)
val CyberTextMuted = Color(0xFF64748B)

// Borders & Glows
val CyberBorderSubtle = Color(0xFF1E293B)
val CyberBorderCyan = Color(0x6600F2FE)
val CyberBorderPink = Color(0x66FF007A)
val CyberBorderGreen = Color(0x6600FF66)
val CyberBorderGold = Color(0x66FFD700)

// Backwards-compatible aliases
val NovaDeepSpace = CyberObsidian
val NovaSurface = CyberSurface
val NovaSurfaceVariant = CyberSurfaceElevated
val NovaCardBg = CyberCardBg
val NovaCyan = CyberNeonCyan
val NovaCyanGlow = Color(0x3300F2FE)
val NovaViolet = CyberNeonPurple
val NovaVioletDark = Color(0xFF6D28D9)
val NovaEmerald = CyberNeonGreen
val NovaRose = CyberNeonPink
val NovaAmber = CyberNeonAmber
val NovaTextPrimary = CyberTextPrimary
val NovaTextSecondary = CyberTextSecondary
val NovaTextMuted = CyberTextMuted
val NovaBorder = CyberBorderSubtle
val NovaBorderGlow = CyberNeonCyan

/**
 * 5 Ultra-Premium Theme Presets for NOVA AI
 */
enum class NovaThemePreset(
    val id: String,
    val displayName: String,
    val badge: String,
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val background: Color,
    val surface: Color,
    val cardBg: Color,
    val borderGlow: Color,
    val gradientColors: List<Color>
) {
    PLATINUM_CYBER(
        id = "cyber",
        displayName = "Titanium Cyan",
        badge = "💎 FLAGSHIP",
        primary = Color(0xFF00F2FE),
        secondary = Color(0xFF8B5CF6),
        accent = Color(0xFFFF007A),
        background = Color(0xFF030712),
        surface = Color(0xFF0A0F1D),
        cardBg = Color(0xFF0F172A),
        borderGlow = Color(0x9900F2FE),
        gradientColors = listOf(Color(0xFF00F2FE), Color(0xFF38BDF8), Color(0xFF818CF8))
    ),
    IMPERIAL_GOLD(
        id = "gold",
        displayName = "Imperial Gold",
        badge = "👑 24K LUXURY",
        primary = Color(0xFFFFD700),
        secondary = Color(0xFFF59E0B),
        accent = Color(0xFFFDE68A),
        background = Color(0xFF070502),
        surface = Color(0xFF140F06),
        cardBg = Color(0xFF1F160A),
        borderGlow = Color(0x99FFD700),
        gradientColors = listOf(Color(0xFFFFD700), Color(0xFFF59E0B), Color(0xFFD97706))
    ),
    SYNTHWAVE_NEBULA(
        id = "synthwave",
        displayName = "Synthwave Neon",
        badge = "🌌 CYBERPUNK",
        primary = Color(0xFFFF007A),
        secondary = Color(0xFFA855F7),
        accent = Color(0xFF00F2FE),
        background = Color(0xFF080214),
        surface = Color(0xFF130724),
        cardBg = Color(0xFF1E0C38),
        borderGlow = Color(0x99FF007A),
        gradientColors = listOf(Color(0xFFFF007A), Color(0xFFA855F7), Color(0xFF6366F1))
    ),
    MATRIX_EMERALD(
        id = "emerald",
        displayName = "Quantum Emerald",
        badge = "⚡ MATRIX PRO",
        primary = Color(0xFF00FF88),
        secondary = Color(0xFF10B981),
        accent = Color(0xFF34D399),
        background = Color(0xFF020A05),
        surface = Color(0xFF06150B),
        cardBg = Color(0xFF0B2113),
        borderGlow = Color(0x9900FF88),
        gradientColors = listOf(Color(0xFF00FF88), Color(0xFF10B981), Color(0xFF059669))
    ),
    RUBY_CRIMSON(
        id = "crimson",
        displayName = "Ruby Crimson",
        badge = "🩸 STEALTH ROYALE",
        primary = Color(0xFFFF2A55),
        secondary = Color(0xFFE11D48),
        accent = Color(0xFFFB7185),
        background = Color(0xFF0A0205),
        surface = Color(0xFF17040B),
        cardBg = Color(0xFF240712),
        borderGlow = Color(0x99FF2A55),
        gradientColors = listOf(Color(0xFFFF2A55), Color(0xFFE11D48), Color(0xFFBE123C))
    )
}
