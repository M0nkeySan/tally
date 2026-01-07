package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "yahtzee_games")
data class YahtzeeGameEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val playerCount: Int,
    val playerIds: String, // Comma-separated player IDs
    val firstPlayerIndex: Int,
    val currentPlayerIndex: Int,
    val isFinished: Boolean = false,
    val winnerName: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "yahtzee_scores")
data class YahtzeeScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameId: String,
    val playerIndex: Int,
    val category: String, // YahtzeeCategory.name
    val score: Int
)
