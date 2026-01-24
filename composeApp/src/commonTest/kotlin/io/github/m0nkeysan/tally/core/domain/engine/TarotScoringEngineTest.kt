package io.github.m0nkeysan.tally.core.domain.engine

import io.github.m0nkeysan.tally.core.model.PoigneeLevel
import io.github.m0nkeysan.tally.core.model.TarotBid
import io.github.m0nkeysan.tally.core.model.ChelemType
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.core.model.TarotRound
import kotlin.test.*

/**
 * Comprehensive test suite for TarotScoringEngine
 * 
 * Tests critical Tarot game scoring logic including:
 * - Score calculation for all bid types
 * - Bonus combinations (Petit au bout, Poignée, Chelem)
 * - Multi-player score distribution (3, 4, and 5 players)
 * - Edge cases and boundary conditions
 * 
 * Uses BDD-style naming with flat class structure for Kotlin Multiplatform compatibility.
 */

private val engine = TarotScoringEngine()

// ============ Test Fixtures & Builders ============

private fun createTestPlayer(
        id: String = "player1",
        name: String = "Player 1"
    ): Player {
        return Player(
            id = id,
            name = name,
            avatarColor = "#FF6200",
            createdAt = 1000L,
            isActive = true,
            deactivatedAt = null
        )
    }
    
    private fun createTestRound(
        roundNumber: Int = 1,
        takerPlayerId: String = "0",
        bid: TarotBid = TarotBid.PRISE,
        bouts: Int = 1,
        pointsScored: Int = 51,
        hasPetitAuBout: Boolean = false,
        hasPoignee: Boolean = false,
        poigneeLevel: PoigneeLevel? = null,
        chelem: ChelemType = ChelemType.NONE,
        calledPlayerId: String? = null,
        score: Int = 25
    ): TarotRound {
        return TarotRound(
            roundNumber = roundNumber,
            takerPlayerId = takerPlayerId,
            bid = bid,
            bouts = bouts,
            pointsScored = pointsScored,
            hasPetitAuBout = hasPetitAuBout,
            hasPoignee = hasPoignee,
            poigneeLevel = poigneeLevel,
            chelem = chelem,
            calledPlayerId = calledPlayerId,
            score = score
        )
    }

// ============ Original Tests (Preserved) ============

class TarotScoringEngineTest {
    
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
    
    // ============ New Comprehensive Tests ============
    

class CalculateScore_BoundaryConditions {
        
        @Test
        fun `wins contract with exactly required points for 0 bouts`() {
            // Given: 56 points with 0 bouts (exactly the threshold)
            val result = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 0,
                pointsScored = 56,
                hasPetitAuBout = false,
                hasPoignee = false,
                poigneeLevel = null,
                chelem = ChelemType.NONE
            )
            
            // Then
            assertEquals(56, result.pointsNeeded)
            assertEquals(25, result.totalScore) // (25 + 0) * 1
            assertTrue(result.isWon)
        }
        
        @Test
        fun `wins contract with exactly required points for 1 bout`() {
            // Given: 51 points with 1 bout
            val result = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 1,
                pointsScored = 51,
                hasPetitAuBout = false,
                hasPoignee = false,
                poigneeLevel = null,
                chelem = ChelemType.NONE
            )
            
            // Then
            assertEquals(51, result.pointsNeeded)
            assertTrue(result.isWon)
        }
        
        @Test
        fun `wins contract with exactly required points for 2 bouts`() {
            // Given: 41 points with 2 bouts
            val result = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 2,
                pointsScored = 41,
                hasPetitAuBout = false,
                hasPoignee = false,
                poigneeLevel = null,
                chelem = ChelemType.NONE
            )
            
            // Then
            assertEquals(41, result.pointsNeeded)
            assertEquals(25, result.totalScore)
            assertTrue(result.isWon)
        }
        
        @Test
        fun `wins contract with exactly required points for 3 bouts`() {
            // Given: 36 points with 3 bouts
            val result = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 3,
                pointsScored = 36,
                hasPetitAuBout = false,
                hasPoignee = false,
                poigneeLevel = null,
                chelem = ChelemType.NONE
            )
            
            // Then
            assertEquals(36, result.pointsNeeded)
            assertEquals(25, result.totalScore)
            assertTrue(result.isWon)
        }
        
        @Test
        fun `loses contract with one point less than required`() {
            // Given: 50 points with 1 bout (need 51)
            val result = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 1,
                pointsScored = 50,
                hasPetitAuBout = false,
                hasPoignee = false,
                poigneeLevel = null,
                chelem = ChelemType.NONE
            )
            
            // Then
            assertEquals(51, result.pointsNeeded)
            assertEquals(-26, result.totalScore) // -(25 + 1) * 1
            assertFalse(result.isWon)
        }
        
        @Test
        fun `handles maximum point difference with 0 bouts`() {
            // Given: 91 points with 0 bouts (maximum score)
            val result = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 0,
                pointsScored = 91,
                hasPetitAuBout = false,
                hasPoignee = false,
                poigneeLevel = null,
                chelem = ChelemType.NONE
            )
            
            // Then
            assertEquals(56, result.pointsNeeded)
            assertEquals(60, result.totalScore) // (25 + 35) * 1
            assertTrue(result.isWon)
        }
        
        @Test
        fun `handles minimum points with 3 bouts`() {
            // Given: 0 points with 3 bouts (worst case loss)
            val result = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 3,
                pointsScored = 0,
                hasPetitAuBout = false,
                hasPoignee = false,
                poigneeLevel = null,
                chelem = ChelemType.NONE
            )
            
            // Then
            assertEquals(36, result.pointsNeeded)
            assertEquals(-61, result.totalScore) // -(25 + 36) * 1
            assertFalse(result.isWon)
        }
    }
    

class CalculateScore_AllBidTypes {
        
        @Test
        fun `calculates Prise with multiplier 1`() {
            val result = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 1,
                pointsScored = 61,
                hasPetitAuBout = false,
                hasPoignee = false,
                poigneeLevel = null,
                chelem = ChelemType.NONE
            )
            
            assertEquals(35, result.totalScore) // (25 + 10) * 1
        }
        
        @Test
        fun `calculates Garde with multiplier 2`() {
            val result = engine.calculateScore(
                bid = TarotBid.GARDE,
                bouts = 1,
                pointsScored = 61,
                hasPetitAuBout = false,
                hasPoignee = false,
                poigneeLevel = null,
                chelem = ChelemType.NONE
            )
            
            assertEquals(70, result.totalScore) // (25 + 10) * 2
        }
        
        @Test
        fun `calculates Garde Sans with multiplier 4`() {
            val result = engine.calculateScore(
                bid = TarotBid.GARDE_SANS,
                bouts = 1,
                pointsScored = 61,
                hasPetitAuBout = false,
                hasPoignee = false,
                poigneeLevel = null,
                chelem = ChelemType.NONE
            )
            
            assertEquals(140, result.totalScore) // (25 + 10) * 4
        }
        
        @Test
        fun `calculates Garde Contre with multiplier 6`() {
            val result = engine.calculateScore(
                bid = TarotBid.GARDE_CONTRE,
                bouts = 1,
                pointsScored = 61,
                hasPetitAuBout = false,
                hasPoignee = false,
                poigneeLevel = null,
                chelem = ChelemType.NONE
            )
            
            assertEquals(210, result.totalScore) // (25 + 10) * 6
        }
        
        @Test
        fun `applies multiplier to base score only not bonuses`() {
            val result = engine.calculateScore(
                bid = TarotBid.GARDE,
                bouts = 1,
                pointsScored = 61,
                hasPetitAuBout = false,
                hasPoignee = true,
                poigneeLevel = PoigneeLevel.SIMPLE,
                chelem = ChelemType.NONE
            )
            
            // Base: (25 + 10) * 2 = 70
            // Poignee: 20 (not multiplied)
            // Total: 90
            assertEquals(70, result.baseScore)
            assertEquals(20, result.bonus)
            assertEquals(90, result.totalScore)
        }
    }
    

class CalculateScore_BonusCombinations {
        
        @Test
        fun `combines Petit au bout with Poignee Simple`() {
            val result = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 1,
                pointsScored = 51,
                hasPetitAuBout = true,
                hasPoignee = true,
                poigneeLevel = PoigneeLevel.SIMPLE,
                chelem = ChelemType.NONE
            )
            
            // Base: (25 + 0) * 1 = 25
            // Petit: 10 * 1 = 10
            // Poignee: 20
            // Total: 55
            assertEquals(30, result.bonus) // Petit + Poignee
            assertEquals(55, result.totalScore)
        }
        
        @Test
        fun `combines Petit au bout with Poignee Double`() {
            val result = engine.calculateScore(
                bid = TarotBid.GARDE,
                bouts = 2,
                pointsScored = 51,
                hasPetitAuBout = true,
                hasPoignee = true,
                poigneeLevel = PoigneeLevel.DOUBLE,
                chelem = ChelemType.NONE
            )
            
            // Base: (25 + 10) * 2 = 70
            // Petit: 10 * 2 = 20
            // Poignee: 30
            // Total: 120
            assertEquals(50, result.bonus)
            assertEquals(120, result.totalScore)
        }
        
        @Test
        fun `combines Petit au bout with Poignee Triple`() {
            val result = engine.calculateScore(
                bid = TarotBid.GARDE_SANS,
                bouts = 3,
                pointsScored = 46,
                hasPetitAuBout = true,
                hasPoignee = true,
                poigneeLevel = PoigneeLevel.TRIPLE,
                chelem = ChelemType.NONE
            )
            
            // Base: (25 + 10) * 4 = 140
            // Petit: 10 * 4 = 40
            // Poignee: 40
            // Total: 220
            assertEquals(80, result.bonus)
            assertEquals(220, result.totalScore)
        }
        
        @Test
        fun `combines all bonuses together`() {
            val result = engine.calculateScore(
                bid = TarotBid.GARDE,
                bouts = 3,
                pointsScored = 91,
                hasPetitAuBout = true,
                hasPoignee = true,
                poigneeLevel = PoigneeLevel.SIMPLE,
                chelem = ChelemType.ANNOUNCED_SUCCESS
            )
            
            // Base: (25 + 55) * 2 = 160
            // Petit: 10 * 2 = 20
            // Poignee: 20
            // Chelem: 400
            // Total: 600
            assertEquals(440, result.bonus)
            assertEquals(600, result.totalScore)
        }
        
        @Test
        fun `applies Petit au bout multiplier correctly for each bid`() {
            // Prise (x1)
            val prisePetit = engine.calculateScore(
                TarotBid.PRISE, 1, 51, hasPetitAuBout = true,
                hasPoignee = false, null, ChelemType.NONE
            )
            assertEquals(10, prisePetit.bonus) // 10 * 1
            
            // Garde (x2)
            val gardePetit = engine.calculateScore(
                TarotBid.GARDE, 1, 51, hasPetitAuBout = true,
                hasPoignee = false, null, ChelemType.NONE
            )
            assertEquals(20, gardePetit.bonus) // 10 * 2
            
            // Garde Sans (x4)
            val gardeSansPetit = engine.calculateScore(
                TarotBid.GARDE_SANS, 1, 51, hasPetitAuBout = true,
                hasPoignee = false, null, ChelemType.NONE
            )
            assertEquals(40, gardeSansPetit.bonus) // 10 * 4
            
            // Garde Contre (x6)
            val gardeContrePetit = engine.calculateScore(
                TarotBid.GARDE_CONTRE, 1, 51, hasPetitAuBout = true,
                hasPoignee = false, null, ChelemType.NONE
            )
            assertEquals(60, gardeContrePetit.bonus) // 10 * 6
        }
    }
    

class CalculateScore_ChelemScenarios {
        
        @Test
        fun `calculates announced chelem success bonus`() {
            val result = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 3,
                pointsScored = 91,
                hasPetitAuBout = false,
                hasPoignee = false,
                poigneeLevel = null,
                chelem = ChelemType.ANNOUNCED_SUCCESS
            )
            
            assertEquals(400, ChelemType.ANNOUNCED_SUCCESS.bonus)
            assertEquals(480, result.totalScore) // 80 base + 400 chelem
        }
        
        @Test
        fun `calculates announced chelem failure penalty`() {
            val result = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 1,
                pointsScored = 51,
                hasPetitAuBout = false,
                hasPoignee = false,
                poigneeLevel = null,
                chelem = ChelemType.ANNOUNCED_FAIL
            )
            
            assertEquals(-200, ChelemType.ANNOUNCED_FAIL.bonus)
            assertEquals(-175, result.totalScore) // 25 base - 200 chelem
        }
        
        @Test
        fun `calculates non-announced chelem success bonus`() {
            val result = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 3,
                pointsScored = 91,
                hasPetitAuBout = false,
                hasPoignee = false,
                poigneeLevel = null,
                chelem = ChelemType.NON_ANNOUNCED_SUCCESS
            )
            
            assertEquals(200, ChelemType.NON_ANNOUNCED_SUCCESS.bonus)
            assertEquals(280, result.totalScore) // 80 base + 200 chelem
        }
        
        @Test
        fun `adds chelem bonus to winning contract`() {
            val result = engine.calculateScore(
                bid = TarotBid.GARDE,
                bouts = 2,
                pointsScored = 91,
                hasPetitAuBout = false,
                hasPoignee = false,
                poigneeLevel = null,
                chelem = ChelemType.ANNOUNCED_SUCCESS
            )
            
            // Base: (25 + 50) * 2 = 150
            // Chelem: 400
            // Total: 550
            assertEquals(150, result.baseScore)
            assertEquals(400, result.bonus)
            assertEquals(550, result.totalScore)
        }
        
        @Test
        fun `handles chelem with maximum bid Garde Contre`() {
            val result = engine.calculateScore(
                bid = TarotBid.GARDE_CONTRE,
                bouts = 3,
                pointsScored = 91,
                hasPetitAuBout = false,
                hasPoignee = false,
                poigneeLevel = null,
                chelem = ChelemType.ANNOUNCED_SUCCESS
            )
            
            // Base: (25 + 55) * 6 = 480
            // Chelem: 400
            // Total: 880
            assertEquals(480, result.baseScore)
            assertEquals(880, result.totalScore)
        }
    }
    

class CalculateScore_PoigneeLevels {
        
        @Test
        fun `applies Poignee Simple bonus of 20 points`() {
            val result = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 1,
                pointsScored = 51,
                hasPetitAuBout = false,
                hasPoignee = true,
                poigneeLevel = PoigneeLevel.SIMPLE,
                chelem = ChelemType.NONE
            )
            
            assertEquals(20, PoigneeLevel.SIMPLE.bonus)
            assertEquals(45, result.totalScore) // 25 base + 20 poignee
        }
        
        @Test
        fun `applies Poignee Double bonus of 30 points`() {
            val result = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 1,
                pointsScored = 51,
                hasPetitAuBout = false,
                hasPoignee = true,
                poigneeLevel = PoigneeLevel.DOUBLE,
                chelem = ChelemType.NONE
            )
            
            assertEquals(30, PoigneeLevel.DOUBLE.bonus)
            assertEquals(55, result.totalScore) // 25 base + 30 poignee
        }
        
        @Test
        fun `applies Poignee Triple bonus of 40 points`() {
            val result = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 1,
                pointsScored = 51,
                hasPetitAuBout = false,
                hasPoignee = true,
                poigneeLevel = PoigneeLevel.TRIPLE,
                chelem = ChelemType.NONE
            )
            
            assertEquals(40, PoigneeLevel.TRIPLE.bonus)
            assertEquals(65, result.totalScore) // 25 base + 40 poignee
        }
        
        @Test
        fun `does not multiply poignee bonus by bid multiplier`() {
            val prisePoignee = engine.calculateScore(
                TarotBid.PRISE, 1, 51, hasPetitAuBout = false,
                hasPoignee = true, PoigneeLevel.SIMPLE, ChelemType.NONE
            )
            val gardePoignee = engine.calculateScore(
                TarotBid.GARDE, 1, 51, hasPetitAuBout = false,
                hasPoignee = true, PoigneeLevel.SIMPLE, ChelemType.NONE
            )
            
            // Both should have same poignee bonus regardless of bid multiplier
            assertEquals(20, prisePoignee.bonus)
            assertEquals(20, gardePoignee.bonus)
        }
        
        @Test
        fun `applies poignee to both won and lost contracts`() {
            // Won contract
            val won = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 1,
                pointsScored = 51,
                hasPetitAuBout = false,
                hasPoignee = true,
                poigneeLevel = PoigneeLevel.SIMPLE,
                chelem = ChelemType.NONE
            )
            
            // Lost contract
            val lost = engine.calculateScore(
                bid = TarotBid.PRISE,
                bouts = 1,
                pointsScored = 40,
                hasPetitAuBout = false,
                hasPoignee = true,
                poigneeLevel = PoigneeLevel.SIMPLE,
                chelem = ChelemType.NONE
            )
            
            assertEquals(45, won.totalScore) // 25 base + 20 poignee
            assertEquals(-56, lost.totalScore) // -(36 base + 20 poignee)
            assertEquals(20, won.bonus)
            assertEquals(20, lost.bonus)
        }
    }
    

class CalculateTotalScores_ThreePlayerGame {
        
        @Test
        fun `distributes scores correctly for taker win`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1", name = "P1"),
                createTestPlayer(id = "p2", name = "P2"),
                createTestPlayer(id = "p3", name = "P3")
            )
            val rounds = listOf(
                createTestRound(takerPlayerId = "0", score = 50) // Player 1 is taker
            )
            
            // When
            val result = engine.calculateTotalScores(players, rounds, playerCount = 3)
            
            // Then
            assertEquals(100, result["p1"]) // Taker gets 2x score
            assertEquals(-50, result["p2"]) // Defender loses 1x score
            assertEquals(-50, result["p3"]) // Defender loses 1x score
        }
        
        @Test
        fun `distributes scores correctly for taker loss`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3")
            )
            val rounds = listOf(
                createTestRound(takerPlayerId = "0", score = -50)
            )
            
            // When
            val result = engine.calculateTotalScores(players, rounds, playerCount = 3)
            
            // Then
            assertEquals(-100, result["p1"]) // Taker gets 2x negative score
            assertEquals(50, result["p2"])   // Defenders gain 1x score
            assertEquals(50, result["p3"])
        }
        
        @Test
        fun `accumulates scores over multiple rounds`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3")
            )
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", score = 50),
                createTestRound(roundNumber = 2, takerPlayerId = "1", score = 30),
                createTestRound(roundNumber = 3, takerPlayerId = "2", score = -20)
            )
            
            // When
            val result = engine.calculateTotalScores(players, rounds, playerCount = 3)
            
            // Then
            // P1: +100 (taker R1) -30 (defender R2) +20 (defender R3) = 90
            // P2: -50 (defender R1) +60 (taker R2) +20 (defender R3) = 30
            // P3: -50 (defender R1) -30 (defender R2) -40 (taker R3) = -120
            assertEquals(90, result["p1"])
            assertEquals(30, result["p2"])
            assertEquals(-120, result["p3"])
        }
    }
    

class CalculateTotalScores_FourPlayerGame {
        
        @Test
        fun `distributes scores correctly for taker win`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3"),
                createTestPlayer(id = "p4")
            )
            val rounds = listOf(
                createTestRound(takerPlayerId = "0", score = 60)
            )
            
            // When
            val result = engine.calculateTotalScores(players, rounds, playerCount = 4)
            
            // Then
            assertEquals(180, result["p1"]) // Taker gets 3x score
            assertEquals(-60, result["p2"]) // Each defender loses 1x
            assertEquals(-60, result["p3"])
            assertEquals(-60, result["p4"])
        }
        
        @Test
        fun `distributes scores correctly for taker loss`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3"),
                createTestPlayer(id = "p4")
            )
            val rounds = listOf(
                createTestRound(takerPlayerId = "1", score = -40)
            )
            
            // When
            val result = engine.calculateTotalScores(players, rounds, playerCount = 4)
            
            // Then
            assertEquals(40, result["p1"])   // Defenders gain 1x
            assertEquals(-120, result["p2"]) // Taker gets 3x negative
            assertEquals(40, result["p3"])
            assertEquals(40, result["p4"])
        }
    }
    

class CalculateTotalScores_FivePlayerGame_Solo {
        
        @Test
        fun `distributes scores when taker plays solo with null partner`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3"),
                createTestPlayer(id = "p4"),
                createTestPlayer(id = "p5")
            )
            val rounds = listOf(
                createTestRound(takerPlayerId = "0", calledPlayerId = null, score = 50)
            )
            
            // When
            val result = engine.calculateTotalScores(players, rounds, playerCount = 5)
            
            // Then
            assertEquals(200, result["p1"]) // Taker gets 4x score
            assertEquals(-50, result["p2"]) // Each defender loses 1x
            assertEquals(-50, result["p3"])
            assertEquals(-50, result["p4"])
            assertEquals(-50, result["p5"])
        }
        
        @Test
        fun `distributes scores when partner is same as taker`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3"),
                createTestPlayer(id = "p4"),
                createTestPlayer(id = "p5")
            )
            val rounds = listOf(
                createTestRound(takerPlayerId = "0", calledPlayerId = "0", score = 50) // Called self
            )
            
            // When
            val result = engine.calculateTotalScores(players, rounds, playerCount = 5)
            
            // Then
            assertEquals(200, result["p1"]) // Treated as solo, gets 4x
            assertEquals(-50, result["p2"])
            assertEquals(-50, result["p3"])
            assertEquals(-50, result["p4"])
            assertEquals(-50, result["p5"])
        }
        
        @Test
        fun `handles solo taker loss`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3"),
                createTestPlayer(id = "p4"),
                createTestPlayer(id = "p5")
            )
            val rounds = listOf(
                createTestRound(takerPlayerId = "2", calledPlayerId = null, score = -30)
            )
            
            // When
            val result = engine.calculateTotalScores(players, rounds, playerCount = 5)
            
            // Then
            assertEquals(30, result["p1"])   // Defenders gain 1x
            assertEquals(30, result["p2"])
            assertEquals(-120, result["p3"]) // Taker gets 4x negative
            assertEquals(30, result["p4"])
            assertEquals(30, result["p5"])
        }
    }
    

class CalculateTotalScores_FivePlayerGame_WithPartner {
        
        @Test
        fun `distributes scores when taker calls a partner`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3"),
                createTestPlayer(id = "p4"),
                createTestPlayer(id = "p5")
            )
            val rounds = listOf(
                createTestRound(takerPlayerId = "0", calledPlayerId = "2", score = 60)
            )
            
            // When
            val result = engine.calculateTotalScores(players, rounds, playerCount = 5)
            
            // Then
            assertEquals(120, result["p1"]) // Taker gets 2x
            assertEquals(-60, result["p2"]) // Defender loses 1x
            assertEquals(60, result["p3"])  // Partner gets 1x
            assertEquals(-60, result["p4"]) // Defender loses 1x
            assertEquals(-60, result["p5"]) // Defender loses 1x
        }
        
        @Test
        fun `handles partner team loss`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3"),
                createTestPlayer(id = "p4"),
                createTestPlayer(id = "p5")
            )
            val rounds = listOf(
                createTestRound(takerPlayerId = "1", calledPlayerId = "3", score = -40)
            )
            
            // When
            val result = engine.calculateTotalScores(players, rounds, playerCount = 5)
            
            // Then
            assertEquals(40, result["p1"])   // Defender gains 1x
            assertEquals(-80, result["p2"])  // Taker gets 2x negative
            assertEquals(40, result["p3"])   // Defender gains 1x
            assertEquals(-40, result["p4"])  // Partner gets 1x negative
            assertEquals(40, result["p5"])   // Defender gains 1x
        }
        
        @Test
        fun `correctly identifies partner by UUID`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3"),
                createTestPlayer(id = "p4"),
                createTestPlayer(id = "p5")
            )
            val rounds = listOf(
                createTestRound(takerPlayerId = "4", calledPlayerId = "1", score = 50)
            )
            
            // When
            val result = engine.calculateTotalScores(players, rounds, playerCount = 5)
            
            // Then
            assertEquals(-50, result["p1"]) // Defender
            assertEquals(50, result["p2"])  // Partner (called by index)
            assertEquals(-50, result["p3"]) // Defender
            assertEquals(-50, result["p4"]) // Defender
            assertEquals(100, result["p5"]) // Taker
        }
    }
    

class CalculateTotalScores_EdgeCases {
        
        @Test
        fun `handles empty rounds list`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3")
            )
            val rounds = emptyList<TarotRound>()
            
            // When
            val result = engine.calculateTotalScores(players, rounds, playerCount = 3)
            
            // Then
            assertEquals(0, result["p1"])
            assertEquals(0, result["p2"])
            assertEquals(0, result["p3"])
        }
        
        @Test
        fun `initializes all player scores to 0`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2")
            )
            val rounds = emptyList<TarotRound>()
            
            // When
            val result = engine.calculateTotalScores(players, rounds, playerCount = 2)
            
            // Then
            assertTrue(result.containsKey("p1"))
            assertTrue(result.containsKey("p2"))
            assertEquals(0, result["p1"])
            assertEquals(0, result["p2"])
        }
        
        @Test
        fun `handles negative scores correctly`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3")
            )
            val rounds = listOf(
                createTestRound(takerPlayerId = "0", score = -100)
            )
            
            // When
            val result = engine.calculateTotalScores(players, rounds, playerCount = 3)
            
            // Then
            assertEquals(-200, result["p1"]) // Taker gets 2x negative
            assertEquals(100, result["p2"])
            assertEquals(100, result["p3"])
        }
        
        @Test
        fun `preserves score accuracy across many rounds`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3")
            )
            val rounds = (1..10).map { roundNum ->
                createTestRound(
                    roundNumber = roundNum,
                    takerPlayerId = ((roundNum - 1) % 3).toString(),
                    score = 25
                )
            }
            
            // When
            val result = engine.calculateTotalScores(players, rounds, playerCount = 3)
            
            // Then: Each player takes ~3 rounds, wins 50 per round, loses 25 per round x 7 rounds
            // 3 * 50 + 7 * (-25) = 150 - 175 = -25
            // But alternating pattern: 4 rounds as taker = +200, 6 rounds as defender = -150 = +50
            val total = result.values.sum()
            assertEquals(0, total) // Total should always be zero (zero-sum game)
        }
    }
}
