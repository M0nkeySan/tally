package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeCategory
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeGame
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeScore
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.core.utils.getCurrentTimeMillis
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class YahtzeeScoringState(
    val game: YahtzeeGame? = null,
    val scores: Map<String, Map<YahtzeeCategory, Int>> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null
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
            withContext(Dispatchers.Default) {
                val game = repository.getGameById(gameId) ?: return@withContext

                val playerIds = game.playerIds.split(",")
                resolvedPlayers = playerIds.map { id ->
                    playerRepository.getPlayerById(id) ?: Player(id = id, name = "Unknown")
                }

                _state.update { it.copy(game = game) }

                repository.getScoresForGame(gameId).collect { playerScores ->
                    // Map PlayerYahtzeeScore list to our state structure
                    val scoresMap = mutableMapOf<String, MutableMap<YahtzeeCategory, Int>>()
                    playerScores.forEach { playerScore ->
                        val playerMap = scoresMap.getOrPut(playerScore.playerId) { mutableMapOf() }
                        playerMap[playerScore.score.category] = playerScore.score.value
                    }
                    _state.update { it.copy(scores = scoresMap, isLoading = false) }
                }
            }
        }
    }

    fun getPlayers(): List<Player> = resolvedPlayers

    fun submitScore(playerId: String, category: YahtzeeCategory, score: Int, moveTurn: Boolean) {
        val currentGame = _state.value.game ?: return

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.saveScore(
                    YahtzeeScore(
                        category = category,
                        value = score,
                        isScored = true
                    ),
                    currentGame.id,
                    playerId
                )

                // Update local state
                val currentScores = _state.value.scores.toMutableMap()
                val playerScores = currentScores.getOrPut(playerId) { mutableMapOf() }.toMutableMap()
                playerScores[category] = score
                currentScores[playerId] = playerScores
                
                if (moveTurn) {
                    val nextPlayerId = currentGame.getNextPlayerId() ?: currentGame.firstPlayerId
                    val updatedGame = currentGame.copy(
                        currentPlayerId = nextPlayerId,
                        updatedAt = getCurrentTimeMillis()
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
            withContext(Dispatchers.Default) {
                val updatedGame = currentGame.copy(
                    isFinished = true,
                    winnerName = winnerNames,
                    updatedAt = getCurrentTimeMillis()
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
        val playerIds = game.playerIds.split(",")

        if (scores.size < playerIds.size) return false

        return playerIds.all { playerId ->
            val playerScores = scores[playerId] ?: return@all false
            playerScores.size == totalCategories
        }
    }

    fun getWinners(): List<Pair<String, Int>> {
        val game = _state.value.game ?: return emptyList()
        val playerIds = game.playerIds.split(",")

        val playerTotalScores = playerIds.map { playerId ->
            (resolvedPlayers.find { it.id == playerId }?.name ?: "Unknown") to calculateTotalScore(playerId)
        }

        val maxScore = playerTotalScores.maxOfOrNull { it.second } ?: 0
        return playerTotalScores.filter { it.second == maxScore }
    }

    fun getAllPlayerScores(): List<Pair<String, Int>> {
        val game = _state.value.game ?: return emptyList()
        val playerIds = game.playerIds.split(",")

        return playerIds.map { playerId ->
            (resolvedPlayers.find { it.id == playerId }?.name ?: "Unknown") to calculateTotalScore(playerId)
        }
    }

    fun calculateTotalScore(playerId: String): Int {
        val playerScores = _state.value.scores[playerId] ?: return 0
        var total = playerScores.values.sum()

        val upperScore = playerScores.filter { it.key.isUpperSection() }.values.sum()
        if (upperScore >= 63) {
            total += 35
        }

        return total
    }

    fun calculateUpperScore(playerId: String): Int {
        val playerScores = _state.value.scores[playerId] ?: return 0
        return playerScores.filter { it.key.isUpperSection() }.values.sum()
    }
}
