package io.github.m0nkeysan.tally.platform

import android.content.Context
import io.github.m0nkeysan.tally.core.data.backup.DatabaseExporterImpl
import io.github.m0nkeysan.tally.core.data.local.DatabaseModule
import io.github.m0nkeysan.tally.core.data.local.driver.DatabaseDriverFactory
import io.github.m0nkeysan.tally.core.data.repository.*
import io.github.m0nkeysan.tally.core.domain.CounterHistoryStore
import io.github.m0nkeysan.tally.core.domain.backup.DatabaseExporter
import io.github.m0nkeysan.tally.core.domain.repository.*
import io.github.m0nkeysan.tally.database.TallyDatabase
import io.github.m0nkeysan.tally.ui.strings.LocaleManager
import kotlinx.coroutines.*

actual object PlatformRepositories {
    private var database: TallyDatabase? = null
    private var driverFactory: DatabaseDriverFactory? = null
    
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
    private var databaseExporter: DatabaseExporter? = null

    fun init(context: Context) {
        if (driverFactory == null) {
            driverFactory = DatabaseDriverFactory(context)
            // Pre-initialize database on background thread
            CoroutineScope(Dispatchers.IO).launch {
                getDatabase()
            }
        }
    }

    private fun getDatabase(): TallyDatabase {
        val factory = driverFactory ?: throw IllegalStateException("Database not initialized. Call PlatformRepositories.init(context) first.")
        return database ?: runBlocking {
            DatabaseModule.getDatabase(factory).also { database = it }
        }
    }

    private fun getHistoryStore(): CounterHistoryStore {
        return historyStore ?: CounterHistoryStore().also {
            historyStore = it
        }
    }

    actual fun getPlayerRepository(): PlayerRepository {
        return playerRepository ?: PlayerRepositoryImpl(
            getDatabase().playerQueries,
            getGameQueryHelper()
        ).also {
            playerRepository = it
        }
    }

    actual fun getUserPreferencesRepository(): UserPreferencesRepository {
        return userPreferencesRepository ?: UserPreferencesRepositoryImpl(getDatabase().preferencesQueries).also {
            userPreferencesRepository = it
        }
    }

    actual fun getCounterRepository(): CounterRepository {
        return counterRepository ?: CounterRepositoryImpl(
            getDatabase().counterQueries,
            getHistoryStore()
        ).also {
            counterRepository = it
        }
    }

    actual fun getTarotRepository(): TarotRepository {
        return tarotRepository ?: TarotRepositoryImpl(getDatabase().tarotQueries).also {
            tarotRepository = it
        }
    }

    actual fun getTarotStatisticsRepository(): TarotStatisticsRepository {
        return tarotStatisticsRepository ?: TarotStatisticsRepositoryImpl(
            getDatabase().tarotQueries,
            getTarotRepository(),
            getPlayerRepository()
        ).also {
            tarotStatisticsRepository = it
        }
    }

    actual fun getYahtzeeRepository(): YahtzeeRepository {
        return yahtzeeRepository ?: YahtzeeRepositoryImpl(getDatabase().yahtzeeQueries).also {
            yahtzeeRepository = it
        }
    }

    actual fun getYahtzeeStatisticsRepository(): YahtzeeStatisticsRepository {
        return yahtzeeStatisticsRepository ?: YahtzeeStatisticsRepositoryImpl(
            getDatabase().yahtzeeQueries,
            getPlayerRepository()
        ).also {
            yahtzeeStatisticsRepository = it
        }
    }

    actual fun getGameQueryHelper(): GameQueryHelper {
        return gameQueryHelper ?: GameQueryHelperImpl(getDatabase().tarotQueries, getDatabase().yahtzeeQueries).also {
            gameQueryHelper = it
        }
    }

    actual fun getLocaleManager(): LocaleManager {
        return localeManager ?: LocaleManager(getUserPreferencesRepository()).also {
            localeManager = it
        }
    }

    actual fun getDatabaseExporter(): DatabaseExporter {
        return databaseExporter ?: DatabaseExporterImpl(getDatabase()).also {
            databaseExporter = it
        }
    }
}

