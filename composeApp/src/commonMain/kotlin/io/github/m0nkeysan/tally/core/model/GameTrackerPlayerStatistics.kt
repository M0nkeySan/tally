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
 * A single record entry: the player who achieved a score in a specific game.
 */
@Serializable
data class GameRecord(
    val playerName: String,
    val score: Int,
    val gameName: String
)

/**
 * A game-length record: shortest or longest game ever played.
 * playerNames is capped at 3 for display; remainingPlayers carries the overflow count.
 */
@Serializable
data class GameLengthRecord(
    val playerNames: List<String>,
    val remainingPlayers: Int,
    val rounds: Int,
    val gameName: String
) {
    fun getDisplayPlayerNames(): String {
        val base = playerNames.joinToString(", ")
        return if (remainingPlayers > 0) "$base +$remainingPlayers" else base
    }
}

/**
 * Records & Milestones across all GameTracker games.
 * Score records are split by scoring logic so the "best" score is always meaningful.
 */
@Serializable
data class GameTrackerRecords(
    val highestScoreInHighGames: GameRecord?,   // best score in HIGH_SCORE_WINS games
    val lowestScoreInLowGames: GameRecord?,     // best score in LOW_SCORE_WINS games
    val longestGame: GameLengthRecord?,
    val shortestCompletedGame: GameLengthRecord?
)

/**
 * A single leaderboard row for GameTracker statistics.
 */
@Serializable
data class GameTrackerLeaderboardEntry(
    val player: Player,
    val value: String,   // pre-formatted display value, e.g. "15" or "75%"
    val rank: Int        // 1-based
)

/**
 * Top-3 leaderboards derived from player statistics.
 */
@Serializable
data class GameTrackerLeaderboards(
    val mostGamesPlayed: List<GameTrackerLeaderboardEntry>,
    val highestWinRate: List<GameTrackerLeaderboardEntry>   // only players with >= 3 completed games
)

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
    val playerStatistics: List<GameTrackerPlayerStatistics>,
    val leaderboards: GameTrackerLeaderboards,
    val records: GameTrackerRecords
) {
    companion object {
        fun empty(): GameTrackerGlobalStatistics {
            return GameTrackerGlobalStatistics(
                totalGames = 0,
                completedGames = 0,
                activeGames = 0,
                totalRounds = 0,
                averageRoundsPerGame = 0.0,
                playerStatistics = emptyList(),
                leaderboards = GameTrackerLeaderboards(emptyList(), emptyList()),
                records = GameTrackerRecords(null, null, null, null)
            )
        }
    }
}
