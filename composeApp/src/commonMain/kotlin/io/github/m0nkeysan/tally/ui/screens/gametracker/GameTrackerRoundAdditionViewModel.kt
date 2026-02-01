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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GameTrackerRoundAdditionState(
    val game: GameTrackerGame? = null,
    val players: List<Player> = emptyList(),
    val roundNumber: Int = 1,
    val playerScores: Map<String, Int> = emptyMap(),
    val existingRoundId: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class GameTrackerRoundAdditionViewModel : ViewModel() {
    private val repository = PlatformRepositories.getGameTrackerRepository()
    private val playerRepository = PlatformRepositories.getPlayerRepository()

    private val _state = MutableStateFlow(GameTrackerRoundAdditionState())
    val state: StateFlow<GameTrackerRoundAdditionState> = _state.asStateFlow()

    fun loadGame(gameId: String, roundNumber: Int, roundId: String?) {
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

                    // If editing, load existing round scores
                    val playerScores = if (roundId != null) {
                        // Get all rounds for this round number
                        val allRounds = repository.getRoundsForGame(gameId).first()
                        val roundsForNumber = allRounds.filter { it.roundNumber == roundNumber }
                        val scores = mutableMapOf<String, Int>()
                        roundsForNumber.forEach { round ->
                            scores[round.playerId] = round.score
                        }
                        // Ensure all players have a score entry
                        playerIds.forEach { id ->
                            if (!scores.containsKey(id)) scores[id] = 0
                        }
                        scores
                    } else {
                        // New round - initialize with 0 scores
                        playerIds.associateWith { 0 }
                    }
                    
                    _state.update {
                        it.copy(
                            game = game.copy(players = players),
                            players = players,
                            roundNumber = roundNumber,
                            playerScores = playerScores,
                            existingRoundId = roundId,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load game: ${e.message}", isLoading = false) }
            }
        }
    }

    fun updatePlayerScore(playerId: String, score: Int) {
        _state.update {
            it.copy(playerScores = it.playerScores + (playerId to score))
        }
    }

    fun adjustPlayerScore(playerId: String, amount: Int) {
        _state.update { state ->
            val currentScore = state.playerScores[playerId] ?: 0
            state.copy(playerScores = state.playerScores + (playerId to (currentScore + amount)))
        }
    }

    fun saveRound(onSaved: () -> Unit, onError: (String) -> Unit = {}) {
        val currentState = _state.value
        val game = currentState.game ?: return
        
        val scores = currentState.playerScores

        viewModelScope.launch {
            try {
                withContext(Dispatchers.Default) {
                    // If editing, delete existing rounds for this round number first
                    if (currentState.existingRoundId != null) {
                        val allRounds = repository.getRoundsForGame(game.id).first()
                        allRounds.filter { it.roundNumber == currentState.roundNumber }
                            .forEach { repository.deleteRound(it.id) }
                    }

                    // Create new rounds for each player
                    val rounds = scores.map { (playerId, score) ->
                        GameTrackerRound.create(
                            gameId = game.id,
                            roundNumber = currentState.roundNumber,
                            playerId = playerId,
                            score = score
                        )
                    }

                    repository.saveRounds(rounds)
                    
                    // Update game's current round if needed
                    if (currentState.roundNumber >= game.currentRound) {
                        repository.updateGame(game.copy(currentRound = currentState.roundNumber))
                    }
                }
                onSaved()
            } catch (e: Exception) {
                onError("Failed to save round: ${e.message}")
            }
        }
    }

    fun onErrorConsumed() {
        _state.update { it.copy(error = null) }
    }
}
