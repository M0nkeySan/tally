package io.github.m0nkeysan.gamekeeper.ui.localization

import androidx.compose.runtime.*

/**
 * Provides the application's resource environment (locale, theme, etc.).
 *
 * Wraps the composition tree with CompositionLocal providers that handle:
 * - Locale switching (language selection)
 * - Theme management
 * - Other resource-related settings
 *
 * Example usage:
 * ```
 * ResourceEnvironment(locale = "fr") {
 *     MyAppContent()
 * }
 * ```
 *
 * When the locale changes, all descendant Composables that depend on
 * stringResource() or other locale-aware resources will recompose automatically.
 *
 * @param locale The locale code to apply (e.g., "en", "fr"). Pass null for system default.
 * @param content The composable tree to wrap with the environment.
 */
@Composable
fun ResourceEnvironment(
    locale: String? = null,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAppLocale provides locale
    ) {
        // Use key to ensure the entire composition tree is recomposed
        // when the locale changes
        key(locale) {
            content()
        }
    }
}
