package io.github.m0nkeysan.gamekeeper.core.domain.repository

import io.github.m0nkeysan.gamekeeper.core.model.CounterGame
import io.github.m0nkeysan.gamekeeper.core.model.TarotGame
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeGame
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getAllTarotGames(): Flow<List<TarotGame>>
    fun getAllYahtzeeGames(): Flow<List<YahtzeeGame>>
    fun getAllCounterGames(): Flow<List<CounterGame>>
    
    suspend fun getTarotGameById(id: String): TarotGame?
    suspend fun getYahtzeeGameById(id: String): YahtzeeGame?
    suspend fun getCounterGameById(id: String): CounterGame?
    
    suspend fun saveTarotGame(game: TarotGame)
    suspend fun saveYahtzeeGame(game: YahtzeeGame)
    suspend fun saveCounterGame(game: CounterGame)
    
    suspend fun deleteTarotGame(id: String)
    suspend fun deleteYahtzeeGame(id: String)
    suspend fun deleteCounterGame(id: String)
    
    suspend fun deleteAllGames()
}
