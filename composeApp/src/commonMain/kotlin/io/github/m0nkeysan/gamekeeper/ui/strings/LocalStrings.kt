package io.github.m0nkeysan.gamekeeper.ui.strings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * CompositionLocal for tracking the current app locale.
 * Default is "en" (English).
 */
val LocalLocale = compositionLocalOf { "en" }

/**
 * Provides the current locale to the composition tree.
 * Triggers recomposition of dependent composables when locale changes.
 * Also updates Android system configuration on locale changes.
 *
 * @param locale The locale code (e.g., "en", "fr")
 * @param content The composable content to wrap
 */
@Composable
fun LocaleProvider(
    locale: String,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Update Android system configuration when locale changes
    SideEffect {
        // Parse locale string: "en" or "en_US" or "en-US"
        val parts = locale.split("_", "-").filter { it.isNotEmpty() }
        val targetLocale = when (parts.size) {
            1 -> Locale(parts[0])
            2 -> Locale(parts[0], parts[1])
            else -> Locale(parts[0], parts[1], parts[2])
        }

        // Set the JVM default locale (affects stringResource locale selection)
        Locale.setDefault(targetLocale)

        // Update Android system configuration
        @Suppress("DEPRECATION")
        val config = context.resources.configuration
        config.setLocale(targetLocale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    CompositionLocalProvider(
        LocalLocale provides locale,
        content = content
    )
}
