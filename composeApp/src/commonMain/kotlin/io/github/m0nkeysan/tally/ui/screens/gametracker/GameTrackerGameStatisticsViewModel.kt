package io.github.m0nkeysan.tally.ui.screens.gametracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.tally.core.model.DistributionCategory
import io.github.m0nkeysan.tally.core.model.GameTrackerGame
import io.github.m0nkeysan.tally.core.model.GameTrackerGameStats
import io.github.m0nkeysan.tally.core.model.GameTrackerRound
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.core.model.PlayerRoundStats
import io.github.m0nkeysan.tally.core.model.RoundProgressData
import io.github.m0nkeysan.tally.core.model.ScoreDistribution
import io.github.m0nkeysan.tally.core.model.Streak
import io.github.m0nkeysan.tally.core.model.StreakType
import io.github.m0nkeysan.tally.platform.PlatformRepositories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GameTrackerGameStatisticsViewModel : ViewModel() {
    private val repository = PlatformRepositories.getGameTrackerRepository()
    private val playerRepository = PlatformRepositories.getPlayerRepository()
    
    private val _stats = MutableStateFlow<GameTrackerGameStats?>(null)
    val stats: StateFlow<GameTrackerGameStats?> = _stats.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun loadGameStats(gameId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val game = repository.getGameById(gameId)
                val rounds = repository.getRoundsForGame(gameId).first()
                
                if (game != null && rounds.isNotEmpty()) {
                    _stats.value = calculateGameStats(game, rounds)
                } else if (game != null) {
                    // Game exists but no rounds yet - create empty stats
                    _stats.value = GameTrackerGameStats(
                        gameId = game.id,
                        gameName = game.name,
                        roundsPlayed = 0,
                        currentLeader = null,
                        leadChanges = 0,
                        playerStats = game.players.map { player ->
                            PlayerRoundStats(
                                player = player,
                                roundsPlayed = 0,
                                totalScore = 0,
                                averageScorePerRound = 0.0,
                                highestRoundScore = null,
                                lowestRoundScore = null,
                                currentStreak = Streak(StreakType.NEUTRAL, 0),
                                scoreDistribution = ScoreDistribution(0, 0, 0, 0, 0)
                            )
                        },
                        progressData = emptyList()
                    )
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun calculateGameStats(game: GameTrackerGame, rounds: List<GameTrackerRound>): GameTrackerGameStats {
        val roundsByNumber = rounds.groupBy { it.roundNumber }.toSortedMap()
        val totalRounds = roundsByNumber.size
        
        // Calculate progress data (cumulative scores per round)
        val progressData = calculateProgressData(roundsByNumber, game.players)
        
        // Calculate lead changes
        val leadChanges = calculateLeadChanges(roundsByNumber, game)
        
        // Get current leader
        val currentLeader = game.getLeader(rounds)
        
        // Calculate per-player stats
        val playerStats = game.players.map { player ->
            calculatePlayerStats(player, rounds, roundsByNumber, game)
        }
        
        return GameTrackerGameStats(
            gameId = game.id,
            gameName = game.name,
            roundsPlayed = totalRounds,
            currentLeader = currentLeader,
            leadChanges = leadChanges,
            playerStats = playerStats,
            progressData = progressData
        )
    }
    
    private fun calculateProgressData(
        roundsByNumber: Map<Int, List<GameTrackerRound>>,
        players: List<Player>
    ): List<RoundProgressData> {
        val progressData = mutableListOf<RoundProgressData>()
        val cumulativeScores = mutableMapOf<String, Int>()
        
        // Initialize cumulative scores to 0
        players.forEach { player ->
            cumulativeScores[player.id] = 0
        }
        
        // Calculate cumulative scores for each round
        roundsByNumber.forEach { (roundNumber, roundScores) ->
            roundScores.forEach { round ->
                cumulativeScores[round.playerId] = (cumulativeScores[round.playerId] ?: 0) + round.score
            }
            
            progressData.add(
                RoundProgressData(
                    roundNumber = roundNumber,
                    cumulativeScores = cumulativeScores.toMap()
                )
            )
        }
        
        return progressData
    }
    
    private fun calculateLeadChanges(
        roundsByNumber: Map<Int, List<GameTrackerRound>>,
        game: GameTrackerGame
    ): Int {
        var previousLeader: String? = null
        var changes = 0
        
        val cumulativeRounds = mutableListOf<GameTrackerRound>()
        
        roundsByNumber.forEach { (_, roundScores) ->
            cumulativeRounds.addAll(roundScores)
            val currentLeader = game.getLeader(cumulativeRounds)
            
            if (previousLeader != null && currentLeader != null && currentLeader != previousLeader) {
                changes++
            }
            previousLeader = currentLeader
        }
        
        return changes
    }
    
    private fun calculatePlayerStats(
        player: Player,
        allRounds: List<GameTrackerRound>,
        roundsByNumber: Map<Int, List<GameTrackerRound>>,
        game: GameTrackerGame
    ): PlayerRoundStats {
        val playerRounds = allRounds.filter { it.playerId == player.id }
        val roundsPlayed = playerRounds.size
        val totalScore = playerRounds.sumOf { it.score }
        val averageScore = if (roundsPlayed > 0) totalScore.toDouble() / roundsPlayed else 0.0
        
        val highestRound = playerRounds.maxOfOrNull { it.score }
        val lowestRound = playerRounds.minOfOrNull { it.score }
        
        val streak = calculateStreak(player.id, roundsByNumber, game)
        val distribution = calculateScoreDistribution(playerRounds)
        
        return PlayerRoundStats(
            player = player,
            roundsPlayed = roundsPlayed,
            totalScore = totalScore,
            averageScorePerRound = averageScore,
            highestRoundScore = highestRound,
            lowestRoundScore = lowestRound,
            currentStreak = streak,
            scoreDistribution = distribution
        )
    }
    
    private fun calculateStreak(
        playerId: String,
        roundsByNumber: Map<Int, List<GameTrackerRound>>,
        game: GameTrackerGame
    ): Streak {
        if (roundsByNumber.isEmpty()) {
            return Streak(StreakType.NEUTRAL, 0)
        }
        
        val cumulativeRounds = mutableListOf<GameTrackerRound>()
        val leaders = mutableListOf<String?>()
        
        // Calculate leader for each round based on cumulative scores
        roundsByNumber.forEach { (_, roundScores) ->
            cumulativeRounds.addAll(roundScores)
            leaders.add(game.getLeader(cumulativeRounds.toList()))
        }
        
        // Take last 5 rounds for streak calculation
        val recentLeaders = leaders.takeLast(5)
        
        var currentStreak = 0
        var streakType = StreakType.NEUTRAL
        
        // Count consecutive leading or non-leading rounds
        for (leader in recentLeaders.reversed()) {
            if (leader == playerId) {
                if (streakType == StreakType.WINNING || currentStreak == 0) {
                    currentStreak++
                    streakType = StreakType.WINNING
                } else {
                    break
                }
            } else {
                if (streakType == StreakType.LOSING || currentStreak == 0) {
                    currentStreak++
                    streakType = StreakType.LOSING
                } else {
                    break
                }
            }
        }
        
        return Streak(streakType, currentStreak)
    }
    
    private fun calculateScoreDistribution(playerRounds: List<GameTrackerRound>): ScoreDistribution {
        var negative = 0
        var zero = 0
        var low = 0
        var medium = 0
        var high = 0
        
        playerRounds.forEach { round ->
            when {
                round.score < 0 -> negative++
                round.score == 0 -> zero++
                round.score in 1..25 -> low++
                round.score in 26..50 -> medium++
                round.score > 50 -> high++
            }
        }
        
        return ScoreDistribution(
            negative = negative,
            zero = zero,
            low = low,
            medium = medium,
            high = high
        )
    }
}
