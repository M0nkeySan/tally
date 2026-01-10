package io.github.m0nkeysan.gamekeeper.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Dark theme color system for GameKeeper.
 * Provides dark variants of all GameColors for dark mode support.
 *
 * Design Principles:
 * - Nearly black backgrounds (#0F1419) for maximum contrast
 * - Text colors at 99% white (#F8F9FA) for eye-friendly reading
 * - Accent colors brightened 30-40% for visibility on dark backgrounds
 * - WCAG AAA compliant contrast ratios (7:1 minimum for text)
 *
 * Example usage in AppTheme:
 * ```
 * if (isDarkTheme) {
 *     primary = DarkGameColors.Primary
 *     surface = DarkGameColors.Surface1
 *     onSurface = DarkGameColors.TextPrimary
 * }
 * ```
 */
object DarkGameColors {
    // Primary Colors (Indigo - lighter for dark backgrounds)
    val Primary = Color(0xFF818CF8)        // Lighter indigo for visibility
    val PrimaryLight = Color(0xFFA5B4FC)   // Even lighter variant
    val PrimaryDark = Color(0xFF6366F1)    // Base primary

    // Secondary Colors (Emerald - adjusted for dark mode)
    val Secondary = Color(0xFF10B981)      // Keep emerald green (good visibility)
    val SecondaryLight = Color(0xFF34D399) // Brighter variant for dark bg
    val SecondaryDark = Color(0xFF059669)  // Darker variant

    // Tertiary Colors (Amber - brightened for dark mode)
    val Tertiary = Color(0xFFF59E0B)       // Bright amber
    val TertiaryLight = Color(0xFFFCD34D)  // Even brighter
    val TertiaryDark = Color(0xFFD97706)   // Darker variant

    // Semantic Colors (Dark variants - all brightened)
    val Success = Color(0xFF10B981)        // Emerald green (high visibility)
    val Error = Color(0xFFEF4444)          // Bright red (high visibility)
    val Warning = Color(0xFFFCD34D)        // Very bright yellow (high visibility)
    val Info = Color(0xFF60A5FA)           // Bright blue (high visibility)

    // Neutral Palette (Nearly black to charcoal)
    val Surface0 = Color(0xFF0F1419)       // Nearly black background (WCAG AAA compliant)
    val Surface1 = Color(0xFF1A1F2E)       // Dark charcoal for cards (~11% lighter)
    val Surface2 = Color(0xFF2D3748)       // Darker accents (~20% lighter for hierarchy)
    val TextPrimary = Color(0xFFF8F9FA)    // 99% white (eye-friendly)
    val TextSecondary = Color(0xFFA0A8B2)  // Light gray for secondary text
    val Divider = Color(0xFF3E4453)        // Dark divider

    // Game-Specific Accent Colors (Brightened for dark mode)
    val TarotAccent = Color(0xFFD8B4FE)    // Light lavender (40% lighter than light mode)
    val YahtzeeAccent = Color(0xFF22D3EE)  // Bright cyan (much brighter for visibility)

    // Player Avatar Colors (Brightened for dark mode - 40-50% lighter)
    val PlayerAvatarColors = listOf(
        Color(0xFFFF9999), // Red (brighter)
        Color(0xFF7EE5DB), // Teal (brighter)
        Color(0xFF7DD3FC), // Blue (brighter)
        Color(0xFFFFB499), // Salmon (brighter)
        Color(0xFFA8E6D9), // Mint (brighter)
        Color(0xFFFFED99), // Yellow (brighter)
        Color(0xFFD8B4FE), // Purple (brighter)
        Color(0xFFB4E3FF), // Sky (brighter)
    )

    // Trophy/Achievement Colors (Adjusted for dark mode)
    val TrophyGold = Color(0xFFFCD34D)     // Brighter gold
    val TrophySilver = Color(0xFFE5E7EB)   // Light silver
    val TrophyBronze = Color(0xFFFBBF24)   // Brighter bronze
}
