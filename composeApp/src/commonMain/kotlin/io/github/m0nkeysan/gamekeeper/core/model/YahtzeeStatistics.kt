package io.github.m0nkeysan.gamekeeper.core.model

/**
 * Complete statistics for a Yahtzee player across all their games
 */
data class YahtzeePlayerStatistics(
    val playerId: String,
    val playerName: String,
    val totalGames: Int,
    val finishedGames: Int,
    val wins: Int,
    val winRate: Double,                            // 0.0 - 100.0
    val averageScore: Double,
    val highScore: Int,
    val totalYahtzees: Int,
    val yahtzeeRate: Double,                        // Yahtzees per game
    val categoryStats: Map<YahtzeeCategory, CategoryStat>,
    val upperBonusRate: Double,                     // 0.0 - 100.0
    val upperSectionAverage: Double,
    val lowerSectionAverage: Double,
    val recentGames: List<GameSummary>
)

/**
 * Statistics for a single Yahtzee category (box)
 */
data class CategoryStat(
    val category: YahtzeeCategory,
    val average: Double,
    val timesScored: Int,
    val timesZeroed: Int,
    val zeroRate: Double,                           // 0.0 - 100.0
    val best: Int
)

/**
 * Summary of a single completed Yahtzee game
 */
data class GameSummary(
    val gameId: String,
    val gameName: String,
    val totalScore: Int,
    val playerCount: Int,
    val rank: Int,                                  // 1 = winner
    val isWinner: Boolean,
    val completedAt: Long
)

/**
 * Constants for Yahtzee statistics calculations
 */
object YahtzeeStatisticsConstants {
    const val TOP_N_LEADERBOARD = 3              // Top 3 for leaderboards
    const val RECENT_GAMES_COUNT = 10            // Last 10 games
    const val ESTIMATED_TURNS_PER_GAME = 13      // 13 categories
    const val ESTIMATED_ROLLS_PER_TURN = 3       // Up to 3 rolls
    const val UPPER_BONUS_THRESHOLD = 63         // Upper section bonus at 63+
    const val UPPER_BONUS_VALUE = 35             // Upper bonus points
}

/**
 * Global statistics across all Yahtzee games and players
 */
data class YahtzeeGlobalStatistics(
    // Overall Game Statistics
    val totalGames: Int,
    val finishedGames: Int,
    val totalPlayers: Int,
    val mostActivePlayer: PlayerSummary?,
    
    // Scoring Statistics
    val allTimeHighScore: ScoreRecord?,
    val averageScore: Double,
    val totalYahtzees: Int,
    val yahtzeeRate: Double,
    val mostYahtzeesInGame: YahtzeeRecord?,
    val upperBonusRate: Double,
    
    // Category Performance
    val categoryStats: Map<YahtzeeCategory, GlobalCategoryStat>,
    val mostScoredCategory: YahtzeeCategory?,
    val leastScoredCategory: YahtzeeCategory?,
    val highestCategoryAverage: CategoryRecord?,
    
    // Competitive Statistics (Leaderboards)
    val topPlayersByWins: List<LeaderboardEntry>,
    val topPlayersByScore: List<LeaderboardEntry>,
    val topPlayersByYahtzees: List<LeaderboardEntry>,
    
    // Recent Activity
    val recentGames: List<GlobalGameSummary>,
    
    // Fun/Quirky Stats
    val estimatedDiceRolls: Long,
    val luckiestPlayer: PlayerSummary?,
    val mostConsistentPlayer: PlayerSummary?,
    val totalPointsScored: Long,
    val averagePlayersPerGame: Double
)

/**
 * Summary of a player for global stats
 */
data class PlayerSummary(
    val playerId: String,
    val playerName: String,
    val gamesPlayed: Int,
    val metric: Double
)

/**
 * Record holder for a score
 */
data class ScoreRecord(
    val score: Int,
    val playerName: String,
    val gameId: String,
    val gameName: String,
    val date: Long
)

/**
 * Record holder for Yahtzees in a single game
 */
data class YahtzeeRecord(
    val count: Int,
    val playerName: String,
    val gameId: String,
    val gameName: String,
    val date: Long
)

/**
 * Category performance record
 */
data class CategoryRecord(
    val category: YahtzeeCategory,
    val average: Double
)

/**
 * Global category statistics
 */
data class GlobalCategoryStat(
    val category: YahtzeeCategory,
    val average: Double,
    val totalTimesScored: Int,
    val totalTimesZeroed: Int,
    val zeroRate: Double,
    val highestScore: Int,
    val highestScorePlayer: String?
)

/**
 * Leaderboard entry
 */
data class LeaderboardEntry(
    val rank: Int,
    val playerId: String,
    val playerName: String,
    val value: Int,
    val secondaryValue: String?
)

/**
 * Global game summary (all players)
 */
data class GlobalGameSummary(
    val gameId: String,
    val gameName: String,
    val winnerName: String,
    val winnerScore: Int,
    val playerCount: Int,
    val completedAt: Long
)
