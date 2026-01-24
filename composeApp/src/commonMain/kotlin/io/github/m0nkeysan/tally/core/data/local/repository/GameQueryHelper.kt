package io.github.m0nkeysan.tally.core.data.local.repository

import io.github.m0nkeysan.tally.core.data.local.database.TarotDao
import io.github.m0nkeysan.tally.core.data.local.database.YahtzeeDao

/**
 * Helper class for querying game-player relationships.
 * Used to detect if a player is linked to any active games.
 */
class GameQueryHelper(
    private val tarotDao: TarotDao,
    private val yahtzeeDao: YahtzeeDao
) {
    /**
     * Get total count of games (Tarot + Yahtzee) that contain this player.
     * Used in deactivation warning dialog to show user how many games will be affected.
     *
     * @param playerId The ID of the player to check
     * @return Count of games containing this player
     */
    suspend fun getGameCountForPlayer(playerId: String): Int {
        val tarotCount = tarotDao.countGamesWithPlayer(playerId)
        val yahtzeeCount = yahtzeeDao.countGamesWithPlayer(playerId)
        return tarotCount + yahtzeeCount
    }
}
