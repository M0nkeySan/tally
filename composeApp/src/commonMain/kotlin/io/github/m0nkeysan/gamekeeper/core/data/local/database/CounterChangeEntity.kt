package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "counter_changes")
data class CounterChangeEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    val counterId: String,           // FK to Counter
    val counterName: String,         // Denormalized for display (in case counter deleted)
    val counterColor: Long,          // ARGB format, denormalized
    
    val previousValue: Int,          // State before change
    val newValue: Int,               // State after change
    val changeDelta: Int,            // newValue - previousValue
    
    val isDeleted: Boolean = false,  // True if this is a deletion entry
    
    val timestamp: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
