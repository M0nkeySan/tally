package io.github.m0nkeysan.gamekeeper.core.model

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
sealed class Game {
    abstract val id: String
    abstract val players: List<Player>
    abstract val createdAt: Long
    abstract val updatedAt: Long
}

@Serializable
data class TarotGame(
    override val id: String,
    override val players: List<Player>,
    override val createdAt: Long = 0L,
    override val updatedAt: Long = 0L,
    val rounds: List<TarotRound> = emptyList(),
    val playerCount: Int,
    val name: String = "Tarot Game",
    val playerIds: String = ""
) : Game() {

    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun create(players: List<Player>, playerCount: Int? = null, name: String = "Tarot Game"): TarotGame {
            val now = getCurrentTimeMillis()

            val finalPlayerCount = playerCount ?: players.size

            require(finalPlayerCount in 3..5) {
                "Le Tarot se joue à 3, 4 ou 5 joueurs uniquement."
            }

            return TarotGame(
                id = Uuid.random().toString(),
                players = players,
                createdAt = now,
                updatedAt = now,
                playerCount = finalPlayerCount,
                rounds = emptyList(),
                name = name,
                playerIds = players.joinToString(",") { it.id }
            )
        }
    }
}

@Serializable
data class YahtzeeGame(
    override val id: String,
    override val players: List<Player>,
    override val createdAt: Long = 0L,
    override val updatedAt: Long = 0L,
    val scores: Map<String, List<YahtzeeScore>> = emptyMap(),
    val name: String = "Yahtzee Game",
    val playerIds: String = "",
    val firstPlayerId: String = "",
    val currentPlayerId: String = "",
    val isFinished: Boolean = false,
    val winnerName: String? = null
) : Game() {
    
    val playerCount: Int get() = if (playerIds.isNotEmpty()) playerIds.split(",").size else players.size
    
    fun getCurrentPlayer(): Player? = players.find { it.id == currentPlayerId }
    
    fun getPlayerById(playerId: String): Player? = players.find { it.id == playerId }
    
    fun getNextPlayerId(): String? {
        val playerIdList = playerIds.split(",").filter { it.isNotEmpty() }
        if (playerIdList.isEmpty()) return null
        val currentIndex = playerIdList.indexOf(currentPlayerId)
        if (currentIndex < 0) return null
        val nextIndex = (currentIndex + 1) % playerIdList.size
        return playerIdList.getOrNull(nextIndex)
    }
    
    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun create(players: List<Player>, name: String = "Yahtzee Game"): YahtzeeGame {
            val now = getCurrentTimeMillis()
            val playerIds = players.joinToString(",") { it.id }
            val firstPlayerId = players.firstOrNull()?.id ?: ""
            return YahtzeeGame(
                id = Uuid.random().toString(),
                players = players,
                createdAt = now,
                updatedAt = now,
                name = name,
                playerIds = playerIds,
                firstPlayerId = firstPlayerId,
                currentPlayerId = firstPlayerId
            )
        }
    }
}
