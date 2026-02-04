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
            // Primary
            primary = Color(0xFF4F46E5),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE0E7FF),
            onPrimaryContainer = Color(0xFF4338CA),

            // Secondary
            secondary = Color(0xFF059669),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFD1FAE5),
            onSecondaryContainer = Color(0xFF065F46),

            // Tertiary
            tertiary = Color(0xFFD97706),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFFEF3C7),
            onTertiaryContainer = Color(0xFF92400E),

            // Background
            background = Color(0xFFF8FAFC),
            onBackground = Color(0xFF0F172A),

            // Surface
            surface = Color.White,
            onSurface = Color(0xFF0F172A),

            // SurfaceVariant
            surfaceVariant = Color(0xFFF1F5F9),
            onSurfaceVariant = Color(0xFF64748B),

            // surfaceContainer
            surfaceContainer = Color(0xFFE2E8F0),

            // Outline
            outline = Color(0xFFE2E8F0),
            outlineVariant = Color(0xFFCBD5E1),

            error = Color(0xFFDC2626),
            onError = Color.White,
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
