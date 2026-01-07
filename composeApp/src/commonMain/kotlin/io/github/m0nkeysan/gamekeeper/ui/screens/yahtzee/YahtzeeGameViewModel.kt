package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeGameEntity
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class YahtzeeGameSelectionState(
    val games: List<YahtzeeGameEntity> = emptyList(),
    val isLoading: Boolean = true
)

class YahtzeeGameViewModel : ViewModel() {
    private val repository = PlatformRepositories.getYahtzeeRepository()
    
    private val _selectionState = MutableStateFlow(YahtzeeGameSelectionState())
    val selectionState: StateFlow<YahtzeeGameSelectionState> = _selectionState.asStateFlow()

    init {
        loadGames()
    }

    private fun loadGames() {
        viewModelScope.launch {
            repository.getAllGames().collect { games ->
                _selectionState.value = YahtzeeGameSelectionState(games = games, isLoading = false)
            }
        }
    }

    fun createGame(name: String, playerCount: Int, playerNames: List<String>, onCreated: (String) -> Unit) {
        val id = Random.nextLong().toString()
        val now = System.currentTimeMillis()
        val firstPlayerIndex = Random.nextInt(playerCount)
        val game = YahtzeeGameEntity(
            id = id,
            name = name,
            playerCount = playerCount,
            playerNames = playerNames.joinToString(","),
            firstPlayerIndex = firstPlayerIndex,
            currentPlayerIndex = firstPlayerIndex,
            createdAt = now,
            updatedAt = now
        )
        viewModelScope.launch {
            repository.saveGame(game)
            onCreated(id)
        }
    }

    fun deleteGame(game: YahtzeeGameEntity) {
        viewModelScope.launch {
            repository.deleteGame(game)
        }
    }
}
