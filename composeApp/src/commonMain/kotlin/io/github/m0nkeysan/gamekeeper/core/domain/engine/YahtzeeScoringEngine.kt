package io.github.m0nkeysan.gamekeeper.core.domain.engine

import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeCategory

class YahtzeeScoringEngine {
    
    fun calculateScore(
        dice: List<Int>,
        category: YahtzeeCategory
    ): YahtzeeScoreResult {
        val value = when (category) {
            YahtzeeCategory.ACES -> dice.count { it == 1 }
            YahtzeeCategory.TWOS -> dice.count { it == 2 } * 2
            YahtzeeCategory.THREES -> dice.count { it == 3 } * 3
            YahtzeeCategory.FOURS -> dice.count { it == 4 } * 4
            YahtzeeCategory.FIVES -> dice.count { it == 5 } * 5
            YahtzeeCategory.SIXES -> dice.count { it == 6 } * 6
            YahtzeeCategory.THREE_OF_KIND -> if (hasNOfAKind(dice, 3)) dice.sum() else 0
            YahtzeeCategory.FOUR_OF_KIND -> if (hasNOfAKind(dice, 4)) dice.sum() else 0
            YahtzeeCategory.FULL_HOUSE -> if (isFullHouse(dice)) 25 else 0
            YahtzeeCategory.SMALL_STRAIGHT -> if (isSmallStraight(dice)) 30 else 0
            YahtzeeCategory.LARGE_STRAIGHT -> if (isLargeStraight(dice)) 40 else 0
            YahtzeeCategory.YAHTZEE -> if (isYahtzee(dice)) 50 else 0
            YahtzeeCategory.CHANCE -> dice.sum()
        }
        
        return YahtzeeScoreResult(
            category = category,
            dice = dice,
            score = value
        )
    }
    
    fun calculateUpperSectionBonus(scores: Map<YahtzeeCategory, Int>): Int {
        val upperSectionCategories = listOf(
            YahtzeeCategory.ACES,
            YahtzeeCategory.TWOS,
            YahtzeeCategory.THREES,
            YahtzeeCategory.FOURS,
            YahtzeeCategory.FIVES,
            YahtzeeCategory.SIXES
        )
        
        val total = upperSectionCategories.sumOf { scores[it] ?: 0 }
        return if (total >= 63) 35 else 0
    }
    
    fun calculateTotalScore(
        scores: Map<YahtzeeCategory, Int>,
        yahtzeeBonuses: Int = 0
    ): Int {
        val upperSection = scores.filterKeys { it.isUpperSection() }.values.sum()
        val lowerSection = scores.filterKeys { it.isLowerSection() }.values.sum()
        val bonus = calculateUpperSectionBonus(scores)
        
        return upperSection + bonus + lowerSection + yahtzeeBonuses
    }
    
    private fun hasNOfAKind(dice: List<Int>, n: Int): Boolean {
        val counts = dice.groupBy { it }.mapValues { it.value.size }
        return counts.values.any { it >= n }
    }
    
    private fun isFullHouse(dice: List<Int>): Boolean {
        val counts = dice.groupBy { it }.mapValues { it.value.size }
        return counts.values.contains(3) && counts.values.contains(2)
    }
    
    private fun isSmallStraight(dice: List<Int>): Boolean {
        val sorted = dice.distinct().sorted()
        val sequences = listOf(
            listOf(1, 2, 3, 4),
            listOf(2, 3, 4, 5),
            listOf(3, 4, 5, 6)
        )
        
        return sequences.any { sequence ->
            sequence.all { it in sorted }
        }
    }
    
    private fun isLargeStraight(dice: List<Int>): Boolean {
        val sorted = dice.distinct().sorted()
        return sorted == listOf(1, 2, 3, 4, 5) || sorted == listOf(2, 3, 4, 5, 6)
    }
    
    private fun isYahtzee(dice: List<Int>): Boolean {
        return dice.all { it == dice[0] }
    }
}

data class YahtzeeScoreResult(
    val category: YahtzeeCategory,
    val dice: List<Int>,
    val score: Int
)
