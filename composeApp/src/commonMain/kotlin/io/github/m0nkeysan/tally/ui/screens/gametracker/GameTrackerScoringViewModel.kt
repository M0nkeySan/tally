package io.github.m0nkeysan.tally.ui.screens.gametracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.tally.core.model.GameTrackerGame
import io.github.m0nkeysan.tally.core.model.GameTrackerRound
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.platform.PlatformRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GameTrackerScoringState(
    val game: GameTrackerGame? = null,
    val players: List<Player> = emptyList(),
    val rounds: List<GameTrackerRound> = emptyList(),
    val totalScores: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class GameTrackerScoringViewModel : ViewModel() {
    private val repository = PlatformRepositories.getGameTrackerRepository()
    private val playerRepository = PlatformRepositories.getPlayerRepository()

    private val _state = MutableStateFlow(GameTrackerScoringState())
    val state: StateFlow<GameTrackerScoringState> = _state.asStateFlow()

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

                    _state.update {
                        it.copy(
                            game = game.copy(players = players),
                            players = players,
                            error = null,
                            isLoading = false
                        )
                    }

                    repository.getRoundsForGame(gameId).collect { rounds ->
                        val totalScores = calculateTotalScores(rounds, playerIds)
                        _state.update { 
                            it.copy(
                                rounds = rounds,
                                totalScores = totalScores
                            ) 
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load game: ${e.message}", isLoading = false) }
            }
        }
    }

    private fun calculateTotalScores(rounds: List<GameTrackerRound>, playerIds: List<String>): Map<String, Int> {
        val scores = mutableMapOf<String, Int>()
        playerIds.forEach { playerId ->
            scores[playerId] = rounds.filter { it.playerId == playerId }.sumOf { it.score }
        }
        return scores
    }

    fun deleteRound(round: GameTrackerRound, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Default) {
                    repository.deleteRound(round.id)
                }
            } catch (e: Exception) {
                onError("Failed to delete round: ${e.message}")
            }
        }
    }

    fun finishGame(onFinished: () -> Unit, onError: (String) -> Unit = {}) {
        val game = _state.value.game ?: return
        val totalScores = _state.value.totalScores
        
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Default) {
                    // Determine winner based on scoring logic
                    val winner = when (game.scoringLogic) {
                        io.github.m0nkeysan.tally.core.domain.model.ScoringLogic.HIGH_SCORE_WINS -> {
                            totalScores.maxByOrNull { it.value }?.key
                        }
                        io.github.m0nkeysan.tally.core.domain.model.ScoringLogic.LOW_SCORE_WINS -> {
                            totalScores.minByOrNull { it.value }?.key
                        }
                    }
                    
                    repository.finishGame(game.id, winner)
                }
                onFinished()
            } catch (e: Exception) {
                onError("Failed to finish game: ${e.message}")
            }
        }
    }

    fun onErrorConsumed() {
        _state.update { it.copy(error = null) }
    }
}
