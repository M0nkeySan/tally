package io.github.m0nkeysan.tally.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.platform.PlatformRepositories
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayerSelectionViewModel : ViewModel() {
    private val playerRepository = PlatformRepositories.getPlayerRepository()
    private val gameQueryHelper = PlatformRepositories.getGameQueryHelper()

    val players: StateFlow<List<Player>> = playerRepository.getAllPlayers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val allPlayersIncludingInactive: StateFlow<List<Player>> = playerRepository.getAllPlayersIncludingInactive()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addPlayer(name: String, color: String) {
        viewModelScope.launch {
            try {
                playerRepository.createPlayerOrReactivate(name, color)
            } catch (e: Exception) {
            }
        }
    }

    fun deletePlayer(player: Player) {
        viewModelScope.launch {
            playerRepository.smartDeletePlayer(player)
        }
    }

    fun updatePlayer(player: Player, newName: String, newColor: String) {
        viewModelScope.launch {
            val updatedPlayer = player.copy(
                name = newName,
                avatarColor = newColor
            )
            playerRepository.updatePlayer(updatedPlayer)
        }
    }
    
    fun reactivatePlayer(player: Player) {
        viewModelScope.launch {
            playerRepository.reactivatePlayer(player)
        }
    }
    
    suspend fun getGameCountForPlayer(playerId: String): Int {
        return gameQueryHelper.getGameCountForPlayer(playerId)
    }
}