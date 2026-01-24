package io.github.m0nkeysan.tally.core.model

import io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

/**
 * Domain model representing a persistent counter.
 */
@Serializable
data class Counter(
    val id: String,
    val name: String,
    val count: Int,
    val color: Long,
    val updatedAt: Long,
    val sortOrder: Int = 0
) {
    companion object {
        fun create(
            name: String,
            count: Int = 0,
            color: Long,
            sortOrder: Int = 0
        ): Counter {
            return Counter(
                id = Uuid.random().toString(),
                name = name,
                count = count,
                color = color,
                updatedAt = getCurrentTimeMillis(),
                sortOrder = sortOrder
            )
        }
    }
}

/**
 * Sanitizes a counter name by:
 * - Trimming leading/trailing whitespace
 * - Returning null if name becomes empty after sanitization
 */
fun sanitizeCounterName(name: String): String? {
    return name.trim().takeIf { it.isNotEmpty() }
}
