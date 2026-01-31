package io.github.m0nkeysan.tally.core.domain.backup

import io.github.m0nkeysan.tally.database.PersistentCounterEntity
import io.github.m0nkeysan.tally.database.PlayerEntity
import io.github.m0nkeysan.tally.database.TarotGameEntity
import io.github.m0nkeysan.tally.database.TarotRoundEntity
import io.github.m0nkeysan.tally.database.UserPreferencesEntity
import io.github.m0nkeysan.tally.database.YahtzeeGameEntity
import io.github.m0nkeysan.tally.database.YahtzeeScoreEntity
import kotlinx.serialization.Serializable

/**
 * Interface for exporting and importing database data
 */
interface DatabaseExporter {
    /**
     * Export all database data to a backup object
     */
    suspend fun exportToBackup(): DatabaseBackup
    
    /**
     * Import database data from a backup object
     * This will clear all existing data and replace it with the imported data
     */
    suspend fun importFromBackup(backup: DatabaseBackup)
}

/**
 * Serializable database backup containing all app data
 */
@Serializable
data class DatabaseBackup(
    val version: Int = 1,
    val exportedAt: Long,
    val players: List<PlayerData>,
    val tarotGames: List<TarotGameData>,
    val tarotRounds: List<TarotRoundData>,
    val yahtzeeGames: List<YahtzeeGameData>,
    val yahtzeeScores: List<YahtzeeScoreData>,
    val counters: List<CounterData>,
    val preferences: List<PreferenceData>
)

/**
 * Serializable player data
 */
@Serializable
data class PlayerData(
    val id: String,
    val name: String,
    val avatarColor: String,
    val createdAt: Long,
    val isActive: Long,
    val deactivatedAt: Long?
)

/**
 * Serializable tarot game data
 */
@Serializable
data class TarotGameData(
    val id: String,
    val name: String,
    val playerCount: Long,
    val playerIds: String,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Serializable tarot round data
 */
@Serializable
data class TarotRoundData(
    val id: String,
    val gameId: String,
    val roundNumber: Long,
    val takerPlayerId: String,
    val bid: String,
    val bouts: Long,
    val pointsScored: Long,
    val hasPetitAuBout: Long,
    val hasPoignee: Long,
    val poigneeLevel: String?,
    val chelem: String,
    val calledPlayerId: String?,
    val score: Long
)

/**
 * Serializable yahtzee game data
 */
@Serializable
data class YahtzeeGameData(
    val id: String,
    val name: String,
    val playerCount: Long,
    val playerIds: String,
    val firstPlayerId: String,
    val currentPlayerId: String,
    val isFinished: Long,
    val winnerName: String?,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Serializable yahtzee score data
 */
@Serializable
data class YahtzeeScoreData(
    val id: String,
    val gameId: String,
    val playerId: String,
    val category: String,
    val score: Long
)

/**
 * Serializable counter data
 */
@Serializable
data class CounterData(
    val id: String,
    val name: String,
    val count: Long,
    val color: Long,
    val updatedAt: Long,
    val sortOrder: Long
)

/**
 * Serializable preference data
 */
@Serializable
data class PreferenceData(
    val key: String,
    val prefValue: String
)

// Extension functions to convert between database entities and serializable data models

fun PlayerEntity.toData() = PlayerData(
    id = id,
    name = name,
    avatarColor = avatarColor,
    createdAt = createdAt,
    isActive = isActive,
    deactivatedAt = deactivatedAt
)

fun PlayerData.toEntity() = PlayerEntity(
    id = id,
    name = name,
    avatarColor = avatarColor,
    createdAt = createdAt,
    isActive = isActive,
    deactivatedAt = deactivatedAt
)

fun TarotGameEntity.toData() = TarotGameData(
    id = id,
    name = name,
    playerCount = playerCount,
    playerIds = playerIds,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun TarotGameData.toEntity() = TarotGameEntity(
    id = id,
    name = name,
    playerCount = playerCount,
    playerIds = playerIds,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun TarotRoundEntity.toData() = TarotRoundData(
    id = id,
    gameId = gameId,
    roundNumber = roundNumber,
    takerPlayerId = takerPlayerId,
    bid = bid,
    bouts = bouts,
    pointsScored = pointsScored,
    hasPetitAuBout = hasPetitAuBout,
    hasPoignee = hasPoignee,
    poigneeLevel = poigneeLevel,
    chelem = chelem,
    calledPlayerId = calledPlayerId,
    score = score
)

fun TarotRoundData.toEntity() = TarotRoundEntity(
    id = id,
    gameId = gameId,
    roundNumber = roundNumber,
    takerPlayerId = takerPlayerId,
    bid = bid,
    bouts = bouts,
    pointsScored = pointsScored,
    hasPetitAuBout = hasPetitAuBout,
    hasPoignee = hasPoignee,
    poigneeLevel = poigneeLevel,
    chelem = chelem,
    calledPlayerId = calledPlayerId,
    score = score
)

fun YahtzeeGameEntity.toData() = YahtzeeGameData(
    id = id,
    name = name,
    playerCount = playerCount,
    playerIds = playerIds,
    firstPlayerId = firstPlayerId,
    currentPlayerId = currentPlayerId,
    isFinished = isFinished,
    winnerName = winnerName,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun YahtzeeGameData.toEntity() = YahtzeeGameEntity(
    id = id,
    name = name,
    playerCount = playerCount,
    playerIds = playerIds,
    firstPlayerId = firstPlayerId,
    currentPlayerId = currentPlayerId,
    isFinished = isFinished,
    winnerName = winnerName,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun YahtzeeScoreEntity.toData() = YahtzeeScoreData(
    id = id,
    gameId = gameId,
    playerId = playerId,
    category = category,
    score = score
)

fun YahtzeeScoreData.toEntity() = YahtzeeScoreEntity(
    id = id,
    gameId = gameId,
    playerId = playerId,
    category = category,
    score = score
)

fun PersistentCounterEntity.toData() = CounterData(
    id = id,
    name = name,
    count = count,
    color = color,
    updatedAt = updatedAt,
    sortOrder = sortOrder
)

fun CounterData.toEntity() = PersistentCounterEntity(
    id = id,
    name = name,
    count = count,
    color = color,
    updatedAt = updatedAt,
    sortOrder = sortOrder
)

fun UserPreferencesEntity.toData() = PreferenceData(
    key = key,
    prefValue = prefValue
)

fun PreferenceData.toEntity() = UserPreferencesEntity(
    key = key,
    prefValue = prefValue
)
