package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeDao
import io.github.m0nkeysan.gamekeeper.core.domain.engine.YahtzeeStatisticsEngine
import io.github.m0nkeysan.gamekeeper.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.YahtzeeStatisticsRepository
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeePlayerStatistics
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeGlobalStatistics

class YahtzeeStatisticsRepositoryImpl(
    private val dao: YahtzeeDao,
    private val playerRepository: PlayerRepository
) : YahtzeeStatisticsRepository {

    override suspend fun getPlayerStatistics(playerId: String): YahtzeePlayerStatistics {
        val games = dao.getAllGamesForPlayer(playerId)
        val playerScores = dao.getAllScoresForPlayer(playerId)
        // Get all scores from the games this player participated in to calculate ranks correctly
        val allScoresFromPlayerGames = dao.getAllScoresFromFinishedGames()
            .filter { score -> games.any { it.id == score.gameId } }
        val player = playerRepository.getPlayerById(playerId)
        val playerName = player?.name ?: "Unknown"
        
        return YahtzeeStatisticsEngine.calculatePlayerStatistics(
            playerId = playerId,
            playerName = playerName,
            games = games,
            playerScores = playerScores,
            allScores = allScoresFromPlayerGames
        )
    }

    override suspend fun getAvailablePlayers(): List<Player> {
        // Get all unique player IDs from finished games
        val playerIds = dao.getDistinctPlayerIds()
            .flatMap { it.split(",") }
            .distinct()
        
        // Fetch player objects for all IDs
        return playerIds
            .mapNotNull { playerId ->
                try {
                    playerRepository.getPlayerById(playerId.trim())
                } catch (e: Exception) {
                    null
                }
            }
            .sortedBy { it.name }
    }
    
    override suspend fun getGlobalStatistics(): YahtzeeGlobalStatistics {
        val allGames = dao.getAllFinishedGames()
        val allScores = dao.getAllScoresFromFinishedGames()
        
        return YahtzeeStatisticsEngine.calculateGlobalStatistics(
            allGames = allGames,
            allScores = allScores,
            playerRepository = playerRepository
        )
    }
}
