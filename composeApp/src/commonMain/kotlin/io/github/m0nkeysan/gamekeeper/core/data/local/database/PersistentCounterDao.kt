package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PersistentCounterDao {
    @Query("SELECT * FROM counters ORDER BY sortOrder ASC")
    fun getAllCounters(): Flow<List<PersistentCounterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCounter(counter: PersistentCounterEntity)

    @Query("DELETE FROM counters WHERE id = :id")
    suspend fun deleteCounter(id: String)

    @Query("UPDATE counters SET count = :count WHERE id = :id")
    suspend fun updateCount(id: String, count: Int)

    @Query("UPDATE counters SET sortOrder = :order WHERE id = :id")
    suspend fun updateOrder(id: String, order: Int)

    @Query("UPDATE counters SET count = 0")
    suspend fun resetAllCounts()

    @Query("DELETE FROM counters")
    suspend fun deleteAllCounters()
}
