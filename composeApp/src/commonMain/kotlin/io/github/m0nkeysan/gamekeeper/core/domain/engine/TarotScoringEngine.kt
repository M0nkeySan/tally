package io.github.m0nkeysan.gamekeeper.core.domain.engine

import io.github.m0nkeysan.gamekeeper.core.model.PoigneeLevel
import io.github.m0nkeysan.gamekeeper.core.model.TarotBid
import io.github.m0nkeysan.gamekeeper.core.model.ChelemType
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
        var score = basePoints * bid.multiplier
        
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
}

data class TarotScoreResult(
    val baseScore: Int,
    val bonus: Int,
    val totalScore: Int,
    val isWon: Boolean,
    val pointsScored: Int,
    val pointsNeeded: Int
)
