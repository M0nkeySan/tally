package io.github.m0nkeysan.tally.core.model

import kotlinx.serialization.Serializable

/**
 * Statistics for a single player across all GameTracker games
 */
@Serializable
data class GameTrackerPlayerStatistics(
    val player: Player,
    val gamesPlayed: Int,
    val gamesWon: Int,
    val winRate: Double, // 0.0 to 1.0
    val totalScore: Int,
    val averageScore: Double,
    val highestGameScore: Int?,
    val lowestGameScore: Int?,
    val totalRoundsPlayed: Int
) {
    /**
     * Formatted win rate as percentage string
     */
    fun getWinRatePercentage(): String {
        return if (gamesPlayed == 0) "0%"
        else "${(winRate * 100).toInt()}%"
    }

    companion object {
        /**
         * Creates an empty statistics object for a player
         */
        fun empty(player: Player): GameTrackerPlayerStatistics {
            return GameTrackerPlayerStatistics(
                player = player,
                gamesPlayed = 0,
                gamesWon = 0,
                winRate = 0.0,
                totalScore = 0,
                averageScore = 0.0,
                highestGameScore = null,
                lowestGameScore = null,
                totalRoundsPlayed = 0
            )
        }
    }
}

/**
 * Global statistics across all GameTracker games
 */
@Serializable
data class GameTrackerGlobalStatistics(
    val totalGames: Int,
    val completedGames: Int,
    val activeGames: Int,
    val totalRounds: Int,
    val averageRoundsPerGame: Double,
    val playerStatistics: List<GameTrackerPlayerStatistics>
) {
    /**
     * Get the most active players (sorted by games played)
     */
    fun getMostActivePlayers(limit: Int = 5): List<GameTrackerPlayerStatistics> {
        return playerStatistics
            .sortedByDescending { it.gamesPlayed }
            .take(limit)
    }

    /**
     * Get the players with the highest win rate (minimum 3 games played)
     */
    fun getTopWinRates(limit: Int = 5, minimumGames: Int = 3): List<GameTrackerPlayerStatistics> {
        return playerStatistics
            .filter { it.gamesPlayed >= minimumGames }
            .sortedByDescending { it.winRate }
            .take(limit)
    }

    companion object {
        /**
         * Creates an empty global statistics object
         */
        fun empty(): GameTrackerGlobalStatistics {
            return GameTrackerGlobalStatistics(
                totalGames = 0,
                completedGames = 0,
                activeGames = 0,
                totalRounds = 0,
                averageRoundsPerGame = 0.0,
                playerStatistics = emptyList()
            )
        }
    }
}
