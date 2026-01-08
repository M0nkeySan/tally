package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.core.model.PoigneeLevel
import io.github.m0nkeysan.gamekeeper.core.model.TarotBid
import io.github.m0nkeysan.gamekeeper.core.model.TarotGame
import io.github.m0nkeysan.gamekeeper.core.model.TarotRound

data class TarotScoringState(
    val game: TarotGame? = null,
    val players: List<Player> = emptyList(),
    val rounds: List<TarotRound> = emptyList(),
    val inputState: TarotRoundInputState = TarotRoundInputState(),
    val isCalculating: Boolean = false,
    val error: String? = null
)

data class TarotRoundInputState(
    val roundNumber: Int = 1,
    val takerPlayerId: String? = null,
    val bid: TarotBid = TarotBid.PRISE,
    val bouts: Int = 0,
    val hasPetitAuBout: Boolean = false,
    val hasPoignee: Boolean = false,
    val poigneeLevel: PoigneeLevel? = null,
    val calledPlayerId: String? = null
)
