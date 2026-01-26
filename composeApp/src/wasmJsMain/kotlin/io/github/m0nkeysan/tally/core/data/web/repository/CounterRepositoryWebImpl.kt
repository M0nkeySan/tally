package io.github.m0nkeysan.tally.core.data.web.repository

import io.github.m0nkeysan.tally.core.data.web.WebFlowStore
import io.github.m0nkeysan.tally.core.domain.CounterHistoryStore
import io.github.m0nkeysan.tally.core.domain.repository.CounterRepository
import io.github.m0nkeysan.tally.core.model.Counter
import io.github.m0nkeysan.tally.core.model.CounterChange
import io.github.m0nkeysan.tally.core.model.MergedCounterChange
import io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

/**
 * Web implementation of CounterRepository using WebFlowStore.
 * Counter history is stored in-memory (CounterHistoryStore).
 */
class CounterRepositoryWebImpl(
    private val counterStore: WebFlowStore<Counter>,
    private val historyStore: CounterHistoryStore
) : CounterRepository {

    override fun getAllCounters(): Flow<List<Counter>> = counterStore.flow

    override suspend fun addCounter(counter: Counter) {
        counterStore.update { counters -> counters + counter }
    }

    override suspend fun deleteCounter(id: String) {
        counterStore.update { counters -> counters.filter { it.id != id } }
    }

    override suspend fun updateCount(id: String, count: Int) {
        counterStore.update { counters ->
            counters.map {
                if (it.id == id) it.copy(count = count, updatedAt = getCurrentTimeMillis())
                else it
            }
        }
    }

    override suspend fun updateCounter(counter: Counter) {
        counterStore.update { counters ->
            counters.map { if (it.id == counter.id) counter else it }
        }
    }

    override suspend fun updateOrder(id: String, order: Int) {
        counterStore.update { counters ->
            counters.map {
                if (it.id == id) it.copy(sortOrder = order)
                else it
            }
        }
    }

    override suspend fun resetAllCounts() {
        counterStore.update { counters ->
            counters.map { it.copy(count = 0, updatedAt = getCurrentTimeMillis()) }
        }
    }

    override suspend fun deleteAllCounters() {
        counterStore.clear()
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

    override fun getCounterHistory(): Flow<List<CounterChange>> = historyStore.getCounterHistory()

    override fun getMergedCounterHistory(): Flow<List<MergedCounterChange>> = historyStore.getMergedCounterHistory()

    override suspend fun clearCounterHistory() {
        historyStore.clearHistory()
    }
}
