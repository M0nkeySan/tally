package io.github.m0nkeysan.tally.core.domain.data

interface YahtzeeGameData {
    val id: String
    val name: String
    val playerCount: Int
    val playerIds: String
    val firstPlayerId: String
    val currentPlayerId: String
    val isFinished: Boolean
    val winnerName: String?
    val createdAt: Long
    val updatedAt: Long
}