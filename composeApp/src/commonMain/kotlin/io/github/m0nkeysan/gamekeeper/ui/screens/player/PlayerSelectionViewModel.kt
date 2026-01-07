package io.github.m0nkeysan.gamekeeper.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayerSelectionViewModel : ViewModel() {
    private val playerRepository = PlatformRepositories.getPlayerRepository()

    val players: StateFlow<List<Player>> = playerRepository.getAllPlayers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addPlayer(name: String, color: String) {
        viewModelScope.launch {
            try {
                playerRepository.insertPlayer(
                    Player.create(name = name, avatarColor = color)
                )
            } catch (e: Exception) {
                // Handle error (e.g., duplicate name) if necessary
            }
        }
    }

    fun deletePlayer(player: Player) {
        viewModelScope.launch {
            playerRepository.deletePlayer(player)
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
}