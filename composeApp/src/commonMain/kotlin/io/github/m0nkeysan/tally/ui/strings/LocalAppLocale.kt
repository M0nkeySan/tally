package io.github.m0nkeysan.tally.ui.strings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue

/**
 * Platform-agnostic locale provider for dynamic language switching.
 * 
 * This follows the official JetBrains Compose Multiplatform pattern for managing
 * in-app locale changes. Each platform implements this differently to ensure
 * resources are loaded correctly with the selected language.
 * 
 * Reference: https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-resource-environment.html#locale
 * 
 * Platform implementations:
 * - iOS: Updates NSUserDefaults with "AppleLanguages" key
 * - Android: Updates Configuration.setLocale()
 * 
 * Usage:
 * ```
 * CompositionLocalProvider(
 *     LocalAppLocale provides selectedLanguage
 * ) {
 *     // Content with dynamic locale
 * }
 * ```
 */
expect object LocalAppLocale {
    /**
     * Gets the current locale code (e.g., "en", "fr").
     * This value is provided via CompositionLocal.
     */
    val current: String
        @Composable get
    
    /**
     * Provides a locale value to the composition tree.
     * This updates the platform-specific locale settings and returns
     * a ProvidedValue that can be used with CompositionLocalProvider.
     * 
     * @param value The locale code to set (e.g., "en", "fr"), or null to reset to system default
     * @return ProvidedValue for CompositionLocalProvider
     */
    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}
