package io.github.m0nkeysan.tally.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.tally.core.domain.model.AppTheme
import io.github.m0nkeysan.tally.core.domain.model.HomeFeatureState
import io.github.m0nkeysan.tally.core.domain.model.defaultFeatureStates
import io.github.m0nkeysan.tally.platform.PlatformRepositories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel : ViewModel() {

    private val userPreferencesRepository = PlatformRepositories.getUserPreferencesRepository()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    // All feature states (including disabled)
    val featureStates: StateFlow<List<HomeFeatureState>> = 
        userPreferencesRepository.getHomeFeatureStates()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = defaultFeatureStates
            )
    
    // Only enabled features, sorted by order (for display on home screen)
    val visibleFeatures: StateFlow<List<HomeFeatureState>> = 
        featureStates.map { states ->
            states.filter { it.enabled }.sortedBy { it.order }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val themePreference: StateFlow<AppTheme> = userPreferencesRepository.getTheme()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM_DEFAULT
        )
}
