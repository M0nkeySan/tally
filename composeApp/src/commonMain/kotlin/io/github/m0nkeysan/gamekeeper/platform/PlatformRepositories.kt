package io.github.m0nkeysan.gamekeeper.platform

import io.github.m0nkeysan.gamekeeper.core.data.local.repository.GameQueryHelper
import io.github.m0nkeysan.gamekeeper.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.UserPreferencesRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.CounterRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.TarotRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.TarotStatisticsRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.YahtzeeRepository

expect object PlatformRepositories {
    fun getPlayerRepository(): PlayerRepository
    fun getUserPreferencesRepository(): UserPreferencesRepository
    fun getCounterRepository(): CounterRepository
    fun getTarotRepository(): TarotRepository
    fun getTarotStatisticsRepository(): TarotStatisticsRepository
    fun getYahtzeeRepository(): YahtzeeRepository
    fun getGameQueryHelper(): GameQueryHelper
}
