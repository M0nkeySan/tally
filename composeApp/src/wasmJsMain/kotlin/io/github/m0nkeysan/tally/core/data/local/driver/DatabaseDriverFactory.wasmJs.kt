package io.github.m0nkeysan.tally.core.data.local.driver

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver

actual class DatabaseDriverFactory {
    actual suspend fun createDriver(): SqlDriver {
        return createDefaultWebWorkerDriver()
    }
}