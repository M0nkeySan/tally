package io.github.m0nkeysan.tally.core.domain.repository

/**
 * Helper interface for querying game-player relationships across different storage implementations.
 */
interface GameQueryHelper {
    suspend fun getGameCountForPlayer(playerId: String): Int
}
