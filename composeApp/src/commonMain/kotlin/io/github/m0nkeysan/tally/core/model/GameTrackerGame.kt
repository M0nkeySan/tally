package io.github.m0nkeysan.tally.core.model

import io.github.m0nkeysan.tally.core.domain.model.DurationMode
import io.github.m0nkeysan.tally.core.domain.model.ScoringLogic
import io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class GameTrackerGame(
    override val id: String,
    override val players: List<Player>,
    override val createdAt: Long,
    override val updatedAt: Long,
    val name: String,
    val playerIds: String,
    val playerCount: Int,
    val scoringLogic: ScoringLogic,
    val targetScore: Int?,
    val durationMode: DurationMode,
    val fixedRoundCount: Int?,
    val currentRound: Int,
    val isFinished: Boolean,
    val winnerPlayerId: String?,
    val winner: Player? = null
) : Game() {

    /**
     * Calculate total scores for all players from a list of rounds
     */
    fun getTotalScores(rounds: List<GameTrackerRound>): Map<String, Int> {
        return rounds
            .groupBy { it.playerId }
            .mapValues { (_, playerRounds) -> playerRounds.sumOf { it.score } }
    }

    /**
     * Get sorted leaderboard based on scoring logic
     */
    fun getLeaderboard(rounds: List<GameTrackerRound>): List<Pair<Player, Int>> {
        val totals = getTotalScores(rounds)
        return players
            .map { player -> player to (totals[player.id] ?: 0) }
            .sortedByDescending { (_, score) ->
                when (scoringLogic) {
                    ScoringLogic.HIGH_SCORE_WINS -> score
                    ScoringLogic.LOW_SCORE_WINS -> -score
                }
            }
    }

    /**
     * Get the current leader
     */
    fun getLeader(rounds: List<GameTrackerRound>): String? {
        return getLeaderboard(rounds).firstOrNull()?.first?.id
    }

    /**
     * Check win conditions and return status
     */
    fun checkWinConditions(rounds: List<GameTrackerRound>): WinStatus {
        if (isFinished) {
            return WinStatus.GameComplete(winnerPlayerId)
        }

        val totals = getTotalScores(rounds)

        // Check fixed rounds completion
        if (durationMode == DurationMode.FIXED_ROUNDS && fixedRoundCount != null) {
            // Get max round number from rounds
            val maxRound = rounds.maxOfOrNull { it.roundNumber } ?: 0
            if (maxRound >= fixedRoundCount) {
                val leader = getLeader(rounds)
                return WinStatus.ShouldFinish(leader)
            }
        }

        // Check target score reached (indicator only, don't auto-finish)
        if (targetScore != null) {
            val playersAtTarget = totals.filter { (_, score) ->
                when (scoringLogic) {
                    ScoringLogic.HIGH_SCORE_WINS -> score >= targetScore
                    ScoringLogic.LOW_SCORE_WINS -> score <= targetScore
                }
            }
            if (playersAtTarget.isNotEmpty()) {
                return WinStatus.TargetReached(playersAtTarget.keys.toList())
            }
        }

        return WinStatus.InProgress
    }

    companion object {
        fun create(
            name: String,
            players: List<Player>,
            scoringLogic: ScoringLogic,
            targetScore: Int?,
            durationMode: DurationMode,
            fixedRoundCount: Int?
        ): GameTrackerGame {
            val now = getCurrentTimeMillis()
            val playerIds = players.joinToString(",") { it.id }

            return GameTrackerGame(
                id = Uuid.random().toString(),
                players = players,
                createdAt = now,
                updatedAt = now,
                name = name,
                playerIds = playerIds,
                playerCount = players.size,
                scoringLogic = scoringLogic,
                targetScore = targetScore,
                durationMode = durationMode,
                fixedRoundCount = fixedRoundCount,
                currentRound = 1,
                isFinished = false,
                winnerPlayerId = null,
                winner = null
            )
        }
    }
}

/**
 * Represents the current win status of a game
 */
sealed class WinStatus {
    object InProgress : WinStatus()
    data class TargetReached(val playerIds: List<String>) : WinStatus()
    data class ShouldFinish(val leaderId: String?) : WinStatus()
    data class GameComplete(val winnerId: String?) : WinStatus()
}
