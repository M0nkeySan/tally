package io.github.m0nkeysan.gamekeeper

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import io.github.m0nkeysan.gamekeeper.core.domain.model.AppLocale
import io.github.m0nkeysan.gamekeeper.core.domain.model.AppTheme as AppThemeModel
import io.github.m0nkeysan.gamekeeper.core.navigation.GameNavGraph
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import io.github.m0nkeysan.gamekeeper.ui.localization.ResourceEnvironment
import io.github.m0nkeysan.gamekeeper.ui.localization.customAppLocale
import io.github.m0nkeysan.gamekeeper.ui.strings.LocalStrings
import io.github.m0nkeysan.gamekeeper.ui.theme.AppTheme

@Composable
fun App() {
    // Setup locale manager and observe active locale changes
    val localeManager = remember { PlatformRepositories.getLocaleManager() }
    val activeLocale = localeManager.getActiveLocale().collectAsState(initial = AppLocale.ENGLISH)
    
    // Convert AppLocale enum to language code string for ResourceEnvironment
    val localeCode = when (activeLocale.value) {
        AppLocale.FRENCH -> "fr"
        AppLocale.ENGLISH -> "en"
        AppLocale.SYSTEM_DEFAULT -> null
    }
    
    // Update the global state that ResourceEnvironment observes
    customAppLocale = localeCode
    
    // Get the appropriate StringProvider based on active locale
    // This will recompose whenever activeLocale changes, enabling reactive language switching
    val strings = localeManager.getStringProvider(activeLocale.value)
    
    // Observe theme preference
    val userPreferencesRepository = remember { PlatformRepositories.getUserPreferencesRepository() }
    val themePreference = userPreferencesRepository.getTheme().collectAsState(initial = AppThemeModel.SYSTEM_DEFAULT)
    
    // Determine if dark theme based on preference or system setting
    val isDarkTheme = when (themePreference.value) {
        AppThemeModel.DARK -> true
        AppThemeModel.LIGHT -> false
        AppThemeModel.SYSTEM_DEFAULT -> isSystemInDarkTheme()
    }
    
    ResourceEnvironment(locale = localeCode) {
        AppTheme(isDarkTheme = isDarkTheme) {
            // Provide locale-aware strings to entire UI tree
            // This enables immediate language switching when user preference changes
            CompositionLocalProvider(LocalStrings provides strings) {
                GameNavGraph()
            }
        }
    }
}