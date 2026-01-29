package io.github.m0nkeysan.tally.core.data.local.driver

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.github.m0nkeysan.tally.database.TallyDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual suspend fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = TallyDatabase.Schema.synchronous(),
            context = context,
            name = "tally.db"
        )
    }
}
