package io.github.m0nkeysan.tally.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.m0nkeysan.tally.core.domain.CounterHistoryStore
import io.github.m0nkeysan.tally.core.domain.repository.CounterRepository
import io.github.m0nkeysan.tally.core.model.Counter
import io.github.m0nkeysan.tally.core.model.CounterChange
import io.github.m0nkeysan.tally.core.model.MergedCounterChange
import io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis
import io.github.m0nkeysan.tally.database.CounterQueries
import io.github.m0nkeysan.tally.database.PersistentCounterEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.Uuid

class CounterRepositoryImpl(
    private val counterQueries: CounterQueries,
    private val historyStore: CounterHistoryStore
) : CounterRepository {
    
    override fun getAllCounters(): Flow<List<Counter>> = 
        counterQueries.selectAllCounters()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun addCounter(counter: Counter) {
        counterQueries.insertCounter(counter.toEntity())
    }

    override suspend fun deleteCounter(id: String) {
        counterQueries.deleteCounter(id)
    }

    override suspend fun updateCount(id: String, count: Int) {
        val counter = counterQueries.selectCounterById(id).awaitAsOneOrNull()
        if (counter != null) {
            counterQueries.insertCounter(
                counter.copy(count = count.toLong(), updatedAt = getCurrentTimeMillis())
            )
        }
    }

    override suspend fun updateCounter(counter: Counter) {
        counterQueries.insertCounter(counter.toEntity())
    }

    override suspend fun updateOrder(id: String, order: Int) {
        val counter = counterQueries.selectCounterById(id).awaitAsOneOrNull()
        if (counter != null) {
            counterQueries.insertCounter(
                counter.copy(sortOrder = order.toLong(), updatedAt = getCurrentTimeMillis())
            )
        }
    }

    override suspend fun resetAllCounts() {
        val counters = counterQueries.selectAllCounters().awaitAsList()
        counterQueries.transaction {
            counters.forEach { counter ->
                counterQueries.insertCounter(
                    counter.copy(count = 0, updatedAt = getCurrentTimeMillis())
                )
            }
        }
    }

    override suspend fun deleteAllCounters() {
        val counters = counterQueries.selectAllCounters().awaitAsList()
        counterQueries.transaction {
            counters.forEach { counter ->
                counterQueries.deleteCounter(counter.id)
            }
        }
    }

    override suspend fun logCounterChange(
        counterId: String,
        counterName: String,
        counterColor: Long,
        previousValue: Int,
        newValue: Int
    ) {
        val change = CounterChange(
            id = Uuid.random().toString(),
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
            id = Uuid.random().toString(),
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
        historyStore.history.map { _ ->
            historyStore.getMergedHistory()
        }

    override suspend fun clearCounterHistory() {
        historyStore.deleteAllChanges()
    }

    private fun PersistentCounterEntity.toDomain() = Counter(
        id = id,
        name = name,
        count = count.toInt(),
        color = color,
        updatedAt = updatedAt,
        sortOrder = sortOrder.toInt()
    )

    private fun Counter.toEntity() = PersistentCounterEntity(
        id = id,
        name = name,
        count = count.toLong(),
        color = color,
        updatedAt = updatedAt,
        sortOrder = sortOrder.toLong()
    )
}
