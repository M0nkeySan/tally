package io.github.m0nkeysan.tally.core.domain.engine

import io.github.m0nkeysan.tally.core.domain.data.YahtzeeGameData
import io.github.m0nkeysan.tally.core.domain.data.YahtzeeScoreData
import io.github.m0nkeysan.tally.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.tally.core.model.CategoryRecord
import io.github.m0nkeysan.tally.core.model.CategoryStat
import io.github.m0nkeysan.tally.core.model.GameSummary
import io.github.m0nkeysan.tally.core.model.GlobalCategoryStat
import io.github.m0nkeysan.tally.core.model.GlobalGameSummary
import io.github.m0nkeysan.tally.core.model.LeaderboardEntry
import io.github.m0nkeysan.tally.core.model.PlayerSummary
import io.github.m0nkeysan.tally.core.model.ScoreRecord
import io.github.m0nkeysan.tally.core.model.YahtzeeCategory
import io.github.m0nkeysan.tally.core.model.YahtzeeGlobalStatistics
import io.github.m0nkeysan.tally.core.model.YahtzeePlayerStatistics
import io.github.m0nkeysan.tally.core.model.YahtzeeRecord
import io.github.m0nkeysan.tally.core.model.YahtzeeStatisticsConstants

/**
 * Engine for calculating comprehensive Yahtzee player statistics
 */
object YahtzeeStatisticsEngine {

    /**
     * Build an in-memory cache of player IDs to names.
     * Single batch query instead of N individual queries.
     */
    private suspend fun buildPlayerCache(
        playerIds: List<String>,
        playerRepository: PlayerRepository
    ): Map<String, String> {
        return try {
            playerRepository.getPlayersByIds(playerIds.distinct())
                .associate { it.id to it.name }
        } catch (e: Exception) {
            // Fallback to individual lookups if batch fails
            playerIds.distinct().mapNotNull { playerId ->
                try {
                    val player = playerRepository.getPlayerById(playerId)
                    player?.let { playerId to it.name }
                } catch (e: Exception) {
                    playerId to "Unknown"
                }
            }.toMap()
        }
    }

    /**
     * Calculate complete statistics for a player
     */
    fun calculatePlayerStatistics(
        playerId: String,
        playerName: String,
        games: List<YahtzeeGameData>,
        playerScores: List<YahtzeeScoreData>,
        allScores: List<YahtzeeScoreData>
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
    internal fun calculateGameTotals(
        games: List<YahtzeeGameData>,
        allScores: List<YahtzeeScoreData>,
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
    internal fun calculateCategoryStats(
        playerScores: List<YahtzeeScoreData>,
        totalGames: Int
    ): Map<YahtzeeCategory, CategoryStat> {
        return YahtzeeCategory.entries.associateWith { category ->
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

            CategoryStat(
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
    internal fun countYahtzees(playerScores: List<YahtzeeScoreData>): Int {
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
    internal fun calculateUpperBonusRate(
        games: List<YahtzeeGameData>,
        allScores: List<YahtzeeScoreData>,
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
    internal fun calculateUpperSectionAverage(playerScores: List<YahtzeeScoreData>): Double {
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
    internal fun calculateLowerSectionAverage(playerScores: List<YahtzeeScoreData>): Double {
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
        games: List<YahtzeeGameData>,
        allScores: List<YahtzeeScoreData>
    ): List<GameSummary> {
        return games
            .filter { it.isFinished }
            .sortedByDescending { it.updatedAt }
            .take(10)
            .map { game ->
                val gameScores = allScores.filter { it.gameId == game.id }
                val totalScoresMap = game.playerIds.split(",").associateWith { pid ->
                    val playerGameScores = gameScores.filter { it.playerId == pid }
                    val baseScore = playerGameScores.sumOf { it.score }
                    val upperScore = playerGameScores
                        .filter { YahtzeeCategory.valueOf(it.category).isUpperSection() }
                        .sumOf { it.score }
                    val bonus = if (upperScore >= 63) 35 else 0
                    (baseScore + bonus)
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
        allGames: List<YahtzeeGameData>,
        allScores: List<YahtzeeScoreData>,
        playerRepository: PlayerRepository
    ): YahtzeeGlobalStatistics {
        val totalGames = allGames.size
        val finishedGames = allGames.count { it.isFinished }

        // Get unique players
        val uniquePlayerIds = allGames
            .flatMap { it.playerIds.split(",") }
            .distinct()
        val totalPlayers = uniquePlayerIds.size

        // Build player cache once for all operations
        val playerCache = buildPlayerCache(uniquePlayerIds, playerRepository)

        // Calculate records and summaries
        val allTimeHighScore = calculateAllTimeHighScore(allGames, allScores, playerCache)
        val totalYahtzees = countYahtzees(allScores)
        val yahtzeeRate = if (finishedGames > 0) totalYahtzees.toDouble() / finishedGames else 0.0
        val mostYahtzeesInGame = calculateMostYahtzeesInGame(allGames, allScores, playerCache)

        // Calculate average score per player across all games
        val allPlayerScores = allGames.flatMap { game ->
            val gameScores = allScores.filter { it.gameId == game.id }
            game.playerIds.split(",").map { playerId ->
                val playerScores = gameScores.filter { it.playerId == playerId }
                val baseScore = playerScores.sumOf { it.score }
                val upperScore = playerScores
                    .filter { YahtzeeCategory.valueOf(it.category).isUpperSection() }
                    .sumOf { it.score }
                val bonus = if (upperScore >= YahtzeeStatisticsConstants.UPPER_BONUS_THRESHOLD)
                    YahtzeeStatisticsConstants.UPPER_BONUS_VALUE else 0
                baseScore + bonus
            }
        }
        val averageScore = if (allPlayerScores.isNotEmpty()) allPlayerScores.average() else 0.0

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
            .filter { it.key != YahtzeeCategory.YAHTZEE }
            .maxByOrNull { it.value.average }
            ?.let { CategoryRecord(it.key, it.value.average) }

        // Leaderboards
        val topPlayersByWins = buildWinsLeaderboard(allGames, playerCache)
        val topPlayersByScore = buildScoreLeaderboard(allGames, allScores, playerCache)
        val topPlayersByYahtzees = buildYahtzeesLeaderboard(allScores, playerCache)

        // Recent games
        val recentGames = calculateGlobalRecentGames(allGames, allScores, playerCache)

        // Fun stats
        val estimatedDiceRolls = (finishedGames.toLong() *
            YahtzeeStatisticsConstants.ESTIMATED_TURNS_PER_GAME *
            YahtzeeStatisticsConstants.ESTIMATED_ROLLS_PER_TURN)
        val luckiestPlayer = findLuckiestPlayer(uniquePlayerIds, allGames, allScores, playerCache)
        val mostConsistentPlayer = findMostConsistentPlayer(uniquePlayerIds, allGames, allScores, playerCache)
        val totalPointsScored = allPlayerScores.sumOf { it.toLong() }
        val averagePlayersPerGame = if (allGames.isNotEmpty()) {
            allGames.mapNotNull { it.playerIds.split(",").size.takeIf { s -> s > 0 } }.average()
        } else {
            0.0
        }

        val mostActivePlayer = findMostActivePlayer(allGames, playerCache)

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

    private fun calculateAllTimeHighScore(
        games: List<YahtzeeGameData>,
        allScores: List<YahtzeeScoreData>,
        playerCache: Map<String, String>
    ): ScoreRecord? {
        var maxScore = 0
        var result: ScoreRecord? = null

        games.forEach { game ->
            val gameScores = allScores.filter { it.gameId == game.id }
            game.playerIds.split(",").forEach { playerId ->
                val playerScores = gameScores.filter { it.playerId == playerId }
                val baseScore = playerScores.sumOf { it.score }
                val upperScore = playerScores
                    .filter { YahtzeeCategory.valueOf(it.category).isUpperSection() }
                    .sumOf { it.score }
                val bonus = if (upperScore >= YahtzeeStatisticsConstants.UPPER_BONUS_THRESHOLD)
                    YahtzeeStatisticsConstants.UPPER_BONUS_VALUE else 0
                val totalScore = baseScore + bonus

                if (totalScore > maxScore) {
                    maxScore = totalScore
                    result = ScoreRecord(
                        score = totalScore,
                        playerName = playerCache[playerId] ?: "Unknown",
                        gameId = game.id,
                        gameName = game.name,
                        date = game.updatedAt
                    )
                }
            }
        }

        return result
    }

    private fun calculateMostYahtzeesInGame(
        games: List<YahtzeeGameData>,
        allScores: List<YahtzeeScoreData>,
        playerCache: Map<String, String>
    ): YahtzeeRecord? {
        var maxYahtzees = 0
        var result: YahtzeeRecord? = null

        games.forEach { game ->
            val gameScores = allScores.filter { it.gameId == game.id }
            game.playerIds.split(",").forEach { playerId ->
                val playerYahtzeeScores = gameScores.filter {
                    it.playerId == playerId &&
                    it.category == YahtzeeCategory.YAHTZEE.name &&
                    it.score >= 50
                }
                val yahtzeeCount = playerYahtzeeScores.sumOf { (it.score - 50) / 100 + 1 }

                if (yahtzeeCount > maxYahtzees) {
                    maxYahtzees = yahtzeeCount
                    result = YahtzeeRecord(
                        count = yahtzeeCount,
                        playerName = playerCache[playerId] ?: "Unknown",
                        gameId = game.id,
                        gameName = game.name,
                        date = game.updatedAt
                    )
                }
            }
        }

        return result.takeIf { it != null && it.count > 0 }
    }

    private fun buildWinsLeaderboard(
        games: List<YahtzeeGameData>,
        playerCache: Map<String, String>
    ): List<LeaderboardEntry> {
        val playerWins = mutableMapOf<String, Int>()

        games.forEach { game ->
            game.winnerName?.let { winner ->
                game.playerIds.split(",").forEach { playerId ->
                    val playerName = playerCache[playerId] ?: "Unknown"

                    if (playerName == winner) {
                        playerWins[playerId] = (playerWins[playerId] ?: 0) + 1
                    }
                }
            }
        }

        return playerWins
            .toList()
            .sortedByDescending { it.second }
            .take(YahtzeeStatisticsConstants.TOP_N_LEADERBOARD)
            .mapIndexed { index, (playerId, wins) ->
                LeaderboardEntry(
                    rank = index + 1,
                    playerId = playerId,
                    playerName = playerCache[playerId] ?: "Unknown",
                    value = wins,
                    secondaryValue = null
                )
            }
    }

    private fun buildScoreLeaderboard(
        games: List<YahtzeeGameData>,
        allScores: List<YahtzeeScoreData>,
        playerCache: Map<String, String>
    ): List<LeaderboardEntry> {
        val playerHighScores = mutableMapOf<String, Int>()

        games.forEach { game ->
            val gameScores = allScores.filter { it.gameId == game.id }
            game.playerIds.split(",").forEach { playerId ->
                val playerScores = gameScores.filter { it.playerId == playerId }
                val baseScore = playerScores.sumOf { it.score }
                val upperScore = playerScores
                    .filter { YahtzeeCategory.valueOf(it.category).isUpperSection() }
                    .sumOf { it.score }
                val bonus = if (upperScore >= YahtzeeStatisticsConstants.UPPER_BONUS_THRESHOLD)
                    YahtzeeStatisticsConstants.UPPER_BONUS_VALUE else 0
                val totalScore = baseScore + bonus

                val currentHighScore = playerHighScores[playerId] ?: 0
                if (totalScore > currentHighScore) {
                    playerHighScores[playerId] = totalScore
                }
            }
        }

        return playerHighScores
            .toList()
            .sortedByDescending { it.second }
            .take(YahtzeeStatisticsConstants.TOP_N_LEADERBOARD)
            .mapIndexed { index, (playerId, score) ->
                LeaderboardEntry(
                    rank = index + 1,
                    playerId = playerId,
                    playerName = playerCache[playerId] ?: "Unknown",
                    value = score,
                    secondaryValue = null
                )
            }
    }

    private fun buildYahtzeesLeaderboard(
        allScores: List<YahtzeeScoreData>,
        playerCache: Map<String, String>
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
                LeaderboardEntry(
                    rank = index + 1,
                    playerId = playerId,
                    playerName = playerCache[playerId] ?: "Unknown",
                    value = count,
                    secondaryValue = null
                )
            }
    }

    private fun calculateGlobalCategoryStats(
        allScores: List<YahtzeeScoreData>,
        totalGames: Int
    ): Map<YahtzeeCategory, GlobalCategoryStat> {
        return YahtzeeCategory.entries.associateWith { category ->
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

            GlobalCategoryStat(
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

    private fun findMostActivePlayer(
        games: List<YahtzeeGameData>,
        playerCache: Map<String, String>
    ): PlayerSummary? {
        val playerGames = mutableMapOf<String, Int>()

        games.forEach { game ->
            game.playerIds.split(",").forEach { playerId ->
                playerGames[playerId] = (playerGames[playerId] ?: 0) + 1
            }
        }

        val mostActive = playerGames.maxByOrNull { it.value } ?: return null

        return PlayerSummary(
            playerId = mostActive.key,
            playerName = playerCache[mostActive.key] ?: "Unknown",
            gamesPlayed = mostActive.value,
            metric = mostActive.value.toDouble()
        )
    }

    private fun findLuckiestPlayer(
        playerIds: List<String>,
        games: List<YahtzeeGameData>,
        allScores: List<YahtzeeScoreData>,
        playerCache: Map<String, String>
    ): PlayerSummary? {
        var maxRate = 0.0
        var luckiestId = ""

        playerIds.forEach { playerId ->
            val playerScores = allScores.filter { it.playerId == playerId }
            val totalYahtzees = countYahtzees(playerScores)
            val playerGames = games.count { it.playerIds.contains(playerId) }
            val rate = if (playerGames > 0) totalYahtzees.toDouble() / playerGames else 0.0

            if (rate > maxRate) {
                maxRate = rate
                luckiestId = playerId
            }
        }

        return if (luckiestId.isNotEmpty() && maxRate > 0) {
            PlayerSummary(
                playerId = luckiestId,
                playerName = playerCache[luckiestId] ?: "Unknown",
                gamesPlayed = 0,
                metric = maxRate
            )
        } else {
            null
        }
    }

    private fun findMostConsistentPlayer(
        playerIds: List<String>,
        games: List<YahtzeeGameData>,
        allScores: List<YahtzeeScoreData>,
        playerCache: Map<String, String>
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
            PlayerSummary(
                playerId = consistentId,
                playerName = playerCache[consistentId] ?: "Unknown",
                gamesPlayed = 0,
                metric = minVariance
            )
        } else {
            null
        }
    }

    private fun calculateGlobalRecentGames(
        games: List<YahtzeeGameData>,
        allScores: List<YahtzeeScoreData>,
        playerCache: Map<String, String>
    ): List<GlobalGameSummary> {
        return games
            .sortedByDescending { it.updatedAt }
            .take(YahtzeeStatisticsConstants.RECENT_GAMES_COUNT)
            .mapNotNull { game ->
                val gameScores = allScores.filter { it.gameId == game.id }
                val totalScoresMap = game.playerIds.split(",").associateWith { pid ->
                    val playerGameScores = gameScores.filter { it.playerId == pid }
                    val baseScore = playerGameScores.sumOf { it.score }
                    val upperScore = playerGameScores
                        .filter { YahtzeeCategory.valueOf(it.category).isUpperSection() }
                        .sumOf { it.score }
                    val bonus = if (upperScore >= YahtzeeStatisticsConstants.UPPER_BONUS_THRESHOLD)
                        YahtzeeStatisticsConstants.UPPER_BONUS_VALUE else 0
                    (baseScore + bonus)
                }

                val winner = totalScoresMap.maxByOrNull { it.value }

                if (winner != null) {
                    GlobalGameSummary(
                        gameId = game.id,
                        gameName = game.name,
                        winnerName = playerCache[winner.key] ?: "Unknown",
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