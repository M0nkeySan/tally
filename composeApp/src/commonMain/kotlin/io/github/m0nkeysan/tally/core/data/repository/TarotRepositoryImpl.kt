package io.github.m0nkeysan.tally.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.m0nkeysan.tally.core.domain.repository.TarotRepository
import io.github.m0nkeysan.tally.core.model.ChelemType
import io.github.m0nkeysan.tally.core.model.PoigneeLevel
import io.github.m0nkeysan.tally.core.model.TarotBid
import io.github.m0nkeysan.tally.core.model.TarotGame
import io.github.m0nkeysan.tally.core.model.TarotRound
import io.github.m0nkeysan.tally.database.TarotGameEntity
import io.github.m0nkeysan.tally.database.TarotQueries
import io.github.m0nkeysan.tally.database.TarotRoundEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TarotRepositoryImpl(
    private val tarotQueries: TarotQueries
) : TarotRepository {
    
    override fun getAllGames(): Flow<List<TarotGame>> = 
        tarotQueries.selectAllGames()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    
    override suspend fun getGameById(id: String): TarotGame? = 
        tarotQueries.selectGameById(id).awaitAsOneOrNull()?.toDomain()
    
    override suspend fun saveGame(game: TarotGame) {
        tarotQueries.insertGame(
            TarotGameEntity(
                id = game.id,
                name = game.name,
                playerCount = game.playerCount.toLong(),
                playerIds = game.playerIds.ifEmpty { game.players.joinToString(",") { it.id } },
                createdAt = game.createdAt,
                updatedAt = game.updatedAt
            )
        )
    }
    
    override suspend fun deleteGame(game: TarotGame) {
        tarotQueries.transaction {
            tarotQueries.deleteRoundsForGame(game.id)
            tarotQueries.deleteGame(game.id)
        }
    }
    
    override fun getRoundsForGame(gameId: String): Flow<List<TarotRound>> = 
        tarotQueries.selectRoundsForGame(gameId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    
    override suspend fun addRound(round: TarotRound, gameId: String) {
        tarotQueries.insertRound(
            TarotRoundEntity(
                id = round.id,
                gameId = gameId,
                roundNumber = round.roundNumber.toLong(),
                takerPlayerId = round.takerPlayerId,
                bid = round.bid.name,
                bouts = round.bouts.toLong(),
                pointsScored = round.pointsScored.toLong(),
                hasPetitAuBout = if (round.hasPetitAuBout) 1L else 0L,
                hasPoignee = if (round.hasPoignee) 1L else 0L,
                poigneeLevel = round.poigneeLevel?.name,
                chelem = round.chelem.name,
                calledPlayerId = round.calledPlayerId,
                score = round.score.toLong()
            )
        )
    }

    private fun TarotGameEntity.toDomain() = TarotGame(
        id = id,
        players = emptyList(),
        createdAt = createdAt,
        updatedAt = updatedAt,
        playerCount = playerCount.toInt(),
        rounds = emptyList(),
        name = name,
        playerIds = playerIds
    )

    private fun TarotRoundEntity.toDomain() = TarotRound(
        id = id,
        roundNumber = roundNumber.toInt(),
        takerPlayerId = takerPlayerId,
        bid = TarotBid.valueOf(bid),
        bouts = bouts.toInt(),
        pointsScored = pointsScored.toInt(),
        hasPetitAuBout = hasPetitAuBout != 0L,
        hasPoignee = hasPoignee != 0L,
        poigneeLevel = poigneeLevel?.let { PoigneeLevel.valueOf(it) },
        chelem = ChelemType.valueOf(chelem),
        calledPlayerId = calledPlayerId,
        score = score.toInt()
    )
}
