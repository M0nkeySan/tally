package io.github.m0nkeysan.gamekeeper.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Custom colors for GameKeeper that extend Material Design 3.
 * These colors are NOT part of the standard Material color scheme.
 * 
 * Usage:
 * ```
 * val customColors = LocalCustomColors.current
 * Icon(tint = customColors.trophyGold)
 * Text(color = customColors.success)
 * ```
 */
@Immutable
data class CustomColors(
    val success: Color,
    val warning: Color,
    val trophyGold: Color
)

/**
 * Light theme custom colors
 */
val LightCustomColors = CustomColors(
    success = Color(0xFF10B981),    // Emerald green
    warning = Color(0xFFF59E0B),    // Amber
    trophyGold = Color(0xFFFFD700)  // Gold
)

/**
 * Dark theme custom colors
 */
val DarkCustomColors = CustomColors(
    success = Color(0xFF10B981),    // Emerald green (same - good visibility)
    warning = Color(0xFFFCD34D),    // Brighter amber for dark mode
    trophyGold = Color(0xFFFCD34D)  // Brighter gold for dark mode
)

/**
 * CompositionLocal for custom colors
 */
val LocalCustomColors = staticCompositionLocalOf { LightCustomColors }
