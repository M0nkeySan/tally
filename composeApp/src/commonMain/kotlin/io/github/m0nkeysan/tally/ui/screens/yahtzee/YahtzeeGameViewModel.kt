package io.github.m0nkeysan.tally.ui.screens.yahtzee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.core.model.YahtzeeGame
import io.github.m0nkeysan.tally.platform.PlatformRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

data class YahtzeeGameDisplayModel(
    val id: String,
    val name: String,
    val playerCount: Int,
    val playerNames: String,
    val isFinished: Boolean,
    val winnerName: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val game: YahtzeeGame
)

data class YahtzeeGameSelectionState(
    val games: List<YahtzeeGameDisplayModel> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class YahtzeeGameViewModel : ViewModel() {
    private val repository = PlatformRepositories.getYahtzeeRepository()
    private val playerRepository = PlatformRepositories.getPlayerRepository()

    private val _selectionState = MutableStateFlow(YahtzeeGameSelectionState())
    val selectionState: StateFlow<YahtzeeGameSelectionState> = _selectionState.asStateFlow()

    val allPlayers: Flow<List<Player>> = playerRepository.getAllPlayers()

    init {
        loadGames()
    }

    private fun loadGames() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Default) {
                    repository.getAllGames().collect { games ->
                        val displayModels = games.map { game ->
                            val names = game.playerIds.split(",").map { id ->
                                playerRepository.getPlayerById(id)?.name ?: "Unknown"
                            }.joinToString(", ")

                            YahtzeeGameDisplayModel(
                                id = game.id,
                                name = game.name,
                                playerCount = game.playerCount,
                                playerNames = names,
                                isFinished = game.isFinished,
                                winnerName = game.winnerName,
                                createdAt = game.createdAt,
                                updatedAt = game.updatedAt,
                                game = game
                            )
                        }
                        _selectionState.value =
                            YahtzeeGameSelectionState(games = displayModels, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _selectionState.value = YahtzeeGameSelectionState(
                    games = emptyList(),
                    isLoading = false,
                    error = "Failed to load games: ${e.message}"
                )
            }
        }
    }

    fun createGame(
        name: String,
        playerCount: Int,
        players: List<Player>,
        onCreated: (String) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        val randomIndex = Random.nextInt(playerCount)
        val randomPlayerId = players.getOrNull(randomIndex)?.id ?: players.firstOrNull()?.id ?: ""

        viewModelScope.launch {
            try {
                val gameId = withContext(Dispatchers.Default) {
                    players.forEach { player ->
                        if (playerRepository.getPlayerById(player.id) == null) {
                            playerRepository.insertPlayer(player)
                        }
                    }

                    val game = YahtzeeGame.create(
                        players = players,
                        name = name
                    ).copy(
                        firstPlayerId = randomPlayerId,
                        currentPlayerId = randomPlayerId
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

    fun deleteGame(game: YahtzeeGame, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Default) {
                    repository.deleteGame(game)
                }
            } catch (e: Exception) {
                onError("Failed to delete game: ${e.message}")
            }
        }
    }

    fun deleteAllGames(onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                selectionState.value.games.forEach { displayModel ->
                    deleteGame(displayModel.game)
                }
            } catch (e: Exception) {
                onError("Failed to delete all games: ${e.message}")
            }
        }
    }

    fun savePlayer(player: Player, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Default) {
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
