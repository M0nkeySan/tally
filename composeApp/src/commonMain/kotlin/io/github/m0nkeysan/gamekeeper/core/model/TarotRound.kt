package io.github.m0nkeysan.gamekeeper.core.model

import kotlinx.serialization.Serializable

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class TarotRound @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val roundNumber: Int,
    val takerPlayerId: String,
    val bid: TarotBid,
    val bouts: Int,
    val pointsScored: Int,
    val hasPetitAuBout: Boolean,
    val hasPoignee: Boolean,
    val poigneeLevel: PoigneeLevel?,
    val chelem: ChelemType,
    val calledPlayerId: String?,
    val score: Int
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
