package io.github.m0nkeysan.gamekeeper.core.domain.repository

import io.github.m0nkeysan.gamekeeper.core.model.Player
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun getAllPlayers(): Flow<List<Player>>
    fun getAllPlayersIncludingInactive(): Flow<List<Player>>
    suspend fun getPlayerById(id: String): Player?
    suspend fun getPlayerByName(name: String): Player?
    suspend fun insertPlayer(player: Player)
    suspend fun updatePlayer(player: Player)
    suspend fun deletePlayer(player: Player)
    suspend fun reactivatePlayer(player: Player)
    suspend fun deleteAllPlayers()
    
    /**
     * Creates a new player or reactivates a deactivated player with the same name.
     * If a deactivated player with the sanitized name exists, reactivates it.
     * Otherwise, creates a new player.
     * Returns the resulting player (newly created or reactivated).
     */
    suspend fun createPlayerOrReactivate(name: String, avatarColor: String): Player?
}
