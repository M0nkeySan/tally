package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.m0nkeysan.gamekeeper.core.model.Player

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val avatarColor: String,
    val createdAt: Long
)

fun PlayerEntity.toDomain(): Player {
    return Player(
        id = id,
        name = name,
        avatarColor = avatarColor,
        createdAt = createdAt
    )
}

fun Player.toEntity(): PlayerEntity {
    return PlayerEntity(
        id = id,
        name = name,
        avatarColor = avatarColor,
        createdAt = createdAt
    )
}
