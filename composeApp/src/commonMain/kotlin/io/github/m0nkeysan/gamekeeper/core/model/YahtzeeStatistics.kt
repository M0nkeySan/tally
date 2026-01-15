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
