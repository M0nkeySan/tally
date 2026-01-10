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
    @Query("SELECT * FROM players WHERE isActive = true ORDER BY name ASC")
    fun getAllActivePlayers(): Flow<List<PlayerEntity>>
    
    @Query("SELECT * FROM players ORDER BY isActive DESC, name ASC")
    fun getAllPlayersIncludingInactive(): Flow<List<PlayerEntity>>
    
    @Query("SELECT * FROM players WHERE id = :id")
    suspend fun getPlayerById(id: String): PlayerEntity?
    
    @Query("SELECT * FROM players WHERE name = :name AND isActive = true LIMIT 1")
    suspend fun getActivePlayerByName(name: String): PlayerEntity?
    
    @Query("SELECT * FROM players WHERE name = :name ORDER BY isActive DESC LIMIT 1")
    suspend fun getPlayerByName(name: String): PlayerEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)
    
    @Update
    suspend fun updatePlayer(player: PlayerEntity)
    
    @Query("UPDATE players SET isActive = false, deactivatedAt = :timestamp WHERE id = :id")
    suspend fun softDeletePlayer(id: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE players SET isActive = true, deactivatedAt = null WHERE id = :id")
    suspend fun reactivatePlayer(id: String)
    
    @Query("DELETE FROM players WHERE id = :id")
    suspend fun deletePlayer(id: String)
    
    @Query("DELETE FROM players")
    suspend fun deleteAllPlayers()
}
