package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(tableName = "tarot_games")
data class TarotGameEntity(
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
data class TarotRoundEntity(
    @PrimaryKey
    val id: String = Uuid.random().toString(),
    val gameId: String,
    val roundNumber: Int,
    val takerPlayerId: String, // Player ID reference
    val bid: String,
    val bouts: Int,
    val pointsScored: Int,
    val hasPetitAuBout: Boolean,
    val hasPoignee: Boolean,
    val poigneeLevel: String?,
    val chelem: String,
    val calledPlayerId: String?, // Player ID reference
    val score: Int
)
