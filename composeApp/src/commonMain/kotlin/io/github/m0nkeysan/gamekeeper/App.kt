package io.github.m0nkeysan.gamekeeper

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import io.github.m0nkeysan.gamekeeper.core.navigation.GameNavGraph
import io.github.m0nkeysan.gamekeeper.ui.theme.AppTheme

@Composable
fun App() {
    val isDarkTheme = isSystemInDarkTheme()  // Auto-detect system dark mode preference
    
    AppTheme(isDarkTheme = isDarkTheme) {
        GameNavGraph()
    }
}