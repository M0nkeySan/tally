package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.PlayerDao
import io.github.m0nkeysan.gamekeeper.core.data.local.database.PlayerEntity
import io.github.m0nkeysan.gamekeeper.core.data.local.database.toDomain
import io.github.m0nkeysan.gamekeeper.core.data.local.database.toEntity
import io.github.m0nkeysan.gamekeeper.core.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayerRepositoryImpl(
    private val playerDao: PlayerDao
) : PlayerRepository {
    
    override fun getAllPlayers(): Flow<List<io.github.m0nkeysan.gamekeeper.core.model.Player>> {
        return playerDao.getAllPlayers().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getPlayerById(id: String): io.github.m0nkeysan.gamekeeper.core.model.Player? {
        return playerDao.getPlayerById(id)?.toDomain()
    }
    
    override suspend fun insertPlayer(player: io.github.m0nkeysan.gamekeeper.core.model.Player) {
        playerDao.insertPlayer(player.toEntity())
    }
    
    override suspend fun updatePlayer(player: io.github.m0nkeysan.gamekeeper.core.model.Player) {
        playerDao.updatePlayer(player.toEntity())
    }
    
    override suspend fun deletePlayer(player: io.github.m0nkeysan.gamekeeper.core.model.Player) {
        playerDao.deletePlayer(player.toEntity())
    }
    
    override suspend fun deleteAllPlayers() {
        playerDao.deleteAllPlayers()
    }
}
