package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(tableName = "yahtzee_games")
data class YahtzeeGameEntity @OptIn(ExperimentalUuidApi::class) constructor(
    @PrimaryKey
    val id: String = Uuid.random().toString(),
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
data class YahtzeeScoreEntity @OptIn(ExperimentalUuidApi::class) constructor(
    @PrimaryKey
    val id: String = Uuid.random().toString(),
    val gameId: String,
    val playerIndex: Int,
    val category: String, // YahtzeeCategory.name
    val score: Int
)
