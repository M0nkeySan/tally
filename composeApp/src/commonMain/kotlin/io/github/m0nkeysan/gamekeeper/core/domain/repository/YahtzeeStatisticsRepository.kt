package io.github.m0nkeysan.gamekeeper.core.domain.repository

import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeePlayerStatistics

interface YahtzeeStatisticsRepository {
    suspend fun getPlayerStatistics(playerId: String): YahtzeePlayerStatistics
    suspend fun getAvailablePlayers(): List<Player>
}
