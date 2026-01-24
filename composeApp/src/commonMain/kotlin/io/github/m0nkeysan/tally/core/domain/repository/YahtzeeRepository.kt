package io.github.m0nkeysan.tally.core.domain.repository

import io.github.m0nkeysan.tally.core.model.PlayerYahtzeeScore
import io.github.m0nkeysan.tally.core.model.YahtzeeGame
import io.github.m0nkeysan.tally.core.model.YahtzeeScore
import kotlinx.coroutines.flow.Flow

interface YahtzeeRepository {
    fun getAllGames(): Flow<List<YahtzeeGame>>
    suspend fun getGameById(id: String): YahtzeeGame?
    suspend fun saveGame(game: YahtzeeGame)
    suspend fun deleteGame(game: YahtzeeGame)
    fun getScoresForGame(gameId: String): Flow<List<PlayerYahtzeeScore>>
    suspend fun saveScore(score: YahtzeeScore, gameId: String, playerId: String)
}
