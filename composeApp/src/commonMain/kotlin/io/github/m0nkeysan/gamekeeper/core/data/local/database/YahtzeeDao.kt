package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface YahtzeeDao {
    @Query("SELECT * FROM yahtzee_games ORDER BY updatedAt DESC")
    fun getAllGames(): Flow<List<YahtzeeGameEntity>>

    @Query("SELECT * FROM yahtzee_games WHERE id = :id")
    suspend fun getGameById(id: String): YahtzeeGameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: YahtzeeGameEntity)

    @Delete
    suspend fun deleteGame(game: YahtzeeGameEntity)

    @Query("SELECT * FROM yahtzee_scores WHERE gameId = :gameId")
    fun getScoresForGame(gameId: String): Flow<List<YahtzeeScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: YahtzeeScoreEntity)

    @Query("DELETE FROM yahtzee_scores WHERE gameId = :gameId")
    suspend fun deleteScoresForGame(gameId: String)
    
    @Query("SELECT COUNT(*) FROM yahtzee_games WHERE playerIds LIKE '%' || :playerId || '%'")
    suspend fun countGamesWithPlayer(playerId: String): Int

    // Statistics queries
    @Query("SELECT * FROM yahtzee_games WHERE playerIds LIKE '%' || :playerId || '%' ORDER BY updatedAt DESC")
    suspend fun getAllGamesForPlayer(playerId: String): List<YahtzeeGameEntity>

    @Query("SELECT * FROM yahtzee_games WHERE playerIds LIKE '%' || :playerId || '%' AND isFinished = 1 ORDER BY updatedAt DESC")
    suspend fun getFinishedGamesForPlayer(playerId: String): List<YahtzeeGameEntity>

    @Query("SELECT * FROM yahtzee_scores WHERE playerId = :playerId")
    suspend fun getAllScoresForPlayer(playerId: String): List<YahtzeeScoreEntity>

    @Query("SELECT DISTINCT playerIds FROM yahtzee_games WHERE isFinished = 1")
    suspend fun getDistinctPlayerIds(): List<String>
}
