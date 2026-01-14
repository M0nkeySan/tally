package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.domain.repository.TarotRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.TarotStatisticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Tarot Statistics Screen
 *
 * Loads and manages statistics data for both current game and cross-game player metrics.
 * Handles loading states and errors gracefully.
 */
class TarotStatisticsViewModel(
    private val gameId: String,
    private val tarotRepository: TarotRepository,
    private val statsRepository: TarotStatisticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TarotStatisticsState())
    val uiState: StateFlow<TarotStatisticsState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    /**
     * Load all statistics for the current game and all players.
     * Updates UI state with data or error messages.
     */
    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                // Load current game
                val game = tarotRepository.getGameById(gameId)
                    ?: throw IllegalStateException("Game not found")

                // Load current game statistics
                val gameStats = statsRepository.getCurrentGameStatistics(gameId)
                val roundBreakdown = statsRepository.getRoundBreakdown(gameId)
                val currentRankings = statsRepository.getPlayerRankings(gameId)

                // Load cross-game statistics for each player
                val playerStats = game.players.mapIndexed { index, player ->
                    statsRepository.getPlayerStatistics(player.id, index)
                }.filterNotNull()

                val bidStats = game.players.mapIndexed { index, player ->
                    player.id to statsRepository.getBidStatistics(player.id, index)
                }.toMap()

                val recentGames = game.players.associate { player ->
                    player.id to statsRepository.getRecentGames(player.id, limit = 10)
                }

                // Update state with all loaded data
                _uiState.update {
                    it.copy(
                        game = game,
                        gameStatistics = gameStats,
                        roundBreakdown = roundBreakdown,
                        currentGameRankings = currentRankings,
                        playerStatistics = playerStats,
                        bidStatistics = bidStats,
                        recentGames = recentGames,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load statistics"
                    )
                }
            }
        }
    }

    /**
     * Retry loading statistics after an error.
     */
    fun retryLoading() {
        loadStatistics()
    }
}
