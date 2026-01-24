package io.github.m0nkeysan.tally.core.domain.repository

import io.github.m0nkeysan.tally.core.model.BidStatistic
import io.github.m0nkeysan.tally.core.model.GameStatistics
import io.github.m0nkeysan.tally.core.model.PlayerRanking
import io.github.m0nkeysan.tally.core.model.PlayerStatistics
import io.github.m0nkeysan.tally.core.model.RoundStatistic
import io.github.m0nkeysan.tally.core.model.TarotGame

/**
 * Repository for Tarot game statistics and analytics.
 *
 * Provides cross-game and per-game statistics for players,
 * including performance metrics, bid statistics, and game history.
 */
interface TarotStatisticsRepository {
    
    /**
     * Get aggregated player statistics across all Tarot games.
     *
     * @param playerId The player ID
     * @return PlayerStatistics with cross-game metrics, or null if no games
     */
    suspend fun getPlayerStatistics(playerId: String): PlayerStatistics?

    /**
     * Get bid-specific statistics for a player.
     *
     * Shows win rates and average scores for each bid type
     * (Prise, Garde, Garde Sans, Garde Contre).
     *
     * @param playerId The player ID
     * @return List of BidStatistics, one per bid type played
     */
    suspend fun getBidStatistics(playerId: String): List<BidStatistic>

    /**
     * Get recent games for a player.
     *
     * @param playerId The player ID
     * @param limit Maximum number of games to return (default 10)
     * @return List of recent TarotGames in descending order
     */
    suspend fun getRecentGames(playerId: String, limit: Int = 10): List<TarotGame>

    /**
     * Get statistics for the current game being viewed.
     *
     * Includes current standings, leading player, and game overview.
     *
     * @param gameId The game ID
     * @return GameStatistics with current rankings, or null if game not found
     */
    suspend fun getCurrentGameStatistics(gameId: String): GameStatistics?

    /**
     * Get detailed round-by-round statistics for a game.
     *
     * @param gameId The game ID
     * @return List of RoundStatistics for each round
     */
    suspend fun getRoundBreakdown(gameId: String): List<RoundStatistic>

    /**
     * Get player rankings for a specific game.
     *
     * Shows final standings with scores and taker performance.
     *
     * @param gameId The game ID
     * @return List of PlayerRankings sorted by rank
     */
    suspend fun getPlayerRankings(gameId: String): List<PlayerRanking>
}
