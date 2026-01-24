package io.github.m0nkeysan.tally.platform

import java.util.Locale as JavaLocale

/**
 * Android implementation of MultiplatformLocale using java.util.Locale.
 */
actual class MultiplatformLocale(internal val javaLocale: JavaLocale) {
    actual val languageCode: String
        get() = javaLocale.language
    
    actual val countryCode: String?
        get() = javaLocale.country.takeIf { it.isNotEmpty() }
    
    actual companion object {
        actual fun setDefault(locale: MultiplatformLocale) {
            JavaLocale.setDefault(locale.javaLocale)
        }
        
        actual fun getDefault(): MultiplatformLocale {
            return MultiplatformLocale(JavaLocale.getDefault())
        }
        
        actual fun forLanguageTag(languageTag: String): MultiplatformLocale {
            val parts = languageTag.split("_", "-")
            val localeBuilder = JavaLocale.Builder().setLanguage(parts[0])
            
            if (parts.size >= 2 && parts[1].isNotEmpty()) {
                localeBuilder.setRegion(parts[1])
            }
            if (parts.size >= 3 && parts[2].isNotEmpty()) {
                localeBuilder.setVariant(parts[2])
            }
            
            return MultiplatformLocale(localeBuilder.build())
        }
    }
}
