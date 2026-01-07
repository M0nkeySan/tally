package io.github.m0nkeysan.gamekeeper.core.model

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class Player(
    val id: String = "",
    val name: String,
    val avatarColor: String = "#FF6200",
    val createdAt: Long = 0L
) {
    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun create(
            name: String,
            avatarColor: String = "#FF6200"
        ): Player {
            return Player(
                id = Uuid.random().toString(),
                name = name,
                avatarColor = avatarColor,
                createdAt = getCurrentTimeMillis()
            )
        }
    }
}

expect fun getCurrentTimeMillis(): Long
