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
    
    // Counter change tracking (session-based, in-memory only)
    /**
     * Log a counter value change to the session history.
     * History is stored in memory only and cleared when the app closes.
     */
    suspend fun logCounterChange(
        counterId: String,
        counterName: String,
        counterColor: Long,
        previousValue: Int,
        newValue: Int
    )
    
    /**
     * Log a counter deletion to the session history.
     * History is stored in memory only and cleared when the app closes.
     */
    suspend fun logCounterDeletion(
        counterId: String,
        counterName: String,
        counterColor: Long
    )
    
    /**
     * Get the raw counter history from the session.
     * Returns all counter changes made during this app session.
     */
    fun getCounterHistory(): Flow<List<CounterChange>>
    
    /**
     * Get the merged counter history from the session.
     * Consecutive changes for the same counter are grouped together.
     * History persists only for the current app session.
     */
    fun getMergedCounterHistory(): Flow<List<MergedCounterChange>>
    
    /**
     * Clear all counter history from the session.
     */
    suspend fun clearCounterHistory()
}
