package io.github.m0nkeysan.gamekeeper.core.domain.model

/**
 * Supported application locales for i18n support.
 *
 * - ENGLISH: Explicitly set to English
 * - FRENCH: Explicitly set to French (Français)
 * - SYSTEM_DEFAULT: Uses device system locale (auto-detects language)
 */
enum class AppLocale(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    FRENCH("fr", "Français");

    companion object {
        /**
         * Convert a locale code string back to an AppLocale.
         * Returns ENGLISH as fallback for unknown codes.
         */
        fun fromCode(code: String): AppLocale =
            entries.find { it.code == code } ?: ENGLISH
    }
}
