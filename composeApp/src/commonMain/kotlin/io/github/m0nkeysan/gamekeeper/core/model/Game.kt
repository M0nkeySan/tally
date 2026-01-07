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
    val playerCount: Int
) : Game() {

    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun create(players: List<Player>, playerCount: Int? = null): TarotGame {
            val now = getCurrentTimeMillis()

            // On utilise le playerCount passé en paramètre, sinon la taille de la liste
            val finalPlayerCount = playerCount ?: players.size

            // Petite validation de sécurité pour le Tarot
            require(finalPlayerCount in 3..5) {
                "Le Tarot se joue à 3, 4 ou 5 joueurs uniquement."
            }

            return TarotGame(
                id = Uuid.random().toString(),
                players = players,
                createdAt = now,
                updatedAt = now,
                playerCount = finalPlayerCount,
                rounds = emptyList()
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
    val scores: Map<String, List<YahtzeeScore>> = emptyMap()
) : Game() {
    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun create(players: List<Player>): YahtzeeGame {
            val now = getCurrentTimeMillis()
            return YahtzeeGame(
                id = Uuid.random().toString(),
                players = players,
                createdAt = now,
                updatedAt = now
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
