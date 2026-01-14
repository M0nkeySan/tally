package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.domain.repository.PlayerRepository
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
    private val statsRepository: TarotStatisticsRepository,
    private val playerRepository: PlayerRepository
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
                val gameEntity = tarotRepository.getGameById(gameId)
                    ?: throw IllegalStateException("Game not found")

                // 🆕 Load players from playerIds
                val playerIdsList = gameEntity.playerIds.split(",").filter { it.isNotEmpty() }
                val players = playerIdsList.mapNotNull { playerId ->
                    playerRepository.getPlayerById(playerId.trim())
                }
                
                if (players.isEmpty()) {
                    throw IllegalStateException("No players found for game")
                }
                
                println("🔍 [Statistics] Loaded ${players.size} players for game $gameId")
                println("🔍 [Statistics] Player IDs: ${players.map { it.name }}")
                
                // 🆕 Create game with loaded players
                val game = gameEntity.copy(players = players)

                // Load current game statistics
                val gameStats = statsRepository.getCurrentGameStatistics(gameId)
                val roundBreakdown = statsRepository.getRoundBreakdown(gameId)
                val currentRankings = statsRepository.getPlayerRankings(gameId)

                println("🔍 [Statistics] Rounds: ${roundBreakdown.size}, Rankings: ${currentRankings.size}")

                // Load cross-game statistics for each player
                // Map game.players to original playerIds indices for correct statistics
                val playerStats = game.players.mapIndexed { index, player ->
                    val originalIndex = playerIdsList.indexOf(player.id)
                    println("🔍 [Statistics] Loading stats for ${player.name} (originalIndex=$originalIndex, gameIndex=$index)")
                    statsRepository.getPlayerStatistics(player.id, originalIndex)
                }.filterNotNull()

                println("🔍 [Statistics] Found ${playerStats.size} player statistics")

                val bidStats = game.players.mapIndexed { index, player ->
                    val originalIndex = playerIdsList.indexOf(player.id)
                    player.id to statsRepository.getBidStatistics(player.id, originalIndex)
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
                println("❌ [Statistics] Error: ${e.message}")
                e.printStackTrace()
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
