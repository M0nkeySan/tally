package io.github.m0nkeysan.gamekeeper.platform

import android.content.Context
import androidx.room.Room
import io.github.m0nkeysan.gamekeeper.core.data.local.database.GameDatabase
import io.github.m0nkeysan.gamekeeper.core.data.local.repository.PlayerRepositoryImpl
import io.github.m0nkeysan.gamekeeper.core.data.local.repository.PlayerStatsRepositoryImpl
import io.github.m0nkeysan.gamekeeper.core.data.local.repository.UserPreferencesRepositoryImpl
import io.github.m0nkeysan.gamekeeper.core.data.local.repository.CounterRepositoryImpl
import io.github.m0nkeysan.gamekeeper.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.PlayerStatsRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.UserPreferencesRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.CounterRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.TarotRepository
import io.github.m0nkeysan.gamekeeper.core.data.local.repository.TarotRepositoryImpl
import io.github.m0nkeysan.gamekeeper.core.domain.repository.YahtzeeRepository
import io.github.m0nkeysan.gamekeeper.core.data.local.repository.YahtzeeRepositoryImpl

actual object PlatformRepositories {
    private var database: GameDatabase? = null
    
    // Singleton instances for repositories
    private var playerRepository: PlayerRepository? = null
    private var playerStatsRepository: PlayerStatsRepository? = null
    private var userPreferencesRepository: UserPreferencesRepository? = null
    private var counterRepository: CounterRepository? = null
    private var tarotRepository: TarotRepository? = null
    private var yahtzeeRepository: YahtzeeRepository? = null

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

    actual fun getPlayerRepository(): PlayerRepository {
        return playerRepository ?: PlayerRepositoryImpl(getDatabase().playerDao()).also {
            playerRepository = it
        }
    }

    actual fun getPlayerStatsRepository(): PlayerStatsRepository {
        return playerStatsRepository ?: PlayerStatsRepositoryImpl(getDatabase().statsDao()).also {
            playerStatsRepository = it
        }
    }

    actual fun getUserPreferencesRepository(): UserPreferencesRepository {
        return userPreferencesRepository ?: UserPreferencesRepositoryImpl(getDatabase().userPreferencesDao()).also {
            userPreferencesRepository = it
        }
    }

    actual fun getCounterRepository(): CounterRepository {
        return counterRepository ?: CounterRepositoryImpl(getDatabase().persistentCounterDao()).also {
            counterRepository = it
        }
    }

    actual fun getTarotRepository(): TarotRepository {
        return tarotRepository ?: TarotRepositoryImpl(getDatabase().tarotDao(), getDatabase()).also {
            tarotRepository = it
        }
    }

    actual fun getYahtzeeRepository(): YahtzeeRepository {
        return yahtzeeRepository ?: YahtzeeRepositoryImpl(getDatabase().yahtzeeDao(), getDatabase()).also {
            yahtzeeRepository = it
        }
    }
}
