package io.github.m0nkeysan.gamekeeper

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import io.github.m0nkeysan.gamekeeper.core.navigation.GameNavGraph
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import io.github.m0nkeysan.gamekeeper.ui.strings.LocaleManager
import io.github.m0nkeysan.gamekeeper.ui.strings.LocaleProvider
import io.github.m0nkeysan.gamekeeper.ui.theme.AppTheme
import io.github.m0nkeysan.gamekeeper.core.domain.model.AppTheme as AppThemeModel

/**
 * Root composable for the GameKeeper application.
 *
 * Sets up:
 * - Locale management (language switching)
 * - Theme management (dark/light mode)
 * - Navigation graph
 *
 * When the user changes language:
 * 1. LocaleManager updates currentLocale StateFlow
 * 2. App recomposes with new locale
 * 3. LocaleProvider updates Android configuration and provides locale to tree
 * 4. All stringResource() calls automatically pick up the new locale
 */
@Composable
fun App() {
    val localeManager = remember { LocaleManager.instance }
    val currentLocale by localeManager.currentLocale.collectAsState()

    val userPreferencesRepository = remember { PlatformRepositories.getUserPreferencesRepository() }
    val themePreference = userPreferencesRepository.getTheme().collectAsState(initial = AppThemeModel.SYSTEM_DEFAULT)

    val isDarkTheme = when (themePreference.value) {
        AppThemeModel.DARK -> true
        AppThemeModel.LIGHT -> false
        AppThemeModel.SYSTEM_DEFAULT -> isSystemInDarkTheme()
    }

    // Wrap entire app with locale provider
    // This updates Android configuration and provides locale to the tree
    LocaleProvider(locale = currentLocale) {
        AppTheme(isDarkTheme = isDarkTheme) {
            GameNavGraph()
        }
    }
}