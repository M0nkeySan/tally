package io.github.m0nkeysan.gamekeeper.core.model

/**
 * Highlights and notable events from the current game
 */
data class GameHighlights(
    val biggestComeback: ComebackStat?,
    val largestLead: LeadStat?,
    val bestRound: RoundHighlight?
)

data class ComebackStat(
    val player: Player,
    val lowestScore: Int,
    val currentScore: Int,
    val recovery: Int, // currentScore - lowestScore
    val roundReached: Int // Round number where lowest score occurred
)

data class LeadStat(
    val player: Player,
    val leadAmount: Int,
    val secondPlacePlayer: Player,
    val roundNumber: Int
)

data class RoundHighlight(
    val player: Player,
    val round: RoundStatistic,
    val pointsGained: Int,
    val scoreBefore: Int,
    val scoreAfter: Int
)

/**
 * Enhanced taker performance metrics
 */
data class TakerPerformance(
    val player: Player,
    val takerRounds: Int,
    val wins: Int,
    val losses: Int,
    val winRate: Double,
    val preferredBid: TarotBid?, // Most used bid
    val bidDistribution: Map<TarotBid, Int>, // Count of each bid
    val avgWinPoints: Double,
    val avgLossPoints: Double,
    val totalPointsGained: Int,
    val totalPointsLost: Int,
    val partnerStats: Map<String, PartnerStat>? = null // 5-player only
)

data class PartnerStat(
    val partnerId: String,
    val partnerName: String,
    val gamesPlayed: Int,
    val wins: Int,
    val losses: Int,
    val winRate: Double
)
