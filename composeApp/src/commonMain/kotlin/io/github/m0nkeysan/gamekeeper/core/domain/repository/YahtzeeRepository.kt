package io.github.m0nkeysan.gamekeeper.core.domain.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeGameEntity
import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeScoreEntity
import kotlinx.coroutines.flow.Flow

interface YahtzeeRepository {
    fun getAllGames(): Flow<List<YahtzeeGameEntity>>
    suspend fun getGameById(id: String): YahtzeeGameEntity?
    suspend fun saveGame(game: YahtzeeGameEntity)
    suspend fun deleteGame(game: YahtzeeGameEntity)
    fun getScoresForGame(gameId: String): Flow<List<YahtzeeScoreEntity>>
    suspend fun saveScore(score: YahtzeeScoreEntity)
}
