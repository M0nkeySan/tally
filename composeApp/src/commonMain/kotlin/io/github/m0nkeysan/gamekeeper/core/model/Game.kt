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
    val playerIds: String = "" // Comma-separated player IDs for backward compatibility
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
    val firstPlayerIndex: Int = 0,
    val currentPlayerIndex: Int = 0,
    val isFinished: Boolean = false,
    val winnerName: String? = null
) : Game() {
    
    val playerCount: Int get() = if (playerIds.isNotEmpty()) playerIds.split(",").size else players.size
    
    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun create(players: List<Player>, name: String = "Yahtzee Game"): YahtzeeGame {
            val now = getCurrentTimeMillis()
            return YahtzeeGame(
                id = Uuid.random().toString(),
                players = players,
                createdAt = now,
                updatedAt = now,
                name = name,
                playerIds = players.joinToString(",") { it.id }
            )
        }
    }
}

@Serializable
data class CounterGame(
    override val id: String,
    override val players: List<Player>,
    override val createdAt: Long = 0L,
    override val updatedAt: Long = 0L,
    val counts: Map<String, Int> = emptyMap(),
    val label: String = "Counter"
) : Game() {
    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun create(players: List<Player>, label: String = "Counter"): CounterGame {
            val now = getCurrentTimeMillis()
            return CounterGame(
                id = Uuid.random().toString(),
                players = players,
                createdAt = now,
                updatedAt = now,
                label = label
            )
        }
    }
}
