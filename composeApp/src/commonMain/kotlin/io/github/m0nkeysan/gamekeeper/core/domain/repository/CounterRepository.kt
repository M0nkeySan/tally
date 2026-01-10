package io.github.m0nkeysan.gamekeeper.core.domain.repository

import io.github.m0nkeysan.gamekeeper.core.model.Counter
import io.github.m0nkeysan.gamekeeper.core.model.CounterChange
import io.github.m0nkeysan.gamekeeper.core.model.MergedCounterChange
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
    
    // Counter change tracking
    suspend fun logCounterChange(
        counterId: String,
        counterName: String,
        counterColor: Long,
        previousValue: Int,
        newValue: Int
    )
    
    suspend fun logCounterDeletion(
        counterId: String,
        counterName: String,
        counterColor: Long
    )
    
    fun getCounterHistory(): Flow<List<CounterChange>>
    
    fun getMergedCounterHistory(): Flow<List<MergedCounterChange>>
    
    suspend fun clearCounterHistory()
}
