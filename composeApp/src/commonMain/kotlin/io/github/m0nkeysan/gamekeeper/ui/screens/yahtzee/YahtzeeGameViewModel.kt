package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeGameEntity
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

data class YahtzeeGameDisplayModel(
    val id: String,
    val name: String,
    val playerCount: Int,
    val playerNames: String,
    val isFinished: Boolean,
    val winnerName: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val rawEntity: YahtzeeGameEntity
)

data class YahtzeeGameSelectionState(
    val games: List<YahtzeeGameDisplayModel> = emptyList(),
    val isLoading: Boolean = true
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
            withContext(Dispatchers.IO) {
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
                            rawEntity = game
                        )
                    }
                    _selectionState.value =
                        YahtzeeGameSelectionState(games = displayModels, isLoading = false)
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
        val id = Random.nextLong().toString()
        val now = System.currentTimeMillis()
        val firstPlayerIndex = Random.nextInt(playerCount)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                players.forEach { player ->
                    if (playerRepository.getPlayerById(player.id) == null) {
                        playerRepository.insertPlayer(player)
                    }
                }

                val game = YahtzeeGameEntity(
                    id = id,
                    name = name,
                    playerCount = playerCount,
                    playerIds = players.joinToString(",") { it.id },
                    firstPlayerIndex = firstPlayerIndex,
                    currentPlayerIndex = firstPlayerIndex,
                    createdAt = now,
                    updatedAt = now
                )
                repository.saveGame(game)
            }
            onCreated(id)
        }
    }

    fun deleteGame(game: YahtzeeGameEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteGame(game)
            }
        }
    }
}
