package io.github.m0nkeysan.tally.ui.screens.tarot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.tally.core.domain.engine.GameProgressionAnalyzer
import io.github.m0nkeysan.tally.core.domain.engine.TarotScoringEngine
import io.github.m0nkeysan.tally.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.tally.core.domain.repository.TarotRepository
import io.github.m0nkeysan.tally.core.domain.repository.TarotStatisticsRepository
import io.github.m0nkeysan.tally.core.model.GameStatistics
import io.github.m0nkeysan.tally.core.model.PlayerRanking
import io.github.m0nkeysan.tally.core.model.RoundStatistic
import io.github.m0nkeysan.tally.core.model.TakerPerformance
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
    
    private val scoringEngine = TarotScoringEngine()

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

                val playerIdsList = gameEntity.playerIds.split(",").filter { it.isNotEmpty() }
                val players = playerIdsList.mapNotNull { playerId ->
                    playerRepository.getPlayerById(playerId.trim())
                }
                
                if (players.isEmpty()) {
                    throw IllegalStateException("No players found for game")
                }

                val rounds = tarotRepository.getRoundsForGame(gameId).first()

                val game = gameEntity.copy(players = players, rounds = rounds)

                val playerScores = scoringEngine.calculateTotalScores(
                    game.players,
                    game.rounds,
                    game.playerCount
                )
                
                val currentRankings = game.players.map { player ->
                    val totalScore = playerScores[player.id] ?: 0

                    val takerRounds = game.rounds.count { it.takerPlayerId == player.id }
                    val takerWins = game.rounds.count { 
                        it.takerPlayerId == player.id && it.score > 0 
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

                val roundBreakdown = game.rounds.mapNotNull { round ->
                    val taker = game.players.find { it.id == round.takerPlayerId }
                        ?: game.players.firstOrNull()
                    
                    if (taker != null) {
                        val displayScore = when (game.playerCount) {
                            5 -> {
                                val partnerId = round.calledPlayerId
                                if (partnerId == null || partnerId == round.takerPlayerId) {
                                    round.score * 4
                                } else {
                                    round.score * 2
                                }
                            }
                            else -> round.score * (game.playerCount - 1)
                        }

                        RoundStatistic(
                            roundNumber = round.roundNumber,
                            taker = taker,
                            bid = round.bid,
                            pointsScored = round.pointsScored,
                            bouts = round.bouts,
                            contractWon = round.score > 0,
                            score = displayScore,
                            hasSpecialAnnounce = round.hasPetitAuBout || 
                                               round.hasPoignee || 
                                               round.chelem.toString() != "NONE"
                        )
                    } else {
                        null
                    }
                }
                
                val gameStats = GameStatistics(
                    gameId = gameId,
                    gameName = game.name,
                    totalRounds = game.rounds.size,
                    leadingPlayer = currentRankings.firstOrNull()?.player,
                    playerRankings = currentRankings
                )

                val hasMinimumRounds = game.rounds.size >= 3
                var takerPerformanceMap = emptyMap<String, TakerPerformance>()

                if (hasMinimumRounds) {
                    val progressionAnalyzer = GameProgressionAnalyzer()
                    
                    takerPerformanceMap = progressionAnalyzer.calculateTakerPerformance(
                        game.players,
                        game.rounds,
                        game.playerCount
                    )
                }

                val playerStats = game.players.mapNotNull { player ->
                    statsRepository.getPlayerStatistics(player.id)
                }

                val bidStats = game.players.associate { player ->
                    player.id to statsRepository.getBidStatistics(player.id)
                }

                _uiState.update {
                    it.copy(
                        game = game,
                        gameStatistics = gameStats,
                        roundBreakdown = roundBreakdown,
                        currentGameRankings = currentRankings,
                        takerPerformance = takerPerformanceMap,
                        hasMinimumRounds = hasMinimumRounds,
                        playerStatistics = playerStats,
                        bidStatistics = bidStats,
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

    fun retryLoading() {
        loadStatistics()
    }
}
