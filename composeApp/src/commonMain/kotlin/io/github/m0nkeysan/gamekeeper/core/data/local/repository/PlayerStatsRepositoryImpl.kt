package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.*
import io.github.m0nkeysan.gamekeeper.core.domain.repository.*
import io.github.m0nkeysan.gamekeeper.core.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayerStatsRepositoryImpl(
    private val dao: StatsDao
) : PlayerStatsRepository {
    override fun getAllPlayerStats(): Flow<List<PlayerStats>> = dao.getAllPlayerStats().map { entities ->
        entities.map { it.toDomain() }
    }

    override fun getPlayerStats(playerId: String): Flow<PlayerStats?> = dao.getPlayerStats(playerId).map { it?.toDomain() }

    override fun getPlayerGameHistory(playerId: String): Flow<List<GameParticipant>> = dao.getPlayerGameHistory(playerId).map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun initializePlayerStats(player: Player) {
        dao.insertPlayerStats(PlayerStatsEntity(
            playerId = player.id,
            playerName = player.name,
            avatarColor = player.avatarColor,
            totalGamesPlayed = 0,
            tarotGamesPlayed = 0,
            tarotGamesWon = 0,
            tarotTotalScore = 0,
            yahtzeeGamesPlayed = 0,
            yahtzeeGamesWon = 0,
            yahtzeeHighestScore = 0,
            yahtzeeTotalScore = 0,
            counterGamesPlayed = 0,
            counterGamesWon = 0,
            counterTotalScore = 0,
            lastPlayedAt = 0
        ))
    }

    override suspend fun recordTarotGame(gameId: String, players: List<Player>, scores: Map<String, Int>, timestamp: Long) {}
    override suspend fun recordYahtzeeGame(gameId: String, players: List<Player>, scores: Map<String, Int>, timestamp: Long) {}
    override suspend fun recordCounterGame(gameId: String, players: List<Player>, counts: Map<String, Int>, timestamp: Long) {}
    override suspend fun deleteGameParticipants(gameId: String) {
        dao.deleteGameParticipants(gameId)
    }

    override suspend fun getGamesByType(gameType: GameType): Flow<List<GameParticipant>> = dao.getGamesByType(gameType.name).map { entities ->
        entities.map { it.toDomain() }
    }
}

fun PlayerStatsEntity.toDomain() = PlayerStats(
    playerId = playerId,
    playerName = playerName,
    avatarColor = avatarColor,
    totalGamesPlayed = totalGamesPlayed,
    tarotGamesPlayed = tarotGamesPlayed,
    tarotGamesWon = tarotGamesWon,
    tarotTotalScore = tarotTotalScore,
    yahtzeeGamesPlayed = yahtzeeGamesPlayed,
    yahtzeeGamesWon = yahtzeeGamesWon,
    yahtzeeHighestScore = yahtzeeHighestScore,
    yahtzeeTotalScore = yahtzeeTotalScore,
    counterGamesPlayed = counterGamesPlayed,
    counterGamesWon = counterGamesWon,
    counterTotalScore = counterTotalScore,
    lastPlayedAt = lastPlayedAt
)

fun GameParticipantEntity.toDomain() = GameParticipant(
    gameId = gameId,
    playerId = playerId,
    gameType = GameType.valueOf(gameType),
    score = score,
    isWinner = isWinner,
    playedAt = playedAt
)
