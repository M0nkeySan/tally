package io.github.m0nkeysan.gamekeeper.core.model

import kotlinx.serialization.Serializable

@Serializable
data class TarotRound(
    val id: Long = 0,
    val roundNumber: Int,
    val takerPlayerId: String,
    val bid: TarotBid,
    val bouts: Int,
    val pointsScored: Int, // Points made by the taker
    val hasPetitAuBout: Boolean = false,
    val hasPoignee: Boolean = false,
    val poigneeLevel: PoigneeLevel? = null,
    val chelem: ChelemType = ChelemType.NONE,
    val calledPlayerId: String? = null,
    val score: Int = 0
)

@Serializable
enum class TarotBid(val displayName: String, val multiplier: Int) {
    PRISE("Prise", 1),
    GARDE("Garde", 2),
    GARDE_SANS("Garde Sans", 4),
    GARDE_CONTRE("Garde Contre", 6)
}

@Serializable
enum class PoigneeLevel(val displayName: String, val bonus: Int) {
    SIMPLE("Simple", 20),
    DOUBLE("Double", 30),
    TRIPLE("Triple", 40)
}

@Serializable
enum class ChelemType(val displayName: String, val bonus: Int) {
    NONE("None", 0),
    ANNOUNCED_SUCCESS("Announced & Made", 400),
    ANNOUNCED_FAIL("Announced & Failed", -200),
    NON_ANNOUNCED_SUCCESS("Not Announced but Made", 200)
}
