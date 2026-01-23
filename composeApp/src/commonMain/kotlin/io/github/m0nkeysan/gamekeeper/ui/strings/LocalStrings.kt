package io.github.m0nkeysan.gamekeeper.ui.strings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.m0nkeysan.gamekeeper.platform.MultiplatformLocale

/**
 * CompositionLocal for tracking the current app locale.
 * Default is "en" (English).
 */
val LocalLocale = compositionLocalOf { "en" }

/**
 * Global mutable state for the custom app locale.
 * 
 * This is the single source of truth for the app's current language preference.
 * When this value changes, the entire app recomposes with the new locale via AppEnvironment.
 * 
 * Set this value to change the app's language:
 * ```
 * customAppLocale = "fr" // Switch to French
 * customAppLocale = "en" // Switch to English
 * customAppLocale = null // Reset to system default
 * ```
 */
var customAppLocale by mutableStateOf<String?>(null)

/**
 * Provides the app environment with custom locale support.
 * 
 * This is the main wrapper for dynamic language switching following the official
 * JetBrains Compose Multiplatform pattern. When customAppLocale changes:
 * 
 * 1. LocalAppLocale.provides() is called with the new locale
 *    - On iOS: Updates NSUserDefaults with "AppleLanguages" key
 *    - On Android: Updates Configuration and resources
 * 
 * 2. key(customAppLocale) forces the entire content tree to recompose
 *    - All stringResource() calls are re-evaluated
 *    - Resources are loaded with the new locale
 * 
 * Usage:
 * ```
 * @Composable
 * fun App() {
 *     // Observe locale from your state management
 *     val currentLocale by localeManager.currentLocale.collectAsState()
 *     
 *     // Sync with customAppLocale
 *     LaunchedEffect(currentLocale) {
 *         customAppLocale = currentLocale
 *     }
 *     
 *     AppEnvironment {
 *         // Your app content
 *     }
 * }
 * ```
 * 
 * Reference: https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-resource-environment.html#locale
 * 
 * @param content The composable content that will use the provided locale
 */
@Composable
fun AppEnvironment(content: @Composable () -> Unit) {
    println("AppEnvironment: Providing locale '$customAppLocale'")
    
    CompositionLocalProvider(
        LocalAppLocale provides customAppLocale,
    ) {
        // The key() ensures that when customAppLocale changes,
        // the entire content tree is disposed and recreated,
        // forcing all stringResource() calls to re-evaluate
        key(customAppLocale) {
            content()
        }
    }
}

/**
 * Provides the current locale to the composition tree.
 * Triggers recomposition of dependent composables when locale changes.
 * Also updates platform default locale on locale changes.
 *
 * @param locale The locale code (e.g., "en", "fr")
 * @param content The composable content to wrap
 * 
 * @deprecated Use AppEnvironment with customAppLocale instead for proper cross-platform support
 */
@Composable
fun LocaleProvider(
    locale: String,
    content: @Composable () -> Unit
) {
    // Update platform default locale when locale changes
    SideEffect {
        val targetLocale = MultiplatformLocale.forLanguageTag(locale)
        MultiplatformLocale.setDefault(targetLocale)
    }

    CompositionLocalProvider(
        LocalLocale provides locale,
        content = content
    )
}
