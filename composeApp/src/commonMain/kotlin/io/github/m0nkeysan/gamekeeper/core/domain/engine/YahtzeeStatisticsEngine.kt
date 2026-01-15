package io.github.m0nkeysan.gamekeeper.core.domain.engine

import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeGameEntity
import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeScoreEntity
import io.github.m0nkeysan.gamekeeper.core.model.CategoryStat
import io.github.m0nkeysan.gamekeeper.core.model.GameSummary
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeCategory
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeePlayerStatistics

/**
 * Engine for calculating comprehensive Yahtzee player statistics
 */
object YahtzeeStatisticsEngine {

    /**
     * Calculate complete statistics for a player
     */
    fun calculatePlayerStatistics(
        playerId: String,
        playerName: String,
        games: List<YahtzeeGameEntity>,
        allScores: List<YahtzeeScoreEntity>
    ): YahtzeePlayerStatistics {
        val totalGames = games.size
        val finishedGames = games.count { it.isFinished }
        val playerScores = allScores.filter { it.playerId == playerId }
        
        val wins = games.count { game ->
            game.isFinished && game.winnerName?.contains(playerName) == true
        }
        
        val winRate = if (finishedGames > 0) {
            (wins.toDouble() / finishedGames) * 100
        } else {
            0.0
        }
        
        val gameScoresMap = calculateGameTotals(games, allScores, playerId)
        val averageScore = if (gameScoresMap.isNotEmpty()) {
            gameScoresMap.values.average()
        } else {
            0.0
        }
        
        val highScore = gameScoresMap.values.maxOrNull() ?: 0
        val totalYahtzees = countYahtzees(playerScores)
        val yahtzeeRate = if (totalGames > 0) {
            totalYahtzees.toDouble() / totalGames
        } else {
            0.0
        }
        
        val categoryStats = calculateCategoryStats(playerScores, finishedGames)
        val upperBonusRate = calculateUpperBonusRate(games, allScores, playerId)
        val upperSectionAverage = calculateUpperSectionAverage(playerScores)
        val lowerSectionAverage = calculateLowerSectionAverage(playerScores)
        val recentGames = calculateGameSummaries(playerId, games, allScores)
        
        return YahtzeePlayerStatistics(
            playerId = playerId,
            playerName = playerName,
            totalGames = totalGames,
            finishedGames = finishedGames,
            wins = wins,
            winRate = winRate,
            averageScore = averageScore,
            highScore = highScore,
            totalYahtzees = totalYahtzees,
            yahtzeeRate = yahtzeeRate,
            categoryStats = categoryStats,
            upperBonusRate = upperBonusRate,
            upperSectionAverage = upperSectionAverage,
            lowerSectionAverage = lowerSectionAverage,
            recentGames = recentGames
        )
    }

    /**
     * Calculate total score for each game a player participated in
     */
    private fun calculateGameTotals(
        games: List<YahtzeeGameEntity>,
        allScores: List<YahtzeeScoreEntity>,
        playerId: String
    ): Map<String, Int> {
        return games.associate { game ->
            val gameScores = allScores.filter { it.gameId == game.id && it.playerId == playerId }
            val baseScore = gameScores.sumOf { it.score }
            val upperScore = gameScores
                .filter { YahtzeeCategory.valueOf(it.category).isUpperSection() }
                .sumOf { it.score }
            val bonus = if (upperScore >= 63) 35 else 0
            val totalScore = baseScore + bonus
            
            game.id to totalScore
        }
    }

    /**
     * Calculate statistics for each scoring category
     */
    private fun calculateCategoryStats(
        playerScores: List<YahtzeeScoreEntity>,
        totalGames: Int
    ): Map<YahtzeeCategory, CategoryStat> {
        return YahtzeeCategory.entries.associate { category ->
            val categoryScores = playerScores.filter { it.category == category.name }
            val average = if (categoryScores.isNotEmpty()) {
                categoryScores.map { it.score }.average()
            } else {
                0.0
            }
            val timesScored = categoryScores.count { it.score > 0 }
            val timesZeroed = if (totalGames > 0) {
                totalGames - timesScored
            } else {
                0
            }
            val zeroRate = if (totalGames > 0) {
                (timesZeroed.toDouble() / totalGames) * 100
            } else {
                0.0
            }
            val best = categoryScores.maxOfOrNull { it.score } ?: 0
            
            category to CategoryStat(
                category = category,
                average = average,
                timesScored = timesScored,
                timesZeroed = timesZeroed,
                zeroRate = zeroRate,
                best = best
            )
        }
    }

    /**
     * Count total Yahtzees scored
     */
    private fun countYahtzees(playerScores: List<YahtzeeScoreEntity>): Int {
        return playerScores.count { 
            it.category == YahtzeeCategory.YAHTZEE.name && it.score >= 50
        }
    }

    /**
     * Calculate percentage of games where upper bonus was achieved
     */
    private fun calculateUpperBonusRate(
        games: List<YahtzeeGameEntity>,
        allScores: List<YahtzeeScoreEntity>,
        playerId: String
    ): Double {
        val finishedGames = games.filter { it.isFinished }
        if (finishedGames.isEmpty()) return 0.0
        
        val gamesWithBonus = finishedGames.count { game ->
            val gameScores = allScores.filter { 
                it.gameId == game.id && it.playerId == playerId
            }
            val upperScore = gameScores
                .filter { YahtzeeCategory.valueOf(it.category).isUpperSection() }
                .sumOf { it.score }
            upperScore >= 63
        }
        
        return (gamesWithBonus.toDouble() / finishedGames.size) * 100
    }

    /**
     * Calculate average upper section score
     */
    private fun calculateUpperSectionAverage(playerScores: List<YahtzeeScoreEntity>): Double {
        val upperScores = playerScores.filter { 
            YahtzeeCategory.valueOf(it.category).isUpperSection()
        }
        return if (upperScores.isNotEmpty()) {
            upperScores.map { it.score }.average()
        } else {
            0.0
        }
    }

    /**
     * Calculate average lower section score
     */
    private fun calculateLowerSectionAverage(playerScores: List<YahtzeeScoreEntity>): Double {
        val lowerScores = playerScores.filter { 
            YahtzeeCategory.valueOf(it.category).isLowerSection()
        }
        return if (lowerScores.isNotEmpty()) {
            lowerScores.map { it.score }.average()
        } else {
            0.0
        }
    }

    /**
     * Calculate summaries of recent games for ranking display
     */
    private fun calculateGameSummaries(
        playerId: String,
        games: List<YahtzeeGameEntity>,
        allScores: List<YahtzeeScoreEntity>
    ): List<GameSummary> {
        return games
            .filter { it.isFinished }
            .sortedByDescending { it.updatedAt }
            .take(10)
            .mapIndexed { _, game ->
                val gameScores = allScores.filter { it.gameId == game.id }
                val totalScoresMap = game.playerIds.split(",").associate { pid ->
                    val playerGameScores = gameScores.filter { it.playerId == pid }
                    val baseScore = playerGameScores.sumOf { it.score }
                    val upperScore = playerGameScores
                        .filter { YahtzeeCategory.valueOf(it.category).isUpperSection() }
                        .sumOf { it.score }
                    val bonus = if (upperScore >= 63) 35 else 0
                    pid to (baseScore + bonus)
                }
                
                val playerScore = totalScoresMap[playerId] ?: 0
                val rank = totalScoresMap.count { it.value > playerScore } + 1
                
                GameSummary(
                    gameId = game.id,
                    gameName = game.name,
                    totalScore = playerScore,
                    playerCount = game.playerCount,
                    rank = rank,
                    isWinner = rank == 1,
                    completedAt = game.updatedAt
                )
            }
    }
}
