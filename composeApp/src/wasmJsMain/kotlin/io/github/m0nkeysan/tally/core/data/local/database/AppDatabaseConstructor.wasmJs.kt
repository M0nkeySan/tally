package io.github.m0nkeysan.tally.core.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = "tally-database.db"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .fallbackToDestructiveMigration(true)
}
