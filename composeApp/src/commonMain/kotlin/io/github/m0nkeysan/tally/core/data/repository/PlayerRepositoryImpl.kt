package io.github.m0nkeysan.tally.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.m0nkeysan.tally.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.core.model.sanitizePlayerName
import io.github.m0nkeysan.tally.database.PlayerEntity
import io.github.m0nkeysan.tally.database.PlayerQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import io.github.m0nkeysan.tally.core.domain.repository.GameQueryHelper as IGameQueryHelper

class PlayerRepositoryImpl(
    private val playerQueries: PlayerQueries,
    private val gameQueryHelper: IGameQueryHelper? = null
) : PlayerRepository {
    
    override fun getAllPlayers(): Flow<List<Player>> {
        return playerQueries.selectActivePlayers()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override fun getAllPlayersIncludingInactive(): Flow<List<Player>> {
        return playerQueries.selectAllPlayers()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override suspend fun getPlayerById(id: String): Player? {
        return playerQueries.selectPlayerById(id).awaitAsOneOrNull()?.toDomain()
    }
    
    override suspend fun getPlayersByIds(playerIds: List<String>): List<Player> {
        return playerQueries.selectPlayersByIds(playerIds).awaitAsList().map { it.toDomain() }
    }
    
    override suspend fun getPlayerByName(name: String): Player? {
        return playerQueries.selectPlayerByName(name).awaitAsOneOrNull()?.toDomain()
    }
    
    override suspend fun insertPlayer(player: Player) {
        playerQueries.insertPlayer(
            id = player.id,
            name = player.name,
            avatarColor = player.avatarColor,
            createdAt = player.createdAt,
            isActive = if (player.isActive) 1L else 0L,
            deactivatedAt = player.deactivatedAt
        )
    }
    
    override suspend fun updatePlayer(player: Player) {
        insertPlayer(player) // insert OR REPLACE handles update
    }
    
    override suspend fun deletePlayer(player: Player) {
        // Soft delete
        playerQueries.insertPlayer(
            id = player.id,
            name = player.name,
            avatarColor = player.avatarColor,
            createdAt = player.createdAt,
            isActive = 0L,
            deactivatedAt = io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis()
        )
    }
    
    override suspend fun reactivatePlayer(player: Player) {
        playerQueries.insertPlayer(
            id = player.id,
            name = player.name,
            avatarColor = player.avatarColor,
            createdAt = player.createdAt,
            isActive = 1L,
            deactivatedAt = null
        )
    }
    
    override suspend fun deleteAllPlayers() {
        playerQueries.getDistinctPlayerIds().awaitAsList().forEach { id ->
            playerQueries.deletePlayer(id)
        }
    }
    
    override suspend fun createPlayerOrReactivate(name: String, avatarColor: String): Player? {
        val sanitized = sanitizePlayerName(name) ?: return null
        
        val deactivatedPlayer = playerQueries.selectAllPlayers().awaitAsList()
            .find { it.name == sanitized }?.toDomain()
        
        return if (deactivatedPlayer != null && !deactivatedPlayer.isActive) {
            reactivatePlayer(deactivatedPlayer)
            getPlayerById(deactivatedPlayer.id)
        } else if (deactivatedPlayer != null && deactivatedPlayer.isActive) {
            null // Already exists and active
        } else {
            val newPlayer = Player.create(sanitized, avatarColor)
            insertPlayer(newPlayer)
            newPlayer
        }
    }
    
    override suspend fun smartDeletePlayer(player: Player): Boolean {
        return try {
            val gameCount = gameQueryHelper?.getGameCountForPlayer(player.id) ?: 0
            
            if (gameCount == 0) {
                playerQueries.deletePlayer(id = player.id)
            } else {
                deletePlayer(player) // soft delete
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun PlayerEntity.toDomain(): Player {
        return Player(
            id = id,
            name = name,
            avatarColor = avatarColor,
            createdAt = createdAt,
            isActive = isActive != 0L,
            deactivatedAt = deactivatedAt
        )
    }
}
