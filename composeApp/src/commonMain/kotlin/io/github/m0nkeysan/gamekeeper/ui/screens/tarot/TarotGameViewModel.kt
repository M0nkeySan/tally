package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.data.local.database.TarotGameEntity
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class TarotGameSelectionState(
    val games: List<TarotGameEntity> = emptyList(),
    val isLoading: Boolean = true
)

class TarotGameViewModel : ViewModel() {
    private val repository = PlatformRepositories.getTarotRepository()
    
    private val _selectionState = MutableStateFlow(TarotGameSelectionState())
    val selectionState: StateFlow<TarotGameSelectionState> = _selectionState.asStateFlow()

    init {
        loadGames()
    }

    private fun loadGames() {
        viewModelScope.launch {
            repository.getAllGames().collect { games ->
                _selectionState.value = TarotGameSelectionState(games = games, isLoading = false)
            }
        }
    }

    fun createGame(name: String, playerCount: Int, playerNames: List<String>, onCreated: (String) -> Unit) {
        val id = Random.nextLong().toString()
        val now = System.currentTimeMillis()
        val game = TarotGameEntity(
            id = id,
            name = name,
            playerCount = playerCount,
            playerNames = playerNames.joinToString(","),
            createdAt = now,
            updatedAt = now
        )
        viewModelScope.launch {
            repository.saveGame(game)
            onCreated(id)
        }
    }

    fun deleteGame(game: TarotGameEntity) {
        viewModelScope.launch {
            repository.deleteGame(game)
        }
    }
}
