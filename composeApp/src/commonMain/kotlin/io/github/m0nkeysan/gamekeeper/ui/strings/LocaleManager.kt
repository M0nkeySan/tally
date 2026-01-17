package io.github.m0nkeysan.gamekeeper.ui.strings

import io.github.m0nkeysan.gamekeeper.core.domain.model.AppLocale
import io.github.m0nkeysan.gamekeeper.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

/**
 * Manages application locale switching and string provider selection.
 *
 * Features:
 * - Resolves system default locale to appropriate language
 * - Persists locale preference to user preferences
 * - Provides appropriate StringProvider based on active locale
 * - Supports system locale detection (SYSTEM_DEFAULT)
 */
class LocaleManager(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    /**
     * Get the currently active locale as a Flow.
     * If SYSTEM_DEFAULT is selected, returns the detected system locale.
     */
    fun getActiveLocale(): Flow<AppLocale> =
        userPreferencesRepository.getLocale().map { savedLocale ->
            if (savedLocale == AppLocale.SYSTEM_DEFAULT) {
                getSystemLocale()
            } else {
                savedLocale
            }
        }

    /**
     * Save the user's locale preference.
     */
    suspend fun setLocale(locale: AppLocale) {
        userPreferencesRepository.saveLocale(locale)
    }

    /**
     * Get the StringProvider for a specific locale.
     */
    fun getStringProvider(locale: AppLocale): StringProvider = when (locale) {
        AppLocale.ENGLISH, AppLocale.SYSTEM_DEFAULT -> AppStringsEn
        AppLocale.FRENCH -> AppStringsFr
    }

    /**
     * Detect the system locale based on device language settings.
     * Returns ENGLISH for unknown language codes.
     */
    private fun getSystemLocale(): AppLocale {
        val systemLang = Locale.getDefault().language
        return when (systemLang) {
            "fr" -> AppLocale.FRENCH
            else -> AppLocale.ENGLISH
        }
    }
}
