package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.PersistentCounterDao
import io.github.m0nkeysan.gamekeeper.core.data.local.database.PersistentCounterEntity
import io.github.m0nkeysan.gamekeeper.core.domain.repository.CounterRepository
import kotlinx.coroutines.flow.Flow

class CounterRepositoryImpl(
    private val dao: PersistentCounterDao
) : CounterRepository {
    override fun getAllCounters(): Flow<List<PersistentCounterEntity>> = dao.getAllCounters()

    override suspend fun addCounter(counter: PersistentCounterEntity) {
        dao.insertCounter(counter)
    }

    override suspend fun deleteCounter(id: String) {
        dao.deleteCounter(id)
    }

    override suspend fun updateCount(id: String, count: Int) {
        dao.updateCount(id, count)
    }

    override suspend fun updateCounter(counter: PersistentCounterEntity) {
        dao.insertCounter(counter)
    }

    override suspend fun updateOrder(id: String, order: Int) {
        dao.updateOrder(id, order)
    }
}
