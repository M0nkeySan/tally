package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.PersistentCounterDao
import io.github.m0nkeysan.gamekeeper.core.data.local.database.PersistentCounterEntity
import io.github.m0nkeysan.gamekeeper.core.domain.repository.CounterRepository
import io.github.m0nkeysan.gamekeeper.core.model.Counter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CounterRepositoryImpl(
    private val dao: PersistentCounterDao
) : CounterRepository {
    
    override fun getAllCounters(): Flow<List<Counter>> = dao.getAllCounters().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun addCounter(counter: Counter) {
        dao.insertCounter(counter.toEntity())
    }

    override suspend fun deleteCounter(id: String) {
        dao.deleteCounter(id)
    }

    override suspend fun updateCount(id: String, count: Int) {
        dao.updateCount(id, count)
    }

    override suspend fun updateCounter(counter: Counter) {
        dao.insertCounter(counter.toEntity())
    }

    override suspend fun updateOrder(id: String, order: Int) {
        dao.updateOrder(id, order)
    }

    override suspend fun resetAllCounts() {
        dao.resetAllCounts()
    }

    override suspend fun deleteAllCounters() {
        dao.deleteAllCounters()
    }
}

// Mapper functions
private fun PersistentCounterEntity.toDomain() = Counter(
    id = id,
    name = name,
    count = count,
    color = color,
    updatedAt = updatedAt,
    sortOrder = sortOrder
)

private fun Counter.toEntity() = PersistentCounterEntity(
    id = id,
    name = name,
    count = count,
    color = color,
    updatedAt = updatedAt,
    sortOrder = sortOrder
)
