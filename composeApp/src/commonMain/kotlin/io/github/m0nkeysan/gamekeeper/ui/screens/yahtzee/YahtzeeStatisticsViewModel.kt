package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.domain.repository.YahtzeeStatisticsRepository
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeePlayerStatistics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class YahtzeeStatisticsUiState(
    val isLoading: Boolean = true,
    val availablePlayers: List<Player> = emptyList(),
    val selectedPlayerId: String? = null,
    val statistics: YahtzeePlayerStatistics? = null,
    val error: String? = null
)

class YahtzeeStatisticsViewModel(
    private val statsRepository: YahtzeeStatisticsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(YahtzeeStatisticsUiState())
    val uiState: StateFlow<YahtzeeStatisticsUiState> = _uiState.asStateFlow()
    
    init {
        loadAvailablePlayers()
    }
    
    private fun loadAvailablePlayers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                withContext(Dispatchers.IO) {
                    val players = statsRepository.getAvailablePlayers()
                    _uiState.update { 
                        it.copy(
                            availablePlayers = players,
                            selectedPlayerId = players.firstOrNull()?.id
                        )
                    }
                    // Auto-load first player's stats
                    players.firstOrNull()?.id?.let { playerId -> 
                        loadStatistics(playerId)
                    } ?: run {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = "No players found with Yahtzee games"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load players: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun selectPlayer(playerId: String) {
        _uiState.update { it.copy(selectedPlayerId = playerId) }
        loadStatistics(playerId)
    }
    
    private fun loadStatistics(playerId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                withContext(Dispatchers.IO) {
                    val stats = statsRepository.getPlayerStatistics(playerId)
                    _uiState.update { 
                        it.copy(
                            statistics = stats,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load statistics: ${e.message}"
                    )
                }
            }
        }
    }
}
