package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.CounterChangeDao
import io.github.m0nkeysan.gamekeeper.core.data.local.database.CounterChangeEntity
import io.github.m0nkeysan.gamekeeper.core.data.local.database.PersistentCounterDao
import io.github.m0nkeysan.gamekeeper.core.data.local.database.PersistentCounterEntity
import io.github.m0nkeysan.gamekeeper.core.domain.repository.CounterRepository
import io.github.m0nkeysan.gamekeeper.core.model.Counter
import io.github.m0nkeysan.gamekeeper.core.model.CounterChange
import io.github.m0nkeysan.gamekeeper.core.model.MergedCounterChange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CounterRepositoryImpl(
    private val dao: PersistentCounterDao,
    private val changeDao: CounterChangeDao
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
        val change = CounterChangeEntity(
            counterId = counterId,
            counterName = counterName,
            counterColor = counterColor,
            previousValue = previousValue,
            newValue = newValue,
            changeDelta = newValue - previousValue
        )
        changeDao.insertChange(change)
    }

    override fun getCounterHistory(): Flow<List<CounterChange>> =
        changeDao.getAllChanges().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getMergedCounterHistory(): Flow<List<MergedCounterChange>> =
        getCounterHistory().map { changes ->
            mergeConsecutiveChanges(changes)
        }

    override suspend fun clearCounterHistory() {
        changeDao.deleteAllChanges()
    }

    private fun mergeConsecutiveChanges(changes: List<CounterChange>): List<MergedCounterChange> {
        if (changes.isEmpty()) return emptyList()

        val merged = mutableListOf<MergedCounterChange>()
        var currentGroup = mutableListOf(changes[0])

        for (i in 1 until changes.size) {
            val current = changes[i]
            val previous = changes[i - 1]

            if (current.counterId == previous.counterId) {
                // Same counter, add to current group
                currentGroup.add(current)
            } else {
                // Different counter, finalize current group and start new one
                merged.add(createMergedChange(currentGroup))
                currentGroup = mutableListOf(current)
            }
        }

        // Don't forget the last group
        merged.add(createMergedChange(currentGroup))

        return merged
    }

    private fun createMergedChange(changes: List<CounterChange>): MergedCounterChange {
        val totalDelta = changes.sumOf { it.changeDelta }
        val firstTimestamp = changes.minOf { it.timestamp }
        val lastTimestamp = changes.maxOf { it.timestamp }
        val first = changes.first()

        return MergedCounterChange(
            counterId = first.counterId,
            counterName = first.counterName,
            counterColor = first.counterColor,
            totalDelta = totalDelta,
            count = changes.size,
            firstTimestamp = firstTimestamp,
            lastTimestamp = lastTimestamp,
            changes = changes
        )
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

private fun CounterChangeEntity.toDomain() = CounterChange(
    id = id,
    counterId = counterId,
    counterName = counterName,
    counterColor = counterColor,
    previousValue = previousValue,
    newValue = newValue,
    changeDelta = changeDelta,
    timestamp = timestamp,
    createdAt = createdAt
)
