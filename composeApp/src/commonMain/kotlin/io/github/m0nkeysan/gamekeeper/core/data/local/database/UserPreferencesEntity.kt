package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey
    val key: String,
    val value: String
)
