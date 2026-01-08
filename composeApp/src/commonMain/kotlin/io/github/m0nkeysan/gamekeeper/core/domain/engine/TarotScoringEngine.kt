package io.github.m0nkeysan.gamekeeper.core.domain.engine

import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.core.model.PoigneeLevel
import io.github.m0nkeysan.gamekeeper.core.model.TarotBid
import io.github.m0nkeysan.gamekeeper.core.model.ChelemType
import io.github.m0nkeysan.gamekeeper.core.model.TarotRound
import kotlin.math.absoluteValue

class TarotScoringEngine {
    
    fun calculateScore(
        bid: TarotBid,
        bouts: Int,
        pointsScored: Int,
        hasPetitAuBout: Boolean,
        hasPoignee: Boolean,
        poigneeLevel: PoigneeLevel?,
        chelem: ChelemType
    ): TarotScoreResult {
                val pointsNeeded = when (bouts) {
            0 -> 56
            1 -> 51
            2 -> 41
            3 -> 36
            else -> 56
        }
        
        val diff = pointsScored - pointsNeeded
        val isContractWon = diff >= 0
        
        // Base score = (25 + diff.absoluteValue) * multiplier
        // diff is rounded up/down to nearest integer for calculation? 
        // Actually it's usually (25 + diff) where diff is points made - points needed
        val basePoints = 25 + diff.absoluteValue
        val score = basePoints * bid.multiplier
        
        var bonus = 0
        
        // Petit au bout: 10 * multiplier
        if (hasPetitAuBout) {
            bonus += 10 * bid.multiplier
        }
        
        // Poignée: fixed bonus (not multiplied)
        if (hasPoignee && poigneeLevel != null) {
            bonus += poigneeLevel.bonus
        }
        
        // Chelem: fixed bonus
        bonus += chelem.bonus
        
        val totalRoundPoints = score + bonus
        val finalScore = if (isContractWon) totalRoundPoints else -totalRoundPoints
        
        return TarotScoreResult(
            baseScore = score,
            bonus = bonus,
            totalScore = finalScore,
            isWon = isContractWon,
            pointsScored = pointsScored,
            pointsNeeded = pointsNeeded
        )
    }
    
    /**
     * Calculate the total scores for all players across all rounds.
     * Handles score distribution for 3, 4, and 5 player games (with partners).
     */
    fun calculateTotalScores(
        players: List<Player>,
        rounds: List<TarotRound>,
        playerCount: Int
    ): Map<String, Int> {
        val scores = players.associate { it.id to 0 }.toMutableMap()

        rounds.forEach { round ->
            val s = round.score
            val takerIndex = round.takerPlayerId.toIntOrNull() ?: return@forEach
            val takerUuid = players.getOrNull(takerIndex)?.id ?: return@forEach
            val calledIndex = round.calledPlayerId?.toIntOrNull()

            when (playerCount) {
                5 -> {
                    // 5 player game - taker can call a partner
                    if (calledIndex == null || calledIndex == takerIndex) {
                        // Solo: taker gets 4x score, others lose 1x
                        scores[takerUuid] = (scores[takerUuid] ?: 0) + (s * 4)
                        players.forEachIndexed { index, p ->
                            if (index != takerIndex) {
                                scores[p.id] = (scores[p.id] ?: 0) - s
                            }
                        }
                    } else {
                        // With partner: taker gets 2x, partner gets 1x, others lose 1x
                        val partnerUuid = players.getOrNull(calledIndex)?.id ?: takerUuid
                        scores[takerUuid] = (scores[takerUuid] ?: 0) + (s * 2)
                        scores[partnerUuid] = (scores[partnerUuid] ?: 0) + s
                        players.forEachIndexed { index, p ->
                            if (index != takerIndex && index != calledIndex) {
                                scores[p.id] = (scores[p.id] ?: 0) - s
                            }
                        }
                    }
                }

                else -> {
                    // 3 or 4 player game: taker vs all
                    val multiplier = playerCount - 1
                    scores[takerUuid] = (scores[takerUuid] ?: 0) + (s * multiplier)
                    players.forEachIndexed { index, p ->
                        if (index != takerIndex) {
                            scores[p.id] = (scores[p.id] ?: 0) - s
                        }
                    }
                }
            }
        }
        return scores
    }
}

data class TarotScoreResult(
    val baseScore: Int,
    val bonus: Int,
    val totalScore: Int,
    val isWon: Boolean,
    val pointsScored: Int,
    val pointsNeeded: Int
)
