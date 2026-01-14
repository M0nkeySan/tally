package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.model.BidStatistic
import io.github.m0nkeysan.gamekeeper.core.model.GameHighlights
import io.github.m0nkeysan.gamekeeper.core.model.GameStatistics
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.core.model.PlayerMomentum
import io.github.m0nkeysan.gamekeeper.core.model.PlayerRanking
import io.github.m0nkeysan.gamekeeper.core.model.PlayerStatistics
import io.github.m0nkeysan.gamekeeper.core.model.RoundStatistic
import io.github.m0nkeysan.gamekeeper.core.model.TakerPerformance
import io.github.m0nkeysan.gamekeeper.core.model.TarotGame
import io.github.m0nkeysan.gamekeeper.core.model.TarotRound
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories

/**
 * Tarot Statistics Screen
 *
 * Displays comprehensive statistics for the current game and cross-game player metrics.
 * Features two tabs:
 * 1. Current Game: Game overview, rankings, and round breakdown
 * 2. Player Stats: Player statistics across all games, bid performance, game history
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarotStatisticsScreen(
    gameId: String,
    onBack: () -> Unit
) {
    val viewModel: TarotStatisticsViewModel = viewModel {
        TarotStatisticsViewModel(
            gameId = gameId,
            tarotRepository = PlatformRepositories.getTarotRepository(),
            statsRepository = PlatformRepositories.getTarotStatisticsRepository(),
            playerRepository = PlatformRepositories.getPlayerRepository()
        )
    }
    
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Statistics",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        state.game?.let {
                            Text(
                                it.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, "Back")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Loading state
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }
            
            // Error state
            if (state.error != null) {
                ErrorStateCard(
                    error = state.error!!,
                    onRetry = { viewModel.retryLoading() }
                )
                return@Column
            }
            
            // Tab navigation
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Current Game") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Player Stats") }
                )
            }
            
            // Tab content
            when (selectedTab) {
                0 -> CurrentGameTab(state)
                1 -> PlayerStatsTab(state)
            }
        }
    }
}

/**
 * Current Game Tab - Shows game overview, rankings, and round breakdown
 */
@Composable
private fun CurrentGameTab(state: TarotStatisticsState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Game Overview Card
        state.gameStatistics?.let { gameStats ->
            item {
                GameOverviewCard(gameStats)
            }
        }
        
        // Game Highlights Card (only if 3+ rounds)
        state.gameHighlights?.let { highlights ->
            item {
                GameHighlightsCard(highlights)
            }
        }
        
        // Momentum Card (only if 3+ rounds)
        if (state.hasMinimumRounds && state.playerMomentum.isNotEmpty()) {
            item {
                MomentumCard(state.playerMomentum)
            }
        }
        
        // Player Rankings Card
        if (state.currentGameRankings.isNotEmpty()) {
            item {
                PlayerRankingsCard(state.currentGameRankings)
            }
        }
        
        // Taker Performance Card (only if 3+ rounds)
        if (state.hasMinimumRounds && state.takerPerformance.isNotEmpty()) {
            item {
                TakerPerformanceCard(state.takerPerformance)
            }
        }
        
        // Round Breakdown Section
        if (state.roundBreakdown.isNotEmpty()) {
            item {
                Text(
                    "Round Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(state.roundBreakdown.reversed()) { round ->
                RoundBreakdownItem(round)
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = GameIcons.History,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                "No rounds played yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Player Stats Tab - Shows cross-game statistics for each player
 */
@Composable
private fun PlayerStatsTab(state: TarotStatisticsState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Per-player statistics for current game
        state.game?.let { game ->
            state.roundBreakdown.let { rounds ->
                game.players.forEachIndexed { playerIndex, player ->
                    item {
                        CurrentGamePlayerStatsCard(
                            player = player,
                            playerIndex = playerIndex,
                            rounds = rounds,
                            allRounds = game.rounds
                        )
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Game overview card showing total rounds, duration, and leader
 */
@Composable
private fun GameOverviewCard(gameStats: GameStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Game Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoItem(
                    icon = GameIcons.Casino,
                    label = "Total Rounds",
                    value = gameStats.totalRounds.toString(),
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    icon = GameIcons.History,
                    label = "Duration",
                    value = gameStats.gameDuration,
                    modifier = Modifier.weight(1f)
                )
            }
            
            gameStats.leadingPlayer?.let { leader ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.shapes.medium
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        GameIcons.Trophy,
                        "Leader",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Leading: ${leader.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Game highlights card showing comebacks, leads, and best rounds
 */
@Composable
private fun GameHighlightsCard(highlights: GameHighlights) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Game Highlights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Biggest Comeback
            highlights.biggestComeback?.let { comeback ->
                HighlightRow(
                    emoji = "💪",
                    title = "Biggest Comeback",
                    description = "${comeback.player.name}",
                    detail = "Recovered ${comeback.recovery} points (${comeback.lowestScore} → ${comeback.currentScore})",
                    round = "From Round ${comeback.roundReached}"
                )
            }
            
            // Largest Lead
            highlights.largestLead?.let { lead ->
                HighlightRow(
                    emoji = "📈",
                    title = "Largest Lead",
                    description = "${lead.player.name}",
                    detail = "+${lead.leadAmount} points vs ${lead.secondPlacePlayer.name}",
                    round = "Round ${lead.roundNumber}"
                )
            }
            
            // Best Round
            highlights.bestRound?.let { best ->
                HighlightRow(
                    emoji = "⭐",
                    title = "Best Round",
                    description = "${best.player.name}",
                    detail = "Gained ${best.pointsGained} points (${best.scoreBefore} → ${best.scoreAfter})",
                    round = "Round ${best.round.roundNumber} - ${best.round.bid.displayName}"
                )
            }
        }
    }
}

/**
 * Individual highlight row
 */
@Composable
private fun HighlightRow(
    emoji: String,
    title: String,
    description: String,
    detail: String,
    round: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                MaterialTheme.shapes.small
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, style = MaterialTheme.typography.headlineSmall)
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(
            detail,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 32.dp)
        )
        Text(
            round,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 32.dp)
        )
    }
}

/**
 * Player rankings card for current game
 */
@Composable
private fun PlayerRankingsCard(rankings: List<PlayerRanking>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Current Standings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            rankings.forEach { ranking ->
                RankingRow(ranking)
            }
        }
    }
}

/**
 * Momentum card showing player streaks and current form
 */
@Composable
private fun MomentumCard(playerMomentumMap: Map<String, PlayerMomentum>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Player Momentum 🔥",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            playerMomentumMap.values.forEach { momentum ->
                MomentumRow(momentum)
            }
        }
    }
}

/**
 * Individual momentum row showing player streak status
 */
@Composable
private fun MomentumRow(momentum: PlayerMomentum) {
    val streakEmoji = when {
        momentum.currentStreak.isHot -> "🔥"
        momentum.currentStreak.isCold -> "❄️"
        else -> "😐"
    }
    
    val streakText = when {
        momentum.currentStreak.isHot -> "On fire! ${momentum.currentStreak.count} wins in a row"
        momentum.currentStreak.isCold -> "Cold streak: ${momentum.currentStreak.count} losses in a row"
        momentum.currentStreak.count > 0 -> "Just started as taker"
        else -> "Mixed results"
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                MaterialTheme.shapes.small
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                momentum.player.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                streakText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            streakEmoji,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

/**
 * Individual ranking row
 */
@Composable
private fun RankingRow(ranking: PlayerRanking) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                MaterialTheme.shapes.small
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                "${ranking.rank}.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(30.dp)
            )
            Column {
                Text(
                    ranking.player.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${ranking.roundsWonAsTaker}/${ranking.roundsPlayedAsTaker} as taker",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Text(
            if (ranking.totalScore > 0) "+${ranking.totalScore}" else "${ranking.totalScore}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (ranking.totalScore > 0) 
                MaterialTheme.colorScheme.secondary 
            else 
                MaterialTheme.colorScheme.error
        )
    }
}

/**
 * Round breakdown item showing round details
 */
@Composable
private fun RoundBreakdownItem(round: RoundStatistic) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Round ${round.roundNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    round.taker.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BidBadge(round.bid.displayName)
                Text(
                    "${round.bouts} bouts • ${round.pointsScored} pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (round.contractWon) "✓ Won" else "✗ Lost",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (round.contractWon) 
                        MaterialTheme.colorScheme.secondary 
                    else 
                        MaterialTheme.colorScheme.error
                )
                Text(
                    if (round.score > 0) "+${round.score}" else "${round.score}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Taker performance card showing bid distribution and partner stats
 */
@Composable
private fun TakerPerformanceCard(performanceMap: Map<String, TakerPerformance>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Taker Performance 📊",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            performanceMap.values.forEach { performance ->
                TakerPerformanceRow(performance)
            }
        }
    }
}

/**
 * Individual taker performance row
 */
@Composable
private fun TakerPerformanceRow(performance: TakerPerformance) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                MaterialTheme.shapes.small
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Player name and basic stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    performance.player.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${performance.wins}/${performance.takerRounds} (${String.format("%.1f", performance.winRate)}%)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Bid distribution
        if (performance.bidDistribution.isNotEmpty()) {
            Text(
                "Bids Used:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                performance.bidDistribution.forEach { (bid, count) ->
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "${bid.displayName}(${count})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        // Partner stats (5-player only)
        performance.partnerStats?.let { partners ->
            if (partners.isNotEmpty()) {
                Text(
                    "With Partners:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                partners.forEach { (_, partner) ->
                    Text(
                        "• ${partner.partnerName}: ${partner.wins}/${partner.gamesPlayed} (${String.format("%.1f", partner.winRate)}%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * Overall rankings card for cross-game statistics
 */
@Composable
private fun OverallRankingsCard(rankings: List<PlayerRanking>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Game Rankings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            rankings.forEach { ranking ->
                RankingRow(ranking)
            }
        }
    }
}

/**
 * Personal statistics card for a player
 */
@Composable
private fun PersonalStatsCard(
    playerStats: PlayerStatistics,
    bidStatistics: List<BidStatistic>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with player name
            Text(
                playerStats.playerName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Stats summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatItem(
                    label = "Win Rate",
                    value = "%.0f%%".format(playerStats.takerWinRate),
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "Avg Score",
                    value = "%.1f".format(playerStats.averageTakerScore),
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Bid statistics
            if (bidStatistics.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Bid Statistics",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    bidStatistics.forEach { bid ->
                        BidStatisticRow(bid)
                    }
                }
            }

        }
    }
}

/**
 * Per-player statistics for the current game only
 */
@Composable
private fun CurrentGamePlayerStatsCard(
    player: Player,
    playerIndex: Int,
    rounds: List<RoundStatistic>,
    allRounds: List<TarotRound>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Player name header
            Text(
                player.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Current game stats
            val takerRounds = allRounds.count { it.takerPlayerId.toIntOrNull() == playerIndex }
            val takerWins = allRounds.count { 
                it.takerPlayerId.toIntOrNull() == playerIndex && it.score > 0 
            }
            val winRate = if (takerRounds > 0) 
                (takerWins.toDouble() / takerRounds) * 100 
            else 0.0
            
            // Find player's current score from rounds
            val playerCurrentScore = rounds.firstOrNull()?.let { firstRound ->
                val ranking = rounds.find { it.taker.id == player.id }?.let { round ->
                    // This is a simplification - in reality we'd need the full ranking calculation
                    // but we can get it from the ranking data if available
                    round.score
                } ?: 0
                ranking
            } ?: 0
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatItem(
                    label = "As Taker",
                    value = "$takerWins/$takerRounds",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "Win Rate",
                    value = "%.0f%%".format(winRate),
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Bid breakdown for this game
            val playerRounds = allRounds.filter { it.takerPlayerId.toIntOrNull() == playerIndex }
            if (playerRounds.isNotEmpty()) {
                val bidsInGame = playerRounds.groupingBy { it.bid }.eachCount()
                
                if (bidsInGame.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Bids in This Game",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        bidsInGame.forEach { (bid, count) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        MaterialTheme.shapes.small
                                    )
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    bid.displayName,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "x$count",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Bid statistic row showing win rate for a bid type
 */
@Composable
private fun BidStatisticRow(bid: BidStatistic) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                MaterialTheme.shapes.small
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            bid.bid.displayName,
            style = MaterialTheme.typography.bodySmall
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "%.0f%%".format(bid.winRate),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "(${bid.timesPlayed})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Info item for game overview
 */
@Composable
private fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            label,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Stat item for player stats
 */
@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                MaterialTheme.shapes.small
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Bid badge for displaying bid type
 */
@Composable
private fun BidBadge(bidName: String) {
    Box(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.shapes.small
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            bidName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Error state card with retry button
 */
@Composable
private fun ErrorStateCard(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.8f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Oops! Something went wrong",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                androidx.compose.material3.Button(
                    onClick = onRetry
                ) {
                    Text("Retry")
                }
            }
        }
    }
}
