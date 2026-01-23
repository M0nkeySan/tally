package io.github.m0nkeysan.gamekeeper.ui.screens.settings// SettingsViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.domain.model.AppTheme
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel() : ViewModel() {

    private val preferencesRepository = PlatformRepositories.getUserPreferencesRepository()
    private val localeManager = PlatformRepositories.getLocaleManager()

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesRepository.getTheme(),
        localeManager.currentLocale
    ) { theme, localeCode ->
        SettingsUiState(
            currentTheme = theme,
            currentLocaleCode = localeCode,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState(isLoading = true)
    )

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferencesRepository.saveTheme(theme)
        }
    }

    fun setLocale(localeCode: String) {
        localeManager.setLocale(localeCode) 
    }
}

data class SettingsUiState(
    val currentTheme: AppTheme = AppTheme.SYSTEM_DEFAULT,
    val currentLocaleCode: String = "en",
    val isLoading: Boolean = false
)