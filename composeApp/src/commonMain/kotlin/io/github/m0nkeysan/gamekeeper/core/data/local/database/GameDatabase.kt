package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        PlayerEntity::class,
        UserPreferencesEntity::class,
        PersistentCounterEntity::class,
        CounterChangeEntity::class,
        TarotGameEntity::class,
        TarotRoundEntity::class,
        PlayerStatsEntity::class,
        GameParticipantEntity::class,
        YahtzeeGameEntity::class,
        YahtzeeScoreEntity::class
    ],
    version = 15
)
abstract class GameDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun persistentCounterDao(): PersistentCounterDao
    abstract fun counterChangeDao(): CounterChangeDao
    abstract fun tarotDao(): TarotDao
    abstract fun statsDao(): StatsDao
    abstract fun yahtzeeDao(): YahtzeeDao
}
