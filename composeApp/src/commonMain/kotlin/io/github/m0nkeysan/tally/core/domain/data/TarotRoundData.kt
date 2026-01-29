package io.github.m0nkeysan.tally.core.domain.data

/**
 * Read-only interface representing Tarot round data for statistics calculation.
 */
interface TarotRoundData {
    val id: String
    val gameId: String
    val roundNumber: Int
    val takerPlayerId: String
    val bid: String  // TarotBid.name
    val bouts: Int
    val pointsScored: Int
    val hasPetitAuBout: Boolean
    val hasPoignee: Boolean
    val poigneeLevel: String?  // PoigneeLevel.name
    val chelem: String  // ChelemType.name
    val calledPlayerId: String?
    val score: Int
}
