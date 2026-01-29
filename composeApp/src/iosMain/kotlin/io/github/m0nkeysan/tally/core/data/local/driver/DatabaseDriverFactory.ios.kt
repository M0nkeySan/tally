package io.github.m0nkeysan.tally.core.data.local.driver

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import io.github.m0nkeysan.tally.database.TallyDatabase

actual class DatabaseDriverFactory {
    actual suspend fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = TallyDatabase.Schema.synchronous(),
            name = "tally.db"
        )
    }
}
