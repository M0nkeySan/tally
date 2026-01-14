package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.TarotDao
import io.github.m0nkeysan.gamekeeper.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.TarotRepository
import io.github.m0nkeysan.gamekeeper.core.domain.repository.TarotStatisticsRepository
import io.github.m0nkeysan.gamekeeper.core.model.BidStatistic
import io.github.m0nkeysan.gamekeeper.core.model.GameStatistics
import io.github.m0nkeysan.gamekeeper.core.model.PlayerRanking
import io.github.m0nkeysan.gamekeeper.core.model.PlayerStatistics
import io.github.m0nkeysan.gamekeeper.core.model.RoundStatistic
import io.github.m0nkeysan.gamekeeper.core.model.TarotBid
import io.github.m0nkeysan.gamekeeper.core.model.TarotGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of TarotStatisticsRepository using local Room database.
 *
 * Aggregates player statistics across games and provides
 * detailed game-specific metrics.
 */
class TarotStatisticsRepositoryImpl(
    private val tarotDao: TarotDao,
    private val tarotRepository: TarotRepository,
    private val playerRepository: PlayerRepository
) : TarotStatisticsRepository {

    override suspend fun getPlayerStatistics(
        playerId: String,
        playerIndex: Int
    ): PlayerStatistics? = withContext(Dispatchers.IO) {
        val raw = tarotDao.getPlayerStatistics(playerId, playerIndex) ?: return@withContext null
        val player = playerRepository.getPlayerById(playerId) ?: return@withContext null
        
        PlayerStatistics(
            playerId = playerId,
            playerName = player.name,
            totalGames = raw.totalGames,
            totalRounds = raw.totalRounds,
            takerRounds = raw.takerRounds,
            takerWins = raw.takerWins,
            takerWinRate = if (raw.takerRounds > 0) 
                (raw.takerWins.toDouble() / raw.takerRounds) * 100 
            else 0.0,
            averageTakerScore = raw.avgTakerScore ?: 0.0,
            totalScore = raw.totalScore,
            averageGameScore = if (raw.totalGames > 0)
                raw.totalScore.toDouble() / raw.totalGames
            else 0.0
        )
    }

    override suspend fun getBidStatistics(
        playerId: String,
        playerIndex: Int
    ): List<BidStatistic> = withContext(Dispatchers.IO) {
        tarotDao.getBidStatistics(playerId, playerIndex).map { raw ->
            val bid = TarotBid.values().find { it.name == raw.bid } ?: TarotBid.PRISE
            
            BidStatistic(
                bid = bid,
                timesPlayed = raw.count,
                wins = raw.wins,
                winRate = if (raw.count > 0) (raw.wins.toDouble() / raw.count) * 100 else 0.0,
                averageScore = raw.avgScore
            )
        }
    }

    override suspend fun getRecentGames(
        playerId: String,
        limit: Int
    ): List<TarotGame> = withContext(Dispatchers.IO) {
        val gameIds = tarotDao.getRecentGamesForPlayer(playerId, limit).map { it.id }
        gameIds.mapNotNull { gameId ->
            tarotRepository.getGameById(gameId)
        }
    }

    override suspend fun getCurrentGameStatistics(
        gameId: String
    ): GameStatistics? = withContext(Dispatchers.IO) {
        val game = tarotRepository.getGameById(gameId) ?: return@withContext null
        val rankings = getPlayerRankings(gameId)
        
        val durationMs = game.updatedAt - game.createdAt
        val durationMinutes = durationMs / (1000 * 60)
        val durationHours = durationMinutes / 60
        val durationFormatted = when {
            durationHours > 0 -> "$durationHours hour${if (durationHours > 1) "s" else ""}"
            else -> "$durationMinutes minute${if (durationMinutes > 1) "s" else ""}"
        }
        
        GameStatistics(
            gameId = gameId,
            gameName = game.name,
            totalRounds = game.rounds.size,
            gameDuration = durationFormatted,
            leadingPlayer = rankings.firstOrNull()?.player,
            playerRankings = rankings
        )
    }

    override suspend fun getRoundBreakdown(
        gameId: String
    ): List<RoundStatistic> = withContext(Dispatchers.IO) {
        val game = tarotRepository.getGameById(gameId) ?: return@withContext emptyList()
        
        game.rounds.map { round ->
            val taker = game.players.getOrNull(round.takerPlayerId.toIntOrNull() ?: 0)
                ?: game.players.first()
            
            RoundStatistic(
                roundNumber = round.roundNumber,
                taker = taker,
                bid = round.bid,
                pointsScored = round.pointsScored,
                bouts = round.bouts,
                contractWon = round.score > 0,
                score = round.score,
                hasSpecialAnnounce = round.hasPetitAuBout || 
                                     round.hasPoignee || 
                                     round.chelem.toString() != "NONE"
            )
        }
    }

    override suspend fun getPlayerRankings(
        gameId: String
    ): List<PlayerRanking> = withContext(Dispatchers.IO) {
        val game = tarotRepository.getGameById(gameId) ?: return@withContext emptyList()
        
        // Calculate total score for each player
        val playerScores = game.players.mapIndexed { index, player ->
            val totalScore = game.rounds
                .filter { it.takerPlayerId.toIntOrNull() == index }
                .sumOf { it.score }
            
            val takerRounds = game.rounds.count { it.takerPlayerId.toIntOrNull() == index }
            val takerWins = game.rounds.count { 
                it.takerPlayerId.toIntOrNull() == index && it.score > 0 
            }
            
            val winRate = if (takerRounds > 0) 
                (takerWins.toDouble() / takerRounds) * 100 
            else 0.0
            
            Triple(player, totalScore, Pair(takerWins, takerRounds)) to winRate
        }
        
        // Sort by total score descending
        val sorted = playerScores.sortedByDescending { it.first.second }
        
        // Create rankings
        sorted.mapIndexed { index, (playerData, winRate) ->
            val (player, totalScore, takerStats) = playerData
            
            PlayerRanking(
                rank = index + 1,
                player = player,
                totalScore = totalScore,
                roundsWonAsTaker = takerStats.first,
                roundsPlayedAsTaker = takerStats.second,
                winRate = winRate
            )
        }
    }
}
