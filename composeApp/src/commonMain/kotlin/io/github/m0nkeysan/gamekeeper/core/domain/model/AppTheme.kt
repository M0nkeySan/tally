package io.github.m0nkeysan.gamekeeper.core.domain.model

/**
 * Supported application themes for appearance settings.
 *
 * - LIGHT: Explicitly set to light theme
 * - DARK: Explicitly set to dark theme
 * - SYSTEM_DEFAULT: Uses device system theme preference (auto-detects light/dark)
 */
enum class AppTheme(val code: String, val displayName: String) {
    LIGHT("light", "Light"),
    DARK("dark", "Dark"),
    SYSTEM_DEFAULT("system", "System Default");

    companion object {
        /**
         * Convert a theme code string back to an AppTheme.
         * Returns SYSTEM_DEFAULT as fallback for unknown codes.
         */
        fun fromCode(code: String): AppTheme =
            entries.find { it.code == code } ?: SYSTEM_DEFAULT
    }
}
