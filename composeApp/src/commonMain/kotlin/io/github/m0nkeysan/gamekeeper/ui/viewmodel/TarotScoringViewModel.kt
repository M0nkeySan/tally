package io.github.m0nkeysan.gamekeeper.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.domain.engine.TarotScoringEngine
import io.github.m0nkeysan.gamekeeper.core.model.*
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import io.github.m0nkeysan.gamekeeper.ui.screens.tarot.TarotRoundInputState
import io.github.m0nkeysan.gamekeeper.ui.screens.tarot.TarotScoringState
import io.github.m0nkeysan.gamekeeper.core.data.local.database.TarotRoundEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TarotScoringViewModel : ViewModel() {
    private val repository = PlatformRepositories.getTarotRepository()
    private val scoringEngine = TarotScoringEngine()
    
    private val _state = MutableStateFlow(TarotScoringState())
    val state: StateFlow<TarotScoringState> = _state.asStateFlow()

    fun loadGame(gameId: String) {
        viewModelScope.launch {
            val gameEntity = repository.getGameById(gameId) ?: return@launch
            
            val players = gameEntity.playerNames.split(",").mapIndexed { index, name ->
                Player(id = index.toString(), name = name)
            }
            
            _state.update { it.copy(
                game = TarotGame.create(players, gameEntity.playerCount).copy(id = gameId),
                players = players
            ) }
            
            repository.getRoundsForGame(gameId).collect { roundEntities ->
                val rounds = roundEntities.map { entity ->
                    TarotRound(
                        id = entity.id,
                        roundNumber = entity.roundNumber,
                        takerPlayerId = entity.takerPlayerIndex.toString(),
                        bid = TarotBid.valueOf(entity.bid),
                        bouts = entity.bouts,
                        pointsScored = entity.pointsScored,
                        hasPetitAuBout = entity.hasPetitAuBout,
                        hasPoignee = entity.hasPoignee,
                        poigneeLevel = entity.poigneeLevel?.let { PoigneeLevel.valueOf(it) },
                        chelem = ChelemType.valueOf(entity.chelem),
                        calledPlayerId = entity.calledPlayerIndex?.toString(),
                        score = entity.score
                    )
                }
                _state.update { it.copy(rounds = rounds) }
            }
        }
    }

    fun addRoundManual(
        roundId: Long? = null,
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

            val entity = TarotRoundEntity(
                id = roundId ?: 0L,
                gameId = gameId,
                roundNumber = roundNumber,
                takerPlayerIndex = takerIndex,
                bid = bid.name,
                bouts = bouts,
                pointsScored = pointsScored,
                hasPetitAuBout = hasPetitAuBout,
                hasPoignee = hasPoignee,
                poigneeLevel = poigneeLevel?.name,
                chelem = chelem.name,
                calledPlayerIndex = calledPlayerIndex,
                score = result.totalScore
            )

            repository.addRound(entity)
        }
    }

    fun getCurrentTotalScores(): Map<String, Int> {
        val currentState = _state.value
        val players = currentState.players
        val playerCount = currentState.game?.playerCount ?: players.size
        val scores = players.associate { it.id to 0 }.toMutableMap()

        currentState.rounds.forEach { round ->
            val s = round.score
            val takerId = round.takerPlayerId
            val calledId = round.calledPlayerId

            when (playerCount) {
                5 -> {
                    val partnerId = calledId ?: takerId
                    if (partnerId == takerId) {
                        scores[takerId] = (scores[takerId] ?: 0) + (s * 4)
                        players.forEach {
                            if (it.id != takerId) scores[it.id] = (scores[it.id] ?: 0) - s
                        }
                    } else {
                        scores[takerId] = (scores[takerId] ?: 0) + (s * 2)
                        scores[partnerId] = (scores[partnerId] ?: 0) + s
                        players.forEach { p ->
                            if (p.id != takerId && p.id != partnerId) {
                                scores[p.id] = (scores[p.id] ?: 0) - s
                            }
                        }
                    }
                }
                else -> {
                    val multiplier = playerCount - 1
                    scores[takerId] = (scores[takerId] ?: 0) + (s * multiplier)
                    players.forEach { p ->
                        if (p.id != takerId) {
                            scores[p.id] = (scores[p.id] ?: 0) - s
                        }
                    }
                }
            }
        }
        return scores
    }
}
