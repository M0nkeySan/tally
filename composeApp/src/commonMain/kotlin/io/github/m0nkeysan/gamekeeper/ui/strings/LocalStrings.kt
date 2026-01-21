package io.github.m0nkeysan.gamekeeper.ui.strings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import io.github.m0nkeysan.gamekeeper.platform.MultiplatformLocale

/**
 * CompositionLocal for tracking the current app locale.
 * Default is "en" (English).
 */
val LocalLocale = compositionLocalOf { "en" }

/**
 * Provides the current locale to the composition tree.
 * Triggers recomposition of dependent composables when locale changes.
 * Also updates platform default locale on locale changes.
 *
 * @param locale The locale code (e.g., "en", "fr")
 * @param content The composable content to wrap
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
