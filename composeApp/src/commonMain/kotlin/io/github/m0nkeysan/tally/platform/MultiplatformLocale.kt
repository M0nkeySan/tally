package io.github.m0nkeysan.tally.platform

/**
 * Multiplatform wrapper for locale functionality.
 * Abstracts platform-specific locale implementations (java.util.Locale on Android, NSLocale on iOS).
 */
expect class MultiplatformLocale {
    val languageCode: String
    val countryCode: String?
    
    companion object {
        fun setDefault(locale: MultiplatformLocale)
        fun getDefault(): MultiplatformLocale
        fun forLanguageTag(languageTag: String): MultiplatformLocale
    }
}
