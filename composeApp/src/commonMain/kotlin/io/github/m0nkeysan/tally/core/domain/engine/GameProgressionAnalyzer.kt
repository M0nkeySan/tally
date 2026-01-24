package io.github.m0nkeysan.tally.core.domain.engine

import io.github.m0nkeysan.tally.core.model.*

/**
 * Analyzes game progression to generate highlights, momentum, and performance stats
 */
class GameProgressionAnalyzer() {
    /**
     * Calculate enhanced taker performance metrics
     * Requires at least 3 rounds
     */
    fun calculateTakerPerformance(
        players: List<Player>,
        rounds: List<TarotRound>,
        playerCount: Int
    ): Map<String, TakerPerformance> {
        if (rounds.size < 3) return emptyMap()
        
        return players.mapIndexed { index, player ->
            val takerRounds = rounds.filter { 
                it.takerPlayerId.toIntOrNull() == index 
            }
            
            val wins = takerRounds.filter { it.score > 0 }
            val losses = takerRounds.filter { it.score <= 0 }
            
            val bidDistribution = takerRounds.groupingBy { it.bid }.eachCount()
            val preferredBid = bidDistribution.maxByOrNull { it.value }?.key
            
            val avgWin = if (wins.isNotEmpty()) 
                wins.map { it.score }.average() else 0.0
            val avgLoss = if (losses.isNotEmpty()) 
                losses.map { it.score }.average() else 0.0
            
            val totalGained = wins.sumOf { it.score }
            val totalLost = losses.sumOf { it.score }
            
            val partnerStats = if (playerCount == 5) {
                calculatePartnerStats(index, players, rounds)
            } else null
            
            player.id to TakerPerformance(
                player = player,
                takerRounds = takerRounds.size,
                wins = wins.size,
                losses = losses.size,
                winRate = if (takerRounds.isNotEmpty()) 
                    (wins.size.toDouble() / takerRounds.size) * 100 
                else 0.0,
                preferredBid = preferredBid,
                bidDistribution = bidDistribution,
                avgWinPoints = avgWin,
                avgLossPoints = avgLoss,
                totalPointsGained = totalGained,
                totalPointsLost = totalLost,
                partnerStats = partnerStats
            )
        }.toMap()
    }
    
    // ============ PRIVATE HELPER FUNCTIONS ============
    
    private fun calculatePartnerStats(
        playerIndex: Int,
        allPlayers: List<Player>,
        rounds: List<TarotRound>
    ): Map<String, PartnerStat>? {
        val takerRoundsWithPartner = rounds.filter { round ->
            round.takerPlayerId.toIntOrNull() == playerIndex &&
            round.calledPlayerId != null &&
            round.calledPlayerId != playerIndex.toString()
        }
        
        if (takerRoundsWithPartner.isEmpty()) return null
        
        return takerRoundsWithPartner
            .groupBy { it.calledPlayerId }
            .mapNotNull { (partnerId, partnerRounds) ->
                val partnerIdx = partnerId?.toIntOrNull() ?: return@mapNotNull null
                val partner = allPlayers.getOrNull(partnerIdx) ?: return@mapNotNull null
                
                val wins = partnerRounds.count { it.score > 0 }
                val losses = partnerRounds.size - wins
                val winRate = (wins.toDouble() / partnerRounds.size) * 100
                
                partner.id to PartnerStat(
                    partnerId = partner.id,
                    partnerName = partner.name,
                    gamesPlayed = partnerRounds.size,
                    wins = wins,
                    losses = losses,
                    winRate = winRate
                )
            }.toMap()
    }
}
