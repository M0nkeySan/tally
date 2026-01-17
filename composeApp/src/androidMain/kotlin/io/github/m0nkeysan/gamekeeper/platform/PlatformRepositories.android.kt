package io.github.m0nkeysan.gamekeeper.platform

import android.content.Context
import androidx.room.Room
import io.github.m0nkeysan.gamekeeper.core.data.local.database.GameDatabase
import io.github.m0nkeysan.gamekeeper.core.data.local.repository.CounterRepositoryImpl
import io.github.m0nkeysan.gamekeeper.core.data.local.repository.GameQueryHelper
import io.github.m0nkeysan.gamekeeper.core.data.local.repository.PlayerRepositoryImpl
import io.github.m0nkeysan.gamekeeper.core.data.local.repository.TarotRepositoryImpl
import io.github.m0nkeysan.gamekeeper.core.data.local.repository.TarotStatisticsRepositoryImpl
import io.github.m0nkeysan.gamekeeper.core.data.local.repository.UserPreferencesRepositoryImpl
import io.github.m0nkeysan.gamekeeper.core.data.local.repository.YahtzeeRepositoryImpl
import io.github.m0nkeysan.gamekeeper.core.data.local.repository.YahtzeeStatisticsRepositoryImpl
import io.github.m0nkeysan.gamekeeper.core.domain.CounterHistoryStore
import io.github.m0nkeysan.gamekeeper.core.domain.repository.CounterRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.TarotRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.TarotStatisticsRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.UserPreferencesRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.YahtzeeRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.YahtzeeStatisticsRepository
import io.github.m0nkeysan.gamekeeper.ui.strings.LocaleManager

actual object PlatformRepositories {
    private var database: GameDatabase? = null
    
    // Singleton instances for repositories
    private var playerRepository: PlayerRepository? = null
    private var userPreferencesRepository: UserPreferencesRepository? = null
    private var counterRepository: CounterRepository? = null
    private var tarotRepository: TarotRepository? = null
    private var tarotStatisticsRepository: TarotStatisticsRepository? = null
    private var yahtzeeRepository: YahtzeeRepository? = null
    private var yahtzeeStatisticsRepository: YahtzeeStatisticsRepository? = null
    private var gameQueryHelper: GameQueryHelper? = null
    private var historyStore: CounterHistoryStore? = null
    private var localeManager: LocaleManager? = null

    fun init(context: Context) {
        if (database == null) {
            database = Room.databaseBuilder(
                context,
                GameDatabase::class.java,
                "gamekeeper-database"
            )
                .fallbackToDestructiveMigration(true)
                .build()
        }
    }

    private fun getDatabase(): GameDatabase {
        return database ?: throw IllegalStateException("Database not initialized. Call PlatformRepositories.init(context) first.")
    }

    private fun getHistoryStore(): CounterHistoryStore {
        return historyStore ?: CounterHistoryStore().also {
            historyStore = it
        }
    }

    actual fun getPlayerRepository(): PlayerRepository {
        return playerRepository ?: PlayerRepositoryImpl(
            getDatabase().playerDao(),
            getGameQueryHelper()
        ).also {
            playerRepository = it
        }
    }

    actual fun getUserPreferencesRepository(): UserPreferencesRepository {
        return userPreferencesRepository ?: UserPreferencesRepositoryImpl(getDatabase().userPreferencesDao()).also {
            userPreferencesRepository = it
        }
    }

    actual fun getCounterRepository(): CounterRepository {
        return counterRepository ?: CounterRepositoryImpl(
            getDatabase().persistentCounterDao(),
            getHistoryStore()
        ).also {
            counterRepository = it
        }
    }

    actual fun getTarotRepository(): TarotRepository {
        return tarotRepository ?: TarotRepositoryImpl(getDatabase().tarotDao(), getDatabase()).also {
            tarotRepository = it
        }
    }

    actual fun getTarotStatisticsRepository(): TarotStatisticsRepository {
        return tarotStatisticsRepository ?: TarotStatisticsRepositoryImpl(
            getDatabase().tarotDao(),
            getTarotRepository(),
            getPlayerRepository()
        ).also {
            tarotStatisticsRepository = it
        }
    }

    actual fun getYahtzeeRepository(): YahtzeeRepository {
        return yahtzeeRepository ?: YahtzeeRepositoryImpl(getDatabase().yahtzeeDao(), getDatabase()).also {
            yahtzeeRepository = it
        }
    }

    actual fun getYahtzeeStatisticsRepository(): YahtzeeStatisticsRepository {
        return yahtzeeStatisticsRepository ?: YahtzeeStatisticsRepositoryImpl(
            getDatabase().yahtzeeDao(),
            getPlayerRepository()
        ).also {
            yahtzeeStatisticsRepository = it
        }
    }

    actual fun getGameQueryHelper(): GameQueryHelper {
        return gameQueryHelper ?: GameQueryHelper(getDatabase().tarotDao(), getDatabase().yahtzeeDao()).also {
            gameQueryHelper = it
        }
    }

    actual fun getLocaleManager(): LocaleManager {
        return localeManager ?: LocaleManager(getUserPreferencesRepository()).also {
            localeManager = it
        }
    }
}
