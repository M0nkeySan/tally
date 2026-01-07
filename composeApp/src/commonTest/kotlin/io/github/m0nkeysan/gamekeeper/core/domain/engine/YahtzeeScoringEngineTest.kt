package io.github.m0nkeysan.gamekeeper.core.domain.engine

import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeCategory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class YahtzeeScoringEngineTest {
    
    private val engine = YahtzeeScoringEngine()
    
    @Test
    fun `calculate Aces score`() {
        val result = engine.calculateScore(listOf(1, 2, 1, 4, 1), YahtzeeCategory.ACES)
        assertEquals(3, result.score)
    }
    
    @Test
    fun `calculate Sixes score`() {
        val result = engine.calculateScore(listOf(6, 6, 2, 6, 5), YahtzeeCategory.SIXES)
        assertEquals(18, result.score)
    }
    
    @Test
    fun `calculate Three of a Kind`() {
        val result = engine.calculateScore(listOf(3, 3, 3, 2, 5), YahtzeeCategory.THREE_OF_KIND)
        assertEquals(16, result.score)
    }
    
    @Test
    fun `calculate Three of a Kind when not present`() {
        val result = engine.calculateScore(listOf(1, 2, 3, 4, 5), YahtzeeCategory.THREE_OF_KIND)
        assertEquals(0, result.score)
    }
    
    @Test
    fun `calculate Four of a Kind`() {
        val result = engine.calculateScore(listOf(4, 4, 4, 4, 2), YahtzeeCategory.FOUR_OF_KIND)
        assertEquals(18, result.score)
    }
    
    @Test
    fun `calculate Full House`() {
        val result = engine.calculateScore(listOf(3, 3, 3, 5, 5), YahtzeeCategory.FULL_HOUSE)
        assertEquals(25, result.score)
    }
    
    @Test
    fun `calculate Full House when not present`() {
        val result = engine.calculateScore(listOf(1, 2, 3, 4, 5), YahtzeeCategory.FULL_HOUSE)
        assertEquals(0, result.score)
    }
    
    @Test
    fun `calculate Small Straight`() {
        val result = engine.calculateScore(listOf(1, 2, 3, 4, 6), YahtzeeCategory.SMALL_STRAIGHT)
        assertEquals(30, result.score)
    }
    
    @Test
    fun `calculate Small Straight at end`() {
        val result = engine.calculateScore(listOf(3, 4, 5, 6, 6), YahtzeeCategory.SMALL_STRAIGHT)
        assertEquals(30, result.score)
    }
    
    @Test
    fun `calculate Small Straight when not present`() {
        val result = engine.calculateScore(listOf(1, 2, 4, 5, 6), YahtzeeCategory.SMALL_STRAIGHT)
        assertEquals(0, result.score)
    }
    
    @Test
    fun `calculate Large Straight`() {
        val result = engine.calculateScore(listOf(1, 2, 3, 4, 5), YahtzeeCategory.LARGE_STRAIGHT)
        assertEquals(40, result.score)
    }
    
    @Test
    fun `calculate Large Straight at end`() {
        val result = engine.calculateScore(listOf(2, 3, 4, 5, 6), YahtzeeCategory.LARGE_STRAIGHT)
        assertEquals(40, result.score)
    }
    
    @Test
    fun `calculate Large Straight when not present`() {
        val result = engine.calculateScore(listOf(1, 2, 3, 4, 6), YahtzeeCategory.LARGE_STRAIGHT)
        assertEquals(0, result.score)
    }
    
    @Test
    fun `calculate Yahtzee`() {
        val result = engine.calculateScore(listOf(5, 5, 5, 5, 5), YahtzeeCategory.YAHTZEE)
        assertEquals(50, result.score)
    }
    
    @Test
    fun `calculate Yahtzee when not present`() {
        val result = engine.calculateScore(listOf(5, 5, 5, 5, 4), YahtzeeCategory.YAHTZEE)
        assertEquals(0, result.score)
    }
    
    @Test
    fun `calculate Chance`() {
        val result = engine.calculateScore(listOf(1, 2, 3, 4, 5), YahtzeeCategory.CHANCE)
        assertEquals(15, result.score)
    }
    
    @Test
    fun `calculate upper section bonus when threshold met`() {
        val scores = mapOf(
            YahtzeeCategory.ACES to 6,
            YahtzeeCategory.TWOS to 12,
            YahtzeeCategory.THREES to 18,
            YahtzeeCategory.FOURS to 24,
            YahtzeeCategory.FIVES to 30,
            YahtzeeCategory.SIXES to 36
        )
        
        val bonus = engine.calculateUpperSectionBonus(scores)
        assertEquals(35, bonus)
    }
    
    @Test
    fun `calculate upper section bonus when threshold not met`() {
        val scores = mapOf(
            YahtzeeCategory.ACES to 3,
            YahtzeeCategory.TWOS to 6,
            YahtzeeCategory.THREES to 9,
            YahtzeeCategory.FOURS to 12,
            YahtzeeCategory.FIVES to 15,
            YahtzeeCategory.SIXES to 18
        )
        
        val bonus = engine.calculateUpperSectionBonus(scores)
        assertEquals(0, bonus)
    }
    
    @Test
    fun `calculate upper section bonus exactly at threshold`() {
        val scores = mapOf(
            YahtzeeCategory.ACES to 3,
            YahtzeeCategory.TWOS to 12,
            YahtzeeCategory.THREES to 9,
            YahtzeeCategory.FOURS to 20,
            YahtzeeCategory.FIVES to 10,
            YahtzeeCategory.SIXES to 9
        )
        
        val bonus = engine.calculateUpperSectionBonus(scores)
        assertEquals(35, bonus)
    }
    
    @Test
    fun `calculate total score with bonus`() {
        val upperScores = mapOf(
            YahtzeeCategory.ACES to 6,
            YahtzeeCategory.TWOS to 12,
            YahtzeeCategory.THREES to 18,
            YahtzeeCategory.FOURS to 24,
            YahtzeeCategory.FIVES to 30,
            YahtzeeCategory.SIXES to 36
        )
        
        val lowerScores = mapOf(
            YahtzeeCategory.THREE_OF_KIND to 25,
            YahtzeeCategory.FOUR_OF_KIND to 30,
            YahtzeeCategory.FULL_HOUSE to 25,
            YahtzeeCategory.SMALL_STRAIGHT to 30,
            YahtzeeCategory.LARGE_STRAIGHT to 40,
            YahtzeeCategory.YAHTZEE to 50,
            YahtzeeCategory.CHANCE to 20
        )
        
        val allScores = upperScores + lowerScores
        val total = engine.calculateTotalScore(allScores)
        
        assertEquals(126, allScores.values.sum())
        assertEquals(35, engine.calculateUpperSectionBonus(allScores))
        assertEquals(161, total)
    }
    
    @Test
    fun `calculate total score without bonus`() {
        val scores = mapOf(
            YahtzeeCategory.ACES to 3,
            YahtzeeCategory.TWOS to 6,
            YahtzeeCategory.THREES to 9,
            YahtzeeCategory.FOURS to 12,
            YahtzeeCategory.FIVES to 15,
            YahtzeeCategory.SIXES to 18,
            YahtzeeCategory.CHANCE to 15
        )
        
        val total = engine.calculateTotalScore(scores)
        assertEquals(78, total)
    }
    
    @Test
    fun `calculate total score with Yahtzee bonuses`() {
        val scores = mapOf(
            YahtzeeCategory.ACES to 5,
            YahtzeeCategory.TWOS to 10,
            YahtzeeCategory.THREES to 15,
            YahtzeeCategory.FOURS to 20,
            YahtzeeCategory.FIVES to 25,
            YahtzeeCategory.SIXES to 30,
            YahtzeeCategory.YAHTZEE to 50
        )
        
        val yahtzeeBonuses = 100
        val total = engine.calculateTotalScore(scores, yahtzeeBonuses)
        
        assertEquals(105, scores.values.sum())
        assertEquals(35, engine.calculateUpperSectionBonus(scores))
        assertEquals(240, total)
    }
}
