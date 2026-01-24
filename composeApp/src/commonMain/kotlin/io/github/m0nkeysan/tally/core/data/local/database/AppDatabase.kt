package io.github.m0nkeysan.tally.core.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

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
    version = 2,
    exportSchema = false
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun userPreferencesDao(): UserPreferencesDao

    abstract fun persistentCounterDao(): PersistentCounterDao
    abstract fun tarotDao(): TarotDao
    abstract fun yahtzeeDao(): YahtzeeDao
}

expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
