package io.github.m0nkeysan.tally.core.domain.data

/**
 * Read-only interface representing Tarot game data for statistics calculation.
 */
interface TarotGameData {
    val id: String
    val name: String
    val playerCount: Int
    val playerIds: String
    val createdAt: Long
    val updatedAt: Long
}
