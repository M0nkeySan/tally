package io.github.m0nkeysan.tally.core.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "counters")
data class PersistentCounterEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val count: Int,
    val color: Long,
    val updatedAt: Long,
    val sortOrder: Int = 0
)
