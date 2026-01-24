package io.github.m0nkeysan.tally.core.domain.engine

import io.github.m0nkeysan.tally.core.data.local.database.YahtzeeGameEntity
import io.github.m0nkeysan.tally.core.data.local.database.YahtzeeScoreEntity
import io.github.m0nkeysan.tally.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.core.model.YahtzeeCategory
import kotlin.test.*

/**
 * Comprehensive test suite for YahtzeeStatisticsEngine
 * 
 * Tests critical game logic for calculating player and global statistics,
 * including edge cases, boundary conditions, and integration scenarios.
 * 
 * Uses BDD-style naming with flat class structure for Kotlin Multiplatform compatibility.
 */

// ============ Test Fixtures & Builders ============

/**
 * Creates a test Player with sensible defaults
 */
private fun createTestPlayer(
        id: String = "player1",
        name: String = "Player 1",
        avatarColor: String = "#FF6200",
        createdAt: Long = 1000L,
        isActive: Boolean = true
    ): Player {
        return Player(
            id = id,
            name = name,
            avatarColor = avatarColor,
            createdAt = createdAt,
            isActive = isActive,
            deactivatedAt = null
        )
    }

    /**
     * Creates a test YahtzeeGameEntity with sensible defaults
     */
    private fun createTestGame(
        id: String = "game1",
        name: String = "Test Game",
        playerIds: List<String> = listOf("player1", "player2"),
        isFinished: Boolean = true,
        winnerName: String? = null,
        createdAt: Long = 1000L,
        updatedAt: Long = 2000L
    ): YahtzeeGameEntity {
        return YahtzeeGameEntity(
            id = id,
            name = name,
            playerCount = playerIds.size,
            playerIds = playerIds.joinToString(","),
            firstPlayerId = playerIds.first(),
            currentPlayerId = playerIds.first(),
            isFinished = isFinished,
            winnerName = winnerName,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    /**
     * Creates a test YahtzeeScoreEntity with sensible defaults
     */
    private fun createTestScore(
        id: String = "score1",
        gameId: String = "game1",
        playerId: String = "player1",
        category: YahtzeeCategory = YahtzeeCategory.ACES,
        score: Int = 0
    ): YahtzeeScoreEntity {
        return YahtzeeScoreEntity(
            id = id,
            gameId = gameId,
            playerId = playerId,
            category = category.name,
            score = score
        )
    }

/**
 * Creates a complete set of upper section scores
 */
private fun createUpperSectionScores(
    gameId: String = "game1",
    playerId: String = "player1",
    acesScore: Int = 3,
    twosScore: Int = 6,
    threesScore: Int = 9,
    foursScore: Int = 12,
    fivesScore: Int = 15,
    sixesScore: Int = 18
): List<YahtzeeScoreEntity> {
    return listOf(
        createTestScore(gameId = gameId, playerId = playerId, category = YahtzeeCategory.ACES, score = acesScore),
        createTestScore(gameId = gameId, playerId = playerId, category = YahtzeeCategory.TWOS, score = twosScore),
        createTestScore(gameId = gameId, playerId = playerId, category = YahtzeeCategory.THREES, score = threesScore),
        createTestScore(gameId = gameId, playerId = playerId, category = YahtzeeCategory.FOURS, score = foursScore),
        createTestScore(gameId = gameId, playerId = playerId, category = YahtzeeCategory.FIVES, score = fivesScore),
        createTestScore(gameId = gameId, playerId = playerId, category = YahtzeeCategory.SIXES, score = sixesScore)
    )
}

/**
 * Fake in-memory PlayerRepository for testing
 * 
 * NOTE: Currently commented out as it doesn't implement all required methods
 * from PlayerRepository interface. Tests don't currently use this, but it's
 * preserved for future expansion.
 */
/*
private class FakePlayerRepository(
    private val players: Map<String, Player> = emptyMap()
) : PlayerRepository {
    override suspend fun getAllPlayers(): List<Player> = players.values.toList()
    override suspend fun getPlayerById(id: String): Player? = players[id]
    override suspend fun getPlayersByIds(ids: List<String>): List<Player> = 
        ids.mapNotNull { players[it] }
    override suspend fun insertPlayer(player: Player) = Unit
    override suspend fun updatePlayer(player: Player) = Unit
    override suspend fun deletePlayer(id: String) = Unit
    override suspend fun getActivePlayers(): List<Player> = 
        players.values.filter { it.isActive }
    override suspend fun deactivatePlayer(id: String) = Unit
    override suspend fun reactivatePlayer(id: String) = Unit
    override suspend fun searchPlayers(query: String): List<Player> = emptyList()
}
*/

// ============ CountYahtzees Tests ============

class CountYahtzees {

        @Test
        fun `returns 0 when no yahtzee scores exist`() {
            // Given
            val scores = listOf(
                createTestScore(category = YahtzeeCategory.ACES, score = 3),
                createTestScore(category = YahtzeeCategory.TWOS, score = 6)
            )

            // When
            val result = YahtzeeStatisticsEngine.countYahtzees(scores)

            // Then
            assertEquals(0, result)
        }

        @Test
        fun `returns 0 when yahtzee score is below 50`() {
            // Given
            val scores = listOf(
                createTestScore(category = YahtzeeCategory.YAHTZEE, score = 0),
                createTestScore(category = YahtzeeCategory.YAHTZEE, score = 25)
            )

            // When
            val result = YahtzeeStatisticsEngine.countYahtzees(scores)

            // Then
            assertEquals(0, result)
        }

        @Test
        fun `returns 1 for first yahtzee with score 50`() {
            // Given
            val scores = listOf(
                createTestScore(category = YahtzeeCategory.YAHTZEE, score = 50)
            )

            // When
            val result = YahtzeeStatisticsEngine.countYahtzees(scores)

            // Then
            assertEquals(1, result)
        }

        @Test
        fun `returns 2 for second yahtzee with score 150`() {
            // Given
            val scores = listOf(
                createTestScore(category = YahtzeeCategory.YAHTZEE, score = 150)
            )

            // When
            val result = YahtzeeStatisticsEngine.countYahtzees(scores)

            // Then
            assertEquals(2, result)
        }

        @Test
        fun `returns 3 for third yahtzee with score 250`() {
            // Given
            val scores = listOf(
                createTestScore(category = YahtzeeCategory.YAHTZEE, score = 250)
            )

            // When
            val result = YahtzeeStatisticsEngine.countYahtzees(scores)

            // Then
            assertEquals(3, result)
        }

        @Test
        fun `returns 4 for fourth yahtzee with score 350`() {
            // Given
            val scores = listOf(
                createTestScore(category = YahtzeeCategory.YAHTZEE, score = 350)
            )

            // When
            val result = YahtzeeStatisticsEngine.countYahtzees(scores)

            // Then
            assertEquals(4, result)
        }

        @Test
        fun `returns 5 for fifth yahtzee with score 450`() {
            // Given
            val scores = listOf(
                createTestScore(category = YahtzeeCategory.YAHTZEE, score = 450)
            )

            // When
            val result = YahtzeeStatisticsEngine.countYahtzees(scores)

            // Then
            assertEquals(5, result)
        }

        @Test
        fun `sums multiple yahtzee entries correctly`() {
            // Given
            val scores = listOf(
                createTestScore(id = "s1", category = YahtzeeCategory.YAHTZEE, score = 50),  // 1 yahtzee
                createTestScore(id = "s2", category = YahtzeeCategory.YAHTZEE, score = 150)  // 2 yahtzees
            )

            // When
            val result = YahtzeeStatisticsEngine.countYahtzees(scores)

            // Then
            assertEquals(3, result) // 1 + 2 = 3 total
        }

        @Test
        fun `ignores non-yahtzee categories`() {
            // Given
            val scores = listOf(
                createTestScore(category = YahtzeeCategory.ACES, score = 50),
                createTestScore(category = YahtzeeCategory.YAHTZEE, score = 150),
                createTestScore(category = YahtzeeCategory.CHANCE, score = 50)
            )

            // When
            val result = YahtzeeStatisticsEngine.countYahtzees(scores)

            // Then
            assertEquals(2, result) // Only the YAHTZEE score counts
        }

        @Test
        fun `handles empty score list`() {
            // Given
            val scores = emptyList<YahtzeeScoreEntity>()

            // When
            val result = YahtzeeStatisticsEngine.countYahtzees(scores)

            // Then
            assertEquals(0, result)
        }
    }

    // ============ CalculateGameTotals Tests ============
class CalculateGameTotals {

        @Test
        fun `calculates base score without upper bonus`() {
            // Given
            val games = listOf(createTestGame(id = "game1"))
            val scores = listOf(
                createTestScore(gameId = "game1", playerId = "player1", category = YahtzeeCategory.ACES, score = 3),
                createTestScore(gameId = "game1", playerId = "player1", category = YahtzeeCategory.CHANCE, score = 20)
            )

            // When
            val result = YahtzeeStatisticsEngine.calculateGameTotals(games, scores, "player1")

            // Then
            assertEquals(1, result.size)
            assertEquals(23, result["game1"]) // 3 + 20 = 23 (no bonus since upper < 63)
        }

        @Test
        fun `adds 35 point bonus when upper section is exactly 63`() {
            // Given
            val games = listOf(createTestGame(id = "game1"))
            val upperScores = createUpperSectionScores(
                gameId = "game1",
                playerId = "player1",
                acesScore = 3,
                twosScore = 10,
                threesScore = 12,
                foursScore = 12,
                fivesScore = 15,
                sixesScore = 11  // Total = 63
            )

            // When
            val result = YahtzeeStatisticsEngine.calculateGameTotals(games, upperScores, "player1")

            // Then
            assertEquals(98, result["game1"]) // 63 + 35 bonus = 98
        }

        @Test
        fun `adds 35 point bonus when upper section exceeds 63`() {
            // Given
            val games = listOf(createTestGame(id = "game1"))
            val upperScores = createUpperSectionScores(
                gameId = "game1",
                playerId = "player1",
                acesScore = 5,
                twosScore = 10,
                threesScore = 15,
                foursScore = 16,
                fivesScore = 20,
                sixesScore = 18  // Total = 84
            )

            // When
            val result = YahtzeeStatisticsEngine.calculateGameTotals(games, upperScores, "player1")

            // Then
            assertEquals(119, result["game1"]) // 84 + 35 bonus = 119
        }

        @Test
        fun `does not add bonus when upper section is 62`() {
            // Given
            val games = listOf(createTestGame(id = "game1"))
            val upperScores = createUpperSectionScores(
                gameId = "game1",
                playerId = "player1",
                acesScore = 3,
                twosScore = 10,
                threesScore = 12,
                foursScore = 12,
                fivesScore = 15,
                sixesScore = 10  // Total = 62
            )

            // When
            val result = YahtzeeStatisticsEngine.calculateGameTotals(games, upperScores, "player1")

            // Then
            assertEquals(62, result["game1"]) // No bonus
        }

        @Test
        fun `handles multiple games for same player`() {
            // Given
            val games = listOf(
                createTestGame(id = "game1"),
                createTestGame(id = "game2")
            )
            val scores = listOf(
                createTestScore(gameId = "game1", playerId = "player1", category = YahtzeeCategory.CHANCE, score = 30),
                createTestScore(gameId = "game2", playerId = "player1", category = YahtzeeCategory.CHANCE, score = 25)
            )

            // When
            val result = YahtzeeStatisticsEngine.calculateGameTotals(games, scores, "player1")

            // Then
            assertEquals(2, result.size)
            assertEquals(30, result["game1"])
            assertEquals(25, result["game2"])
        }

        @Test
        fun `returns empty map when no games exist`() {
            // Given
            val games = emptyList<YahtzeeGameEntity>()
            val scores = emptyList<YahtzeeScoreEntity>()

            // When
            val result = YahtzeeStatisticsEngine.calculateGameTotals(games, scores, "player1")

            // Then
            assertTrue(result.isEmpty())
        }

        @Test
        fun `correctly separates scores by game ID`() {
            // Given
            val games = listOf(
                createTestGame(id = "game1"),
                createTestGame(id = "game2")
            )
            val scores = listOf(
                createTestScore(gameId = "game1", playerId = "player1", category = YahtzeeCategory.ACES, score = 5),
                createTestScore(gameId = "game1", playerId = "player2", category = YahtzeeCategory.ACES, score = 3), // Different player
                createTestScore(gameId = "game2", playerId = "player1", category = YahtzeeCategory.ACES, score = 4)
            )

            // When
            val result = YahtzeeStatisticsEngine.calculateGameTotals(games, scores, "player1")

            // Then
            assertEquals(5, result["game1"]) // Only player1's score from game1
            assertEquals(4, result["game2"]) // Only player1's score from game2
        }
    }

    // ============ CalculateCategoryStats Tests ============
class CalculateCategoryStats {

        @Test
        fun `calculates correct average for single category`() {
            // Given
            val scores = listOf(
                createTestScore(category = YahtzeeCategory.ACES, score = 3),
                createTestScore(category = YahtzeeCategory.ACES, score = 5),
                createTestScore(category = YahtzeeCategory.ACES, score = 4)
            )

            // When
            val result = YahtzeeStatisticsEngine.calculateCategoryStats(scores, totalGames = 3)

            // Then
            val acesStat = result[YahtzeeCategory.ACES]!!
            assertEquals(4.0, acesStat.average) // (3 + 5 + 4) / 3 = 4.0
            assertEquals(3, acesStat.timesScored)
            assertEquals(5, acesStat.best)
        }

        @Test
        fun `handles zero scores in average calculation`() {
            // Given
            val scores = listOf(
                createTestScore(category = YahtzeeCategory.ACES, score = 0),
                createTestScore(category = YahtzeeCategory.ACES, score = 3)
            )

            // When
            val result = YahtzeeStatisticsEngine.calculateCategoryStats(scores, totalGames = 2)

            // Then
            val acesStat = result[YahtzeeCategory.ACES]!!
            assertEquals(1.5, acesStat.average) // (0 + 3) / 2 = 1.5
            assertEquals(1, acesStat.timesScored) // Only counts score > 0
        }

        @Test
        fun `counts times scored correctly`() {
            // Given
            val scores = listOf(
                createTestScore(category = YahtzeeCategory.CHANCE, score = 25),
                createTestScore(category = YahtzeeCategory.CHANCE, score = 30),
                createTestScore(category = YahtzeeCategory.CHANCE, score = 0)
            )

            // When
            val result = YahtzeeStatisticsEngine.calculateCategoryStats(scores, totalGames = 3)

            // Then
            val chanceStat = result[YahtzeeCategory.CHANCE]!!
            assertEquals(2, chanceStat.timesScored) // Only non-zero scores count
        }

        @Test
        fun `counts times zeroed correctly when total games provided`() {
            // Given
            val scores = listOf(
                createTestScore(category = YahtzeeCategory.YAHTZEE, score = 50),
                createTestScore(category = YahtzeeCategory.YAHTZEE, score = 0)
            )

            // When
            val result = YahtzeeStatisticsEngine.calculateCategoryStats(scores, totalGames = 5)

            // Then
            val yahtzeeStat = result[YahtzeeCategory.YAHTZEE]!!
            assertEquals(1, yahtzeeStat.timesScored)
            assertEquals(4, yahtzeeStat.timesZeroed) // 5 total games - 1 scored = 4 zeroed
        }

        @Test
        fun `calculates zero rate percentage correctly`() {
            // Given
            val scores = listOf(
                createTestScore(category = YahtzeeCategory.FULL_HOUSE, score = 25),
                createTestScore(category = YahtzeeCategory.FULL_HOUSE, score = 0)
            )

            // When
            val result = YahtzeeStatisticsEngine.calculateCategoryStats(scores, totalGames = 10)

            // Then
            val stat = result[YahtzeeCategory.FULL_HOUSE]!!
            assertEquals(90.0, stat.zeroRate) // 9 zeroed / 10 total * 100 = 90%
        }

        @Test
        fun `finds best score for category`() {
            // Given
            val scores = listOf(
                createTestScore(category = YahtzeeCategory.LARGE_STRAIGHT, score = 30),
                createTestScore(category = YahtzeeCategory.LARGE_STRAIGHT, score = 40),
                createTestScore(category = YahtzeeCategory.LARGE_STRAIGHT, score = 35)
            )

            // When
            val result = YahtzeeStatisticsEngine.calculateCategoryStats(scores, totalGames = 3)

            // Then
            val stat = result[YahtzeeCategory.LARGE_STRAIGHT]!!
            assertEquals(40, stat.best)
        }

        @Test
        fun `returns 0 average when category never scored`() {
            // Given
            val scores = listOf(
                createTestScore(category = YahtzeeCategory.ACES, score = 3)
            )

            // When
            val result = YahtzeeStatisticsEngine.calculateCategoryStats(scores, totalGames = 1)

            // Then
            val twosStat = result[YahtzeeCategory.TWOS]!! // TWOS was never scored
            assertEquals(0.0, twosStat.average)
            assertEquals(0, twosStat.timesScored)
            assertEquals(0, twosStat.best)
        }

        @Test
        fun `handles all 13 categories correctly`() {
            // Given
            val scores = emptyList<YahtzeeScoreEntity>()

            // When
            val result = YahtzeeStatisticsEngine.calculateCategoryStats(scores, totalGames = 0)

            // Then
            assertEquals(13, result.size) // All categories should be present
            assertTrue(result.containsKey(YahtzeeCategory.ACES))
            assertTrue(result.containsKey(YahtzeeCategory.YAHTZEE))
            assertTrue(result.containsKey(YahtzeeCategory.CHANCE))
        }
    }

    // ============ CalculateUpperBonusRate Tests ============
class CalculateUpperBonusRate {

        @Test
        fun `returns 0 percent when no games are finished`() {
            // Given
            val games = listOf(
                createTestGame(id = "game1", isFinished = false)
            )
            val scores = createUpperSectionScores(gameId = "game1", acesScore = 5, twosScore = 10,
                threesScore = 15, foursScore = 16, fivesScore = 20, sixesScore = 18) // Total = 84

            // When
            val result = YahtzeeStatisticsEngine.calculateUpperBonusRate(games, scores, "player1")

            // Then
            assertEquals(0.0, result)
        }

        @Test
        fun `returns 100 percent when all games achieve bonus`() {
            // Given
            val games = listOf(
                createTestGame(id = "game1", isFinished = true),
                createTestGame(id = "game2", isFinished = true)
            )
            val scores = createUpperSectionScores(gameId = "game1", acesScore = 5, twosScore = 10,
                threesScore = 15, foursScore = 16, fivesScore = 20, sixesScore = 18) + // Total = 84
                createUpperSectionScores(gameId = "game2", acesScore = 5, twosScore = 10,
                    threesScore = 15, foursScore = 16, fivesScore = 20, sixesScore = 18) // Total = 84

            // When
            val result = YahtzeeStatisticsEngine.calculateUpperBonusRate(games, scores, "player1")

            // Then
            assertEquals(100.0, result)
        }

        @Test
        fun `returns 0 percent when no games achieve bonus`() {
            // Given
            val games = listOf(
                createTestGame(id = "game1", isFinished = true),
                createTestGame(id = "game2", isFinished = true)
            )
            val scores = createUpperSectionScores(gameId = "game1", acesScore = 1, twosScore = 2,
                threesScore = 3, foursScore = 4, fivesScore = 5, sixesScore = 6) + // Total = 21
                createUpperSectionScores(gameId = "game2", acesScore = 2, twosScore = 4,
                    threesScore = 6, foursScore = 8, fivesScore = 10, sixesScore = 12) // Total = 42

            // When
            val result = YahtzeeStatisticsEngine.calculateUpperBonusRate(games, scores, "player1")

            // Then
            assertEquals(0.0, result)
        }

        @Test
        fun `calculates correct percentage for partial achievement`() {
            // Given
            val games = listOf(
                createTestGame(id = "game1", isFinished = true),
                createTestGame(id = "game2", isFinished = true),
                createTestGame(id = "game3", isFinished = true),
                createTestGame(id = "game4", isFinished = true)
            )
            val scores = createUpperSectionScores(gameId = "game1", acesScore = 5, twosScore = 10,
                threesScore = 15, foursScore = 16, fivesScore = 20, sixesScore = 18) + // Total = 84 ✓
                createUpperSectionScores(gameId = "game2", acesScore = 1, twosScore = 2,
                    threesScore = 3, foursScore = 4, fivesScore = 5, sixesScore = 6) + // Total = 21 ✗
                createUpperSectionScores(gameId = "game3", acesScore = 3, twosScore = 10,
                    threesScore = 12, foursScore = 12, fivesScore = 15, sixesScore = 11) + // Total = 63 ✓
                createUpperSectionScores(gameId = "game4", acesScore = 2, twosScore = 4,
                    threesScore = 6, foursScore = 8, fivesScore = 10, sixesScore = 12) // Total = 42 ✗

            // When
            val result = YahtzeeStatisticsEngine.calculateUpperBonusRate(games, scores, "player1")

            // Then
            assertEquals(50.0, result) // 2 out of 4 = 50%
        }

        @Test
        fun `only counts finished games`() {
            // Given
            val games = listOf(
                createTestGame(id = "game1", isFinished = true),
                createTestGame(id = "game2", isFinished = false) // Not finished
            )
            val scores = createUpperSectionScores(gameId = "game1", acesScore = 5, twosScore = 10,
                threesScore = 15, foursScore = 16, fivesScore = 20, sixesScore = 18) + // Total = 84 ✓
                createUpperSectionScores(gameId = "game2", acesScore = 5, twosScore = 10,
                    threesScore = 15, foursScore = 16, fivesScore = 20, sixesScore = 18) // Total = 84 but not finished

            // When
            val result = YahtzeeStatisticsEngine.calculateUpperBonusRate(games, scores, "player1")

            // Then
            assertEquals(100.0, result) // Only game1 counts, and it achieved bonus
        }

        @Test
        fun `correctly sums upper section categories only`() {
            // Given
            val games = listOf(createTestGame(id = "game1", isFinished = true))
            val scores = createUpperSectionScores(gameId = "game1", acesScore = 3, twosScore = 10,
                threesScore = 12, foursScore = 12, fivesScore = 15, sixesScore = 11) + // Total = 63
                listOf(
                    createTestScore(gameId = "game1", playerId = "player1", category = YahtzeeCategory.CHANCE, score = 30),
                    createTestScore(gameId = "game1", playerId = "player1", category = YahtzeeCategory.YAHTZEE, score = 50)
                )

            // When
            val result = YahtzeeStatisticsEngine.calculateUpperBonusRate(games, scores, "player1")

            // Then
            assertEquals(100.0, result) // Upper section = 63, lower section scores ignored
        }
    }

    // ============ CalculateUpperSectionAverage and CalculateLowerSectionAverage Tests ============
class SectionAverages {

        @Test
        fun `calculates correct average for upper section`() {
            // Given
            val scores = createUpperSectionScores(
                acesScore = 3,
                twosScore = 6,
                threesScore = 9,
                foursScore = 12,
                fivesScore = 15,
                sixesScore = 18
            ) // Total = 63, Average = 10.5

            // When
            val result = YahtzeeStatisticsEngine.calculateUpperSectionAverage(scores)

            // Then
            assertEquals(10.5, result)
        }

        @Test
        fun `calculates correct average for lower section`() {
            // Given
            val scores = listOf(
                createTestScore(category = YahtzeeCategory.CHANCE, score = 20),
                createTestScore(category = YahtzeeCategory.THREE_OF_KIND, score = 18),
                createTestScore(category = YahtzeeCategory.FOUR_OF_KIND, score = 22),
                createTestScore(category = YahtzeeCategory.FULL_HOUSE, score = 25),
                createTestScore(category = YahtzeeCategory.SMALL_STRAIGHT, score = 30),
                createTestScore(category = YahtzeeCategory.LARGE_STRAIGHT, score = 40),
                createTestScore(category = YahtzeeCategory.YAHTZEE, score = 50)
            ) // Total = 205, Average = 29.29

            // When
            val result = YahtzeeStatisticsEngine.calculateLowerSectionAverage(scores)

            // Then
            assertEquals(205.0 / 7, result, 0.01)
        }

        @Test
        fun `excludes lower section from upper average`() {
            // Given
            val scores = createUpperSectionScores(acesScore = 5, twosScore = 10, threesScore = 15,
                foursScore = 20, fivesScore = 25, sixesScore = 30) + // Total = 105
                listOf(createTestScore(category = YahtzeeCategory.CHANCE, score = 50))

            // When
            val result = YahtzeeStatisticsEngine.calculateUpperSectionAverage(scores)

            // Then
            assertEquals(17.5, result) // 105 / 6 = 17.5, CHANCE ignored
        }

        @Test
        fun `excludes upper section from lower average`() {
            // Given
            val scores = createUpperSectionScores(acesScore = 5, twosScore = 10, threesScore = 15,
                foursScore = 20, fivesScore = 25, sixesScore = 30) +
                listOf(createTestScore(category = YahtzeeCategory.CHANCE, score = 30))

            // When
            val result = YahtzeeStatisticsEngine.calculateLowerSectionAverage(scores)

            // Then
            assertEquals(30.0, result) // Only CHANCE counted
        }

        @Test
        fun `returns 0 when no scores exist for upper section`() {
            // Given
            val scores = emptyList<YahtzeeScoreEntity>()

            // When
            val result = YahtzeeStatisticsEngine.calculateUpperSectionAverage(scores)

            // Then
            assertEquals(0.0, result)
        }

        @Test
        fun `returns 0 when no scores exist for lower section`() {
            // Given
            val scores = emptyList<YahtzeeScoreEntity>()

            // When
            val result = YahtzeeStatisticsEngine.calculateLowerSectionAverage(scores)

            // Then
            assertEquals(0.0, result)
        }
    }

    // ============ CalculatePlayerStatistics Tests ============
class CalculatePlayerStatistics {

        @Test
        fun `calculates total games correctly`() {
            // Given
            val games = listOf(
                createTestGame(id = "game1"),
                createTestGame(id = "game2"),
                createTestGame(id = "game3")
            )

            // When
            val result = YahtzeeStatisticsEngine.calculatePlayerStatistics(
                playerId = "player1",
                playerName = "Player 1",
                games = games,
                playerScores = emptyList(),
                allScores = emptyList()
            )

            // Then
            assertEquals(3, result.totalGames)
        }

        @Test
        fun `counts finished games separately from total games`() {
            // Given
            val games = listOf(
                createTestGame(id = "game1", isFinished = true),
                createTestGame(id = "game2", isFinished = false),
                createTestGame(id = "game3", isFinished = true)
            )

            // When
            val result = YahtzeeStatisticsEngine.calculatePlayerStatistics(
                playerId = "player1",
                playerName = "Player 1",
                games = games,
                playerScores = emptyList(),
                allScores = emptyList()
            )

            // Then
            assertEquals(3, result.totalGames)
            assertEquals(2, result.finishedGames)
        }

        @Test
        fun `counts wins correctly from winner name`() {
            // Given
            val games = listOf(
                createTestGame(id = "game1", isFinished = true, winnerName = "Player 1"),
                createTestGame(id = "game2", isFinished = true, winnerName = "Player 2"),
                createTestGame(id = "game3", isFinished = true, winnerName = "Player 1")
            )

            // When
            val result = YahtzeeStatisticsEngine.calculatePlayerStatistics(
                playerId = "player1",
                playerName = "Player 1",
                games = games,
                playerScores = emptyList(),
                allScores = emptyList()
            )

            // Then
            assertEquals(2, result.wins)
        }

        @Test
        fun `calculates win rate as percentage`() {
            // Given
            val games = listOf(
                createTestGame(id = "game1", isFinished = true, winnerName = "Player 1"),
                createTestGame(id = "game2", isFinished = true, winnerName = "Player 2"),
                createTestGame(id = "game3", isFinished = true, winnerName = "Player 2"),
                createTestGame(id = "game4", isFinished = true, winnerName = "Player 1")
            )

            // When
            val result = YahtzeeStatisticsEngine.calculatePlayerStatistics(
                playerId = "player1",
                playerName = "Player 1",
                games = games,
                playerScores = emptyList(),
                allScores = emptyList()
            )

            // Then
            assertEquals(50.0, result.winRate) // 2 wins / 4 finished games = 50%
        }

        @Test
        fun `calculates average score across all games`() {
            // Given
            val games = listOf(
                createTestGame(id = "game1"),
                createTestGame(id = "game2")
            )
            val scores = createUpperSectionScores(gameId = "game1", acesScore = 3, twosScore = 10,
                threesScore = 12, foursScore = 12, fivesScore = 15, sixesScore = 11) + // Total = 63 + 35 = 98
                createUpperSectionScores(gameId = "game2", acesScore = 5, twosScore = 10,
                    threesScore = 15, foursScore = 16, fivesScore = 20, sixesScore = 18) // Total = 84 + 35 = 119

            // When
            val result = YahtzeeStatisticsEngine.calculatePlayerStatistics(
                playerId = "player1",
                playerName = "Player 1",
                games = games,
                playerScores = scores,
                allScores = scores
            )

            // Then
            assertEquals(108.5, result.averageScore) // (98 + 119) / 2 = 108.5
        }

        @Test
        fun `finds high score from all games`() {
            // Given
            val games = listOf(
                createTestGame(id = "game1"),
                createTestGame(id = "game2"),
                createTestGame(id = "game3")
            )
            val scores = listOf(
                createTestScore(gameId = "game1", category = YahtzeeCategory.CHANCE, score = 30)
            ) +
                createUpperSectionScores(gameId = "game2", acesScore = 5, twosScore = 10,
                    threesScore = 15, foursScore = 16, fivesScore = 20, sixesScore = 18) + // Total = 84 + 35 = 119
                listOf(createTestScore(gameId = "game3", category = YahtzeeCategory.CHANCE, score = 25))

            // When
            val result = YahtzeeStatisticsEngine.calculatePlayerStatistics(
                playerId = "player1",
                playerName = "Player 1",
                games = games,
                playerScores = scores,
                allScores = scores
            )

            // Then
            assertEquals(119, result.highScore)
        }

        @Test
        fun `returns 0 win rate when no finished games`() {
            // Given
            val games = listOf(
                createTestGame(id = "game1", isFinished = false),
                createTestGame(id = "game2", isFinished = false)
            )

            // When
            val result = YahtzeeStatisticsEngine.calculatePlayerStatistics(
                playerId = "player1",
                playerName = "Player 1",
                games = games,
                playerScores = emptyList(),
                allScores = emptyList()
            )

            // Then
            assertEquals(0.0, result.winRate)
        }

        @Test
        fun `handles player with no games gracefully`() {
            // Given
            val games = emptyList<YahtzeeGameEntity>()

            // When
            val result = YahtzeeStatisticsEngine.calculatePlayerStatistics(
                playerId = "player1",
                playerName = "Player 1",
                games = games,
                playerScores = emptyList(),
                allScores = emptyList()
            )

            // Then
            assertEquals(0, result.totalGames)
            assertEquals(0, result.finishedGames)
            assertEquals(0, result.wins)
            assertEquals(0.0, result.winRate)
            assertEquals(0.0, result.averageScore)
            assertEquals(0, result.highScore)
        }
    }

    // ============ Edge Cases & Integration Tests ============
class YahtzeeStatisticsEngine_EdgeCases {

        @Test
        fun `handles empty player list`() {
            // Given
            val players = emptyList<Player>()
            val games = emptyList<YahtzeeGameEntity>()

            // When
            val result = YahtzeeStatisticsEngine.calculateGameTotals(games, emptyList(), "player1")

            // Then
            assertTrue(result.isEmpty())
        }

        @Test
        fun `handles empty game list`() {
            // Given
            val games = emptyList<YahtzeeGameEntity>()
            val scores = listOf(createTestScore())

            // When
            val result = YahtzeeStatisticsEngine.calculateGameTotals(games, scores, "player1")

            // Then
            assertTrue(result.isEmpty())
        }

        @Test
        fun `handles empty score list`() {
            // Given
            val games = listOf(createTestGame())
            val scores = emptyList<YahtzeeScoreEntity>()

            // When
            val result = YahtzeeStatisticsEngine.calculateGameTotals(games, scores, "player1")

            // Then
            assertEquals(0, result["game1"]) // Game exists but no scores
        }

        @Test
        fun `handles player IDs not in score data`() {
            // Given
            val games = listOf(createTestGame(id = "game1"))
            val scores = listOf(
                createTestScore(gameId = "game1", playerId = "player2", category = YahtzeeCategory.ACES, score = 5)
            )

            // When
            val result = YahtzeeStatisticsEngine.calculateGameTotals(games, scores, "player1")

            // Then
            assertEquals(0, result["game1"]) // player1 has no scores in game1
        }
    }
