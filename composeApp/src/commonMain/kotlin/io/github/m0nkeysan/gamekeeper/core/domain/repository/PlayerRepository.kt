package io.github.m0nkeysan.gamekeeper.core.domain.repository

import io.github.m0nkeysan.gamekeeper.core.model.Player
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun getAllPlayers(): Flow<List<Player>>
    suspend fun getPlayerById(id: String): Player?
    suspend fun insertPlayer(player: Player)
    suspend fun updatePlayer(player: Player)
    suspend fun deletePlayer(player: Player)
    suspend fun deleteAllPlayers()
}
