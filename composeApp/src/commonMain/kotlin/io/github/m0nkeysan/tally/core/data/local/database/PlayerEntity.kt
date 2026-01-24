package io.github.m0nkeysan.tally.core.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.m0nkeysan.tally.core.model.Player

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val avatarColor: String,
    val createdAt: Long,
    val isActive: Boolean = true,
    val deactivatedAt: Long? = null
)

fun PlayerEntity.toDomain(): Player {
    return Player(
        id = id,
        name = name,
        avatarColor = avatarColor,
        createdAt = createdAt,
        isActive = isActive,
        deactivatedAt = deactivatedAt
    )
}

fun Player.toEntity(): PlayerEntity {
    return PlayerEntity(
        id = id,
        name = name,
        avatarColor = avatarColor,
        createdAt = createdAt,
        isActive = isActive,
        deactivatedAt = deactivatedAt
    )
}
