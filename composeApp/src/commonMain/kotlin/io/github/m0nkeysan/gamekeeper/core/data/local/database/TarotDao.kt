package io.github.m0nkeysan.gamekeeper.core.data.local.database

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
}
