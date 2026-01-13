package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        PlayerEntity::class,
        UserPreferencesEntity::class,
        PersistentCounterEntity::class,
        TarotGameEntity::class,
        TarotRoundEntity::class,
        YahtzeeGameEntity::class,
        YahtzeeScoreEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GameDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun persistentCounterDao(): PersistentCounterDao
    abstract fun tarotDao(): TarotDao
    abstract fun yahtzeeDao(): YahtzeeDao
}
