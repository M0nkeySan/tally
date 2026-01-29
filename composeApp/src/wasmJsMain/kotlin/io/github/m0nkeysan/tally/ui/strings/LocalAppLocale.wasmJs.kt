package io.github.m0nkeysan.tally.ui.strings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import io.github.m0nkeysan.tally.platform.console
import io.github.m0nkeysan.tally.platform.localStorage
import io.github.m0nkeysan.tally.platform.window

/**
 * Web implementation of LocalAppLocale.
 * Uses browser's localStorage to persist language preference.
 */
actual object LocalAppLocale {
    private const val LANG_KEY = "app_language"

    private val default: String = run {
        // Try to get from localStorage first
        val stored = try {
            localStorage.getItem(LANG_KEY)
        } catch (e: Exception) {
            null
        }
        stored ?: run {
            // Fall back to browser language
            val browserLocale = window.navigator.language
            browserLocale.split("-", "_")[0]
        }
    }

    private val LocalAppLocaleCompositionLocal = staticCompositionLocalOf { default }

    actual val current: String
        @Composable get() = LocalAppLocaleCompositionLocal.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val new = value ?: default

        // Persist to localStorage
        try {
            if (value == null) {
                localStorage.removeItem(LANG_KEY)
            } else {
                localStorage.setItem(LANG_KEY, new)
            }
        } catch (e: Exception) {
            console.error("Failed to save language preference to localStorage: ${e.message}")
        }

        return LocalAppLocaleCompositionLocal.provides(new)
    }
}
