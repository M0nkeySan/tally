package io.github.m0nkeysan.gamekeeper.core.model

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class Player(
    val id: String = "",
    val name: String,
    val avatarColor: String = "#FF6200",
    val createdAt: Long = 0L,
    val isActive: Boolean = true,
    val deactivatedAt: Long? = null
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
                createdAt = getCurrentTimeMillis(),
                isActive = true,
                deactivatedAt = null
            )
        }
    }
}

expect fun getCurrentTimeMillis(): Long

/**
 * Sanitizes a player name by:
 * - Trimming leading/trailing whitespace
 * - Converting to lowercase for consistency
 * - Returning null if name becomes empty after sanitization
 */
fun sanitizePlayerName(name: String): String? {
    return name.trim().takeIf { it.isNotEmpty() }
}

/**
 * Checks if two player names should be considered the same
 * (case-insensitive comparison of sanitized names)
 */
fun playerNamesEqual(name1: String, name2: String): Boolean {
    val sanitized1 = sanitizePlayerName(name1)?.lowercase() ?: return false
    val sanitized2 = sanitizePlayerName(name2)?.lowercase() ?: return false
    return sanitized1 == sanitized2
}
