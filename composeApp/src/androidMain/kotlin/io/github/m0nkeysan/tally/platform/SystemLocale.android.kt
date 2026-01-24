package io.github.m0nkeysan.tally.platform

import java.util.Locale

/**
 * Android implementation: Returns the system's current language code.
 * Examples: "en", "fr", "es", "de", etc.
 */
actual fun getSystemLocaleCode(): String {
    return Locale.getDefault().language
}
