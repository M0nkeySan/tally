package io.github.m0nkeysan.gamekeeper.core.domain.engine

import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeGameEntity
import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeScoreEntity
import io.github.m0nkeysan.gamekeeper.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.gamekeeper.core.model.*
import kotlin.math.sqrt

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
        playerScores: List<YahtzeeScoreEntity>,
        allScores: List<YahtzeeScoreEntity>
    ): YahtzeePlayerStatistics {
        val totalGames = games.size
        val finishedGames = games.count { it.isFinished }
        
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
     * Score progression: 50 (1st), 150 (2nd), 250 (3rd), 350 (4th), 450 (5th)
     * Formula: (score - 50) / 100 + 1 for scores >= 50
     */
    private fun countYahtzees(playerScores: List<YahtzeeScoreEntity>): Int {
        return playerScores
            .filter { it.category == YahtzeeCategory.YAHTZEE.name && it.score >= 50 }
            .sumOf { 
                // Convert score to yahtzee count: 50→1, 150→2, 250→3, 350→4, 450→5
                (it.score - 50) / 100 + 1
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
            .map { game ->
                val gameScores = allScores.filter { it.gameId == game.id }
                val totalScoresMap = game.playerIds.split(",").associate { pid ->
                    val trimmedPid = pid.trim()
                    val playerGameScores = gameScores.filter { it.playerId == trimmedPid }
                    val baseScore = playerGameScores.sumOf { it.score }
                    val upperScore = playerGameScores
                        .filter { YahtzeeCategory.valueOf(it.category).isUpperSection() }
                        .sumOf { it.score }
                    val bonus = if (upperScore >= 63) 35 else 0
                    trimmedPid to (baseScore + bonus)
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

    /**
     * Calculate comprehensive global statistics across all games and players
     */
    suspend fun calculateGlobalStatistics(
        allGames: List<YahtzeeGameEntity>,
        allScores: List<YahtzeeScoreEntity>,
        playerRepository: PlayerRepository
    ): YahtzeeGlobalStatistics {
        val totalGames = allGames.size
        val finishedGames = allGames.count { it.isFinished }
        
        // Get unique players
        val uniquePlayerIds = allGames
            .flatMap { it.playerIds.split(",") }
            .map { it.trim() }
            .distinct()
        val totalPlayers = uniquePlayerIds.size

        // Calculate records and summaries
        val allTimeHighScore = calculateAllTimeHighScore(allGames, allScores, playerRepository)
        val totalYahtzees = countYahtzees(allScores)
        val yahtzeeRate = if (finishedGames > 0) totalYahtzees.toDouble() / finishedGames else 0.0
        val mostYahtzeesInGame = calculateMostYahtzeesInGame(allGames, allScores, playerRepository)
        
        // Calculate averages
        val gameScores = allGames.map { game ->
            val gameScores = allScores.filter { it.gameId == game.id }
            gameScores.sumOf { it.score }
        }
        val averageScore = if (gameScores.isNotEmpty()) gameScores.average() else 0.0
        
        // Calculate upper bonus rate
        val gamesWithBonus = allGames.count { game ->
            val gameScores = allScores.filter { it.gameId == game.id }
            val upperScore = gameScores
                .filter { YahtzeeCategory.valueOf(it.category).isUpperSection() }
                .sumOf { it.score }
            upperScore >= YahtzeeStatisticsConstants.UPPER_BONUS_THRESHOLD
        }
        val upperBonusRate = if (finishedGames > 0) {
            (gamesWithBonus.toDouble() / finishedGames) * 100
        } else {
            0.0
        }
        
        // Category stats
        val categoryStats = calculateGlobalCategoryStats(allScores, finishedGames)
        val mostScoredCategory = categoryStats.entries.maxByOrNull { it.value.totalTimesScored }?.key
        val leastScoredCategory = categoryStats.entries.minByOrNull { it.value.totalTimesScored }?.key
        val highestCategoryAverage = categoryStats.entries
            .maxByOrNull { it.value.average }
            ?.let { CategoryRecord(it.key, it.value.average) }
        
        // Leaderboards
        val topPlayersByWins = buildWinsLeaderboard(allGames, playerRepository)
        val topPlayersByScore = buildScoreLeaderboard(allGames, allScores, playerRepository)
        val topPlayersByYahtzees = buildYahtzeesLeaderboard(allScores, playerRepository)
        
        // Recent games
        val recentGames = calculateGlobalRecentGames(allGames, allScores, playerRepository)
        
        // Fun stats
        val estimatedDiceRolls = (finishedGames.toLong() * 
            YahtzeeStatisticsConstants.ESTIMATED_TURNS_PER_GAME * 
            YahtzeeStatisticsConstants.ESTIMATED_ROLLS_PER_TURN)
        val luckiestPlayer = findLuckiestPlayer(uniquePlayerIds, allGames, allScores, playerRepository)
        val mostConsistentPlayer = findMostConsistentPlayer(uniquePlayerIds, allGames, allScores, playerRepository)
        val totalPointsScored = gameScores.sumOf { it.toLong() }
        val averagePlayersPerGame = if (allGames.isNotEmpty()) {
            allGames.mapNotNull { it.playerIds.split(",").size.takeIf { s -> s > 0 } }.average()
        } else {
            0.0
        }
        
        val mostActivePlayer = findMostActivePlayer(uniquePlayerIds, allGames, playerRepository)
        
        return YahtzeeGlobalStatistics(
            totalGames = totalGames,
            finishedGames = finishedGames,
            totalPlayers = totalPlayers,
            mostActivePlayer = mostActivePlayer,
            allTimeHighScore = allTimeHighScore,
            averageScore = averageScore,
            totalYahtzees = totalYahtzees,
            yahtzeeRate = yahtzeeRate,
            mostYahtzeesInGame = mostYahtzeesInGame,
            upperBonusRate = upperBonusRate,
            categoryStats = categoryStats,
            mostScoredCategory = mostScoredCategory,
            leastScoredCategory = leastScoredCategory,
            highestCategoryAverage = highestCategoryAverage,
            topPlayersByWins = topPlayersByWins,
            topPlayersByScore = topPlayersByScore,
            topPlayersByYahtzees = topPlayersByYahtzees,
            recentGames = recentGames,
            estimatedDiceRolls = estimatedDiceRolls,
            luckiestPlayer = luckiestPlayer,
            mostConsistentPlayer = mostConsistentPlayer,
            totalPointsScored = totalPointsScored,
            averagePlayersPerGame = averagePlayersPerGame
        )
    }

    private suspend fun calculateAllTimeHighScore(
        games: List<YahtzeeGameEntity>,
        allScores: List<YahtzeeScoreEntity>,
        playerRepository: PlayerRepository
    ): ScoreRecord? {
        var maxScore = 0
        var result: ScoreRecord? = null
        
        games.forEach { game ->
            val gameScores = allScores.filter { it.gameId == game.id }
            game.playerIds.split(",").forEach { playerId ->
                val playerScores = gameScores.filter { it.playerId == playerId.trim() }
                val baseScore = playerScores.sumOf { it.score }
                val upperScore = playerScores
                    .filter { YahtzeeCategory.valueOf(it.category).isUpperSection() }
                    .sumOf { it.score }
                val bonus = if (upperScore >= YahtzeeStatisticsConstants.UPPER_BONUS_THRESHOLD) 
                    YahtzeeStatisticsConstants.UPPER_BONUS_VALUE else 0
                val totalScore = baseScore + bonus
                
                if (totalScore > maxScore) {
                    maxScore = totalScore
                    val player = try {
                        playerRepository.getPlayerById(playerId.trim())
                    } catch (e: Exception) {
                        null
                    }
                    result = ScoreRecord(
                        score = totalScore,
                        playerName = player?.name ?: "Unknown",
                        gameId = game.id,
                        gameName = game.name,
                        date = game.updatedAt
                    )
                }
            }
        }
        
        return result
    }

    private suspend fun calculateMostYahtzeesInGame(
        games: List<YahtzeeGameEntity>,
        allScores: List<YahtzeeScoreEntity>,
        playerRepository: PlayerRepository
    ): YahtzeeRecord? {
        var maxYahtzees = 0
        var result: YahtzeeRecord? = null
        
        games.forEach { game ->
            val gameScores = allScores.filter { it.gameId == game.id }
            game.playerIds.split(",").forEach { playerId ->
                val playerYahtzeeScores = gameScores.filter { 
                    it.playerId == playerId.trim() && 
                    it.category == YahtzeeCategory.YAHTZEE.name && 
                    it.score >= 50
                }
                val yahtzeeCount = playerYahtzeeScores.sumOf { (it.score - 50) / 100 + 1 }
                
                if (yahtzeeCount > maxYahtzees) {
                    maxYahtzees = yahtzeeCount
                    val player = try {
                        playerRepository.getPlayerById(playerId.trim())
                    } catch (e: Exception) {
                        null
                    }
                    result = YahtzeeRecord(
                        count = yahtzeeCount,
                        playerName = player?.name ?: "Unknown",
                        gameId = game.id,
                        gameName = game.name,
                        date = game.updatedAt
                    )
                }
            }
        }
        
        return result.takeIf { it != null && it.count > 0 }
    }

    private suspend fun buildWinsLeaderboard(
        games: List<YahtzeeGameEntity>,
        playerRepository: PlayerRepository
    ): List<LeaderboardEntry> {
        val playerWins = mutableMapOf<String, Int>()
        val playerNames = mutableMapOf<String, String>()
        
        games.forEach { game ->
            game.winnerName?.let { winner ->
                game.playerIds.split(",").forEach { playerId ->
                    val trimmedId = playerId.trim()
                    val player = try {
                        playerRepository.getPlayerById(trimmedId)
                    } catch (e: Exception) {
                        null
                    }
                    playerNames[trimmedId] = player?.name ?: "Unknown"
                    
                    if (player?.name == winner) {
                        playerWins[trimmedId] = (playerWins[trimmedId] ?: 0) + 1
                    }
                }
            }
        }
        
        return playerWins
            .toList()
            .sortedByDescending { it.second }
            .take(YahtzeeStatisticsConstants.TOP_N_LEADERBOARD)
            .mapIndexed { index, (playerId, wins) ->
                val player = try {
                    playerRepository.getPlayerById(playerId)
                } catch (e: Exception) {
                    null
                }
                LeaderboardEntry(
                    rank = index + 1,
                    playerId = playerId,
                    playerName = player?.name ?: "Unknown",
                    value = wins,
                    secondaryValue = null
                )
            }
    }

    private suspend fun buildScoreLeaderboard(
        games: List<YahtzeeGameEntity>,
        allScores: List<YahtzeeScoreEntity>,
        playerRepository: PlayerRepository
    ): List<LeaderboardEntry> {
        val playerHighScores = mutableMapOf<String, Int>()
        val playerNames = mutableMapOf<String, String>()
        
        games.forEach { game ->
            val gameScores = allScores.filter { it.gameId == game.id }
            game.playerIds.split(",").forEach { playerId ->
                val trimmedId = playerId.trim()
                val player = try {
                    playerRepository.getPlayerById(trimmedId)
                } catch (e: Exception) {
                    null
                }
                playerNames[trimmedId] = player?.name ?: "Unknown"
                
                val playerScores = gameScores.filter { it.playerId == trimmedId }
                val baseScore = playerScores.sumOf { it.score }
                val upperScore = playerScores
                    .filter { YahtzeeCategory.valueOf(it.category).isUpperSection() }
                    .sumOf { it.score }
                val bonus = if (upperScore >= YahtzeeStatisticsConstants.UPPER_BONUS_THRESHOLD)
                    YahtzeeStatisticsConstants.UPPER_BONUS_VALUE else 0
                val totalScore = baseScore + bonus
                
                val currentHighScore = playerHighScores[trimmedId] ?: 0
                if (totalScore > currentHighScore) {
                    playerHighScores[trimmedId] = totalScore
                }
            }
        }
        
        return playerHighScores
            .toList()
            .sortedByDescending { it.second }
            .take(YahtzeeStatisticsConstants.TOP_N_LEADERBOARD)
            .mapIndexed { index, (playerId, score) ->
                val player = try {
                    playerRepository.getPlayerById(playerId)
                } catch (e: Exception) {
                    null
                }
                LeaderboardEntry(
                    rank = index + 1,
                    playerId = playerId,
                    playerName = player?.name ?: "Unknown",
                    value = score,
                    secondaryValue = null
                )
            }
    }

    private suspend fun buildYahtzeesLeaderboard(
        allScores: List<YahtzeeScoreEntity>,
        playerRepository: PlayerRepository
    ): List<LeaderboardEntry> {
        val playerYahtzees = mutableMapOf<String, Int>()
        
        allScores
            .filter { it.category == YahtzeeCategory.YAHTZEE.name && it.score >= 50 }
            .forEach { score ->
                val yahtzeeCount = (score.score - 50) / 100 + 1
                playerYahtzees[score.playerId] = (playerYahtzees[score.playerId] ?: 0) + yahtzeeCount
            }
        
        return playerYahtzees
            .toList()
            .sortedByDescending { it.second }
            .take(YahtzeeStatisticsConstants.TOP_N_LEADERBOARD)
            .mapIndexed { index, (playerId, count) ->
                val player = try {
                    playerRepository.getPlayerById(playerId)
                } catch (e: Exception) {
                    null
                }
                LeaderboardEntry(
                    rank = index + 1,
                    playerId = playerId,
                    playerName = player?.name ?: "Unknown",
                    value = count,
                    secondaryValue = null
                )
            }
    }

    private fun calculateGlobalCategoryStats(
        allScores: List<YahtzeeScoreEntity>,
        totalGames: Int
    ): Map<YahtzeeCategory, GlobalCategoryStat> {
        return YahtzeeCategory.entries.associate { category ->
            val categoryScores = allScores.filter { it.category == category.name }
            val average = if (categoryScores.isNotEmpty()) {
                categoryScores.map { it.score }.average()
            } else {
                0.0
            }
            val timesScored = categoryScores.count { it.score > 0 }
            val timesZeroed = if (totalGames > 0) {
                (totalGames * 8) - timesScored  // 8 players max per game assumption, conservative
            } else {
                0
            }
            val zeroRate = if (totalGames > 0) {
                (timesZeroed.toDouble() / ((totalGames * 8))) * 100
            } else {
                0.0
            }
            val best = categoryScores.maxOfOrNull { it.score } ?: 0
            val bestPlayer = categoryScores.maxByOrNull { it.score }?.playerId
            
            category to GlobalCategoryStat(
                category = category,
                average = average,
                totalTimesScored = timesScored,
                totalTimesZeroed = timesZeroed,
                zeroRate = zeroRate,
                highestScore = best,
                highestScorePlayer = bestPlayer
            )
        }
    }

    private suspend fun findMostActivePlayer(
        playerIds: List<String>,
        games: List<YahtzeeGameEntity>,
        playerRepository: PlayerRepository
    ): PlayerSummary? {
        val playerGames = mutableMapOf<String, Int>()
        
        games.forEach { game ->
            game.playerIds.split(",").forEach { playerId ->
                val trimmedId = playerId.trim()
                playerGames[trimmedId] = (playerGames[trimmedId] ?: 0) + 1
            }
        }
        
        val mostActive = playerGames.maxByOrNull { it.value } ?: return null
        val player = try {
            playerRepository.getPlayerById(mostActive.key)
        } catch (e: Exception) {
            null
        }
        
        return PlayerSummary(
            playerId = mostActive.key,
            playerName = player?.name ?: "Unknown",
            gamesPlayed = mostActive.value,
            metric = mostActive.value.toDouble()
        )
    }

    private suspend fun findLuckiestPlayer(
        playerIds: List<String>,
        games: List<YahtzeeGameEntity>,
        allScores: List<YahtzeeScoreEntity>,
        playerRepository: PlayerRepository
    ): PlayerSummary? {
        var maxRate = 0.0
        var luckiestId = ""
        
        playerIds.forEach { playerId ->
            val playerScores = allScores.filter { it.playerId == playerId }
            val totalYahtzees = countYahtzees(playerScores)
            val playerGames = games.count { it.playerIds.contains(playerId) }
            val rate = if (playerGames > 0) totalYahtzees.toDouble() / playerGames else 0.0
            
            if (rate > maxRate && playerGames > 0) {
                maxRate = rate
                luckiestId = playerId
            }
        }
        
        return if (luckiestId.isNotEmpty() && maxRate > 0) {
            val player = try {
                playerRepository.getPlayerById(luckiestId)
            } catch (e: Exception) {
                null
            }
            PlayerSummary(
                playerId = luckiestId,
                playerName = player?.name ?: "Unknown",
                gamesPlayed = 0,
                metric = maxRate
            )
        } else {
            null
        }
    }

    private suspend fun findMostConsistentPlayer(
        playerIds: List<String>,
        games: List<YahtzeeGameEntity>,
        allScores: List<YahtzeeScoreEntity>,
        playerRepository: PlayerRepository
    ): PlayerSummary? {
        var minVariance = Double.MAX_VALUE
        var consistentId = ""
        
        playerIds.forEach { playerId ->
            val gameScores = mutableListOf<Int>()
            
            games.forEach { game ->
                if (game.playerIds.contains(playerId)) {
                    val playerScores = allScores.filter { 
                        it.gameId == game.id && it.playerId == playerId 
                    }
                    val baseScore = playerScores.sumOf { it.score }
                    val upperScore = playerScores
                        .filter { YahtzeeCategory.valueOf(it.category).isUpperSection() }
                        .sumOf { it.score }
                    val bonus = if (upperScore >= YahtzeeStatisticsConstants.UPPER_BONUS_THRESHOLD)
                        YahtzeeStatisticsConstants.UPPER_BONUS_VALUE else 0
                    gameScores.add(baseScore + bonus)
                }
            }
            
            if (gameScores.size > 1) {
                val average = gameScores.average()
                val variance = gameScores.map { (it - average) * (it - average) }.average()
                
                if (variance < minVariance) {
                    minVariance = variance
                    consistentId = playerId
                }
            }
        }
        
        return if (consistentId.isNotEmpty() && minVariance < Double.MAX_VALUE) {
            val player = try {
                playerRepository.getPlayerById(consistentId)
            } catch (e: Exception) {
                null
            }
            PlayerSummary(
                playerId = consistentId,
                playerName = player?.name ?: "Unknown",
                gamesPlayed = 0,
                metric = minVariance
            )
        } else {
            null
        }
    }

    private suspend fun calculateGlobalRecentGames(
        games: List<YahtzeeGameEntity>,
        allScores: List<YahtzeeScoreEntity>,
        playerRepository: PlayerRepository
    ): List<GlobalGameSummary> {
        return games
            .sortedByDescending { it.updatedAt }
            .take(YahtzeeStatisticsConstants.RECENT_GAMES_COUNT)
            .mapNotNull { game ->
                val gameScores = allScores.filter { it.gameId == game.id }
                val totalScoresMap = game.playerIds.split(",").associate { pid ->
                    val playerGameScores = gameScores.filter { it.playerId == pid.trim() }
                    val baseScore = playerGameScores.sumOf { it.score }
                    val upperScore = playerGameScores
                        .filter { YahtzeeCategory.valueOf(it.category).isUpperSection() }
                        .sumOf { it.score }
                    val bonus = if (upperScore >= YahtzeeStatisticsConstants.UPPER_BONUS_THRESHOLD)
                        YahtzeeStatisticsConstants.UPPER_BONUS_VALUE else 0
                    pid.trim() to (baseScore + bonus)
                }
                
                val winner = totalScoresMap.maxByOrNull { it.value }
                
                if (winner != null) {
                    val player = try {
                        playerRepository.getPlayerById(winner.key)
                    } catch (e: Exception) {
                        null
                    }
                    GlobalGameSummary(
                        gameId = game.id,
                        gameName = game.name,
                        winnerName = player?.name ?: "Unknown",
                        winnerScore = winner.value,
                        playerCount = game.playerCount,
                        completedAt = game.updatedAt
                    )
                } else {
                    null
                }
            }
    }
}
