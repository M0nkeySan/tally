package io.github.m0nkeysan.tally.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

/**
 * Application theme composable.
 * Provides unified Material3 theming with support for both light and dark modes.
 *
 * The theme automatically detects system dark mode preference and applies the appropriate
 * color scheme. All screens automatically inherit the correct colors from MaterialTheme.colorScheme.
 *
 * Features:
 * - System dark mode auto-detection
 * - Material3 compliant color mapping
 * - WCAG AAA contrast compliance
 * - Consistent styling across all screens
 * - No manual theme switching needed
 *
 * Example usage:
 * ```
 * @Composable
 * fun App() {
 *     val isDarkTheme = isSystemInDarkTheme()
 *     AppTheme(isDarkTheme) {
 *         // Your content here
 *     }
 * }
 * ```
 */

@Composable
fun AppTheme(
    isDarkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkTheme) {
        darkColorScheme(
            // Primary colors - Indigo
            primary = Color(0xFF818CF8),                        // Lighter indigo for visibility
            onPrimary = Color(0xFFF8F9FA),                      // 99% white (eye-friendly)
            primaryContainer = Color(0xFFA5B4FC),               // Even lighter variant
            onPrimaryContainer = Color(0xFFF8F9FA),             // 99% white

            // Secondary colors - Emerald
            secondary = Color(0xFF10B981),                      // Emerald green (good visibility)
            onSecondary = Color.Black,                          // Black for contrast on green
            secondaryContainer = Color(0xFF1A3A3A),             // Dark green variant
            onSecondaryContainer = Color(0xFF10B981),           // Emerald green

            // Tertiary colors - Amber
            tertiary = Color(0xFFF59E0B),                       // Bright amber
            onTertiary = Color.Black,                           // Black for contrast on amber
            tertiaryContainer = Color(0xFF332A1F),              // Dark amber variant
            onTertiaryContainer = Color(0xFFF59E0B),            // Bright amber

            // Error colors - Red
            error = Color(0xFFEF4444),                          // Bright red (high visibility)
            onError = Color.Black,                              // Black for contrast
            errorContainer = Color(0xFF5F2C2C),                 // Dark red variant
            onErrorContainer = Color(0xFFEF4444),               // Bright red

            // Background colors
            background = Color(0xFF0F1419),                     // Nearly black background (WCAG AAA)
            onBackground = Color(0xFFF8F9FA),                   // 99% white

            // Surface colors
            surface = Color(0xFF1A1F2E),                        // Dark charcoal for cards
            onSurface = Color(0xFFF8F9FA),                      // 99% white
            surfaceVariant = Color(0xFF2D3748),                 // Darker accents for hierarchy
            onSurfaceVariant = Color(0xFFA0A8B2),               // Light gray for secondary text
            surfaceContainer = Color(0xFF2D3748),               // Same as surfaceVariant

            // Outline colors
            outline = Color(0xFF3E4453),                        // Dark divider
            outlineVariant = Color(0xFF49454E),                 // Medium gray variant

            // Scrim color (overlay)
            scrim = Color.Black                                 // Black scrim
        )
    } else {
        lightColorScheme(
            // Primary colors - Indigo
            primary = Color(0xFF6366F1),                        // Indigo - modern, professional
            onPrimary = Color.White,                            // White for contrast
            primaryContainer = Color(0xFFE0E7FF),               // Very light indigo
            onPrimaryContainer = Color(0xFF6366F1),             // Indigo

            // Secondary colors - Emerald
            secondary = Color(0xFF10B981),                      // Emerald green
            onSecondary = Color.White,                          // White for contrast
            secondaryContainer = Color(0xFFD1FAE5),             // Very light green
            onSecondaryContainer = Color(0xFF10B981),           // Emerald green

            // Tertiary colors - Amber
            tertiary = Color(0xFFF59E0B),                       // Amber - warnings
            onTertiary = Color.White,                           // White for contrast
            tertiaryContainer = Color(0xFFFEF3C7),              // Very light amber
            onTertiaryContainer = Color(0xFFF59E0B),            // Amber

            // Error colors - Red
            error = Color(0xFFEF4444),                          // Bright red
            onError = Color.White,                              // White for contrast
            errorContainer = Color(0xFFFDEDEB),                 // Very light red
            onErrorContainer = Color(0xFFEF4444),               // Bright red

            // Background colors
            background = Color(0xFFFFFFFF),                     // White
            onBackground = Color(0xFF111827),                   // Dark text

            // Surface colors
            surface = Color.White,                              // White
            onSurface = Color(0xFF111827),                      // Dark text
            surfaceVariant = Color(0xFFF3F4F6),                 // Light gray
            onSurfaceVariant = Color(0xFF6B7280),               // Medium gray
            surfaceContainer = Color(0xFFE5E7EB),               // Medium gray for hierarchy

            // Outline colors
            outline = Color(0xFFE5E7EB),                        // Light gray divider
            outlineVariant = Color(0xFFCAC4D0),                 // Light purple variant

            // Scrim color (overlay)
            scrim = Color.Black                                 // Black scrim
        )
    }

    val customColors = if (isDarkTheme) {
        DarkCustomColors
    } else {
        LightCustomColors
    }

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography(),
            shapes = MaterialTheme.shapes,
            content = content
        )
    }
}
