package io.github.m0nkeysan.tally.core.domain.repository

import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.core.model.YahtzeePlayerStatistics
import io.github.m0nkeysan.tally.core.model.YahtzeeGlobalStatistics

interface YahtzeeStatisticsRepository {
    suspend fun getPlayerStatistics(playerId: String): YahtzeePlayerStatistics
    suspend fun getAvailablePlayers(): List<Player>
    suspend fun getGlobalStatistics(): YahtzeeGlobalStatistics
}
