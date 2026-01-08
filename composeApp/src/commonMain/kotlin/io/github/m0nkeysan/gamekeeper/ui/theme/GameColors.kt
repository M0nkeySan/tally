package io.github.m0nkeysan.gamekeeper.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Centralized color system for GameKeeper flat design.
 * All colors should be referenced from this object instead of using hardcoded hex values.
 *
 * This theme provides:
 * - Primary & Secondary colors for main interactions
 * - Semantic colors (Success, Error, Warning, Info) for status feedback
 * - Neutral palette for backgrounds and text
 * - Game-specific accents (Tarot purple, Yahtzee cyan)
 * - Player avatar colors for consistent visual identification
 * - Trophy colors for achievement display
 *
 * Example usage:
 * ```
 * Text("Hello", color = GameColors.TextPrimary)
 * Surface(color = GameColors.Primary) { ... }
 * Icon(Icons.Default.Check, tint = GameColors.Success)
 * ```
 */
object GameColors {
    // Primary Colors (Indigo - modern, professional)
    val Primary = Color(0xFF6366F1)
    val PrimaryLight = Color(0xFFE0E7FF)
    val PrimaryDark = Color(0xFF4F46E5)
    
    // Secondary Colors (Emerald - success)
    val Secondary = Color(0xFF10B981)
    val SecondaryLight = Color(0xFFD1FAE5)
    val SecondaryDark = Color(0xFF059669)
    
    // Tertiary Colors (Amber - warnings)
    val Tertiary = Color(0xFFF59E0B)
    val TertiaryLight = Color(0xFFFEF3C7)
    val TertiaryDark = Color(0xFFD97706)
    
    // Semantic Colors
    val Success = Color(0xFF10B981)
    val Error = Color(0xFFEF4444)
    val Warning = Color(0xFFF59E0B)
    val Info = Color(0xFF3B82F6)
    
    // Neutral Palette (Flat Design)
    val Surface0 = Color(0xFFFFFFFF)     // White
    val Surface1 = Color(0xFFF9FAFB)     // Almost white
    val Surface2 = Color(0xFFF3F4F6)     // Light gray
    val TextPrimary = Color(0xFF111827)   // Dark text
    val TextSecondary = Color(0xFF6B7280) // Medium gray
    val Divider = Color(0xFFE5E7EB)       // Light divider
    
    // Game-Specific Accent Colors
    val TarotAccent = Color(0xFF9333EA)   // Purple - mystical
    val YahtzeeAccent = Color(0xFF06B6D4) // Cyan - fun
    
    // Player Avatar Colors (8-color consistent palette)
    val PlayerAvatarColors = listOf(
        Color(0xFFFF6B6B), // Red
        Color(0xFF4ECDC4), // Teal
        Color(0xFF45B7D1), // Blue
        Color(0xFFFFA07A), // Salmon
        Color(0xFF98D8C8), // Mint
        Color(0xFFF7DC6F), // Yellow
        Color(0xFFBB8FCE), // Purple
        Color(0xFF85C1E2), // Sky
    )
    
    // Trophy/Achievement Colors
    val TrophyGold = Color(0xFFFFD700)
    val TrophySilver = Color(0xFFC0C0C0)
    val TrophyBronze = Color(0xFFCD7F32)
}
