package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.PlayerDao
import io.github.m0nkeysan.gamekeeper.core.data.local.database.PlayerEntity
import io.github.m0nkeysan.gamekeeper.core.data.local.database.toDomain
import io.github.m0nkeysan.gamekeeper.core.data.local.database.toEntity
import io.github.m0nkeysan.gamekeeper.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.core.model.playerNamesEqual
import io.github.m0nkeysan.gamekeeper.core.model.sanitizePlayerName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayerRepositoryImpl(
    private val playerDao: PlayerDao,
    private val gameQueryHelper: GameQueryHelper? = null
) : PlayerRepository {
    
    override fun getAllPlayers(): Flow<List<io.github.m0nkeysan.gamekeeper.core.model.Player>> {
        return playerDao.getAllActivePlayers().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getAllPlayersIncludingInactive(): Flow<List<io.github.m0nkeysan.gamekeeper.core.model.Player>> {
        return playerDao.getAllPlayersIncludingInactive().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getPlayerById(id: String): io.github.m0nkeysan.gamekeeper.core.model.Player? {
        return playerDao.getPlayerById(id)?.toDomain()
    }
    
    override suspend fun getPlayerByName(name: String): io.github.m0nkeysan.gamekeeper.core.model.Player? {
        return playerDao.getPlayerByName(name)?.toDomain()
    }
    
    override suspend fun insertPlayer(player: io.github.m0nkeysan.gamekeeper.core.model.Player) {
        playerDao.insertPlayer(player.toEntity())
    }
    
    override suspend fun updatePlayer(player: io.github.m0nkeysan.gamekeeper.core.model.Player) {
        playerDao.updatePlayer(player.toEntity())
    }
    
    override suspend fun deletePlayer(player: io.github.m0nkeysan.gamekeeper.core.model.Player) {
        playerDao.softDeletePlayer(player.id)
    }
    
    override suspend fun reactivatePlayer(player: io.github.m0nkeysan.gamekeeper.core.model.Player) {
        playerDao.reactivatePlayer(player.id)
    }
    
    override suspend fun deleteAllPlayers() {
        playerDao.deleteAllPlayers()
    }
    
    override suspend fun createPlayerOrReactivate(name: String, avatarColor: String): Player? {
        val sanitized = sanitizePlayerName(name) ?: return null
        
        // Check if a deactivated player with this name exists
        val deactivatedPlayer = playerDao.getPlayerByName(sanitized)?.toDomain()
        
        return if (deactivatedPlayer != null && !deactivatedPlayer.isActive) {
            // Reactivate the deactivated player
            playerDao.reactivatePlayer(deactivatedPlayer.id)
            // Return the reactivated player
            playerDao.getPlayerById(deactivatedPlayer.id)?.toDomain()
        } else {
            // Create a new player with the sanitized name
            val newPlayer = Player.create(sanitized, avatarColor)
            insertPlayer(newPlayer)
            newPlayer
        }
    }
    
    override suspend fun smartDeletePlayer(player: Player): Boolean {
        return try {
            // Check if player is linked to any games
            val gameCount = gameQueryHelper?.getGameCountForPlayer(player.id) ?: 0
            
            if (gameCount == 0) {
                // No games linked - hard delete the player completely
                playerDao.deletePlayer(player.id)
            } else {
                // Has games linked - soft delete (mark as inactive)
                playerDao.softDeletePlayer(player.id)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
