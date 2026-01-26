package io.github.m0nkeysan.tally.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import io.github.m0nkeysan.tally.core.domain.model.AppTheme

/**
 * Resolves the user's theme preference to a boolean indicating dark theme.
 *
 * This function handles the three possible theme preferences:
 * - DARK: Always returns true
 * - LIGHT: Always returns false
 * - SYSTEM_DEFAULT: Delegates to system dark mode detection
 *
 * @param themePreference The user's theme preference from UserPreferencesRepository
 * @return true if dark theme should be displayed, false otherwise
 */
@Composable
fun resolveIsDarkTheme(themePreference: AppTheme): Boolean {
    return when (themePreference) {
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
        AppTheme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
    }
}
