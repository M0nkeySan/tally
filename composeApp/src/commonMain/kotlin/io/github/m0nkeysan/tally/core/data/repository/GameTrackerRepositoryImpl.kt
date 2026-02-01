package io.github.m0nkeysan.tally.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.m0nkeysan.tally.core.domain.model.DurationMode
import io.github.m0nkeysan.tally.core.domain.model.ScoringLogic
import io.github.m0nkeysan.tally.core.domain.repository.GameTrackerRepository
import io.github.m0nkeysan.tally.core.model.GameTrackerGame
import io.github.m0nkeysan.tally.core.model.GameTrackerRound
import io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis
import io.github.m0nkeysan.tally.database.GameTrackerGameEntity
import io.github.m0nkeysan.tally.database.GameTrackerQueries
import io.github.m0nkeysan.tally.database.GameTrackerRoundEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameTrackerRepositoryImpl(
    private val gameTrackerQueries: GameTrackerQueries
) : GameTrackerRepository {

    override fun getAllGames(): Flow<List<GameTrackerGame>> =
        gameTrackerQueries.selectAllGames()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun getGameById(id: String): GameTrackerGame? =
        gameTrackerQueries.selectGameById(id).awaitAsOneOrNull()?.toDomain()

    override suspend fun saveGame(game: GameTrackerGame) {
        gameTrackerQueries.insertGame(
            GameTrackerGameEntity(
                id = game.id,
                name = game.name,
                playerCount = game.playerCount.toLong(),
                playerIds = game.playerIds.ifEmpty { game.players.joinToString(",") { it.id } },
                scoringLogic = game.scoringLogic.name,
                targetScore = game.targetScore?.toLong(),
                durationMode = game.durationMode.name,
                fixedRoundCount = game.fixedRoundCount?.toLong(),
                currentRound = game.currentRound.toLong(),
                isFinished = if (game.isFinished) 1L else 0L,
                winnerPlayerId = game.winnerPlayerId,
                createdAt = game.createdAt,
                updatedAt = game.updatedAt
            )
        )
    }

    override suspend fun updateGame(game: GameTrackerGame) {
        gameTrackerQueries.updateGame(
            name = game.name,
            playerCount = game.playerCount.toLong(),
            playerIds = game.playerIds,
            scoringLogic = game.scoringLogic.name,
            targetScore = game.targetScore?.toLong(),
            durationMode = game.durationMode.name,
            fixedRoundCount = game.fixedRoundCount?.toLong(),
            currentRound = game.currentRound.toLong(),
            isFinished = if (game.isFinished) 1L else 0L,
            winnerPlayerId = game.winnerPlayerId,
            updatedAt = getCurrentTimeMillis(),
            id = game.id
        )
    }

    override suspend fun deleteGame(game: GameTrackerGame) {
        gameTrackerQueries.transaction {
            gameTrackerQueries.deleteRoundsForGame(game.id)
            gameTrackerQueries.deleteGame(game.id)
        }
    }

    override fun getRoundsForGame(gameId: String): Flow<List<GameTrackerRound>> =
        gameTrackerQueries.selectRoundsForGame(gameId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun getRoundById(roundId: String): GameTrackerRound? =
        gameTrackerQueries.selectRoundById(roundId).awaitAsOneOrNull()?.toDomain()

    override suspend fun saveRound(round: GameTrackerRound) {
        gameTrackerQueries.insertRound(
            GameTrackerRoundEntity(
                id = round.id,
                gameId = round.gameId,
                roundNumber = round.roundNumber.toLong(),
                playerId = round.playerId,
                score = round.score.toLong(),
                notes = round.notes,
                createdAt = round.createdAt
            )
        )
    }

    override suspend fun saveRounds(rounds: List<GameTrackerRound>) {
        gameTrackerQueries.transaction {
            rounds.forEach { round ->
                gameTrackerQueries.insertRound(
                    GameTrackerRoundEntity(
                        id = round.id,
                        gameId = round.gameId,
                        roundNumber = round.roundNumber.toLong(),
                        playerId = round.playerId,
                        score = round.score.toLong(),
                        notes = round.notes,
                        createdAt = round.createdAt
                    )
                )
            }
        }
    }

    override suspend fun updateRound(round: GameTrackerRound) {
        gameTrackerQueries.updateRound(
            score = round.score.toLong(),
            notes = round.notes,
            id = round.id
        )
    }

    override suspend fun deleteRound(roundId: String) {
        gameTrackerQueries.deleteRound(roundId)
    }

    override suspend fun finishGame(gameId: String, winnerPlayerId: String?) {
        gameTrackerQueries.finishGame(
            winnerPlayerId = winnerPlayerId,
            updatedAt = getCurrentTimeMillis(),
            id = gameId
        )
    }

    // Extension functions for entity to domain mapping
    private fun GameTrackerGameEntity.toDomain() = GameTrackerGame(
        id = id,
        players = emptyList(), // Will be resolved by ViewModel
        createdAt = createdAt,
        updatedAt = updatedAt,
        name = name,
        playerIds = playerIds,
        playerCount = playerCount.toInt(),
        scoringLogic = ScoringLogic.valueOf(scoringLogic),
        targetScore = targetScore?.toInt(),
        durationMode = DurationMode.valueOf(durationMode),
        fixedRoundCount = fixedRoundCount?.toInt(),
        currentRound = currentRound.toInt(),
        isFinished = isFinished != 0L,
        winnerPlayerId = winnerPlayerId,
        winner = null // Will be resolved by ViewModel
    )

    private fun GameTrackerRoundEntity.toDomain() = GameTrackerRound(
        id = id,
        gameId = gameId,
        roundNumber = roundNumber.toInt(),
        playerId = playerId,
        score = score.toInt(),
        notes = notes,
        createdAt = createdAt
    )
}
