package io.github.m0nkeysan.tally.platform

import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleLanguageCode
import platform.Foundation.NSLocaleCountryCode
import platform.Foundation.currentLocale
import platform.Foundation.localeWithLocaleIdentifier

/**
 * iOS implementation of MultiplatformLocale using NSLocale.
 */
actual class MultiplatformLocale(internal val nsLocale: NSLocale) {
    actual val languageCode: String
        get() = nsLocale.objectForKey(NSLocaleLanguageCode) as? String ?: "en"
    
    actual val countryCode: String?
        get() = nsLocale.objectForKey(NSLocaleCountryCode) as? String
    
    actual companion object {
        private var defaultLocale: MultiplatformLocale? = null
        
        actual fun setDefault(locale: MultiplatformLocale) {
            defaultLocale = locale
        }
        
        actual fun getDefault(): MultiplatformLocale {
            return defaultLocale ?: MultiplatformLocale(NSLocale.currentLocale)
        }
        
        actual fun forLanguageTag(languageTag: String): MultiplatformLocale {
            // Convert underscore to hyphen for NSLocale compatibility
            val localeIdentifier = languageTag.replace("_", "-")
            val nsLocale = NSLocale.localeWithLocaleIdentifier(localeIdentifier)
            return MultiplatformLocale(nsLocale)
        }
    }
}
