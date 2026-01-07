package io.github.m0nkeysan.gamekeeper.core.domain.repository

import io.github.m0nkeysan.gamekeeper.core.model.GameType
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.core.model.PlayerStats
import kotlinx.coroutines.flow.Flow

interface PlayerStatsRepository {
    fun getAllPlayerStats(): Flow<List<PlayerStats>>
    fun getPlayerStats(playerId: String): Flow<PlayerStats?>
    fun getPlayerGameHistory(playerId: String): Flow<List<GameParticipant>>
    
    suspend fun initializePlayerStats(player: Player)
    suspend fun recordTarotGame(
        gameId: String,
        players: List<Player>,
        scores: Map<String, Int>,
        timestamp: Long
    )
    suspend fun recordYahtzeeGame(
        gameId: String,
        players: List<Player>,
        scores: Map<String, Int>,
        timestamp: Long
    )
    suspend fun recordCounterGame(
        gameId: String,
        players: List<Player>,
        counts: Map<String, Int>,
        timestamp: Long
    )
    suspend fun deleteGameParticipants(gameId: String)
    suspend fun getGamesByType(gameType: GameType): Flow<List<GameParticipant>>
}

data class GameParticipant(
    val gameId: String,
    val playerId: String,
    val gameType: GameType,
    val score: Int,
    val isWinner: Boolean,
    val playedAt: Long
)
