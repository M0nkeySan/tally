package io.github.m0nkeysan.tally.ui.screens.gametracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.tally.core.model.GameTrackerScoreChange
import io.github.m0nkeysan.tally.platform.PlatformRepositories
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameTrackerHistoryViewModel : ViewModel() {
    private val repository = PlatformRepositories.getGameTrackerRepository()
    
    val history: StateFlow<List<GameTrackerScoreChange>> =
        repository.getScoreHistory()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    
    fun clearHistory() {
        viewModelScope.launch {
            repository.clearScoreHistory()
        }
    }
}
