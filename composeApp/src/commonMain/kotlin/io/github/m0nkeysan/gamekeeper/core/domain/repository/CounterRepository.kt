package io.github.m0nkeysan.gamekeeper.core.domain.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.PersistentCounterEntity
import kotlinx.coroutines.flow.Flow

interface CounterRepository {
    fun getAllCounters(): Flow<List<PersistentCounterEntity>>
    suspend fun addCounter(counter: PersistentCounterEntity)
    suspend fun deleteCounter(id: String)
    suspend fun updateCount(id: String, count: Int)
    suspend fun updateCounter(counter: PersistentCounterEntity)
    suspend fun updateOrder(id: String, order: Int)
    suspend fun resetAllCounts()
    suspend fun deleteAllCounters()
}
