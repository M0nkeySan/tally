package io.github.m0nkeysan.tally.core.domain.engine

import io.github.m0nkeysan.tally.core.model.PoigneeLevel
import io.github.m0nkeysan.tally.core.model.TarotBid
import io.github.m0nkeysan.tally.core.model.ChelemType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TarotScoringEngineTest {
    
    private val engine = TarotScoringEngine()
    
    @Test
    fun `calculate simple Prise win with 1 bout`() {
        val result = engine.calculateScore(
            bid = TarotBid.PRISE,
            bouts = 1,
            pointsScored = 51,
            hasPetitAuBout = false,
            hasPoignee = false,
            poigneeLevel = null,
            chelem = ChelemType.NONE
        )
        
        assertEquals(51, result.pointsScored)
        assertEquals(51, result.pointsNeeded)
        assertEquals(25, result.baseScore)
        assertEquals(0, result.bonus)
        assertEquals(25, result.totalScore)
        assertTrue(result.isWon)
    }
    
    @Test
    fun `calculate Prise loss with 0 bouts`() {
        val result = engine.calculateScore(
            bid = TarotBid.PRISE,
            bouts = 0,
            pointsScored = 40,
            hasPetitAuBout = false,
            hasPoignee = false,
            poigneeLevel = null,
            chelem = ChelemType.NONE
        )
        
        assertEquals(40, result.pointsScored)
        assertEquals(56, result.pointsNeeded)
        assertEquals(-41, result.totalScore) // (25 + 16) * 1
        assertFalse(result.isWon)
    }
    
    @Test
    fun `calculate Garde with Petit au bout bonus`() {
        val result = engine.calculateScore(
            bid = TarotBid.GARDE,
            bouts = 2,
            pointsScored = 51,
            hasPetitAuBout = true,
            hasPoignee = false,
            poigneeLevel = null,
            chelem = ChelemType.NONE
        )
        
        assertEquals(51, result.pointsScored)
        assertEquals(41, result.pointsNeeded)
        // (25 + 10) * 2 = 70. Petit: 10 * 2 = 20. Total = 90
        assertEquals(90, result.totalScore)
        assertTrue(result.isWon)
    }
    
    @Test
    fun `calculate Garde Sans with Poignee Simple`() {
        val result = engine.calculateScore(
            bid = TarotBid.GARDE_SANS,
            bouts = 3,
            pointsScored = 40,
            hasPetitAuBout = false,
            hasPoignee = true,
            poigneeLevel = PoigneeLevel.SIMPLE,
            chelem = ChelemType.NONE
        )
        
        assertEquals(40, result.pointsScored)
        assertEquals(36, result.pointsNeeded)
        // (25 + 4) * 4 = 116. Poignee: 20. Total = 136
        assertEquals(136, result.totalScore)
        assertTrue(result.isWon)
    }
    
    @Test
    fun `calculate with Chelem bonus`() {
        val result = engine.calculateScore(
            bid = TarotBid.PRISE,
            bouts = 3,
            pointsScored = 91,
            hasPetitAuBout = false,
            hasPoignee = false,
            poigneeLevel = null,
            chelem = ChelemType.ANNOUNCED_SUCCESS
        )
        
        assertEquals(91, result.pointsScored)
        // (25 + 55) * 1 = 80. Chelem: 400. Total = 480
        assertEquals(480, result.totalScore)
    }
}
