package io.github.m0nkeysan.tally.core.data.local.driver

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

@JsFun("() => new Worker(new URL('./worker.js', import.meta.url), { type: 'module' })")
private external fun createWorker(): Worker

actual class DatabaseDriverFactory {
    actual suspend fun createDriver(): SqlDriver {
        return WebWorkerDriver(createWorker())
    }
}
