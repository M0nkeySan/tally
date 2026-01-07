package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.TarotDao
import io.github.m0nkeysan.gamekeeper.core.data.local.database.TarotGameEntity
import io.github.m0nkeysan.gamekeeper.core.data.local.database.TarotRoundEntity
import io.github.m0nkeysan.gamekeeper.core.domain.repository.TarotRepository
import kotlinx.coroutines.flow.Flow

class TarotRepositoryImpl(
    private val dao: TarotDao
) : TarotRepository {
    override fun getAllGames(): Flow<List<TarotGameEntity>> = dao.getAllGames()
    
    override suspend fun getGameById(id: String): TarotGameEntity? = dao.getGameById(id)
    
    override suspend fun saveGame(game: TarotGameEntity) {
        dao.insertGame(game)
    }
    
    override suspend fun deleteGame(game: TarotGameEntity) {
        dao.deleteRoundsForGame(game.id)
        dao.deleteGame(game)
    }
    
    override fun getRoundsForGame(gameId: String): Flow<List<TarotRoundEntity>> = dao.getRoundsForGame(gameId)
    
    override suspend fun addRound(round: TarotRoundEntity) {
        dao.insertRound(round)
    }
}
