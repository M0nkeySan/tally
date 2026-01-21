package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import io.github.m0nkeysan.gamekeeper.core.data.local.database.GameDatabase
import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeDao
import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeGameEntity
import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeScoreEntity
import io.github.m0nkeysan.gamekeeper.core.domain.repository.YahtzeeRepository
import io.github.m0nkeysan.gamekeeper.core.model.PlayerYahtzeeScore
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeCategory
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeGame
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeScore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class YahtzeeRepositoryImpl(
    private val dao: YahtzeeDao,
    private val database: GameDatabase
) : YahtzeeRepository {
    
    override fun getAllGames(): Flow<List<YahtzeeGame>> = dao.getAllGames().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun getGameById(id: String): YahtzeeGame? = try {
        dao.getGameById(id)?.toDomain()
    } catch (e: Exception) {
        null
    }

    override suspend fun saveGame(game: YahtzeeGame) {
        try {
            dao.insertGame(game.toEntity())
        } catch (e: Exception) {
            throw Exception("Failed to save Yahtzee game: ${e.message}", e)
        }
    }

    override suspend fun deleteGame(game: YahtzeeGame) {
        try {
            database.useWriterConnection { transactor ->
                transactor.immediateTransaction<Unit> {
                    dao.deleteScoresForGame(game.id)
                    dao.deleteGame(game.toEntity())
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to delete Yahtzee game: ${e.message}", e)
        }
    }

    override fun getScoresForGame(gameId: String): Flow<List<PlayerYahtzeeScore>> = 
        dao.getScoresForGame(gameId).map { entities ->
            entities.map { 
                PlayerYahtzeeScore(
                    playerId = it.playerId,
                    score = it.toDomain()
                )
            }
        }

    override suspend fun saveScore(score: YahtzeeScore, gameId: String, playerId: String) {
        try {
            dao.insertScore(score.toEntity(gameId, playerId))
        } catch (e: Exception) {
            throw Exception("Failed to save Yahtzee score: ${e.message}", e)
        }
    }
}

// Mapper functions - Entity to Domain
private fun YahtzeeGameEntity.toDomain() = YahtzeeGame(
    id = id,
    players = emptyList(), // Players are resolved separately by ViewModel
    createdAt = createdAt,
    updatedAt = updatedAt,
    name = name,
    playerIds = playerIds,
    firstPlayerId = firstPlayerId,
    currentPlayerId = currentPlayerId,
    isFinished = isFinished,
    winnerName = winnerName
)

private fun YahtzeeScoreEntity.toDomain() = YahtzeeScore(
    category = YahtzeeCategory.valueOf(category),
    value = score,
    isScored = true
)

// Mapper functions - Domain to Entity

private fun YahtzeeGame.toEntity() = YahtzeeGameEntity(
    id = id,
    name = name,
    playerCount = playerCount,
    playerIds = playerIds.ifEmpty { players.joinToString(",") { it.id } },
    firstPlayerId = firstPlayerId,
    currentPlayerId = currentPlayerId,
    isFinished = isFinished,
    winnerName = winnerName,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun YahtzeeScore.toEntity(gameId: String, playerId: String) = YahtzeeScoreEntity(
    gameId = gameId,
    playerId = playerId,
    category = category.name,
    score = value
)
