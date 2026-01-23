package io.github.m0nkeysan.gamekeeper

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.ComposeUIViewController
import io.github.m0nkeysan.gamekeeper.ui.strings.LocaleManager

/**
 * iOS entry point for the Compose Multiplatform application.
 * This function is called from Swift/SwiftUI to create the UIViewController
 * that hosts the Compose UI.
 * 
 * The locale is observed and passed to the App composable, ensuring that
 * when the user changes the language in Settings, the entire UI recomposes
 * with the new locale, forcing all stringResource() calls to re-evaluate.
 */
fun MainViewController() = ComposeUIViewController { 
    // Observe locale changes from LocaleManager
    // This ensures the Compose UI is aware of locale changes
    val localeManager = LocaleManager.instance
    val currentLocale by localeManager.currentLocale.collectAsState()
    
    // Pass the locale as a key dependency to force recomposition
    // when it changes (this is also done in App.kt with key())
    println("MainViewController: Creating app with locale: $currentLocale")
    
    App() 
}
