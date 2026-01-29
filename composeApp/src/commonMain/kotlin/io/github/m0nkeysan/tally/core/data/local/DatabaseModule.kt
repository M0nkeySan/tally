package io.github.m0nkeysan.tally.core.data.local

import app.cash.sqldelight.async.coroutines.awaitCreate
import io.github.m0nkeysan.tally.core.data.local.driver.DatabaseDriverFactory
import io.github.m0nkeysan.tally.database.TallyDatabase

object DatabaseModule {
    private var database: TallyDatabase? = null

    suspend fun getDatabase(driverFactory: DatabaseDriverFactory): TallyDatabase {
        if (database == null) {
            val driver = driverFactory.createDriver()
            TallyDatabase.Schema.awaitCreate(driver)
            database = TallyDatabase(driver)
        }
        return database!!
    }

}
