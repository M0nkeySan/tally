package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.domain.engine.TarotScoringEngine
import io.github.m0nkeysan.gamekeeper.core.model.*
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TarotScoringViewModel : ViewModel() {
    private val repository = PlatformRepositories.getTarotRepository()
    private val playerRepository = PlatformRepositories.getPlayerRepository()
    private val scoringEngine = TarotScoringEngine()

    private val _state = MutableStateFlow(TarotScoringState())
    val state: StateFlow<TarotScoringState> = _state.asStateFlow()

    fun loadGame(gameId: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val game = repository.getGameById(gameId)
                    if (game == null) {
                        _state.update { it.copy(error = "Game not found") }
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
                            error = null
                        )
                    }

                    repository.getRoundsForGame(gameId).collect { rounds ->
                        _state.update { it.copy(rounds = rounds) }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load game: ${e.message}") }
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun addRoundManual(
        roundId: String? = null,
        takerIndex: Int,
        bid: TarotBid,
        bouts: Int,
        pointsScored: Int,
        hasPetitAuBout: Boolean,
        hasPoignee: Boolean,
        poigneeLevel: PoigneeLevel?,
        chelem: ChelemType,
        calledPlayerIndex: Int?
    ) {
        val gameId = _state.value.game?.id ?: return

        viewModelScope.launch {
            try {
                val currentState = _state.value

                val existingRound = roundId?.let { id -> currentState.rounds.find { it.id == id } }
                val roundNumber = existingRound?.roundNumber ?: (currentState.rounds.size + 1)

                val result = scoringEngine.calculateScore(
                    bid = bid,
                    bouts = bouts,
                    pointsScored = pointsScored,
                    hasPetitAuBout = hasPetitAuBout,
                    hasPoignee = hasPoignee,
                    poigneeLevel = poigneeLevel,
                    chelem = chelem
                )

                val round = TarotRound(
                    id = roundId ?: Uuid.random().toString(),
                    roundNumber = roundNumber,
                    takerPlayerId = takerIndex.toString(),
                    bid = bid,
                    bouts = bouts,
                    pointsScored = pointsScored,
                    hasPetitAuBout = hasPetitAuBout,
                    hasPoignee = hasPoignee,
                    poigneeLevel = poigneeLevel,
                    chelem = chelem,
                    calledPlayerId = calledPlayerIndex?.toString(),
                    score = result.totalScore
                )

                withContext(Dispatchers.IO) {
                    repository.addRound(round, gameId)
                }
                _state.update { it.copy(error = null) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to add round: ${e.message}") }
            }
        }
    }

    fun getCurrentTotalScores(): Map<String, Int> {
        val currentState = _state.value
        val players = currentState.players
        val playerCount = currentState.game?.playerCount ?: players.size
        return scoringEngine.calculateTotalScores(players, currentState.rounds, playerCount)
    }
}
