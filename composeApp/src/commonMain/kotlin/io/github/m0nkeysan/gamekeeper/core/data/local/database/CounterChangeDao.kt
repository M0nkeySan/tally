package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterChangeDao {
    @Query("SELECT * FROM counter_changes ORDER BY timestamp DESC")
    fun getAllChanges(): Flow<List<CounterChangeEntity>>
    
    @Query("SELECT * FROM counter_changes WHERE counterId = :counterId ORDER BY timestamp DESC")
    fun getChangesForCounter(counterId: String): Flow<List<CounterChangeEntity>>
    
    @Insert
    suspend fun insertChange(change: CounterChangeEntity)
    
    @Delete
    suspend fun deleteChange(change: CounterChangeEntity)
    
    @Query("DELETE FROM counter_changes")
    suspend fun deleteAllChanges()
    
    @Query("DELETE FROM counter_changes WHERE counterId = :counterId")
    suspend fun deleteChangesForCounter(counterId: String)
}
