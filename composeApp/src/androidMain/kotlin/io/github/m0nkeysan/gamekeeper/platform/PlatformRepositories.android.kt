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
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories.database

actual object PlatformRepositories {
    private var database: GameDatabase? = null

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
        return PlayerRepositoryImpl(getDatabase().playerDao())
    }

    actual fun getPlayerStatsRepository(): PlayerStatsRepository {
        return PlayerStatsRepositoryImpl(getDatabase().statsDao())
    }

    actual fun getUserPreferencesRepository(): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(getDatabase().userPreferencesDao())
    }

    actual fun getCounterRepository(): CounterRepository {
        return CounterRepositoryImpl(getDatabase().persistentCounterDao())
    }

    actual fun getTarotRepository(): TarotRepository {
        return TarotRepositoryImpl(getDatabase().tarotDao())
    }

    actual fun getYahtzeeRepository(): YahtzeeRepository {
        return YahtzeeRepositoryImpl(getDatabase().yahtzeeDao())
    }
}
