package io.github.m0nkeysan.gamekeeper

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import io.github.m0nkeysan.gamekeeper.core.navigation.GameNavGraph
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import io.github.m0nkeysan.gamekeeper.ui.strings.LocalStrings
import io.github.m0nkeysan.gamekeeper.ui.theme.AppTheme

@Composable
fun App() {
    val isDarkTheme = isSystemInDarkTheme()  // Auto-detect system dark mode preference
    
    // Setup locale manager and observe active locale changes
    val localeManager = remember { PlatformRepositories.getLocaleManager() }
    val activeLocale = localeManager.getActiveLocale().collectAsState(initial = null)
    
    // Get the appropriate StringProvider based on active locale
    val strings = remember(activeLocale.value) {
        activeLocale.value?.let { localeManager.getStringProvider(it) } 
            ?: localeManager.getStringProvider(io.github.m0nkeysan.gamekeeper.core.domain.model.AppLocale.ENGLISH)
    }
    
    AppTheme(isDarkTheme = isDarkTheme) {
        CompositionLocalProvider(LocalStrings provides strings) {
            GameNavGraph()
        }
    }
}