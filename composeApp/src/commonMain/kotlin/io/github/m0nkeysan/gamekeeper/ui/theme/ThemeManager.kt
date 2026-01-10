package io.github.m0nkeysan.gamekeeper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

/**
 * Utility object for theme management and system dark mode detection.
 *
 * This object provides a centralized place for theme-related logic.
 * Currently, it exposes the system dark mode preference detection,
 * but can be extended in the future to support:
 * - Manual theme toggles
 * - Theme persistence
 * - Dynamic color schemes
 *
 * Example usage:
 * ```
 * @Composable
 * fun MyScreen() {
 *     val isDarkTheme = ThemeManager.isDarkTheme()
 *     // Use isDarkTheme to customize component behavior if needed
 * }
 * ```
 */
object ThemeManager {
    /**
     * Detects whether the system is currently in dark mode.
     *
     * Uses Android's native dark mode preference (available on Android 5.0+).
     * This is a Composable function and should only be called from Composables.
     *
     * The detection is automatic and real-time:
     * - Changes immediately when user toggles system dark mode
     * - No manual configuration needed
     * - No persistence layer required
     *
     * @return true if system is in dark mode, false otherwise
     */
    @Composable
    fun isDarkTheme(): Boolean = isSystemInDarkTheme()
}
