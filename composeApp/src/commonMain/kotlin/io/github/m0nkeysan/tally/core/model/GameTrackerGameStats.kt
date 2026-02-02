package io.github.m0nkeysan.tally.core.model

import kotlinx.serialization.Serializable

/**
 * Statistics for a single GameTracker game (not global stats)
 * Tracks progression and performance within one game session
 */
@Serializable
data class GameTrackerGameStats(
    val gameId: String,
    val gameName: String,
    val roundsPlayed: Int,
    val currentLeader: String?, // Player ID
    val leadChanges: Int,
    val playerStats: List<PlayerRoundStats>,
    val progressData: List<RoundProgressData> // For line chart - cumulative scores
)

/**
 * Statistics for a player within a specific game
 */
@Serializable
data class PlayerRoundStats(
    val player: Player,
    val roundsPlayed: Int,
    val totalScore: Int,
    val averageScorePerRound: Double,
    val highestRoundScore: Int?,
    val lowestRoundScore: Int?,
    val currentStreak: Streak,
    val scoreDistribution: ScoreDistribution
)

/**
 * Represents a player's winning or losing streak
 */
@Serializable
data class Streak(
    val type: StreakType,
    val length: Int
) {
    fun isActive(): Boolean = length > 1
}

@Serializable
enum class StreakType {
    WINNING,  // Player is leading in recent rounds
    LOSING,   // Player is not leading in recent rounds
    NEUTRAL   // No clear pattern
}

/**
 * Distribution of a player's scores across different ranges
 */
@Serializable
data class ScoreDistribution(
    val negative: Int,     // Rounds with score < 0
    val zero: Int,         // Rounds with score == 0
    val low: Int,          // Rounds with score 1-25
    val medium: Int,       // Rounds with score 26-50
    val high: Int          // Rounds with score > 50
) {
    fun total(): Int = negative + zero + low + medium + high
    
    fun getPercentage(category: DistributionCategory): Float {
        val total = total()
        if (total == 0) return 0f
        
        val count = when (category) {
            DistributionCategory.NEGATIVE -> negative
            DistributionCategory.ZERO -> zero
            DistributionCategory.LOW -> low
            DistributionCategory.MEDIUM -> medium
            DistributionCategory.HIGH -> high
        }
        
        return (count.toFloat() / total) * 100f
    }
}

enum class DistributionCategory {
    NEGATIVE, ZERO, LOW, MEDIUM, HIGH
}

/**
 * Data point for the progress line chart
 * Contains cumulative scores for all players at a specific round
 */
@Serializable
data class RoundProgressData(
    val roundNumber: Int,
    val cumulativeScores: Map<String, Int> // playerId -> cumulative score up to this round
)
