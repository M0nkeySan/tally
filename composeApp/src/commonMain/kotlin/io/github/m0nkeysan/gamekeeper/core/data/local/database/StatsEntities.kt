package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.*
import io.github.m0nkeysan.gamekeeper.core.model.PlayerStats
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "player_stats")
data class PlayerStatsEntity(
    @PrimaryKey
    val playerId: String,
    val playerName: String,
    val avatarColor: String,
    val totalGamesPlayed: Int,
    val tarotGamesPlayed: Int,
    val tarotGamesWon: Int,
    val tarotTotalScore: Int,
    val yahtzeeGamesPlayed: Int,
    val yahtzeeGamesWon: Int,
    val yahtzeeHighestScore: Int,
    val yahtzeeTotalScore: Int,
    val counterGamesPlayed: Int,
    val counterGamesWon: Int,
    val counterTotalScore: Int,
    val lastPlayedAt: Long
)

@Entity(tableName = "game_participants")
data class GameParticipantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameId: String,
    val playerId: String,
    val gameType: String,
    val score: Int,
    val isWinner: Boolean,
    val playedAt: Long
)

@Dao
interface StatsDao {
    @Query("SELECT * FROM player_stats")
    fun getAllPlayerStats(): Flow<List<PlayerStatsEntity>>

    @Query("SELECT * FROM player_stats WHERE playerId = :playerId")
    fun getPlayerStats(playerId: String): Flow<PlayerStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerStats(stats: PlayerStatsEntity)

    @Query("SELECT * FROM game_participants WHERE playerId = :playerId ORDER BY playedAt DESC")
    fun getPlayerGameHistory(playerId: String): Flow<List<GameParticipantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameParticipant(participant: GameParticipantEntity)
    
    @Query("DELETE FROM game_participants WHERE gameId = :gameId")
    suspend fun deleteGameParticipants(gameId: String)
    
    @Query("SELECT * FROM game_participants WHERE gameType = :gameType ORDER BY playedAt DESC")
    fun getGamesByType(gameType: String): Flow<List<GameParticipantEntity>>
}
