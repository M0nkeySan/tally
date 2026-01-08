package io.github.m0nkeysan.gamekeeper.core.domain.repository

import io.github.m0nkeysan.gamekeeper.core.model.Counter
import kotlinx.coroutines.flow.Flow

interface CounterRepository {
    fun getAllCounters(): Flow<List<Counter>>
    suspend fun addCounter(counter: Counter)
    suspend fun deleteCounter(id: String)
    suspend fun updateCount(id: String, count: Int)
    suspend fun updateCounter(counter: Counter)
    suspend fun updateOrder(id: String, order: Int)
    suspend fun resetAllCounts()
    suspend fun deleteAllCounters()
}
