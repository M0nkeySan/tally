package io.github.m0nkeysan.tally.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.m0nkeysan.tally.core.domain.GameTrackerHistoryStore
import io.github.m0nkeysan.tally.core.domain.model.DurationMode
import io.github.m0nkeysan.tally.core.domain.model.ScoringLogic
import io.github.m0nkeysan.tally.core.domain.repository.GameTrackerRepository
import io.github.m0nkeysan.tally.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.tally.core.model.GameTrackerGlobalStatistics
import io.github.m0nkeysan.tally.core.model.GameTrackerPlayerStatistics
import io.github.m0nkeysan.tally.core.model.GameTrackerGame
import io.github.m0nkeysan.tally.core.model.GameTrackerRound
import io.github.m0nkeysan.tally.core.model.GameTrackerScoreChange
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis
import io.github.m0nkeysan.tally.database.GameTrackerGameEntity
import io.github.m0nkeysan.tally.database.GameTrackerQueries
import io.github.m0nkeysan.tally.database.GameTrackerRoundEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GameTrackerRepositoryImpl(
    private val gameTrackerQueries: GameTrackerQueries,
    private val playerRepository: PlayerRepository,
    private val historyStore: GameTrackerHistoryStore = GameTrackerHistoryStore()
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

    override suspend fun deleteRoundsByNumber(gameId: String, roundNumber: Int) {
        gameTrackerQueries.deleteRoundsByNumber(gameId, roundNumber.toLong())
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
    
    // Score history tracking
    override fun logRoundScores(
        gameId: String,
        roundNumber: Int,
        rounds: List<GameTrackerRound>,
        players: List<Player>
    ) {
        val changes = rounds.mapNotNull { round ->
            val player = players.find { it.id == round.playerId }
            player?.let {
                GameTrackerScoreChange.create(
                    gameId = gameId,
                    playerId = player.id,
                    playerName = player.name,
                    playerAvatarColor = player.avatarColor,
                    roundNumber = roundNumber,
                    score = round.score
                )
            }
        }
        historyStore.addChanges(changes)
    }
    
    override fun updateRoundScores(
        gameId: String,
        roundNumber: Int,
        rounds: List<GameTrackerRound>,
        players: List<Player>
    ) {
        val changes = rounds.mapNotNull { round ->
            val player = players.find { it.id == round.playerId }
            player?.let {
                GameTrackerScoreChange.create(
                    gameId = gameId,
                    playerId = player.id,
                    playerName = player.name,
                    playerAvatarColor = player.avatarColor,
                    roundNumber = roundNumber,
                    score = round.score
                )
            }
        }
        historyStore.replaceRound(gameId, roundNumber, changes)
    }
    
    override fun removeRoundScores(gameId: String, roundNumber: Int) {
        historyStore.removeRound(gameId, roundNumber)
    }
    
    override fun getScoreHistory(): Flow<List<GameTrackerScoreChange>> =
        historyStore.history.map { historyStore.getHistory() }
    
    override suspend fun clearScoreHistory() {
        historyStore.deleteAllChanges()
    }
    
    // Statistics
    override suspend fun getGlobalStatistics(): GameTrackerGlobalStatistics {
        val totalGames = gameTrackerQueries.countTotalGames().awaitAsOne().toInt()
        val completedGames = gameTrackerQueries.countCompletedGames().awaitAsOne().toInt()
        val activeGames = gameTrackerQueries.countActiveGames().awaitAsOne().toInt()
        val totalRounds = gameTrackerQueries.countTotalRounds().awaitAsOne().toInt()
        val averageRoundsPerGame = if (totalGames > 0) {
            totalRounds.toDouble() / totalGames
        } else {
            0.0
        }
        
        // Get all players and calculate their individual statistics
        val allPlayers = playerRepository.getAllPlayersIncludingInactive().first()
        val playerStatistics = allPlayers.mapNotNull { player ->
            getPlayerStatistics(player.id)
        }.filter { it.gamesPlayed > 0 } // Only include players who have played
        
        return GameTrackerGlobalStatistics(
            totalGames = totalGames,
            completedGames = completedGames,
            activeGames = activeGames,
            totalRounds = totalRounds,
            averageRoundsPerGame = averageRoundsPerGame,
            playerStatistics = playerStatistics
        )
    }
    
    override suspend fun getPlayerStatistics(playerId: String): GameTrackerPlayerStatistics? {
        val player = playerRepository.getPlayerById(playerId) ?: return null
        
        // Get games played
        val gamesPlayed = gameTrackerQueries.countGamesWithPlayer(playerId).awaitAsOne().toInt()
        
        // Get games won
        val gamesWon = gameTrackerQueries.countWinsForPlayer(playerId).awaitAsOne().toInt()
        
        // Calculate win rate
        val winRate = if (gamesPlayed > 0) {
            gamesWon.toDouble() / gamesPlayed
        } else {
            0.0
        }
        
        // Get total score
        val totalScore = gameTrackerQueries.getTotalScoreForPlayer(playerId).awaitAsOne().toInt()
        
        // Get average score
        val averageScore = if (gamesPlayed > 0) {
            totalScore.toDouble() / gamesPlayed
        } else {
            0.0
        }
        
        // Get highest and lowest game scores
        val gameScores = gameTrackerQueries.getScoresForPlayerByGame(playerId)
            .awaitAsList()
            .map { it.totalScore.toInt() }
        
        val highestGameScore = gameScores.maxOrNull()
        val lowestGameScore = gameScores.minOrNull()
        
        // Get total rounds played
        val totalRoundsPlayed = gameTrackerQueries.countRoundsForPlayer(playerId).awaitAsOne().toInt()
        
        return GameTrackerPlayerStatistics(
            player = player,
            gamesPlayed = gamesPlayed,
            gamesWon = gamesWon,
            winRate = winRate,
            totalScore = totalScore,
            averageScore = averageScore,
            highestGameScore = highestGameScore,
            lowestGameScore = lowestGameScore,
            totalRoundsPlayed = totalRoundsPlayed
        )
    }
}
