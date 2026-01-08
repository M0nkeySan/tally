package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(tableName = "tarot_games")
data class TarotGameEntity @OptIn(ExperimentalUuidApi::class) constructor(
    @PrimaryKey
    val id: String = Uuid.random().toString(),
    val name: String,
    val playerCount: Int,
    val playerIds: String, // Comma-separated player IDs
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "tarot_rounds",
    indices = [Index(value = ["gameId"])]
)
data class TarotRoundEntity @OptIn(ExperimentalUuidApi::class) constructor(
    @PrimaryKey
    val id: String = Uuid.random().toString(),
    val gameId: String,
    val roundNumber: Int,
    val takerPlayerIndex: Int, // Index in the playerIds list
    val bid: String,
    val bouts: Int,
    val pointsScored: Int,
    val hasPetitAuBout: Boolean,
    val hasPoignee: Boolean,
    val poigneeLevel: String?,
    val chelem: String,
    val calledPlayerIndex: Int?,
    val score: Int
)
