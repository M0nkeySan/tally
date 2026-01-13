package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.PersistentCounterDao
import io.github.m0nkeysan.gamekeeper.core.data.local.database.PersistentCounterEntity
import io.github.m0nkeysan.gamekeeper.core.domain.CounterHistoryStore
import io.github.m0nkeysan.gamekeeper.core.domain.repository.CounterRepository
import io.github.m0nkeysan.gamekeeper.core.model.Counter
import io.github.m0nkeysan.gamekeeper.core.model.CounterChange
import io.github.m0nkeysan.gamekeeper.core.model.getCurrentTimeMillis
import io.github.m0nkeysan.gamekeeper.core.model.MergedCounterChange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class CounterRepositoryImpl(
    private val dao: PersistentCounterDao,
    private val historyStore: CounterHistoryStore
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

    override suspend fun logCounterChange(
        counterId: String,
        counterName: String,
        counterColor: Long,
        previousValue: Int,
        newValue: Int
    ) {
        val change = CounterChange(
            id = UUID.randomUUID().toString(),
            counterId = counterId,
            counterName = counterName,
            counterColor = counterColor,
            previousValue = previousValue,
            newValue = newValue,
            changeDelta = newValue - previousValue,
            isDeleted = false,
            timestamp = getCurrentTimeMillis(),
            createdAt = getCurrentTimeMillis()
        )
        historyStore.addChange(change)
    }

    override suspend fun logCounterDeletion(
        counterId: String,
        counterName: String,
        counterColor: Long
    ) {
        val change = CounterChange(
            id = UUID.randomUUID().toString(),
            counterId = counterId,
            counterName = counterName,
            counterColor = counterColor,
            previousValue = 0,
            newValue = 0,
            changeDelta = 0,
            isDeleted = true,
            timestamp = getCurrentTimeMillis(),
            createdAt = getCurrentTimeMillis()
        )
        historyStore.addChange(change)
    }

    override fun getCounterHistory(): Flow<List<CounterChange>> = historyStore.history

    override fun getMergedCounterHistory(): Flow<List<MergedCounterChange>> =
        historyStore.history.map { changes ->
            historyStore.getMergedHistory()
        }

    override suspend fun clearCounterHistory() {
        historyStore.deleteAllChanges()
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
