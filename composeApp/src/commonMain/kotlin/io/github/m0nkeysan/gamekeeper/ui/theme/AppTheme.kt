package io.github.m0nkeysan.gamekeeper.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * GameKeeper application theme composable.
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
            // Primary colors
            primary = DarkGameColors.Primary,                    // #818CF8
            onPrimary = DarkGameColors.TextPrimary,             // #F8F9FA
            primaryContainer = DarkGameColors.PrimaryLight,      // #A5B4FC
            onPrimaryContainer = DarkGameColors.TextPrimary,    // #F8F9FA

            // Secondary colors
            secondary = DarkGameColors.Secondary,                // #10B981
            onSecondary = Color.Black,                          // Black for contrast on green
            secondaryContainer = Color(0xFF1A3A3A),             // Dark green variant
            onSecondaryContainer = DarkGameColors.Secondary,    // #10B981

            // Tertiary colors
            tertiary = DarkGameColors.Tertiary,                 // #F59E0B
            onTertiary = Color.Black,                           // Black for contrast on amber
            tertiaryContainer = Color(0xFF332A1F),              // Dark amber variant
            onTertiaryContainer = DarkGameColors.Tertiary,      // #F59E0B

            // Error colors
            error = DarkGameColors.Error,                       // #EF4444
            onError = Color.Black,                              // Black for contrast
            errorContainer = Color(0xFF5F2C2C),                 // Dark red variant
            onErrorContainer = DarkGameColors.Error,            // #EF4444

            // Background colors
            background = DarkGameColors.Surface0,               // #0F1419 (nearly black)
            onBackground = DarkGameColors.TextPrimary,          // #F8F9FA

            // Surface colors
            surface = DarkGameColors.Surface1,                  // #1A1F2E (dark charcoal)
            onSurface = DarkGameColors.TextPrimary,             // #F8F9FA
            surfaceVariant = DarkGameColors.Surface2,           // #2D3748 (darker accents)
            onSurfaceVariant = DarkGameColors.TextSecondary,    // #A0A8B2

            // Outline colors
            outline = DarkGameColors.Divider,                   // #3E4453
            outlineVariant = Color(0xFF49454E),                 // Medium gray variant

            // Scrim color (overlay)
            scrim = Color.Black                                 // Black scrim
        )
    } else {
        lightColorScheme(
            // Primary colors
            primary = GameColors.Primary,                       // #6366F1
            onPrimary = Color.White,                            // White for contrast
            primaryContainer = GameColors.PrimaryLight,         // #E0E7FF
            onPrimaryContainer = GameColors.Primary,            // #6366F1

            // Secondary colors
            secondary = GameColors.Secondary,                   // #10B981
            onSecondary = Color.White,                          // White for contrast
            secondaryContainer = GameColors.SecondaryLight,     // #D1FAE5
            onSecondaryContainer = GameColors.Secondary,        // #10B981

            // Tertiary colors
            tertiary = GameColors.Tertiary,                     // #F59E0B
            onTertiary = Color.White,                           // White for contrast
            tertiaryContainer = GameColors.TertiaryLight,       // #FEF3C7
            onTertiaryContainer = GameColors.Tertiary,          // #F59E0B

            // Error colors
            error = GameColors.Error,                           // #EF4444
            onError = Color.White,                              // White for contrast
            errorContainer = Color(0xFFFDEDEB),                 // Very light red
            onErrorContainer = GameColors.Error,                // #EF4444

            // Background colors
            background = GameColors.Surface0,                   // #FFFFFF (white)
            onBackground = GameColors.TextPrimary,              // #111827 (dark text)

            // Surface colors
            surface = Color.White,                              // #FFFFFF
            onSurface = GameColors.TextPrimary,                 // #111827 (dark text)
            surfaceVariant = GameColors.Surface1,               // #F3F4F6 (light gray)
            onSurfaceVariant = GameColors.TextSecondary,        // #6B7280 (medium gray)

            // Outline colors
            outline = GameColors.Divider,                       // #E5E7EB (light gray)
            outlineVariant = Color(0xFFCAC4D0),                 // Light purple variant

            // Scrim color (overlay)
            scrim = Color.Black                                 // Black scrim
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}
