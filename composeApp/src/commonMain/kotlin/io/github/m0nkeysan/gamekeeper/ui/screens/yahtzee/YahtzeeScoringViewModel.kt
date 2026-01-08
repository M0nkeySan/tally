package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeCategory
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeGame
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeScore
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class YahtzeeScoringState(
    val game: YahtzeeGame? = null,
    val scores: Map<Int, Map<YahtzeeCategory, Int>> = emptyMap(),
    val isLoading: Boolean = true
)

class YahtzeeScoringViewModel : ViewModel() {
    private val repository = PlatformRepositories.getYahtzeeRepository()
    private val playerRepository = PlatformRepositories.getPlayerRepository()

    private val _state = MutableStateFlow(YahtzeeScoringState())
    val state: StateFlow<YahtzeeScoringState> = _state.asStateFlow()

    // Store resolved player objects
    private var resolvedPlayers: List<Player> = emptyList()

    fun loadGame(gameId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val game = repository.getGameById(gameId) ?: return@withContext

                val playerIds = game.playerIds.split(",")
                resolvedPlayers = playerIds.map { id ->
                    playerRepository.getPlayerById(id) ?: Player(id = id, name = "Unknown")
                }

                _state.update { it.copy(game = game) }

                repository.getScoresForGame(gameId).collect { playerScores ->
                    // Map PlayerYahtzeeScore list to our state structure
                    val scoresMap = mutableMapOf<Int, MutableMap<YahtzeeCategory, Int>>()
                    playerScores.forEach { playerScore ->
                        val playerMap = scoresMap.getOrPut(playerScore.playerIndex) { mutableMapOf() }
                        playerMap[playerScore.score.category] = playerScore.score.value
                    }
                    _state.update { it.copy(scores = scoresMap, isLoading = false) }
                }
            }
        }
    }

    fun getPlayerName(index: Int): String {
        return resolvedPlayers.getOrNull(index)?.name ?: "Unknown"
    }

    fun getPlayers(): List<Player> = resolvedPlayers

    fun submitScore(playerIndex: Int, category: YahtzeeCategory, score: Int, moveTurn: Boolean) {
        val currentGame = _state.value.game ?: return

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.saveScore(
                    YahtzeeScore(
                        category = category,
                        value = score,
                        isScored = true
                    ),
                    currentGame.id,
                    playerIndex
                )

                // Update local state
                val currentScores = _state.value.scores.toMutableMap()
                val playerScores = currentScores.getOrPut(playerIndex) { mutableMapOf() }.toMutableMap()
                playerScores[category] = score
                currentScores[playerIndex] = playerScores
                
                if (moveTurn) {
                    val nextPlayerIndex = (playerIndex + 1) % currentGame.playerCount
                    val updatedGame = currentGame.copy(
                        currentPlayerIndex = nextPlayerIndex,
                        updatedAt = System.currentTimeMillis()
                    )
                    repository.saveGame(updatedGame)
                    _state.update { it.copy(game = updatedGame, scores = currentScores) }
                } else {
                    _state.update { it.copy(scores = currentScores) }
                }
            }
        }
    }

    fun markAsFinished() {
        val currentGame = _state.value.game ?: return
        if (currentGame.isFinished) return

        val winners = getWinners()
        val winnerNames = winners.joinToString(" & ") { it.first }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val updatedGame = currentGame.copy(
                    isFinished = true,
                    winnerName = winnerNames,
                    updatedAt = System.currentTimeMillis()
                )
                repository.saveGame(updatedGame)
                _state.update { it.copy(game = updatedGame) }
            }
        }
    }

    fun isGameFinished(): Boolean {
        val game = _state.value.game ?: return false
        if (game.isFinished) return true

        val scores = _state.value.scores
        val totalCategories = YahtzeeCategory.entries.size

        if (scores.size < game.playerCount && game.playerCount > 0) return false

        return (0 until game.playerCount).all { playerIndex ->
            val playerScores = scores[playerIndex] ?: return@all false
            playerScores.size == totalCategories
        }
    }

    fun getWinners(): List<Pair<String, Int>> {
        val game = _state.value.game ?: return emptyList()

        val playerTotalScores = (0 until game.playerCount).map { index ->
            getPlayerName(index) to calculateTotalScore(index)
        }

        val maxScore = playerTotalScores.maxOfOrNull { it.second } ?: 0
        return playerTotalScores.filter { it.second == maxScore }
    }

    fun getAllPlayerScores(): List<Pair<String, Int>> {
        val game = _state.value.game ?: return emptyList()

        return (0 until game.playerCount).map { index ->
            getPlayerName(index) to calculateTotalScore(index)
        }
    }

    fun calculateTotalScore(playerIndex: Int): Int {
        val playerScores = _state.value.scores[playerIndex] ?: return 0
        var total = playerScores.values.sum()

        val upperScore = playerScores.filter { it.key.isUpperSection() }.values.sum()
        if (upperScore >= 63) {
            total += 35
        }

        return total
    }

    fun calculateUpperScore(playerIndex: Int): Int {
        val playerScores = _state.value.scores[playerIndex] ?: return 0
        return playerScores.filter { it.key.isUpperSection() }.values.sum()
    }
}
