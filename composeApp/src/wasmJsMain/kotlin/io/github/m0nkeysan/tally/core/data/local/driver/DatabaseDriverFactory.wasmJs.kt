package io.github.m0nkeysan.tally.core.data.local.driver

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

@JsFun("() => { return new URL('./worker.mjs', import.meta.url).href }")
private external fun getWorkerUrl(): String

@JsFun("(url) => { return new Worker(url, { type: 'module' }) }")
private external fun createModuleWorker(url: String): Worker

actual class DatabaseDriverFactory {
    actual suspend fun createDriver(): SqlDriver {
        val url = getWorkerUrl()
        val worker = createModuleWorker(url)
        return WebWorkerDriver(worker)
    }
}