package io.github.m0nkeysan.tally.ui.screens.gametracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.tally.core.model.GameTrackerGlobalStatistics
import io.github.m0nkeysan.tally.platform.PlatformRepositories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameTrackerStatisticsViewModel : ViewModel() {
    private val repository = PlatformRepositories.getGameTrackerRepository()
    
    private val _statistics = MutableStateFlow(GameTrackerGlobalStatistics.empty())
    val statistics: StateFlow<GameTrackerGlobalStatistics> = _statistics.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadStatistics()
    }
    
    fun loadStatistics() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _statistics.value = repository.getGlobalStatistics()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refresh() {
        loadStatistics()
    }
}
