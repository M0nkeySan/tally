package io.github.m0nkeysan.tally.platform

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.m0nkeysan.tally.core.data.local.database.AppDatabase
import io.github.m0nkeysan.tally.core.data.local.repository.CounterRepositoryImpl
import io.github.m0nkeysan.tally.core.data.local.repository.GameQueryHelper
import io.github.m0nkeysan.tally.core.data.local.repository.PlayerRepositoryImpl
import io.github.m0nkeysan.tally.core.data.local.repository.TarotRepositoryImpl
import io.github.m0nkeysan.tally.core.data.local.repository.TarotStatisticsRepositoryImpl
import io.github.m0nkeysan.tally.core.data.local.repository.UserPreferencesRepositoryImpl
import io.github.m0nkeysan.tally.core.data.local.repository.YahtzeeRepositoryImpl
import io.github.m0nkeysan.tally.core.data.local.repository.YahtzeeStatisticsRepositoryImpl
import io.github.m0nkeysan.tally.core.domain.CounterHistoryStore
import io.github.m0nkeysan.tally.core.domain.repository.CounterRepository
import io.github.m0nkeysan.tally.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.tally.core.domain.repository.TarotRepository
import io.github.m0nkeysan.tally.core.domain.repository.TarotStatisticsRepository
import io.github.m0nkeysan.tally.core.domain.repository.UserPreferencesRepository
import io.github.m0nkeysan.tally.core.domain.repository.YahtzeeRepository
import io.github.m0nkeysan.tally.core.domain.repository.YahtzeeStatisticsRepository
import io.github.m0nkeysan.tally.ui.strings.LocaleManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual object PlatformRepositories {
    private var database: AppDatabase? = null
    
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

    private fun getDatabasePath(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        return requireNotNull(documentDirectory?.path) + "/tally-database.db"
    }

    private fun getDatabase(): AppDatabase {
        if (database == null) {
            database = Room.databaseBuilder<AppDatabase>(
                name = getDatabasePath()
            )
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .fallbackToDestructiveMigration(true)
                .build()
        }
        return database!!
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
