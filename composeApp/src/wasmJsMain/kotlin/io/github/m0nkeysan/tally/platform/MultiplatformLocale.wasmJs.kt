package io.github.m0nkeysan.tally.platform

/**
 * Web implementation of MultiplatformLocale using browser's navigator.language.
 */
actual class MultiplatformLocale(
    actual val languageCode: String,
    actual val countryCode: String?
) {
    actual companion object {
        private var defaultLocale: MultiplatformLocale? = null
        
        actual fun setDefault(locale: MultiplatformLocale) {
            defaultLocale = locale
        }
        
        actual fun getDefault(): MultiplatformLocale {
            return defaultLocale ?: run {
                val browserLocale = window.navigator.language
                forLanguageTag(browserLocale)
            }
        }
        
        actual fun forLanguageTag(languageTag: String): MultiplatformLocale {
            val parts = languageTag.split("_", "-")
            val language = parts[0]
            val country = if (parts.size >= 2 && parts[1].isNotEmpty()) parts[1] else null
            
            return MultiplatformLocale(language, country)
        }
    }
}
