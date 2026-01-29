package io.github.m0nkeysan.tally.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.m0nkeysan.tally.core.domain.repository.YahtzeeRepository
import io.github.m0nkeysan.tally.core.model.PlayerYahtzeeScore
import io.github.m0nkeysan.tally.core.model.YahtzeeCategory
import io.github.m0nkeysan.tally.core.model.YahtzeeGame
import io.github.m0nkeysan.tally.core.model.YahtzeeScore
import io.github.m0nkeysan.tally.database.YahtzeeGameEntity
import io.github.m0nkeysan.tally.database.YahtzeeQueries
import io.github.m0nkeysan.tally.database.YahtzeeScoreEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class YahtzeeRepositoryImpl(
    private val yahtzeeQueries: YahtzeeQueries
) : YahtzeeRepository {
    
    override fun getAllGames(): Flow<List<YahtzeeGame>> = 
        yahtzeeQueries.selectAllGames()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun getGameById(id: String): YahtzeeGame? = 
        yahtzeeQueries.selectGameById(id).awaitAsOneOrNull()?.toDomain()

    override suspend fun saveGame(game: YahtzeeGame) {
        yahtzeeQueries.insertGame(
            YahtzeeGameEntity(
                id = game.id,
                name = game.name,
                playerCount = game.playerCount.toLong(),
                playerIds = game.playerIds.ifEmpty { game.players.joinToString(",") { it.id } },
                firstPlayerId = game.firstPlayerId,
                currentPlayerId = game.currentPlayerId,
                isFinished = if (game.isFinished) 1L else 0L,
                winnerName = game.winnerName,
                createdAt = game.createdAt,
                updatedAt = game.updatedAt
            )
        )
    }

    override suspend fun deleteGame(game: YahtzeeGame) {
        yahtzeeQueries.transaction {
            yahtzeeQueries.deleteScoresForGame(game.id)
            yahtzeeQueries.deleteGame(game.id)
        }
    }

    override fun getScoresForGame(gameId: String): Flow<List<PlayerYahtzeeScore>> = 
        yahtzeeQueries.selectScoresForGame(gameId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { 
                    PlayerYahtzeeScore(
                        playerId = it.playerId,
                        score = it.toDomain()
                    )
                }
            }

    override suspend fun saveScore(score: YahtzeeScore, gameId: String, playerId: String) {
        yahtzeeQueries.insertScore(
            YahtzeeScoreEntity(
                id = "${gameId}_${playerId}_${score.category.name}",
                gameId = gameId,
                playerId = playerId,
                category = score.category.name,
                score = score.value.toLong()
            )
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

    private fun YahtzeeScoreEntity.toDomain() = YahtzeeScore(
        category = YahtzeeCategory.valueOf(category),
        value = score.toInt(),
        isScored = true
    )
}
