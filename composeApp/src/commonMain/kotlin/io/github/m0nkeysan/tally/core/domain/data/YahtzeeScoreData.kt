package io.github.m0nkeysan.tally.core.domain.data

interface YahtzeeScoreData {
    val id: String
    val gameId: String
    val playerId: String
    val category: String
    val score: Int
}