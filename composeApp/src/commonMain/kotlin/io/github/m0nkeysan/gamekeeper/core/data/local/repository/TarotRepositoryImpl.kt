package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import androidx.room.withTransaction
import io.github.m0nkeysan.gamekeeper.core.data.local.database.GameDatabase
import io.github.m0nkeysan.gamekeeper.core.data.local.database.TarotDao
import io.github.m0nkeysan.gamekeeper.core.data.local.database.TarotGameEntity
import io.github.m0nkeysan.gamekeeper.core.data.local.database.TarotRoundEntity
import io.github.m0nkeysan.gamekeeper.core.domain.repository.TarotRepository
import io.github.m0nkeysan.gamekeeper.core.model.ChelemType
import io.github.m0nkeysan.gamekeeper.core.model.PoigneeLevel
import io.github.m0nkeysan.gamekeeper.core.model.TarotBid
import io.github.m0nkeysan.gamekeeper.core.model.TarotGame
import io.github.m0nkeysan.gamekeeper.core.model.TarotRound
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TarotRepositoryImpl(
    private val dao: TarotDao,
    private val database: GameDatabase
) : TarotRepository {
    
    override fun getAllGames(): Flow<List<TarotGame>> = dao.getAllGames().map { entities ->
        entities.map { it.toDomain() }
    }
    
    override suspend fun getGameById(id: String): TarotGame? = try {
        dao.getGameById(id)?.toDomain()
    } catch (e: Exception) {
        null
    }
    
    override suspend fun saveGame(game: TarotGame) {
        try {
            dao.insertGame(game.toEntity())
        } catch (e: Exception) {
            throw Exception("Failed to save Tarot game: ${e.message}", e)
        }
    }
    
    override suspend fun deleteGame(game: TarotGame) {
        try {
            // Use transaction for atomic delete operation
            database.withTransaction {
                dao.deleteRoundsForGame(game.id)
                dao.deleteGame(game.toEntity())
            }
        } catch (e: Exception) {
            throw Exception("Failed to delete Tarot game: ${e.message}", e)
        }
    }
    
    override fun getRoundsForGame(gameId: String): Flow<List<TarotRound>> = 
        dao.getRoundsForGame(gameId).map { entities ->
            entities.map { it.toDomain() }
        }
    
    override suspend fun addRound(round: TarotRound, gameId: String) {
        try {
            dao.insertRound(round.toEntity(gameId))
        } catch (e: Exception) {
            throw Exception("Failed to add Tarot round: ${e.message}", e)
        }
    }
}

// Mapper functions - Entity to Domain
private fun TarotGameEntity.toDomain() = TarotGame(
    id = id,
    players = emptyList(), // Players are resolved separately by ViewModel
    createdAt = createdAt,
    updatedAt = updatedAt,
    playerCount = playerCount,
    rounds = emptyList(), // Rounds are loaded separately
    name = name,
    playerIds = playerIds
)

private fun TarotRoundEntity.toDomain() = TarotRound(
    id = id,
    roundNumber = roundNumber,
    takerPlayerId = takerPlayerId,
    bid = TarotBid.valueOf(bid),
    bouts = bouts,
    pointsScored = pointsScored,
    hasPetitAuBout = hasPetitAuBout,
    hasPoignee = hasPoignee,
    poigneeLevel = poigneeLevel?.let { PoigneeLevel.valueOf(it) },
    chelem = ChelemType.valueOf(chelem),
    calledPlayerId = calledPlayerId,
    score = score
)

// Mapper functions - Domain to Entity
private fun TarotGame.toEntity() = TarotGameEntity(
    id = id,
    name = name,
    playerCount = playerCount,
    playerIds = playerIds.ifEmpty { players.joinToString(",") { it.id } },
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun TarotRound.toEntity(gameId: String) = TarotRoundEntity(
    id = id,
    gameId = gameId,
    roundNumber = roundNumber,
    takerPlayerId = takerPlayerId,
    bid = bid.name,
    bouts = bouts,
    pointsScored = pointsScored,
    hasPetitAuBout = hasPetitAuBout,
    hasPoignee = hasPoignee,
    poigneeLevel = poigneeLevel?.name,
    chelem = chelem.name,
    calledPlayerId = calledPlayerId,
    score = score
)
