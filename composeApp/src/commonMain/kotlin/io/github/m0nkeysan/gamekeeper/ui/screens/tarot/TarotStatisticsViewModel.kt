package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.TarotRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.TarotStatisticsRepository
import io.github.m0nkeysan.gamekeeper.core.model.GameStatistics
import io.github.m0nkeysan.gamekeeper.core.model.PlayerRanking
import io.github.m0nkeysan.gamekeeper.core.model.RoundStatistic
import kotlinx.coroutines.flow.first
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

                val gameEntity = tarotRepository.getGameById(gameId)
                    ?: throw IllegalStateException("Game not found")

                println("🔍 [Statistics] Loading game: ${gameEntity.id}")

                // 🆕 Load players from playerIds
                val playerIdsList = gameEntity.playerIds.split(",").filter { it.isNotEmpty() }
                val players = playerIdsList.mapNotNull { playerId ->
                    playerRepository.getPlayerById(playerId.trim())
                }
                
                if (players.isEmpty()) {
                    throw IllegalStateException("No players found for game")
                }

                println("🔍 [Statistics] Loaded ${players.size} players: ${players.map { it.name }}")

                // 🆕 Load rounds for the game
                val rounds = tarotRepository.getRoundsForGame(gameId).first()
                println("🔍 [Statistics] Loaded ${rounds.size} rounds")

                val game = gameEntity.copy(players = players, rounds = rounds)

                // 🆕 Calculate rankings directly from game data
                val currentRankings = game.players.mapIndexed { playerIndex, player ->
                    val totalScore = game.rounds
                        .filter { it.takerPlayerId.toIntOrNull() == playerIndex }
                        .sumOf { it.score }
                    
                    val takerRounds = game.rounds.count { it.takerPlayerId.toIntOrNull() == playerIndex }
                    val takerWins = game.rounds.count { 
                        it.takerPlayerId.toIntOrNull() == playerIndex && it.score > 0 
                    }
                    
                    val winRate = if (takerRounds > 0) 
                        (takerWins.toDouble() / takerRounds) * 100 
                    else 0.0
                    
                    Triple(player, totalScore, Pair(takerWins, takerRounds)) to winRate
                }
                    .sortedByDescending { it.first.second }
                    .mapIndexed { index, (playerData, winRate) ->
                        val (player, totalScore, takerStats) = playerData
                        
                        PlayerRanking(
                            rank = index + 1,
                            player = player,
                            totalScore = totalScore,
                            roundsWonAsTaker = takerStats.first,
                            roundsPlayedAsTaker = takerStats.second,
                            winRate = winRate
                        )
                    }

                // 🆕 Convert loaded rounds to RoundStatistic using game data
                val roundBreakdown = game.rounds.mapNotNull { round ->
                    val taker = game.players.getOrNull(round.takerPlayerId.toIntOrNull() ?: 0)
                        ?: game.players.firstOrNull()
                    
                    if (taker != null) {
                        RoundStatistic(
                            roundNumber = round.roundNumber,
                            taker = taker,
                            bid = round.bid,
                            pointsScored = round.pointsScored,
                            bouts = round.bouts,
                            contractWon = round.score > 0,
                            score = round.score,
                            hasSpecialAnnounce = round.hasPetitAuBout || 
                                               round.hasPoignee || 
                                               round.chelem.toString() != "NONE"
                        )
                    } else {
                        null
                    }
                }

                // 🆕 Calculate game statistics directly
                val durationMs = game.updatedAt - game.createdAt
                val durationMinutes = durationMs / (1000 * 60)
                val durationHours = durationMinutes / 60
                val durationFormatted = when {
                    durationHours > 0 -> "$durationHours hour${if (durationHours > 1) "s" else ""}"
                    else -> "$durationMinutes minute${if (durationMinutes > 1) "s" else ""}"
                }
                
                val gameStats = GameStatistics(
                    gameId = gameId,
                    gameName = game.name,
                    totalRounds = game.rounds.size,
                    gameDuration = durationFormatted,
                    leadingPlayer = currentRankings.firstOrNull()?.player,
                    playerRankings = currentRankings
                )

                println("🔍 [Statistics] Game stats: ${roundBreakdown.size} rounds, ${currentRankings.size} rankings")

                val playerStats = game.players.mapIndexed { index, player ->
                    val originalIndex = playerIdsList.indexOf(player.id)
                    println("🔍 [Statistics] Loading stats for ${player.name} (index=$originalIndex)")
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

    fun retryLoading() {
        loadStatistics()
    }
}
