package io.github.m0nkeysan.tally.ui.screens.tarot

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.GameIcons
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.core.model.PlayerRanking
import io.github.m0nkeysan.tally.core.model.RoundStatistic
import io.github.m0nkeysan.tally.core.model.TakerPerformance
import io.github.m0nkeysan.tally.core.model.TarotRound
import io.github.m0nkeysan.tally.core.utils.format
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.action_back
import io.github.m0nkeysan.tally.generated.resources.cd_toggle_collapse
import io.github.m0nkeysan.tally.generated.resources.cd_toggle_expand
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_contract_lost
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_contract_won
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_empty_rounds
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_format_as_taker
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_label_as_taker
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_label_avg_bouts
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_label_avg_lost
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_label_avg_won
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_label_bids_used
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_label_called
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_label_favorite_bid
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_label_most_successful
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_label_round
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_label_win_rate
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_label_with_partners
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_section_bids_in_game
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_section_called_performance
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_section_current_standings
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_section_performance_details
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_section_round_breakdown
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_section_taker_performance
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_tab_current_game
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_tab_player_stats
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_title
import io.github.m0nkeysan.tally.platform.PlatformRepositories
import io.github.m0nkeysan.tally.ui.components.ErrorState
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Tarot Statistics Screen
 *
 * Displays comprehensive statistics for the current game and cross-game player metrics.
 * Features two tabs:
 * 1. Current Game: Game overview, rankings, and round breakdown
 * 2. Player Stats: Player statistics across all games, bid performance, game history
 */
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
                            stringResource(Res.string.tarot_stats_title),
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
                        Icon(GameIcons.ArrowBack, stringResource(Res.string.action_back))
                    }
                },
                modifier = Modifier.shadow(elevation = 2.dp)
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
                ErrorState(
                    message = state.error!!,
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
                    text = { Text(stringResource(Res.string.tarot_stats_tab_current_game)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(stringResource(Res.string.tarot_stats_tab_player_stats)) }
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            stringResource(Res.string.tarot_stats_section_round_breakdown),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    MaterialTheme.shapes.small
                                )
                                .padding(8.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        state.roundBreakdown.reversed().forEach { round ->
                            RoundBreakdownItem(round)
                        }
                    }
                }
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
                                stringResource(Res.string.tarot_stats_empty_rounds),
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
                game.players.forEach { player ->
                    item {
                        CurrentGamePlayerStatsCard(
                            player = player,
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
 * Player rankings card for current game
 */
@Composable
private fun PlayerRankingsCard(rankings: List<PlayerRanking>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(Res.string.tarot_stats_section_current_standings),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        MaterialTheme.shapes.small
                    )
                    .padding(8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            rankings.forEach { ranking ->
                RankingRow(ranking)
            }
        }
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
                    stringResource(
                        Res.string.tarot_stats_format_as_taker,
                        ranking.roundsWonAsTaker,
                        ranking.roundsPlayedAsTaker
                    ),
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
                    stringResource(Res.string.tarot_stats_label_round, round.roundNumber),
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (round.contractWon) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (round.contractWon) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = if (round.contractWon) stringResource(Res.string.tarot_stats_contract_won) else stringResource(
                        Res.string.tarot_stats_contract_lost
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (round.contractWon) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = if (round.score > 0) "+${round.score}" else "${round.score}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(Res.string.tarot_stats_section_taker_performance),
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
                    "${performance.wins}/${performance.takerRounds} (${performance.winRate.format(1)}%)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Bid distribution
        if (performance.bidDistribution.isNotEmpty()) {
            Text(
                stringResource(Res.string.tarot_stats_label_bids_used),
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
                    stringResource(Res.string.tarot_stats_label_with_partners),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                partners.forEach { (_, partner) ->
                    Text(
                        "• ${partner.partnerName}: ${partner.wins}/${partner.gamesPlayed} (${
                            partner.winRate.format(
                                1
                            )
                        }%)",
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
 * Per-player statistics for the current game only
 */
@Composable
private fun CurrentGamePlayerStatsCard(
    player: Player,
    rounds: List<RoundStatistic>,
    allRounds: List<TarotRound>
) {
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    player.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isExpanded) GameIcons.ExpandLess else GameIcons.ExpandMore,
                    contentDescription = if (isExpanded) stringResource(Res.string.cd_toggle_collapse) else stringResource(
                        Res.string.cd_toggle_expand
                    ),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Expanded content
            if (isExpanded) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Current game stats
                    val takerRounds = allRounds.count { it.takerPlayerId == player.id }
                    val takerWins = allRounds.count {
                        it.takerPlayerId == player.id && it.score > 0
                    }

                    // Called rounds (times player was called as partner)
                    val calledRounds = allRounds.count { it.calledPlayerId == player.id }
                    val calledWins = allRounds.count {
                        it.calledPlayerId == player.id && it.score > 0
                    }

                    // Total rounds as taker or called
                    val totalRounds = takerRounds + calledRounds
                    val totalWins = takerWins + calledWins
                    val winRate = if (totalRounds > 0)
                        (totalWins.toDouble() / totalRounds) * 100
                    else 0.0

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatItem(
                            label = stringResource(Res.string.tarot_stats_label_called),
                            value = calledRounds.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        StatItem(
                            label = stringResource(Res.string.tarot_stats_label_as_taker),
                            value = "$takerWins/$takerRounds",
                            modifier = Modifier.weight(1f)
                        )
                        StatItem(
                            label = stringResource(Res.string.tarot_stats_label_win_rate),
                            value = "${winRate.format(0)}%",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Bid breakdown for this game - using RoundStatistic which has proper data
                    val playerGameRounds = rounds.filter { it.taker.id == player.id }
                    val playerAllRounds = allRounds.filter { it.takerPlayerId == player.id }

                    if (playerAllRounds.isNotEmpty()) {
                        val bidsInGame = playerAllRounds.groupingBy { it.bid }.eachCount()

                        if (bidsInGame.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Favorite bid (most played)
                                val favoriteBid = bidsInGame.maxByOrNull { it.value }
                                if (favoriteBid != null) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                MaterialTheme.colorScheme.secondaryContainer,
                                                MaterialTheme.shapes.small
                                            )
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            stringResource(Res.string.tarot_stats_label_favorite_bid),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "${favoriteBid.key.displayName} (${favoriteBid.value}x)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Most successful bid (highest win rate) - use playerGameRounds with RoundStatistic
                                val bidWinRates = bidsInGame.keys.associateWith { bid ->
                                    val bidRounds = playerGameRounds.filter { it.bid == bid }
                                    val wins = bidRounds.count { it.contractWon }
                                    if (bidRounds.isNotEmpty()) wins.toDouble() / bidRounds.size * 100 else 0.0
                                }
                                val mostSuccessfulBid = bidWinRates.maxByOrNull { it.value }
                                if (mostSuccessfulBid != null && mostSuccessfulBid.value > 0) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                MaterialTheme.colorScheme.tertiaryContainer,
                                                MaterialTheme.shapes.small
                                            )
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            stringResource(Res.string.tarot_stats_label_most_successful),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "${mostSuccessfulBid.key.displayName} (${
                                                mostSuccessfulBid.value.format(
                                                    0
                                                )
                                            }%",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    stringResource(Res.string.tarot_stats_section_bids_in_game),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                            MaterialTheme.shapes.small
                                        )
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center
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

                        // Detailed averages section - use playerGameRounds with RoundStatistic
                        if (playerGameRounds.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    stringResource(Res.string.tarot_stats_section_performance_details),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                            MaterialTheme.shapes.small
                                        )
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )

                                // Average bouts
                                val totalBouts = playerGameRounds.sumOf { it.bouts }
                                val avgBouts = totalBouts.toDouble() / playerGameRounds.size
                                StatItem(
                                    label = stringResource(Res.string.tarot_stats_label_avg_bouts),
                                    value = avgBouts.format(1),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Points won/lost
                                val pointsWon =
                                    playerGameRounds.filter { it.contractWon }.sumOf { it.score }
                                val roundsWon = playerGameRounds.count { it.contractWon }
                                val avgPointsWon =
                                    if (roundsWon > 0) pointsWon.toDouble() / roundsWon else 0.0

                                val pointsLost =
                                    playerGameRounds.filter { !it.contractWon }.sumOf { it.score }
                                val roundsLost = playerGameRounds.count { !it.contractWon }
                                val avgPointsLost =
                                    if (roundsLost > 0) abs(pointsLost.toDouble() / roundsLost) else 0.0

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    StatItem(
                                        label = stringResource(Res.string.tarot_stats_label_avg_won),
                                        value = avgPointsWon.format(0),
                                        modifier = Modifier.weight(1f),
                                        valueColor = MaterialTheme.colorScheme.secondary
                                    )
                                    StatItem(
                                        label = stringResource(Res.string.tarot_stats_label_avg_lost),
                                        value = avgPointsLost.format(0),
                                        modifier = Modifier.weight(1f),
                                        valueColor = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    // Called player statistics - outside playerAllRounds check so it shows for called-only players
                    if (calledRounds > 0) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                stringResource(Res.string.tarot_stats_section_called_performance),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        MaterialTheme.shapes.small
                                    )
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )

                            // Called win/loss statistics
                            val calledGameRounds =
                                allRounds.filter { it.calledPlayerId == player.id }
                                    .mapNotNull { tarotRound ->
                                        rounds.find { it.roundNumber == tarotRound.roundNumber }
                                    }

                            if (calledGameRounds.isNotEmpty()) {
                                val calledPointsWon =
                                    calledGameRounds.filter { it.contractWon }.sumOf { it.score }
                                val calledRoundsWon = calledGameRounds.count { it.contractWon }
                                val avgCalledPointsWon =
                                    if (calledRoundsWon > 0) calledPointsWon.toDouble() / calledRoundsWon else 0.0

                                val calledPointsLost =
                                    calledGameRounds.filter { !it.contractWon }.sumOf { it.score }
                                val calledRoundsLost = calledGameRounds.count { !it.contractWon }
                                val avgCalledPointsLost =
                                    if (calledRoundsLost > 0) abs(calledPointsLost.toDouble() / calledRoundsLost) else 0.0

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    StatItem(
                                        label = stringResource(Res.string.tarot_stats_label_avg_won),
                                        value = avgCalledPointsWon.roundToInt().toString(),
                                        modifier = Modifier.weight(1f),
                                        valueColor = MaterialTheme.colorScheme.secondary
                                    )
                                    StatItem(
                                        label = stringResource(Res.string.tarot_stats_label_avg_lost),
                                        value = avgCalledPointsLost.roundToInt().toString(),
                                        modifier = Modifier.weight(1f),
                                        valueColor = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Stat item for player stats
 */
@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color? = null
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
            fontWeight = FontWeight.Bold,
            color = valueColor ?: MaterialTheme.colorScheme.onSurfaceVariant
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
