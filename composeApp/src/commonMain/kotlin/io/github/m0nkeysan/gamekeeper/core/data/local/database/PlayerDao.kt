package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players ORDER BY createdAt DESC")
    fun getAllPlayers(): Flow<List<PlayerEntity>>
    
    @Query("SELECT * FROM players WHERE id = :id")
    fun getPlayerById(id: String): PlayerEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)
    
    @Update
    suspend fun updatePlayer(player: PlayerEntity)
    
    @Delete
    suspend fun deletePlayer(player: PlayerEntity)
    
    @Query("DELETE FROM players")
    suspend fun deleteAllPlayers()
}
