package io.github.m0nkeysan.gamekeeper.ui.screens.addplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddPlayerState(
    val name: String = "",
    val avatarColor: String = "#FF6200",
    val nameError: String? = null
)

class AddPlayerViewModel : ViewModel() {
    private val _state = MutableStateFlow(AddPlayerState())
    val state: StateFlow<AddPlayerState> = _state.asStateFlow()

    private val playerRepository = PlatformRepositories.getPlayerRepository()

    fun updateName(name: String) {
        _state.update { it.copy(
            name = name,
            nameError = if (name.isBlank()) "Name cannot be empty" else null
        ) }
    }

    fun updateColor(color: String) {
        _state.update { it.copy(avatarColor = color) }
    }

    fun addPlayer() {
        // Utilisation du viewModelScope standard de androidx.lifecycle
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState.name.isNotBlank() && currentState.nameError == null) {
                try {
                    playerRepository.insertPlayer(
                        Player.create(
                            name = currentState.name,
                            avatarColor = currentState.avatarColor
                        )
                    )
                } catch (e: Exception) {
                    // Optionnel : Gérer l'erreur d'insertion (ex: nom déjà pris)
                    _state.update { it.copy(nameError = "Failed to save player") }
                }
            }
        }
    }
}