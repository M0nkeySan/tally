package io.github.m0nkeysan.gamekeeper.core.model

/**
 * Comprehensive statistics for a player across all Tarot games.
 *
 * Tracks:
 * - Total games and rounds played
 * - Rounds as taker and win statistics
 * - Score metrics (average, total)
 */
data class PlayerStatistics(
    val playerId: String,
    val playerName: String,
    val totalGames: Int,
    val totalRounds: Int,
    val takerRounds: Int,
    val takerWins: Int,
    val takerWinRate: Double,  // Percentage: 0-100
    val averageTakerScore: Double,
    val totalScore: Int,
    val averageGameScore: Double  // Average score per game
)

/**
 * Statistics for a specific bid type for a player.
 *
 * Tracks win rates and average scores for each bid level
 * (Prise, Garde, Garde Sans, Garde Contre).
 */
data class BidStatistic(
    val bid: TarotBid,
    val timesPlayed: Int,
    val wins: Int,
    val winRate: Double,  // Percentage: 0-100
    val averageScore: Double
)

/**
 * Player ranking in a specific context (current game or overall).
 *
 * Shows player position with their score and taker statistics.
 */
data class PlayerRanking(
    val rank: Int,
    val player: Player,
    val totalScore: Int,
    val roundsWonAsTaker: Int,
    val roundsPlayedAsTaker: Int,
    val winRate: Double  // Percentage: 0-100
)

/**
 * Statistics for the current game being viewed.
 *
 * Includes:
 * - Game metadata (name, round count, duration)
 * - Current standings
 * - Leading player information
 */
data class GameStatistics(
    val gameId: String,
    val gameName: String,
    val totalRounds: Int,
    val gameDuration: String,  // Formatted: "45 minutes" or "2 hours"
    val leadingPlayer: Player?,
    val playerRankings: List<PlayerRanking>
)

/**
 * Statistics for an individual round in a game.
 *
 * Includes round details, bid information, scoring, and special announces.
 */
data class RoundStatistic(
    val roundNumber: Int,
    val taker: Player,
    val bid: TarotBid,
    val pointsScored: Int,  // 0-91
    val bouts: Int,  // 0-3
    val contractWon: Boolean,
    val score: Int,  // Final calculated score
    val hasSpecialAnnounce: Boolean  // Petit au bout, Poignée, Chelem
)
