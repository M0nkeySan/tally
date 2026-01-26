package io.github.m0nkeysan.tally.core.data.web.repository

import io.github.m0nkeysan.tally.core.data.local.repository.GameQueryHelper
import io.github.m0nkeysan.tally.core.data.web.WebFlowStore
import io.github.m0nkeysan.tally.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.core.model.sanitizePlayerName
import io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Web implementation of PlayerRepository using WebFlowStore with localStorage.
 */
class PlayerRepositoryWebImpl(
    private val playerStore: WebFlowStore<Player>,
    private val gameQueryHelper: GameQueryHelper? = null
) : PlayerRepository {

    override fun getAllPlayers(): Flow<List<Player>> {
        return playerStore.flow.map { players ->
            players.filter { it.isActive }.sortedBy { it.name }
        }
    }

    override fun getAllPlayersIncludingInactive(): Flow<List<Player>> {
        return playerStore.flow.map { players ->
            players.sortedWith(compareByDescending<Player> { it.isActive }.thenBy { it.name })
        }
    }

    override suspend fun getPlayerById(id: String): Player? {
        val players = playerStore.get()
        return players.firstOrNull { it.id == id }
    }

    override suspend fun getPlayersByIds(playerIds: List<String>): List<Player> {
        val players = playerStore.get()
        return players.filter { it.id in playerIds }
    }

    override suspend fun getPlayerByName(name: String): Player? {
        val players = playerStore.get()
        return players.firstOrNull { player ->
            player.name.equals(name, ignoreCase = true)
        }
    }

    override suspend fun insertPlayer(player: Player) {
        playerStore.update { players ->
            players + player
        }
    }

    override suspend fun updatePlayer(player: Player) {
        playerStore.update { players ->
            players.map { if (it.id == player.id) player else it }
        }
    }

    override suspend fun deletePlayer(player: Player) {
        // Soft delete: mark as inactive
        playerStore.update { players ->
            players.map {
                if (it.id == player.id) {
                    it.copy(
                        isActive = false,
                        deactivatedAt = getCurrentTimeMillis()
                    )
                } else {
                    it
                }
            }
        }
    }

    override suspend fun reactivatePlayer(player: Player) {
        playerStore.update { players ->
            players.map {
                if (it.id == player.id) {
                    it.copy(
                        isActive = true,
                        deactivatedAt = null
                    )
                } else {
                    it
                }
            }
        }
    }

    override suspend fun deleteAllPlayers() {
        playerStore.clear()
    }

    override suspend fun createPlayerOrReactivate(name: String, avatarColor: String): Player? {
        val sanitized = sanitizePlayerName(name) ?: return null

        // Check if a deactivated player with this name exists
        val deactivatedPlayer = getPlayerByName(sanitized)

        return if (deactivatedPlayer != null && !deactivatedPlayer.isActive) {
            // Reactivate the deactivated player
            reactivatePlayer(deactivatedPlayer)
            getPlayerById(deactivatedPlayer.id)
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
                playerStore.update { players ->
                    players.filter { it.id != player.id }
                }
            } else {
                // Has games linked - soft delete (mark as inactive)
                deletePlayer(player)
            }
            true
        } catch (e: Exception) {
            console.error("Failed to delete player: ${e.message}")
            false
        }
    }
}
