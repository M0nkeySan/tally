package io.github.m0nkeysan.tally.ui.screens.gametracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.tally.core.model.GameTrackerGame
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.platform.PlatformRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GameTrackerSummaryState(
    val game: GameTrackerGame? = null,
    val players: List<Player> = emptyList(),
    val totalScores: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class GameTrackerSummaryViewModel : ViewModel() {
    private val repository = PlatformRepositories.getGameTrackerRepository()
    private val playerRepository = PlatformRepositories.getPlayerRepository()

    private val _state = MutableStateFlow(GameTrackerSummaryState())
    val state: StateFlow<GameTrackerSummaryState> = _state.asStateFlow()

    fun loadGame(gameId: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Default) {
                    val game = repository.getGameById(gameId)
                    if (game == null) {
                        _state.update { it.copy(error = "Game not found", isLoading = false) }
                        return@withContext
                    }

                    val playerIds = game.playerIds.split(",")
                    val players = playerIds.map { id ->
                        playerRepository.getPlayerById(id) ?: Player(id = id, name = "Unknown")
                    }

                    val rounds = repository.getRoundsForGame(gameId).first()
                    val totalScores = playerIds.associateWith { playerId ->
                        rounds.filter { it.playerId == playerId }.sumOf { it.score }
                    }

                    _state.update {
                        it.copy(
                            game = game.copy(players = players),
                            players = players,
                            totalScores = totalScores,
                            error = null,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load summary: ${e.message}", isLoading = false) }
            }
        }
    }

    fun rematch(onCreated: (String) -> Unit) {
        val currentGame = _state.value.game ?: return
        val players = _state.value.players

        viewModelScope.launch {
            try {
                println("GK-DEBUG: Rematch starting...")
                val newGameId = withContext(Dispatchers.Default) {
                    val newGame = GameTrackerGame.create(
                        name = currentGame.name,
                        players = players,
                        scoringLogic = currentGame.scoringLogic,
                        targetScore = currentGame.targetScore,
                        durationMode = currentGame.durationMode,
                        fixedRoundCount = currentGame.fixedRoundCount
                    )
                    repository.saveGame(newGame)
                    println("GK-DEBUG: Rematch saved game: ${newGame.id}")
                    newGame.id
                }
                println("GK-DEBUG: Rematch calling onCreated($newGameId)")
                onCreated(newGameId)
            } catch (e: Exception) {
                println("GK-DEBUG: Rematch error: ${e.message}")
                _state.update { it.copy(error = "Failed to create rematch: ${e.message}") }
            }
        }
    }
}
