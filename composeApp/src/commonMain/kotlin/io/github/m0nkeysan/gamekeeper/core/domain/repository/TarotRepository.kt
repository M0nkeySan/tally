package io.github.m0nkeysan.gamekeeper.core.domain.repository

import io.github.m0nkeysan.gamekeeper.core.model.TarotGame
import io.github.m0nkeysan.gamekeeper.core.model.TarotRound
import kotlinx.coroutines.flow.Flow

interface TarotRepository {
    fun getAllGames(): Flow<List<TarotGame>>
    suspend fun getGameById(id: String): TarotGame?
    suspend fun saveGame(game: TarotGame)
    suspend fun deleteGame(game: TarotGame)
    fun getRoundsForGame(gameId: String): Flow<List<TarotRound>>
    suspend fun addRound(round: TarotRound, gameId: String)
}
