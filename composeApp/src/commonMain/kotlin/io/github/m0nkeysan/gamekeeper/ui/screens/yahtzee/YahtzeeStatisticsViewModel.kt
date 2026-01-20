package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.domain.repository.YahtzeeStatisticsRepository
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeGlobalStatistics
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeePlayerStatistics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import io.github.m0nkeysan.gamekeeper.generated.resources.error_unknown
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_error_global_failed
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_error_load_failed
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_error_stats_failed
import io.github.m0nkeysan.gamekeeper.generated.resources.Res

data class YahtzeeStatisticsUiState(
    val isLoading: Boolean = true,
    val availablePlayers: List<Player> = emptyList(),
    val selectedPlayerId: String? = null,
    val statistics: YahtzeePlayerStatistics? = null,
    val globalStatistics: YahtzeeGlobalStatistics? = null,
    val error: String? = null
)

class YahtzeeStatisticsViewModel(
    private val statsRepository: YahtzeeStatisticsRepository
) : ViewModel() {
    
    companion object {
        const val GLOBAL_ID = "GLOBAL"
    }
    
    private val cache = YahtzeeStatisticsCache()
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
                            selectedPlayerId = GLOBAL_ID  // Default to Global
                        )
                    }
                    // Auto-load global stats
                    loadGlobalStatistics()
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load: ${e.message ?: "Unknown error"}"
                    )
                }
            }
        }
    }
    
    fun selectPlayer(playerId: String) {
        _uiState.update { it.copy(selectedPlayerId = playerId) }
        if (playerId == GLOBAL_ID) {
            loadGlobalStatistics()
        } else {
            loadStatistics(playerId)
        }
    }
    
    private fun loadStatistics(playerId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Try cache first
                val cached = cache.getPlayerStatistics(playerId)
                if (cached != null) {
                    _uiState.update { 
                        it.copy(
                            statistics = cached,
                            globalStatistics = null,
                            isLoading = false,
                            error = null
                        )
                    }
                    return@launch
                }
                
                // Cache miss - fetch from database
                withContext(Dispatchers.IO) {
                    val stats = statsRepository.getPlayerStatistics(playerId)
                    cache.putPlayerStatistics(playerId, stats)
                    _uiState.update { 
                        it.copy(
                            statistics = stats,
                            globalStatistics = null,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load statistics: ${e.message ?: "Unknown error"}"
                    )
                }
            }
        }
    }
    
    private fun loadGlobalStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Try cache first
                val cached = cache.getGlobalStatistics()
                if (cached != null) {
                    _uiState.update { 
                        it.copy(
                            globalStatistics = cached,
                            statistics = null,
                            isLoading = false,
                            error = null
                        )
                    }
                    return@launch
                }
                
                // Cache miss - fetch from database
                withContext(Dispatchers.IO) {
                    val stats = statsRepository.getGlobalStatistics()
                    cache.putGlobalStatistics(stats)
                    _uiState.update { 
                        it.copy(
                            globalStatistics = stats,
                            statistics = null,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load global statistics: ${e.message ?: "Unknown error"}"
                    )
                }
            }
        }
    }
}
