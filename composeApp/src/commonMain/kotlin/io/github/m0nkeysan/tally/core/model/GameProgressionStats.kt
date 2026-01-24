package io.github.m0nkeysan.tally.core.model


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
