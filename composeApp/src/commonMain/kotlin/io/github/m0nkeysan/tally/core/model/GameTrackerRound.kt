package io.github.m0nkeysan.tally.core.model

import io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class GameTrackerRound(
    val id: String,
    val gameId: String,
    val roundNumber: Int,
    val playerId: String,
    val score: Int,
    val notes: String?,
    val createdAt: Long
) {
    companion object {
        fun create(
            gameId: String,
            roundNumber: Int,
            playerId: String,
            score: Int,
            notes: String? = null
        ): GameTrackerRound {
            return GameTrackerRound(
                id = Uuid.random().toString(),
                gameId = gameId,
                roundNumber = roundNumber,
                playerId = playerId,
                score = score,
                notes = notes,
                createdAt = getCurrentTimeMillis()
            )
        }
    }
}
