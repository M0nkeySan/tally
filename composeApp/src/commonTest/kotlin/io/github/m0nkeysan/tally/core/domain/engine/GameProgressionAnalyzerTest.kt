package io.github.m0nkeysan.tally.core.domain.engine

import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.core.model.TarotBid
import io.github.m0nkeysan.tally.core.model.TarotRound
import io.github.m0nkeysan.tally.core.model.ChelemType
import io.github.m0nkeysan.tally.core.model.PoigneeLevel
import kotlin.test.*

private val analyzer = GameProgressionAnalyzer()

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

// ============ CalculateTakerPerformance - Basic Metrics ============

class CalculateTakerPerformance_BasicMetrics {
        
        @Test
        fun `returns empty map when fewer than 3 rounds`() {
            // Given
            val players = listOf(createTestPlayer(id = "p1"))
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0"),
                createTestRound(roundNumber = 2, takerPlayerId = "0")
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            assertTrue(result.isEmpty())
        }
        
        @Test
        fun `counts taker rounds correctly for player`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3")
            )
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", score = 50),  // P1 taker
                createTestRound(roundNumber = 2, takerPlayerId = "1", score = 30),  // P2 taker
                createTestRound(roundNumber = 3, takerPlayerId = "0", score = 40),  // P1 taker
                createTestRound(roundNumber = 4, takerPlayerId = "2", score = 20)   // P3 taker
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            assertEquals(2, result["p1"]?.takerRounds) // P1 took 2 rounds
            assertEquals(1, result["p2"]?.takerRounds) // P2 took 1 round
            assertEquals(1, result["p3"]?.takerRounds) // P3 took 1 round
        }
        
        @Test
        fun `separates wins from losses`() {
            // Given
            val players = listOf(createTestPlayer(id = "p1"))
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", score = 50),   // Win
                createTestRound(roundNumber = 2, takerPlayerId = "0", score = -30),  // Loss
                createTestRound(roundNumber = 3, takerPlayerId = "0", score = 40)    // Win
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            val p1Stats = result["p1"]!!
            assertEquals(2, p1Stats.wins)
            assertEquals(1, p1Stats.losses)
        }
        
        @Test
        fun `calculates win rate percentage`() {
            // Given
            val players = listOf(createTestPlayer(id = "p1"))
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", score = 50),
                createTestRound(roundNumber = 2, takerPlayerId = "0", score = -30),
                createTestRound(roundNumber = 3, takerPlayerId = "0", score = -20),
                createTestRound(roundNumber = 4, takerPlayerId = "0", score = 40)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            val p1Stats = result["p1"]!!
            assertEquals(50.0, p1Stats.winRate) // 2 wins / 4 rounds = 50%
        }
        
        @Test
        fun `handles player who never took`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2")
            )
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", score = 50),
                createTestRound(roundNumber = 2, takerPlayerId = "0", score = 30),
                createTestRound(roundNumber = 3, takerPlayerId = "0", score = 40)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            assertEquals(3, result["p1"]?.takerRounds)
            assertEquals(0, result["p2"]?.takerRounds) // P2 never took
            assertEquals(0.0, result["p2"]?.winRate)
        }
        
        @Test
        fun `handles player with 100 percent win rate`() {
            // Given
            val players = listOf(createTestPlayer(id = "p1"))
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", score = 50),
                createTestRound(roundNumber = 2, takerPlayerId = "0", score = 30),
                createTestRound(roundNumber = 3, takerPlayerId = "0", score = 40)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            val p1Stats = result["p1"]!!
            assertEquals(3, p1Stats.wins)
            assertEquals(0, p1Stats.losses)
            assertEquals(100.0, p1Stats.winRate)
        }
        
        @Test
        fun `handles player with 0 percent win rate`() {
            // Given
            val players = listOf(createTestPlayer(id = "p1"))
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", score = -50),
                createTestRound(roundNumber = 2, takerPlayerId = "0", score = -30),
                createTestRound(roundNumber = 3, takerPlayerId = "0", score = -40)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            val p1Stats = result["p1"]!!
            assertEquals(0, p1Stats.wins)
            assertEquals(3, p1Stats.losses)
            assertEquals(0.0, p1Stats.winRate)
        }
    }
    
    // ============ CalculateTakerPerformance - Bid Statistics ============
    

class CalculateTakerPerformance_BidStatistics {
        
        @Test
        fun `identifies preferred bid as most frequently used`() {
            // Given
            val players = listOf(createTestPlayer(id = "p1"))
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", bid = TarotBid.PRISE),
                createTestRound(roundNumber = 2, takerPlayerId = "0", bid = TarotBid.GARDE),
                createTestRound(roundNumber = 3, takerPlayerId = "0", bid = TarotBid.PRISE),
                createTestRound(roundNumber = 4, takerPlayerId = "0", bid = TarotBid.PRISE)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            val p1Stats = result["p1"]!!
            assertEquals(TarotBid.PRISE, p1Stats.preferredBid)
        }
        
        @Test
        fun `creates bid distribution map`() {
            // Given
            val players = listOf(createTestPlayer(id = "p1"))
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", bid = TarotBid.PRISE),
                createTestRound(roundNumber = 2, takerPlayerId = "0", bid = TarotBid.GARDE),
                createTestRound(roundNumber = 3, takerPlayerId = "0", bid = TarotBid.GARDE),
                createTestRound(roundNumber = 4, takerPlayerId = "0", bid = TarotBid.GARDE_SANS)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            val p1Stats = result["p1"]!!
            assertEquals(1, p1Stats.bidDistribution[TarotBid.PRISE])
            assertEquals(2, p1Stats.bidDistribution[TarotBid.GARDE])
            assertEquals(1, p1Stats.bidDistribution[TarotBid.GARDE_SANS])
        }
        
        @Test
        fun `handles tie in bid frequency`() {
            // Given
            val players = listOf(createTestPlayer(id = "p1"))
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", bid = TarotBid.PRISE),
                createTestRound(roundNumber = 2, takerPlayerId = "0", bid = TarotBid.GARDE),
                createTestRound(roundNumber = 3, takerPlayerId = "0", bid = TarotBid.GARDE_SANS)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            val p1Stats = result["p1"]!!
            assertNotNull(p1Stats.preferredBid) // Should still have a preference (first max)
            assertEquals(1, p1Stats.bidDistribution[TarotBid.PRISE])
            assertEquals(1, p1Stats.bidDistribution[TarotBid.GARDE])
            assertEquals(1, p1Stats.bidDistribution[TarotBid.GARDE_SANS])
        }
        
        @Test
        fun `tracks all bid types correctly`() {
            // Given
            val players = listOf(createTestPlayer(id = "p1"))
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", bid = TarotBid.PRISE),
                createTestRound(roundNumber = 2, takerPlayerId = "0", bid = TarotBid.GARDE),
                createTestRound(roundNumber = 3, takerPlayerId = "0", bid = TarotBid.GARDE_SANS),
                createTestRound(roundNumber = 4, takerPlayerId = "0", bid = TarotBid.GARDE_CONTRE)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            val p1Stats = result["p1"]!!
            assertTrue(p1Stats.bidDistribution.containsKey(TarotBid.PRISE))
            assertTrue(p1Stats.bidDistribution.containsKey(TarotBid.GARDE))
            assertTrue(p1Stats.bidDistribution.containsKey(TarotBid.GARDE_SANS))
            assertTrue(p1Stats.bidDistribution.containsKey(TarotBid.GARDE_CONTRE))
        }
    }
    
    // ============ CalculateTakerPerformance - Score Averages ============
    

class CalculateTakerPerformance_ScoreAverages {
        
        @Test
        fun `calculates average win points correctly`() {
            // Given
            val players = listOf(createTestPlayer(id = "p1"))
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", score = 50),
                createTestRound(roundNumber = 2, takerPlayerId = "0", score = 30),
                createTestRound(roundNumber = 3, takerPlayerId = "0", score = 40)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            val p1Stats = result["p1"]!!
            assertEquals(40.0, p1Stats.avgWinPoints) // (50 + 30 + 40) / 3 = 40
        }
        
        @Test
        fun `calculates average loss points correctly`() {
            // Given
            val players = listOf(createTestPlayer(id = "p1"))
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", score = -50),
                createTestRound(roundNumber = 2, takerPlayerId = "0", score = -30),
                createTestRound(roundNumber = 3, takerPlayerId = "0", score = -40)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            val p1Stats = result["p1"]!!
            assertEquals(-40.0, p1Stats.avgLossPoints) // (-50 + -30 + -40) / 3 = -40
        }
        
        @Test
        fun `handles only wins with no losses`() {
            // Given
            val players = listOf(createTestPlayer(id = "p1"))
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", score = 50),
                createTestRound(roundNumber = 2, takerPlayerId = "0", score = 60),
                createTestRound(roundNumber = 3, takerPlayerId = "0", score = 40)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            val p1Stats = result["p1"]!!
            assertEquals(50.0, p1Stats.avgWinPoints)
            assertEquals(0.0, p1Stats.avgLossPoints) // No losses
        }
        
        @Test
        fun `handles only losses with no wins`() {
            // Given
            val players = listOf(createTestPlayer(id = "p1"))
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", score = -50),
                createTestRound(roundNumber = 2, takerPlayerId = "0", score = -60),
                createTestRound(roundNumber = 3, takerPlayerId = "0", score = -40)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            val p1Stats = result["p1"]!!
            assertEquals(0.0, p1Stats.avgWinPoints) // No wins
            assertEquals(-50.0, p1Stats.avgLossPoints)
        }
        
        @Test
        fun `sums total points gained from wins`() {
            // Given
            val players = listOf(createTestPlayer(id = "p1"))
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", score = 50),
                createTestRound(roundNumber = 2, takerPlayerId = "0", score = -30),
                createTestRound(roundNumber = 3, takerPlayerId = "0", score = 40),
                createTestRound(roundNumber = 4, takerPlayerId = "0", score = 60)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            val p1Stats = result["p1"]!!
            assertEquals(150, p1Stats.totalPointsGained) // 50 + 40 + 60 = 150
        }
        
        @Test
        fun `sums total points lost from losses`() {
            // Given
            val players = listOf(createTestPlayer(id = "p1"))
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", score = 50),
                createTestRound(roundNumber = 2, takerPlayerId = "0", score = -30),
                createTestRound(roundNumber = 3, takerPlayerId = "0", score = -20),
                createTestRound(roundNumber = 4, takerPlayerId = "0", score = 60)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            val p1Stats = result["p1"]!!
            assertEquals(-50, p1Stats.totalPointsLost) // -30 + -20 = -50
        }
    }
    
    // ============ CalculatePartnerStats Tests ============
    

class CalculatePartnerStats {
        
        @Test
        fun `returns null when not 5 player game`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3")
            )
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", calledPlayerId = "1"),
                createTestRound(roundNumber = 2, takerPlayerId = "0", calledPlayerId = "2"),
                createTestRound(roundNumber = 3, takerPlayerId = "0", calledPlayerId = "1")
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            assertNull(result["p1"]?.partnerStats)
        }
        
        @Test
        fun `returns null when player never called partner`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3"),
                createTestPlayer(id = "p4"),
                createTestPlayer(id = "p5")
            )
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", calledPlayerId = null), // Solo
                createTestRound(roundNumber = 2, takerPlayerId = "0", calledPlayerId = null), // Solo
                createTestRound(roundNumber = 3, takerPlayerId = "0", calledPlayerId = null)  // Solo
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 5)
            
            // Then
            assertNull(result["p1"]?.partnerStats)
        }
        
        @Test
        fun `calculates partner statistics for each partner`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3"),
                createTestPlayer(id = "p4"),
                createTestPlayer(id = "p5")
            )
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", calledPlayerId = "1", score = 50),
                createTestRound(roundNumber = 2, takerPlayerId = "0", calledPlayerId = "2", score = 30),
                createTestRound(roundNumber = 3, takerPlayerId = "0", calledPlayerId = "1", score = 40)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 5)
            
            // Then
            val p1Stats = result["p1"]!!
            assertNotNull(p1Stats.partnerStats)
            assertTrue(p1Stats.partnerStats!!.containsKey("p2"))
            assertTrue(p1Stats.partnerStats.containsKey("p3"))
        }
        
        @Test
        fun `counts games played with each partner`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3"),
                createTestPlayer(id = "p4"),
                createTestPlayer(id = "p5")
            )
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", calledPlayerId = "1", score = 50),
                createTestRound(roundNumber = 2, takerPlayerId = "0", calledPlayerId = "1", score = 30),
                createTestRound(roundNumber = 3, takerPlayerId = "0", calledPlayerId = "2", score = 40)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 5)
            
            // Then
            val p1Stats = result["p1"]!!
            assertEquals(2, p1Stats.partnerStats!!["p2"]?.gamesPlayed)
            assertEquals(1, p1Stats.partnerStats["p3"]?.gamesPlayed)
        }
        
        @Test
        fun `calculates win rate with each partner`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3"),
                createTestPlayer(id = "p4"),
                createTestPlayer(id = "p5")
            )
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", calledPlayerId = "1", score = 50),  // Win
                createTestRound(roundNumber = 2, takerPlayerId = "0", calledPlayerId = "1", score = -30), // Loss
                createTestRound(roundNumber = 3, takerPlayerId = "0", calledPlayerId = "1", score = 40),  // Win
                createTestRound(roundNumber = 4, takerPlayerId = "0", calledPlayerId = "1", score = 60)   // Win
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 5)
            
            // Then
            val p1Stats = result["p1"]!!
            val p2PartnerStats = p1Stats.partnerStats!!["p2"]!!
            assertEquals(3, p2PartnerStats.wins)
            assertEquals(1, p2PartnerStats.losses)
            assertEquals(75.0, p2PartnerStats.winRate) // 3/4 = 75%
        }
        
        @Test
        fun `separates stats for different partners`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3"),
                createTestPlayer(id = "p4"),
                createTestPlayer(id = "p5")
            )
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", calledPlayerId = "1", score = 50),
                createTestRound(roundNumber = 2, takerPlayerId = "0", calledPlayerId = "1", score = 30),
                createTestRound(roundNumber = 3, takerPlayerId = "0", calledPlayerId = "2", score = -40),
                createTestRound(roundNumber = 4, takerPlayerId = "0", calledPlayerId = "2", score = 60)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 5)
            
            // Then
            val p1Stats = result["p1"]!!
            val p2Partner = p1Stats.partnerStats!!["p2"]!!
            val p3Partner = p1Stats.partnerStats["p3"]!!
            
            assertEquals(2, p2Partner.gamesPlayed)
            assertEquals(2, p2Partner.wins)
            assertEquals(100.0, p2Partner.winRate)
            
            assertEquals(2, p3Partner.gamesPlayed)
            assertEquals(1, p3Partner.wins)
            assertEquals(50.0, p3Partner.winRate)
        }
        
        @Test
        fun `ignores rounds where player was called as partner`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2"),
                createTestPlayer(id = "p3"),
                createTestPlayer(id = "p4"),
                createTestPlayer(id = "p5")
            )
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", calledPlayerId = "1", score = 50), // P1 taker, calls P2
                createTestRound(roundNumber = 2, takerPlayerId = "1", calledPlayerId = "0", score = 30), // P2 taker, calls P1
                createTestRound(roundNumber = 3, takerPlayerId = "0", calledPlayerId = "1", score = 40)  // P1 taker, calls P2
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 5)
            
            // Then
            val p1Stats = result["p1"]!!
            // P1 took 2 rounds, both times calling P2
            assertEquals(2, p1Stats.takerRounds)
            assertEquals(2, p1Stats.partnerStats!!["p2"]?.gamesPlayed)
            
            // Round 2 where P1 was called should not count in P1's taker stats
            val p2Stats = result["p2"]!!
            assertEquals(1, p2Stats.takerRounds) // Only round 2
        }
    }
    
    // ============ Edge Cases ============
    

class GameProgressionAnalyzer_EdgeCases {
        
        @Test
        fun `handles empty player list`() {
            // Given
            val players = emptyList<Player>()
            val rounds = listOf(
                createTestRound(roundNumber = 1),
                createTestRound(roundNumber = 2),
                createTestRound(roundNumber = 3)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            assertTrue(result.isEmpty())
        }
        
        @Test
        fun `handles empty rounds list`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2")
            )
            val rounds = emptyList<TarotRound>()
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            assertTrue(result.isEmpty()) // Less than 3 rounds
        }
        
        @Test
        fun `handles malformed player IDs in rounds`() {
            // Given
            val players = listOf(
                createTestPlayer(id = "p1"),
                createTestPlayer(id = "p2")
            )
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "999"), // Invalid index
                createTestRound(roundNumber = 2, takerPlayerId = "abc"), // Non-numeric
                createTestRound(roundNumber = 3, takerPlayerId = "0")
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            // Should handle gracefully - only round 3 should count for P1
            assertNotNull(result["p1"])
        }
        
        @Test
        fun `handles exactly 3 rounds at threshold`() {
            // Given
            val players = listOf(createTestPlayer(id = "p1"))
            val rounds = listOf(
                createTestRound(roundNumber = 1, takerPlayerId = "0", score = 50),
                createTestRound(roundNumber = 2, takerPlayerId = "0", score = 30),
                createTestRound(roundNumber = 3, takerPlayerId = "0", score = 40)
            )
            
            // When
            val result = analyzer.calculateTakerPerformance(players, rounds, playerCount = 3)
            
            // Then
            assertFalse(result.isEmpty()) // Exactly 3 rounds should be processed
            assertEquals(3, result["p1"]?.takerRounds)
        }
    }
