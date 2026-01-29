package io.github.m0nkeysan.tally.core.domain.engine

import io.github.m0nkeysan.tally.core.domain.data.TarotGameData
import io.github.m0nkeysan.tally.core.domain.data.TarotRoundData
import io.github.m0nkeysan.tally.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.tally.core.model.*

/**
 * Engine for calculating comprehensive Tarot player and game statistics.
 */
object TarotStatisticsEngine {

    /**
     * Calculate statistics for a single player across multiple games and rounds.
     */
    suspend fun calculatePlayerStatistics(
        playerId: String,
        playerName: String,
        playerGames: List<TarotGameData>,
        playerRounds: List<TarotRoundData>,
        allRounds: List<TarotRoundData>
    ): PlayerStatistics {
        val totalGames = playerGames.size
        val totalRounds = playerRounds.size
        
        val takerRounds = playerRounds.count { it.takerPlayerId == playerId }
        val takerWins = playerRounds.count { it.takerPlayerId == playerId && it.score > 0 }
        
        val takerWinRate = if (takerRounds > 0) {
            (takerWins.toDouble() / takerRounds) * 100
        } else 0.0
        
        val totalTakerScore = playerRounds.filter { it.takerPlayerId == playerId }.sumOf { it.score }
        val averageTakerScore = if (takerRounds > 0) {
            totalTakerScore.toDouble() / takerRounds
        } else 0.0
        
        // Calculate total score for the player (as taker or partner/opponent)
        var totalScore = 0
        for (game in playerGames) {
            val gameRounds = allRounds.filter { it.gameId == game.id }
            for (round in gameRounds) {
                totalScore += calculatePlayerScoreForRound(round, playerId, game.playerCount)
            }
        }
        
        val averageGameScore = if (totalGames > 0) {
            totalScore.toDouble() / totalGames
        } else 0.0
        
        return PlayerStatistics(
            playerId = playerId,
            playerName = playerName,
            totalGames = totalGames,
            totalRounds = totalRounds,
            takerRounds = takerRounds,
            takerWins = takerWins,
            takerWinRate = takerWinRate,
            averageTakerScore = averageTakerScore,
            totalScore = totalScore,
            averageGameScore = averageGameScore
        )
    }

    /**
     * Calculate bid-specific statistics for a player.
     */
    fun calculateBidStatistics(
        playerId: String,
        playerRounds: List<TarotRoundData>
    ): List<BidStatistic> {
        val takerRounds = playerRounds.filter { it.takerPlayerId == playerId }
        
        return TarotBid.entries.map { bid ->
            val bidRounds = takerRounds.filter { it.bid == bid.name }
            val wins = bidRounds.count { it.score > 0 }
            
            BidStatistic(
                bid = bid,
                timesPlayed = bidRounds.size,
                wins = wins,
                winRate = if (bidRounds.isNotEmpty()) (wins.toDouble() / bidRounds.size) * 100 else 0.0,
                averageScore = if (bidRounds.isNotEmpty()) bidRounds.map { it.score }.average() else 0.0
            )
        }.filter { it.timesPlayed > 0 }.sortedByDescending { it.timesPlayed }
    }

    /**
     * Calculate player rankings for a specific game.
     */
    suspend fun calculatePlayerRankings(
        game: TarotGameData,
        rounds: List<TarotRoundData>,
        playerRepository: PlayerRepository
    ): List<PlayerRanking> {
        val playerIds = game.playerIds.split(",").filter { it.isNotEmpty() }
        val players = playerRepository.getPlayersByIds(playerIds)
        
        val playerScores = playerIds.map { pid ->
            val player = players.find { it.id == pid } ?: Player(pid, "Unknown", "", 0)
            
            var totalScore = 0
            var takerRounds = 0
            var takerWins = 0
            
            for (round in rounds) {
                val score = calculatePlayerScoreForRound(round, pid, game.playerCount)
                totalScore += score
                
                if (round.takerPlayerId == pid) {
                    takerRounds++
                    if (round.score > 0) takerWins++
                }
            }
            
            val winRate = if (takerRounds > 0) (takerWins.toDouble() / takerRounds) * 100 else 0.0
            
            PlayerRanking(
                rank = 0, // Will be set after sorting
                player = player,
                totalScore = totalScore,
                roundsPlayedAsTaker = takerRounds,
                roundsWonAsTaker = takerWins,
                winRate = winRate
            )
        }
        
        return playerScores.sortedByDescending { it.totalScore }
            .mapIndexed { index, ranking -> ranking.copy(rank = index + 1) }
    }

    /**
     * Helper to calculate a player's score for a specific round.
     */
    private fun calculatePlayerScoreForRound(
        round: TarotRoundData,
        playerId: String,
        playerCount: Int
    ): Int {
        val isTaker = round.takerPlayerId == playerId
        val isCalled = round.calledPlayerId == playerId
        
        return when {
            isTaker -> round.score
            isCalled && playerCount == 5 -> round.score
            else -> {
                // Opponent score is negative of taker's score divided by number of opponents
                // In 5-player game, there might be a partner
                val opponentCount = playerCount - 1 - if (playerCount == 5 && round.calledPlayerId != null) 1 else 0
                if (opponentCount > 0) {
                    -round.score / opponentCount
                } else 0
            }
        }
    }
}
