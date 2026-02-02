package io.github.m0nkeysan.tally.core.model

import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class GameTrackerScoreChange(
    val id: String,
    val gameId: String,
    val playerId: String,
    val playerName: String,
    val playerAvatarColor: String,
    val roundNumber: Int,
    val score: Int,
    val timestamp: Long,
    val createdAt: Long
) {
    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun create(
            gameId: String,
            playerId: String,
            playerName: String,
            playerAvatarColor: String,
            roundNumber: Int,
            score: Int
        ): GameTrackerScoreChange {
            val now = Clock.System.now().toEpochMilliseconds()
            return GameTrackerScoreChange(
                id = Uuid.random().toString(),
                gameId = gameId,
                playerId = playerId,
                playerName = playerName,
                playerAvatarColor = playerAvatarColor,
                roundNumber = roundNumber,
                score = score,
                timestamp = now,
                createdAt = now
            )
        }
    }
}
