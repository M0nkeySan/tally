package io.github.m0nkeysan.gamekeeper.ui.localization

import androidx.compose.runtime.*

/**
 * Mutable state for the application's current locale (language).
 * When changed, triggers recomposition of LocalAppLocale provider.
 */
var customAppLocale by mutableStateOf<String?>(null)

/**
 * CompositionLocal provider for the current application locale.
 * 
 * This is an expect/actual pattern that allows platform-specific locale management
 * while maintaining a common interface.
 *
 * Usage in Composables:
 * ```
 * val currentLocale = LocalAppLocale.current
 * ```
 *
 * Usage in App composition:
 * ```
 * ResourceEnvironment(locale = "fr") {
 *     MyContent()
 * }
 * ```
 */
expect object LocalAppLocale {
    /**
     * The current locale code (e.g., "en", "fr", "de")
     */
    val current: String
        @Composable get

    /**
     * CompositionLocal provider for the given locale value.
     * Passing null resets to system default.
     */
    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}
