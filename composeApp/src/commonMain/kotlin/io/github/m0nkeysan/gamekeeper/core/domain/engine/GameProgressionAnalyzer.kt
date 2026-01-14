package io.github.m0nkeysan.gamekeeper.core.domain.engine

import io.github.m0nkeysan.gamekeeper.core.model.*

/**
 * Analyzes game progression to generate highlights, momentum, and performance stats
 */
class GameProgressionAnalyzer(
    private val scoringEngine: TarotScoringEngine
) {
    
    /**
     * Calculate game highlights (comebacks, leads, best rounds)
     * Requires at least 3 rounds
     */
    fun calculateGameHighlights(
        players: List<Player>,
        rounds: List<TarotRound>,
        playerCount: Int
    ): GameHighlights? {
        if (rounds.size < 3) return null
        
        // Track cumulative scores per player after each round
        val scoreHistory = buildScoreHistory(players, rounds, playerCount)
        
        val comeback = findBiggestComeback(players, scoreHistory)
        val lead = findLargestLead(players, scoreHistory)
        val bestRound = findBestRound(players, rounds, scoreHistory, playerCount)
        
        return GameHighlights(
            biggestComeback = comeback,
            largestLead = lead,
            bestRound = bestRound
        )
    }
    
    /**
     * Calculate current momentum and streaks for all players
     * Requires at least 3 rounds
     */
    fun calculatePlayerMomentum(
        players: List<Player>,
        rounds: List<TarotRound>
    ): Map<String, PlayerMomentum> {
        if (rounds.size < 3) return emptyMap()
        
        return players.mapIndexed { index, player ->
            val playerRounds = rounds.filter { 
                it.takerPlayerId.toIntOrNull() == index 
            }
            
            val currentStreak = calculateCurrentStreak(playerRounds)
            val longestWin = calculateLongestWinStreak(playerRounds)
            val longestLoss = calculateLongestLossStreak(playerRounds)
            
            player.id to PlayerMomentum(
                player = player,
                currentStreak = currentStreak,
                longestWinStreak = longestWin,
                longestLossStreak = longestLoss
            )
        }.toMap()
    }
    
    /**
     * Calculate enhanced taker performance metrics
     * Requires at least 3 rounds
     */
    fun calculateTakerPerformance(
        players: List<Player>,
        rounds: List<TarotRound>,
        playerCount: Int
    ): Map<String, TakerPerformance> {
        if (rounds.size < 3) return emptyMap()
        
        return players.mapIndexed { index, player ->
            val takerRounds = rounds.filter { 
                it.takerPlayerId.toIntOrNull() == index 
            }
            
            val wins = takerRounds.filter { it.score > 0 }
            val losses = takerRounds.filter { it.score <= 0 }
            
            val bidDistribution = takerRounds.groupingBy { it.bid }.eachCount()
            val preferredBid = bidDistribution.maxByOrNull { it.value }?.key
            
            val avgWin = if (wins.isNotEmpty()) 
                wins.map { it.score }.average() else 0.0
            val avgLoss = if (losses.isNotEmpty()) 
                losses.map { it.score }.average() else 0.0
            
            val totalGained = wins.sumOf { it.score }
            val totalLost = losses.sumOf { it.score }
            
            val partnerStats = if (playerCount == 5) {
                calculatePartnerStats(player, index, players, rounds)
            } else null
            
            player.id to TakerPerformance(
                player = player,
                takerRounds = takerRounds.size,
                wins = wins.size,
                losses = losses.size,
                winRate = if (takerRounds.isNotEmpty()) 
                    (wins.size.toDouble() / takerRounds.size) * 100 
                else 0.0,
                preferredBid = preferredBid,
                bidDistribution = bidDistribution,
                avgWinPoints = avgWin,
                avgLossPoints = avgLoss,
                totalPointsGained = totalGained,
                totalPointsLost = totalLost,
                partnerStats = partnerStats
            )
        }.toMap()
    }
    
    // ============ PRIVATE HELPER FUNCTIONS ============
    
    private fun buildScoreHistory(
        players: List<Player>,
        rounds: List<TarotRound>,
        playerCount: Int
    ): Map<String, List<Int>> {
        val history = players.associate { it.id to mutableListOf(0) }.toMutableMap()
        
        rounds.forEachIndexed { roundIndex, _ ->
            val currentScores = scoringEngine.calculateTotalScores(
                players,
                rounds.take(roundIndex + 1),
                playerCount
            )
            
            players.forEach { player ->
                history[player.id]?.add(currentScores[player.id] ?: 0)
            }
        }
        
        return history
    }
    
    private fun findBiggestComeback(
        players: List<Player>,
        scoreHistory: Map<String, List<Int>>
    ): ComebackStat? {
        val comebacks = players.mapNotNull { player ->
            val scores = scoreHistory[player.id] ?: return@mapNotNull null
            val lowestScore = scores.minOrNull() ?: return@mapNotNull null
            val currentScore = scores.lastOrNull() ?: return@mapNotNull null
            
            if (lowestScore < 0 && currentScore > lowestScore) {
                val recovery = currentScore - lowestScore
                val roundReached = scores.indexOf(lowestScore)
                
                ComebackStat(
                    player = player,
                    lowestScore = lowestScore,
                    currentScore = currentScore,
                    recovery = recovery,
                    roundReached = roundReached
                )
            } else null
        }
        
        return comebacks.maxByOrNull { it.recovery }
    }
    
    private fun findLargestLead(
        players: List<Player>,
        scoreHistory: Map<String, List<Int>>
    ): LeadStat? {
        if (scoreHistory.isEmpty()) return null
        
        val roundCount = scoreHistory.values.firstOrNull()?.size ?: 0
        var largestLead: LeadStat? = null
        var maxLeadAmount = 0
        
        for (roundIndex in 0 until roundCount) {
            val scoresAtRound = players.mapNotNull { player ->
                val score = scoreHistory[player.id]?.getOrNull(roundIndex)
                if (score != null) player to score else null
            }.sortedByDescending { it.second }
            
            if (scoresAtRound.size >= 2) {
                val first = scoresAtRound[0]
                val second = scoresAtRound[1]
                val lead = first.second - second.second
                
                if (lead > maxLeadAmount) {
                    maxLeadAmount = lead
                    largestLead = LeadStat(
                        player = first.first,
                        leadAmount = lead,
                        secondPlacePlayer = second.first,
                        roundNumber = roundIndex
                    )
                }
            }
        }
        
        return largestLead
    }
    
    private fun findBestRound(
        players: List<Player>,
        rounds: List<TarotRound>,
        scoreHistory: Map<String, List<Int>>,
        playerCount: Int
    ): RoundHighlight? {
        // For each round, calculate points gained by each player
        val roundHighlights = rounds.mapIndexed { roundIndex, round ->
            players.mapNotNull { player ->
                val scores = scoreHistory[player.id] ?: return@mapNotNull null
                if (scores.size <= roundIndex + 1) return@mapNotNull null
                
                val scoreBefore = scores[roundIndex]
                val scoreAfter = scores[roundIndex + 1]
                val pointsGained = scoreAfter - scoreBefore
                
                if (pointsGained > 0) {
                    // Find the taker for this round to get RoundStatistic
                    val takerIndex = round.takerPlayerId.toIntOrNull() ?: return@mapNotNull null
                    val taker = players.getOrNull(takerIndex) ?: return@mapNotNull null
                    
                    val roundStat = RoundStatistic(
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
                    
                    RoundHighlight(
                        player = player,
                        round = roundStat,
                        pointsGained = pointsGained,
                        scoreBefore = scoreBefore,
                        scoreAfter = scoreAfter
                    )
                } else null
            }
        }.flatten()
        
        return roundHighlights.maxByOrNull { it.pointsGained }
    }
    
    private fun calculateCurrentStreak(rounds: List<TarotRound>): Streak {
        if (rounds.isEmpty()) return Streak(StreakType.NONE, 0)
        
        val recentRounds = rounds.takeLast(10) // Look at last 10 rounds max
        var streakCount = 0
        var streakType = StreakType.NONE
        
        // Walk backwards to find current streak
        for (round in recentRounds.reversed()) {
            val isWin = round.score > 0
            
            if (streakCount == 0) {
                // Start of streak
                streakType = if (isWin) StreakType.WIN else StreakType.LOSS
                streakCount = 1
            } else if ((streakType == StreakType.WIN && isWin) ||
                       (streakType == StreakType.LOSS && !isWin)) {
                // Continue streak
                streakCount++
            } else {
                // Streak broken
                break
            }
        }
        
        return Streak(streakType, streakCount)
    }
    
    private fun calculateLongestWinStreak(rounds: List<TarotRound>): Int {
        var longest = 0
        var current = 0
        
        rounds.forEach { round ->
            if (round.score > 0) {
                current++
                longest = maxOf(longest, current)
            } else {
                current = 0
            }
        }
        
        return longest
    }
    
    private fun calculateLongestLossStreak(rounds: List<TarotRound>): Int {
        var longest = 0
        var current = 0
        
        rounds.forEach { round ->
            if (round.score <= 0) {
                current++
                longest = maxOf(longest, current)
            } else {
                current = 0
            }
        }
        
        return longest
    }
    
    private fun calculatePartnerStats(
        player: Player,
        playerIndex: Int,
        allPlayers: List<Player>,
        rounds: List<TarotRound>
    ): Map<String, PartnerStat>? {
        val takerRoundsWithPartner = rounds.filter { round ->
            round.takerPlayerId.toIntOrNull() == playerIndex &&
            round.calledPlayerId != null &&
            round.calledPlayerId != playerIndex.toString()
        }
        
        if (takerRoundsWithPartner.isEmpty()) return null
        
        return takerRoundsWithPartner
            .groupBy { it.calledPlayerId }
            .mapNotNull { (partnerId, partnerRounds) ->
                val partnerIdx = partnerId?.toIntOrNull() ?: return@mapNotNull null
                val partner = allPlayers.getOrNull(partnerIdx) ?: return@mapNotNull null
                
                val wins = partnerRounds.count { it.score > 0 }
                val losses = partnerRounds.size - wins
                val winRate = (wins.toDouble() / partnerRounds.size) * 100
                
                partner.id to PartnerStat(
                    partnerId = partner.id,
                    partnerName = partner.name,
                    gamesPlayed = partnerRounds.size,
                    wins = wins,
                    losses = losses,
                    winRate = winRate
                )
            }.toMap()
    }
}
