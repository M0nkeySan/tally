package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

import kotlin.uuid.Uuid

@Entity(tableName = "yahtzee_games")
data class YahtzeeGameEntity(
    @PrimaryKey
    val id: String = Uuid.random().toString(),
    val name: String,
    val playerCount: Int,
    val playerIds: String, // Comma-separated player IDs
    val firstPlayerId: String,
    val currentPlayerId: String,
    val isFinished: Boolean = false,
    val winnerName: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "yahtzee_scores",
    indices = [
        Index(value = ["gameId"]),
        Index(value = ["playerId"]),
        Index(value = ["gameId", "playerId"])
    ]
)
data class YahtzeeScoreEntity(
    @PrimaryKey
    val id: String = Uuid.random().toString(),
    val gameId: String,
    val playerId: String,
    val category: String, // YahtzeeCategory.name
    val score: Int
)
