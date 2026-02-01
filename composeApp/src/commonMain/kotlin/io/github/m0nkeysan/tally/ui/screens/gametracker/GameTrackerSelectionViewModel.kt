package io.github.m0nkeysan.tally.ui.screens.gametracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.tally.core.domain.model.DurationMode
import io.github.m0nkeysan.tally.core.domain.model.ScoringLogic
import io.github.m0nkeysan.tally.core.model.GameTrackerGame
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.platform.PlatformRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GameTrackerDisplayModel(
    val id: String,
    val name: String,
    val playerCount: Int,
    val playerNames: String,
    val isFinished: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val game: GameTrackerGame
)

data class GameTrackerSelectionState(
    val games: List<GameTrackerDisplayModel> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class GameTrackerSelectionViewModel : ViewModel() {
    private val repository = PlatformRepositories.getGameTrackerRepository()
    private val playerRepository = PlatformRepositories.getPlayerRepository()

    private val _selectionState = MutableStateFlow(GameTrackerSelectionState())
    val selectionState: StateFlow<GameTrackerSelectionState> = _selectionState.asStateFlow()

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

                            GameTrackerDisplayModel(
                                id = game.id,
                                name = game.name,
                                playerCount = game.playerCount,
                                playerNames = names,
                                isFinished = game.isFinished,
                                createdAt = game.createdAt,
                                updatedAt = game.updatedAt,
                                game = game
                            )
                        }
                        _selectionState.value =
                            GameTrackerSelectionState(games = displayModels, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _selectionState.value = GameTrackerSelectionState(
                    games = emptyList(),
                    isLoading = false,
                    error = "Failed to load games: ${e.message}"
                )
            }
        }
    }

    fun deleteGame(game: GameTrackerGame, onError: (String) -> Unit = {}) {
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

    fun createGame(
        name: String,
        players: List<Player>,
        scoringLogic: ScoringLogic,
        targetScore: Int?,
        durationMode: DurationMode,
        fixedRoundCount: Int?,
        onCreated: (String) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val gameId = withContext(Dispatchers.Default) {
                    // Save players if they don't exist
                    players.forEach { player ->
                        if (playerRepository.getPlayerById(player.id) == null) {
                            playerRepository.insertPlayer(player)
                        }
                    }

                    val game = GameTrackerGame.create(
                        name = name,
                        players = players,
                        scoringLogic = scoringLogic,
                        targetScore = targetScore,
                        durationMode = durationMode,
                        fixedRoundCount = fixedRoundCount
                    )
                    repository.saveGame(game)
                    game.id
                }
                // Call onCreated on the main thread
                onCreated(gameId)
            } catch (e: Exception) {
                onError("Failed to create game: ${e.message}")
            }
        }
    }

    fun savePlayer(player: Player, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Default) {
                    if (playerRepository.getPlayerById(player.id) == null) {
                        playerRepository.createPlayerOrReactivate(player.name, player.avatarColor)
                    }
                }
            } catch (e: Exception) {
                onError("Failed to save player: ${e.message}")
            }
        }
    }
}
