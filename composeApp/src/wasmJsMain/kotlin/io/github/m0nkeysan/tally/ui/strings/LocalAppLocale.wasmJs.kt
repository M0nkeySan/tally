package io.github.m0nkeysan.tally.ui.strings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Web implementation of LocalAppLocale.
 * Uses browser's localStorage to persist language preference.
 */
actual object LocalAppLocale {
    private const val LANG_KEY = "app_language"

    private val default: String = run {
        // Try to get from localStorage first
        val stored = try {
            js("localStorage.getItem('app_language')") as? String
        } catch (e: Exception) {
            null
        }
        stored ?: run {
            // Fall back to browser language
            val browserLocale = js("navigator.language || navigator.userLanguage").toString()
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
                js("localStorage.removeItem('app_language')")
            } else {
                js("localStorage.setItem('app_language', new)")
            }
        } catch (e: Exception) {
            console.log("Failed to save language preference to localStorage: ${e.message}")
        }

        return LocalAppLocaleCompositionLocal.provides(new)
    }
}
