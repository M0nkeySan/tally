package io.github.m0nkeysan.gamekeeper.platform

import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleLanguageCode
import platform.Foundation.currentLocale

/**
 * iOS implementation: Returns the system's current language code.
 * Examples: "en", "fr", "es", "de", etc.
 */
actual fun getSystemLocaleCode(): String {
    val locale = NSLocale.currentLocale
    return locale.objectForKey(NSLocaleLanguageCode) as? String ?: "en"
}
