package io.github.m0nkeysan.tally.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import io.github.m0nkeysan.tally.core.domain.engine.YahtzeeStatisticsEngine
import io.github.m0nkeysan.tally.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.tally.core.domain.repository.YahtzeeStatisticsRepository
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.core.model.YahtzeeGame
import io.github.m0nkeysan.tally.core.model.YahtzeeGlobalStatistics
import io.github.m0nkeysan.tally.core.model.YahtzeePlayerStatistics
import io.github.m0nkeysan.tally.database.YahtzeeGameEntity
import io.github.m0nkeysan.tally.database.YahtzeeQueries
import io.github.m0nkeysan.tally.database.YahtzeeScoreEntity

class YahtzeeStatisticsRepositoryImpl(
    private val yahtzeeQueries: YahtzeeQueries,
    private val playerRepository: PlayerRepository
) : YahtzeeStatisticsRepository {

    override suspend fun getPlayerStatistics(playerId: String): YahtzeePlayerStatistics {
        val games = yahtzeeQueries.selectGamesByPlayer(playerId).awaitAsList().map { it.toDomain() }
        val playerScores = yahtzeeQueries.selectScoresForPlayer(playerId).awaitAsList().map { it.toScoreData() }
        
        val gameIds = games.map { it.id }
        val allScoresFromPlayerGames = if (gameIds.isNotEmpty()) {
            yahtzeeQueries.selectScoresForGames(gameIds).awaitAsList().map { it.toScoreData() }
        } else {
            emptyList()
        }
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
        val playerIds = yahtzeeQueries.getDistinctPlayerIds().awaitAsList()
            .flatMap { it.split(",") }
            .distinct()
        
        return playerIds
            .mapNotNull { playerId ->
                try {
                    playerRepository.getPlayerById(playerId)
                } catch (e: Exception) {
                    null
                }
            }
            .sortedBy { it.name }
    }
    
    override suspend fun getGlobalStatistics(): YahtzeeGlobalStatistics {
        val allGames = yahtzeeQueries.selectFinishedGames().awaitAsList().map { it.toDomain() }
        val allScores = yahtzeeQueries.selectAllFinishedScores().awaitAsList().map { it.toScoreData() }
        
        return YahtzeeStatisticsEngine.calculateGlobalStatistics(
            allGames = allGames,
            allScores = allScores,
            playerRepository = playerRepository
        )
    }

    private fun YahtzeeGameEntity.toDomain() = YahtzeeGame(
        id = id,
        players = emptyList(),
        createdAt = createdAt,
        updatedAt = updatedAt,
        name = name,
        playerIds = playerIds,
        firstPlayerId = firstPlayerId,
        currentPlayerId = currentPlayerId,
        isFinished = isFinished != 0L,
        winnerName = winnerName
    )

    private fun YahtzeeScoreEntity.toScoreData() = object : io.github.m0nkeysan.tally.core.domain.data.YahtzeeScoreData {
        override val id: String = this@toScoreData.id
        override val gameId: String = this@toScoreData.gameId
        override val playerId: String = this@toScoreData.playerId
        override val category: String = this@toScoreData.category
        override val score: Int = this@toScoreData.score.toInt()
    }
}
