package io.github.m0nkeysan.tally.core.data.local.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TarotDao {
    @Query("SELECT * FROM tarot_games ORDER BY updatedAt DESC")
    fun getAllGames(): Flow<List<TarotGameEntity>>

    @Query("SELECT * FROM tarot_games WHERE id = :id")
    suspend fun getGameById(id: String): TarotGameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: TarotGameEntity)

    @Delete
    suspend fun deleteGame(game: TarotGameEntity)

    @Query("SELECT * FROM tarot_rounds WHERE gameId = :gameId ORDER BY roundNumber ASC")
    fun getRoundsForGame(gameId: String): Flow<List<TarotRoundEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: TarotRoundEntity)
    
    @Query("DELETE FROM tarot_rounds WHERE gameId = :gameId")
    suspend fun deleteRoundsForGame(gameId: String)
    
    @Query("SELECT COUNT(*) FROM tarot_games WHERE playerIds LIKE '%' || :playerId || '%'")
    suspend fun countGamesWithPlayer(playerId: String): Int

    // ============ STATISTICS QUERIES ============

    /**
     * Get aggregated player statistics across all games.
     * Returns null if player has no games.
     */
    @Query("""
        SELECT 
            COUNT(DISTINCT r.gameId) as totalGames,
            COUNT(*) as totalRounds,
            SUM(CASE WHEN r.takerPlayerId = :playerId THEN 1 ELSE 0 END) as takerRounds,
            SUM(CASE WHEN r.takerPlayerId = :playerId AND r.score > 0 THEN 1 ELSE 0 END) as takerWins,
            AVG(CASE WHEN r.takerPlayerId = :playerId THEN r.score ELSE NULL END) as avgTakerScore,
            SUM(r.score) as totalScore
        FROM tarot_rounds r
        INNER JOIN tarot_games g ON r.gameId = g.id
        WHERE (',' || g.playerIds || ',') LIKE ('%,' || :playerId || ',%')
    """)
    suspend fun getPlayerStatistics(playerId: String): PlayerStatisticsRaw?

    /**
     * Get bid statistics for a player across all games.
     * Shows win rates and averages for each bid type.
     */
    @Query("""
        SELECT 
            r.bid,
            COUNT(*) as count,
            SUM(CASE WHEN r.score > 0 THEN 1 ELSE 0 END) as wins,
            AVG(r.score) as avgScore
        FROM tarot_rounds r
        INNER JOIN tarot_games g ON r.gameId = g.id
        WHERE r.takerPlayerId = :playerId 
          AND (',' || g.playerIds || ',') LIKE ('%,' || :playerId || ',%')
        GROUP BY r.bid
        ORDER BY r.bid ASC
    """)
    suspend fun getBidStatistics(playerId: String): List<BidStatisticsRaw>

    /**
     * Get recent games for a player.
     */
    @Query("""
        SELECT * FROM tarot_games 
        WHERE (',' || playerIds || ',') LIKE ('%,' || :playerId || ',%')
        ORDER BY updatedAt DESC
        LIMIT :limit
    """)
    suspend fun getRecentGamesForPlayer(playerId: String, limit: Int): List<TarotGameEntity>

    /**
     * Get all rounds for a game with detailed information.
     */
    @Query("""
        SELECT * FROM tarot_rounds
        WHERE gameId = :gameId
        ORDER BY roundNumber ASC
    """)
    suspend fun getRoundBreakdown(gameId: String): List<TarotRoundEntity>
}

/**
 * Raw query result for player statistics aggregation.
 */
data class PlayerStatisticsRaw(
    val totalGames: Int,
    val totalRounds: Int,
    val takerRounds: Int,
    val takerWins: Int,
    val avgTakerScore: Double?,
    val totalScore: Int
)

/**
 * Raw query result for bid statistics aggregation.
 */
data class BidStatisticsRaw(
    val bid: String,
    val count: Int,
    val wins: Int,
    val avgScore: Double
)
