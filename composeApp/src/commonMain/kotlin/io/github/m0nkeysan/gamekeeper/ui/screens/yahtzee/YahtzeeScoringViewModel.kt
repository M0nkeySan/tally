package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeGameEntity
import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeScoreEntity
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeCategory
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class YahtzeeScoringState(
    val game: YahtzeeGameEntity? = null,
    val scores: Map<Int, Map<YahtzeeCategory, Int>> = emptyMap(),
    val isLoading: Boolean = true
)

class YahtzeeScoringViewModel : ViewModel() {
    private val repository = PlatformRepositories.getYahtzeeRepository()
    
    private val _state = MutableStateFlow(YahtzeeScoringState())
    val state: StateFlow<YahtzeeScoringState> = _state.asStateFlow()

    fun loadGame(gameId: String) {
        viewModelScope.launch {
            val game = repository.getGameById(gameId) ?: return@launch
            _state.update { it.copy(game = game) }
            
            repository.getScoresForGame(gameId).collect { scoreEntities ->
                val scoresMap = mutableMapOf<Int, MutableMap<YahtzeeCategory, Int>>()
                scoreEntities.forEach { entity ->
                    val playerMap = scoresMap.getOrPut(entity.playerIndex) { mutableMapOf() }
                    playerMap[YahtzeeCategory.valueOf(entity.category)] = entity.score
                }
                _state.update { it.copy(scores = scoresMap, isLoading = false) }
            }
        }
    }

    fun submitScore(playerIndex: Int, category: YahtzeeCategory, score: Int, moveTurn: Boolean) {
        val currentGame = _state.value.game ?: return
        
        viewModelScope.launch {
            repository.saveScore(
                YahtzeeScoreEntity(
                    gameId = currentGame.id,
                    playerIndex = playerIndex,
                    category = category.name,
                    score = score
                )
            )
            
            if (moveTurn) {
                val nextPlayerIndex = (playerIndex + 1) % currentGame.playerCount
                val updatedGame = currentGame.copy(
                    currentPlayerIndex = nextPlayerIndex,
                    updatedAt = System.currentTimeMillis()
                )
                repository.saveGame(updatedGame)
                _state.update { it.copy(game = updatedGame) }
            }
        }
    }

    fun markAsFinished() {
        val currentGame = _state.value.game ?: return
        if (currentGame.isFinished) return

        val winners = getWinners()
        val winnerNames = winners.joinToString(" & ") { it.first }

        viewModelScope.launch {
            val updatedGame = currentGame.copy(
                isFinished = true,
                winnerName = winnerNames,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveGame(updatedGame)
            _state.update { it.copy(game = updatedGame) }
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
        val players = game.playerNames.split(",")
        
        val playerTotalScores = (0 until game.playerCount).map { index ->
            players[index] to calculateTotalScore(index)
        }
        
        val maxScore = playerTotalScores.maxOfOrNull { it.second } ?: 0
        return playerTotalScores.filter { it.second == maxScore }
    }

    fun getAllPlayerScores(): List<Pair<String, Int>> {
        val game = _state.value.game ?: return emptyList()
        val players = game.playerNames.split(",")
        
        return (0 until game.playerCount).map { index ->
            players[index] to calculateTotalScore(index)
        }
    }

    fun calculateTotalScore(playerIndex: Int): Int {
        val playerScores = _state.value.scores[playerIndex] ?: return 0
        var total = playerScores.values.sum()
        
        // Add upper bonus if applicable
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
