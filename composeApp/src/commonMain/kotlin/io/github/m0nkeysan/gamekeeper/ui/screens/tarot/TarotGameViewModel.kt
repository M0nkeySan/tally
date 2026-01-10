package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.core.model.TarotGame
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class TarotGameDisplayModel(
    val id: String,
    val name: String,
    val playerCount: Int,
    val playerNames: String,
    val createdAt: Long,
    val updatedAt: Long,
    val game: TarotGame
)

data class TarotGameSelectionState(
    val games: List<TarotGameDisplayModel> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
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
            try {
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
                                game = game
                            )
                        }
                        _selectionState.value =
                            TarotGameSelectionState(games = displayModels, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _selectionState.value = TarotGameSelectionState(
                    games = emptyList(),
                    isLoading = false,
                    error = "Failed to load games: ${e.message}"
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun createGame(
        name: String,
        playerCount: Int,
        players: List<Player>,
        onCreated: (String) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val gameId = withContext(Dispatchers.IO) {
                    players.forEach { player ->
                        if (playerRepository.getPlayerById(player.id) == null) {
                            playerRepository.insertPlayer(player)
                        }
                    }

                    val game = TarotGame.create(
                        players = players,
                        playerCount = playerCount,
                        name = name
                    )
                    repository.saveGame(game)
                    game.id
                }
                // Call onCreated on the main thread (viewModelScope uses Main dispatcher)
                onCreated(gameId)
            } catch (e: Exception) {
                onError("Failed to create game: ${e.message}")
            }
        }
    }

    fun deleteGame(game: TarotGame, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.deleteGame(game)
                }
            } catch (e: Exception) {
                onError("Failed to delete game: ${e.message}")
            }
        }
    }

    fun savePlayer(player: Player, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    if (playerRepository.getPlayerById(player.id) == null) {
                        // Use createPlayerOrReactivate to check for deactivated players
                        playerRepository.createPlayerOrReactivate(player.name, player.avatarColor)
                    }
                }
            } catch (e: Exception) {
                onError("Failed to save player: ${e.message}")
            }
        }
    }
}
