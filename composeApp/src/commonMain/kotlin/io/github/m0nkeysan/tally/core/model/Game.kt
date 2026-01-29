package io.github.m0nkeysan.tally.core.model

import io.github.m0nkeysan.tally.core.domain.data.TarotGameData
import io.github.m0nkeysan.tally.core.domain.data.YahtzeeGameData
import io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis
import kotlinx.serialization.Serializable
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
    override val playerCount: Int,
    override val name: String = "Tarot Game",
    override val playerIds: String = ""
) : Game(), TarotGameData {

    companion object {
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
    override val name: String = "Yahtzee Game",
    override val playerIds: String = "",
    override val firstPlayerId: String = "",
    override val currentPlayerId: String = "",
    override val isFinished: Boolean = false,
    override val winnerName: String? = null
) : Game(), YahtzeeGameData {
    
    override val playerCount: Int get() = if (playerIds.isNotEmpty()) playerIds.split(",").size else players.size
    
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
