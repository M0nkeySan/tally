package io.github.m0nkeysan.gamekeeper

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import io.github.m0nkeysan.gamekeeper.core.navigation.GameNavGraph
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import io.github.m0nkeysan.gamekeeper.ui.strings.AppEnvironment
import io.github.m0nkeysan.gamekeeper.ui.strings.LocaleManager
import io.github.m0nkeysan.gamekeeper.ui.strings.customAppLocale
import io.github.m0nkeysan.gamekeeper.ui.theme.AppTheme
import io.github.m0nkeysan.gamekeeper.core.domain.model.AppTheme as AppThemeModel

/**
 * Root composable for the GameKeeper application.
 *
 * Sets up:
 * - Locale management (language switching) via AppEnvironment
 * - Theme management (dark/light mode)
 * - Navigation graph
 *
 * Language Switching Flow:
 * 1. User changes language in Settings
 * 2. LocaleManager updates currentLocale StateFlow
 * 3. LaunchedEffect syncs currentLocale → customAppLocale
 * 4. AppEnvironment detects customAppLocale change
 * 5. Platform-specific locale update:
 *    - iOS: Updates NSUserDefaults["AppleLanguages"]
 *    - Android: Updates Configuration and resources
 * 6. key(customAppLocale) forces full recomposition
 * 7. All stringResource() calls re-evaluate with new locale
 * 8. UI displays in new language ✓
 * 
 * This follows the official JetBrains Compose Multiplatform pattern:
 * https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-resource-environment.html#locale
 */
@Composable
fun App() {
    // Observe locale from LocaleManager
    val localeManager = remember { LocaleManager.instance }
    val currentLocale by localeManager.currentLocale.collectAsState()
    
    // Sync LocaleManager's locale with the global customAppLocale
    // This bridges our existing state management with the new AppEnvironment system
    LaunchedEffect(currentLocale) {
        println("App: Syncing locale to customAppLocale: $currentLocale")
        customAppLocale = currentLocale
    }

    // Observe theme preference
    val userPreferencesRepository = remember { PlatformRepositories.getUserPreferencesRepository() }
    val themePreference = userPreferencesRepository.getTheme().collectAsState(initial = AppThemeModel.SYSTEM_DEFAULT)

    val isDarkTheme = when (themePreference.value) {
        AppThemeModel.DARK -> true
        AppThemeModel.LIGHT -> false
        AppThemeModel.SYSTEM_DEFAULT -> isSystemInDarkTheme()
    }

    // Wrap entire app with AppEnvironment for proper cross-platform locale handling
    // This replaces the old LocaleProvider approach with the official JetBrains pattern
    AppEnvironment {
        AppTheme(isDarkTheme = isDarkTheme) {
            GameNavGraph()
        }
    }
}