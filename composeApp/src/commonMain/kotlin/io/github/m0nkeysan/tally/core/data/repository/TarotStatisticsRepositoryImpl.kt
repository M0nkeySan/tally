package io.github.m0nkeysan.tally.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import io.github.m0nkeysan.tally.core.domain.engine.TarotStatisticsEngine
import io.github.m0nkeysan.tally.core.domain.repository.PlayerRepository
import io.github.m0nkeysan.tally.core.domain.repository.TarotRepository
import io.github.m0nkeysan.tally.core.domain.repository.TarotStatisticsRepository
import io.github.m0nkeysan.tally.core.model.BidStatistic
import io.github.m0nkeysan.tally.core.model.GameStatistics
import io.github.m0nkeysan.tally.core.model.PlayerRanking
import io.github.m0nkeysan.tally.core.model.PlayerStatistics
import io.github.m0nkeysan.tally.core.model.RoundStatistic
import io.github.m0nkeysan.tally.core.model.TarotBid
import io.github.m0nkeysan.tally.core.model.TarotGame
import io.github.m0nkeysan.tally.database.TarotGameEntity
import io.github.m0nkeysan.tally.database.TarotQueries
import io.github.m0nkeysan.tally.database.TarotRoundEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TarotStatisticsRepositoryImpl(
    private val tarotQueries: TarotQueries,
    private val tarotRepository: TarotRepository,
    private val playerRepository: PlayerRepository
) : TarotStatisticsRepository {

    override suspend fun getPlayerStatistics(playerId: String): PlayerStatistics? = withContext(Dispatchers.Default) {
        val player = playerRepository.getPlayerById(playerId) ?: return@withContext null
        val games = tarotQueries.selectGamesByPlayer(playerId).awaitAsList().map { it.toDomain() }
        val playerRounds = tarotQueries.selectRoundsByPlayer(playerId).awaitAsList().map { it.toRoundData() }
        
        val gameIds = games.map { it.id }
        val allRounds = if (gameIds.isNotEmpty()) {
            tarotQueries.selectRoundsForGames(gameIds).awaitAsList().map { it.toRoundData() }
        } else emptyList()
        
        TarotStatisticsEngine.calculatePlayerStatistics(
            playerId = playerId,
            playerName = player.name,
            playerGames = games,
            playerRounds = playerRounds,
            allRounds = allRounds
        )
    }

    override suspend fun getBidStatistics(playerId: String): List<BidStatistic> = withContext(Dispatchers.Default) {
        val playerRounds = tarotQueries.selectRoundsByPlayer(playerId).awaitAsList().map { it.toRoundData() }
        TarotStatisticsEngine.calculateBidStatistics(playerId, playerRounds)
    }

    override suspend fun getRecentGames(
        playerId: String,
        limit: Int
    ): List<TarotGame> = withContext(Dispatchers.Default) {
        tarotQueries.selectGamesByPlayer(playerId).awaitAsList().take(limit).mapNotNull { gameEntity ->
            tarotRepository.getGameById(gameEntity.id)
        }
    }

    override suspend fun getCurrentGameStatistics(
        gameId: String
    ): GameStatistics? = withContext(Dispatchers.Default) {
        val gameEntity = tarotQueries.selectGameById(gameId).awaitAsOneOrNull() ?: return@withContext null
        val game = gameEntity.toDomain()
        val rounds = tarotQueries.selectRoundsForGame(gameId).awaitAsList().map { it.toRoundData() }
        val rankings = TarotStatisticsEngine.calculatePlayerRankings(game, rounds, playerRepository)
        
        GameStatistics(
            gameId = gameId,
            gameName = game.name,
            totalRounds = rounds.size,
            leadingPlayer = rankings.firstOrNull()?.player,
            playerRankings = rankings
        )
    }

    override suspend fun getRoundBreakdown(
        gameId: String
    ): List<RoundStatistic> = withContext(Dispatchers.Default) {
        val gameEntity = tarotQueries.selectGameById(gameId).awaitAsOneOrNull() ?: return@withContext emptyList()
        val game = gameEntity.toDomain()
        val rounds = tarotQueries.selectRoundsForGame(gameId).awaitAsList().map { it.toRoundData() }
        
        rounds.map { round ->
            val playerIds = gameEntity.playerIds.split(",")
            val players = playerRepository.getPlayersByIds(playerIds)
            val taker = players.find { it.id == round.takerPlayerId }
                ?: players.first()
            
            RoundStatistic(
                roundNumber = round.roundNumber,
                taker = taker,
                bid = TarotBid.valueOf(round.bid),
                pointsScored = round.pointsScored,
                bouts = round.bouts,
                contractWon = round.score > 0,
                score = round.score,
                hasSpecialAnnounce = round.hasPetitAuBout || 
                                     round.hasPoignee || 
                                     round.chelem != "NONE"
            )
        }
    }

    override suspend fun getPlayerRankings(
        gameId: String
    ): List<PlayerRanking> = withContext(Dispatchers.Default) {
        val gameEntity = tarotQueries.selectGameById(gameId).awaitAsOneOrNull() ?: return@withContext emptyList()
        val game = gameEntity.toDomain()
        val rounds = tarotQueries.selectRoundsForGame(gameId).awaitAsList().map { it.toRoundData() }
        
        TarotStatisticsEngine.calculatePlayerRankings(game, rounds, playerRepository)
    }

    private fun TarotGameEntity.toDomain() = TarotGame(
        id = id,
        players = emptyList(),
        createdAt = createdAt,
        updatedAt = updatedAt,
        playerCount = playerCount.toInt(),
        rounds = emptyList(),
        name = name,
        playerIds = playerIds
    )

    private fun TarotRoundEntity.toRoundData() = object : io.github.m0nkeysan.tally.core.domain.data.TarotRoundData {
        override val id: String = this@toRoundData.id
        override val gameId: String = this@toRoundData.gameId
        override val roundNumber: Int = this@toRoundData.roundNumber.toInt()
        override val takerPlayerId: String = this@toRoundData.takerPlayerId
        override val bid: String = this@toRoundData.bid
        override val bouts: Int = this@toRoundData.bouts.toInt()
        override val pointsScored: Int = this@toRoundData.pointsScored.toInt()
        override val hasPetitAuBout: Boolean = this@toRoundData.hasPetitAuBout != 0L
        override val hasPoignee: Boolean = this@toRoundData.hasPoignee != 0L
        override val poigneeLevel: String? = this@toRoundData.poigneeLevel
        override val chelem: String = this@toRoundData.chelem
        override val calledPlayerId: String? = this@toRoundData.calledPlayerId
        override val score: Int = this@toRoundData.score.toInt()
    }
}
