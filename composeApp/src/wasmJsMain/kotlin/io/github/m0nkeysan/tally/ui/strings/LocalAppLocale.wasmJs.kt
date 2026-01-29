package io.github.m0nkeysan.tally.ui.strings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import io.github.m0nkeysan.tally.platform.window

/**
 * Web implementation of LocalAppLocale.
 */
actual object LocalAppLocale {
    private val default: String = run {
        // Fall back to browser language
        val browserLocale = window.navigator.language
        browserLocale.split("-", "_")[0]
    }

    private val LocalAppLocaleCompositionLocal = staticCompositionLocalOf { default }

    actual val current: String
        @Composable get() = LocalAppLocaleCompositionLocal.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val new = value ?: default
        return LocalAppLocaleCompositionLocal.provides(new)
    }
}
