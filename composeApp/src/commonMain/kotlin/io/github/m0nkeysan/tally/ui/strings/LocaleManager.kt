package io.github.m0nkeysan.tally.ui.strings

import io.github.m0nkeysan.tally.core.domain.model.AppLocale
import io.github.m0nkeysan.tally.core.domain.repository.UserPreferencesRepository
import io.github.m0nkeysan.tally.platform.MultiplatformLocale
import io.github.m0nkeysan.tally.platform.PlatformRepositories
import io.github.m0nkeysan.tally.platform.getSystemLocaleCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocaleManager(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _currentLocale = MutableStateFlow(getDefaultLocale())
    val currentLocale: StateFlow<String> = _currentLocale.asStateFlow()

    init {
        scope.launch {
            userPreferencesRepository.getLocale().collect { savedLanguage ->
                val code = savedLanguage.code
                _currentLocale.value = code
                applySystemLocale(code)
            }
        }
    }

    fun setLocale(languageCode: String) {
        applySystemLocale(languageCode)
        _currentLocale.value = languageCode
        scope.launch {
            userPreferencesRepository.saveLocale(AppLocale.fromCode(languageCode))
        }
    }

    fun getCurrentLocale(): String = _currentLocale.value

    private fun getDefaultLocale(): String {
        val supportedLanguages = listOf("en", "fr")
        val systemLang = getSystemLocaleCode()
        return if (systemLang in supportedLanguages) systemLang else "en"
    }

    private fun applySystemLocale(languageCode: String) {
        try {
            val locale = MultiplatformLocale.forLanguageTag(languageCode)
            MultiplatformLocale.setDefault(locale)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        val instance: LocaleManager by lazy {
            LocaleManager(PlatformRepositories.getUserPreferencesRepository())
        }
    }
}