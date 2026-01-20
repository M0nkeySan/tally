package io.github.m0nkeysan.gamekeeper.platform

/**
 * Returns the system's current language code (e.g., "en", "fr").
 * Platform-specific implementation.
 */
expect fun getSystemLocaleCode(): String
