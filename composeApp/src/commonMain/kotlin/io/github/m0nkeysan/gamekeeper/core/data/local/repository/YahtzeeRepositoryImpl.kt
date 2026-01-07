package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeDao
import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeGameEntity
import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeScoreEntity
import io.github.m0nkeysan.gamekeeper.core.domain.repository.YahtzeeRepository
import kotlinx.coroutines.flow.Flow

class YahtzeeRepositoryImpl(
    private val dao: YahtzeeDao
) : YahtzeeRepository {
    override fun getAllGames(): Flow<List<YahtzeeGameEntity>> = dao.getAllGames()

    override suspend fun getGameById(id: String): YahtzeeGameEntity? = dao.getGameById(id)

    override suspend fun saveGame(game: YahtzeeGameEntity) {
        dao.insertGame(game)
    }

    override suspend fun deleteGame(game: YahtzeeGameEntity) {
        dao.deleteScoresForGame(game.id)
        dao.deleteGame(game)
    }

    override fun getScoresForGame(gameId: String): Flow<List<YahtzeeScoreEntity>> = dao.getScoresForGame(gameId)

    override suspend fun saveScore(score: YahtzeeScoreEntity) {
        dao.insertScore(score)
    }
}
