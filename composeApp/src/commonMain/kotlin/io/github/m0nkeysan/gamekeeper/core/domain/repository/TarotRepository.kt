package io.github.m0nkeysan.gamekeeper.core.domain.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.TarotGameEntity
import io.github.m0nkeysan.gamekeeper.core.data.local.database.TarotRoundEntity
import kotlinx.coroutines.flow.Flow

interface TarotRepository {
    fun getAllGames(): Flow<List<TarotGameEntity>>
    suspend fun getGameById(id: String): TarotGameEntity?
    suspend fun saveGame(game: TarotGameEntity)
    suspend fun deleteGame(game: TarotGameEntity)
    fun getRoundsForGame(gameId: String): Flow<List<TarotRoundEntity>>
    suspend fun addRound(round: TarotRoundEntity)
}
