package io.github.m0nkeysan.tally.core.domain.repository

import io.github.m0nkeysan.tally.core.model.GameTrackerGame
import io.github.m0nkeysan.tally.core.model.GameTrackerRound
import kotlinx.coroutines.flow.Flow

interface GameTrackerRepository {
    fun getAllGames(): Flow<List<GameTrackerGame>>
    suspend fun getGameById(id: String): GameTrackerGame?
    suspend fun saveGame(game: GameTrackerGame)
    suspend fun updateGame(game: GameTrackerGame)
    suspend fun deleteGame(game: GameTrackerGame)
    fun getRoundsForGame(gameId: String): Flow<List<GameTrackerRound>>
    suspend fun getRoundById(roundId: String): GameTrackerRound?
    suspend fun saveRound(round: GameTrackerRound)
    suspend fun saveRounds(rounds: List<GameTrackerRound>)
    suspend fun updateRound(round: GameTrackerRound)
    suspend fun deleteRound(roundId: String)
    suspend fun finishGame(gameId: String, winnerPlayerId: String?)
}
