package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeDao
import io.github.m0nkeysan.gamekeeper.core.domain.engine.YahtzeeStatisticsEngine
import io.github.m0nkeysan.gamekeeper.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.YahtzeeStatisticsRepository
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeePlayerStatistics

class YahtzeeStatisticsRepositoryImpl(
    private val dao: YahtzeeDao,
    private val playerRepository: PlayerRepository
) : YahtzeeStatisticsRepository {

    override suspend fun getPlayerStatistics(playerId: String): YahtzeePlayerStatistics {
        val games = dao.getAllGamesForPlayer(playerId)
        val allScores = dao.getAllScoresForPlayer(playerId)
        val player = playerRepository.getPlayerById(playerId)
        val playerName = player?.name ?: "Unknown"
        
        return YahtzeeStatisticsEngine.calculatePlayerStatistics(
            playerId = playerId,
            playerName = playerName,
            games = games,
            allScores = allScores
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
}
