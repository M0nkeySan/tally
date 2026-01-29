package io.github.m0nkeysan.tally.platform

/**
 * Web implementation: Returns the browser's current language code.
 * Examples: "en", "fr", "es", "de", etc.
 */
actual fun getSystemLocaleCode(): String {
    val browserLocale = window.navigator.language
    // Extract just the language code (e.g., "en" from "en-US")
    return browserLocale.split("-", "_")[0]
}
