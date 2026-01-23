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

@Composable
fun App() {
    val localeManager = remember { LocaleManager.instance }
    val currentLocale by localeManager.currentLocale.collectAsState()

    LaunchedEffect(currentLocale) {
        customAppLocale = currentLocale
    }

    val userPreferencesRepository = remember { PlatformRepositories.getUserPreferencesRepository() }
    val themePreference = userPreferencesRepository.getTheme().collectAsState(initial = AppThemeModel.SYSTEM_DEFAULT)

    val isDarkTheme = when (themePreference.value) {
        AppThemeModel.DARK -> true
        AppThemeModel.LIGHT -> false
        AppThemeModel.SYSTEM_DEFAULT -> isSystemInDarkTheme()
    }

    AppEnvironment {
        AppTheme(isDarkTheme = isDarkTheme) {
            GameNavGraph()
        }
    }
}