package io.github.m0nkeysan.tally.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import io.github.m0nkeysan.tally.core.domain.repository.GameQueryHelper
import io.github.m0nkeysan.tally.database.GameTrackerQueries
import io.github.m0nkeysan.tally.database.TarotQueries
import io.github.m0nkeysan.tally.database.YahtzeeQueries

class GameQueryHelperImpl(
    private val tarotQueries: TarotQueries,
    private val yahtzeeQueries: YahtzeeQueries,
    private val gameTrackerQueries: GameTrackerQueries
) : GameQueryHelper {
    override suspend fun getGameCountForPlayer(playerId: String): Int {
        val tarotCount = tarotQueries.countGamesWithPlayer(playerId).awaitAsOne()
        val yahtzeeCount = yahtzeeQueries.countGamesWithPlayer(playerId).awaitAsOne()
        val gameTrackerCount = gameTrackerQueries.countGamesWithPlayer(playerId).awaitAsOne()
        return (tarotCount + yahtzeeCount + gameTrackerCount).toInt()
    }
}
