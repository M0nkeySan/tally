package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.data.local.database.TarotGameEntity
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

data class TarotGameDisplayModel(
    val id: String,
    val name: String,
    val playerCount: Int,
    val playerNames: String,
    val createdAt: Long,
    val updatedAt: Long,
    val rawEntity: TarotGameEntity
)

data class TarotGameSelectionState(
    val games: List<TarotGameDisplayModel> = emptyList(),
    val isLoading: Boolean = true
)

class TarotGameViewModel : ViewModel() {
    private val repository = PlatformRepositories.getTarotRepository()
    private val playerRepository = PlatformRepositories.getPlayerRepository()

    private val _selectionState = MutableStateFlow(TarotGameSelectionState())
    val selectionState: StateFlow<TarotGameSelectionState> = _selectionState.asStateFlow()

    val allPlayers: Flow<List<Player>> = playerRepository.getAllPlayers()

    init {
        loadGames()
    }

    private fun loadGames() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.getAllGames().collect { games ->
                    val displayModels = games.map { game ->
                        val names = game.playerIds.split(",").map { id ->
                            playerRepository.getPlayerById(id)?.name ?: "Unknown"
                        }.joinToString(", ")

                        TarotGameDisplayModel(
                            id = game.id,
                            name = game.name,
                            playerCount = game.playerCount,
                            playerNames = names,
                            createdAt = game.createdAt,
                            updatedAt = game.updatedAt,
                            rawEntity = game
                        )
                    }
                    _selectionState.value =
                        TarotGameSelectionState(games = displayModels, isLoading = false)
                }
            }
        }
    }

    fun createGame(
        name: String,
        playerCount: Int,
        players: List<Player>,
        onCreated: (String) -> Unit
    ) {
        val id = kotlin.random.Random.nextLong().toString()
        val now = System.currentTimeMillis()

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                players.forEach { player ->
                    if (playerRepository.getPlayerById(player.id) == null) {
                        playerRepository.insertPlayer(player)
                    }
                }

                val game = TarotGameEntity(
                    id = id,
                    name = name,
                    playerCount = playerCount,
                    playerIds = players.joinToString(",") { it.id },
                    createdAt = now,
                    updatedAt = now
                )
                repository.saveGame(game)
            }
            onCreated(id)
        }
    }

    fun deleteGame(game: TarotGameEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteGame(game)
            }
        }
    }
}
