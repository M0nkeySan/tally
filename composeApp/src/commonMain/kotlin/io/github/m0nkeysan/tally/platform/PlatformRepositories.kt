package io.github.m0nkeysan.tally.platform

import io.github.m0nkeysan.tally.core.domain.repository.GameQueryHelper
import io.github.m0nkeysan.tally.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.tally.core.domain.repository.UserPreferencesRepository
import io.github.m0nkeysan.tally.core.domain.repository.CounterRepository
import io.github.m0nkeysan.tally.core.domain.repository.TarotRepository
import io.github.m0nkeysan.tally.core.domain.repository.TarotStatisticsRepository
import io.github.m0nkeysan.tally.core.domain.repository.YahtzeeRepository
import io.github.m0nkeysan.tally.core.domain.repository.YahtzeeStatisticsRepository
import io.github.m0nkeysan.tally.ui.strings.LocaleManager

expect object PlatformRepositories {
    fun getPlayerRepository(): PlayerRepository
    fun getUserPreferencesRepository(): UserPreferencesRepository
    fun getCounterRepository(): CounterRepository
    fun getTarotRepository(): TarotRepository
    fun getTarotStatisticsRepository(): TarotStatisticsRepository
    fun getYahtzeeRepository(): YahtzeeRepository
    fun getYahtzeeStatisticsRepository(): YahtzeeStatisticsRepository
    fun getGameQueryHelper(): GameQueryHelper
    fun getLocaleManager(): LocaleManager
}
