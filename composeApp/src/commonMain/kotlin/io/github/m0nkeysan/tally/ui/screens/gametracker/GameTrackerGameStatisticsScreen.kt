package io.github.m0nkeysan.tally.ui.screens.gametracker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.GameIcons
import io.github.m0nkeysan.tally.core.domain.model.ScoringLogic
import io.github.m0nkeysan.tally.core.model.PlayerRoundStats
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.action_back
import io.github.m0nkeysan.tally.generated.resources.cd_toggle_collapse
import io.github.m0nkeysan.tally.generated.resources.cd_toggle_expand
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_avg_per_round
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_current_leader
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_graph_title
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_highest_round
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_lead_changes
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_loading
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_lowest_round
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_no_rounds
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_player_count
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_quick_stats
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_rounds_played
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_score_distribution
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_title
import io.github.m0nkeysan.tally.ui.components.EmptyState
import io.github.m0nkeysan.tally.ui.components.LoadingState
import io.github.m0nkeysan.tally.ui.components.PlayerAvatar
import io.github.m0nkeysan.tally.ui.components.ProgressLineChart
import io.github.m0nkeysan.tally.ui.components.ScoreDistributionChart
import io.github.m0nkeysan.tally.ui.components.SectionHeader
import io.github.m0nkeysan.tally.ui.components.StatRow
import io.github.m0nkeysan.tally.ui.components.StreakBadge
import io.github.m0nkeysan.tally.ui.utils.formatAverage
import io.github.m0nkeysan.tally.ui.utils.medalEmoji
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameTrackerGameStatisticsScreen(
    gameId: String,
    onBack: () -> Unit,
    viewModel: GameTrackerGameStatisticsViewModel = viewModel { GameTrackerGameStatisticsViewModel() }
) {
    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Collapsible states
    var graphExpanded by remember { mutableStateOf(true) }
    var quickStatsExpanded by remember { mutableStateOf(true) }
    var playersExpanded by remember { mutableStateOf(true) }
    var expandedPlayerId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(gameId) {
        viewModel.loadGameStats(gameId)
    }

    // Pre-compute medal map
    val medalMap: Map<String, Int> = remember(stats) {
        stats?.let { s ->
            s.playerStats
                .sortedWith(compareByDescending<PlayerRoundStats> { 
                    if (s.scoringLogic == ScoringLogic.HIGH_SCORE_WINS) it.totalScore else -it.totalScore
                })
                .take(3)
                .mapIndexed { index, pStats -> pStats.player.id to (index + 1) }
                .toMap()
        } ?: emptyMap()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(Res.string.game_tracker_game_stats_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        stats?.let {
                            Text(
                                text = it.gameName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = GameIcons.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                },
                modifier = Modifier.shadow(elevation = 2.dp)
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            LoadingState(
                modifier = Modifier.padding(paddingValues),
                message = stringResource(Res.string.game_tracker_game_stats_loading)
            )
        } else if (stats == null || stats?.roundsPlayed == 0) {
            EmptyState(
                modifier = Modifier.padding(paddingValues),
                message = stringResource(Res.string.game_tracker_game_stats_no_rounds)
            )
        } else {
            stats?.let { gameStats ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Progress Graph Section
                    item {
                        SectionHeader(
                            title = stringResource(Res.string.game_tracker_game_stats_graph_title),
                            isExpanded = graphExpanded,
                            onToggle = { graphExpanded = !graphExpanded }
                        )
                    }

                    item {
                        AnimatedVisibility(
                            visible = graphExpanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            ) {
                                ProgressLineChart(
                                    progressData = gameStats.progressData,
                                    players = gameStats.playerStats.map { it.player },
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }

                    // Quick Stats Section
                    item {
                        SectionHeader(
                            title = stringResource(Res.string.game_tracker_game_stats_quick_stats),
                            isExpanded = quickStatsExpanded,
                            onToggle = { quickStatsExpanded = !quickStatsExpanded }
                        )
                    }

                    item {
                        AnimatedVisibility(
                            visible = quickStatsExpanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            QuickStatsCard(
                                roundsPlayed = gameStats.roundsPlayed,
                                currentLeader = gameStats.playerStats.find { it.player.id == gameStats.currentLeader }?.player?.name,
                                leadChanges = gameStats.leadChanges
                            )
                        }
                    }

                    // Player Statistics Section
                    item {
                        SectionHeader(
                            title = stringResource(
                                Res.string.game_tracker_stats_player_count,
                                gameStats.playerStats.size
                            ),
                            isExpanded = playersExpanded,
                            onToggle = {
                                playersExpanded = !playersExpanded
                                if (!playersExpanded) expandedPlayerId = null
                            }
                        )
                    }

                    if (playersExpanded) {
                        items(gameStats.playerStats.sortedWith(compareByDescending<PlayerRoundStats> { 
                            if (gameStats.scoringLogic == ScoringLogic.HIGH_SCORE_WINS) it.totalScore else -it.totalScore
                        })) { playerStats ->
                            val isExpanded = expandedPlayerId == playerStats.player.id
                            PlayerStatsCard(
                                playerStats = playerStats,
                                rank = medalMap[playerStats.player.id],
                                isExpanded = isExpanded,
                                onToggle = {
                                    expandedPlayerId = if (isExpanded) null else playerStats.player.id
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickStatsCard(
    roundsPlayed: Int,
    currentLeader: String?,
    leadChanges: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatRow(
                label = stringResource(Res.string.game_tracker_game_stats_rounds_played),
                value = roundsPlayed.toString()
            )
            StatRow(
                label = stringResource(Res.string.game_tracker_game_stats_current_leader),
                value = currentLeader ?: "—"
            )
            StatRow(
                label = stringResource(Res.string.game_tracker_game_stats_lead_changes),
                value = leadChanges.toString()
            )
        }
    }
}

@Composable
private fun PlayerStatsCard(
    playerStats: PlayerRoundStats,
    rank: Int?,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded)
                MaterialTheme.colorScheme.surfaceContainerHighest
            else
                MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Medal emoji (only for top 3)
                    if (rank != null && rank <= 3) {
                        Text(
                            text = medalEmoji(rank),
                            style = MaterialTheme.typography.titleSmall
                        )
                    } else {
                        // keep alignment consistent
                        Spacer(modifier = Modifier.width(24.dp))
                    }

                    PlayerAvatar(
                        name = playerStats.player.name,
                        avatarColorHex = playerStats.player.avatarColor,
                        size = 36.dp
                    )

                    Text(
                        text = playerStats.player.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Right side: score + streak + expand icon
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = playerStats.totalScore.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    StreakBadge(streak = playerStats.currentStreak)
                    Icon(
                        imageVector = if (isExpanded) GameIcons.ExpandLess else GameIcons.ExpandMore,
                        contentDescription = stringResource(
                            if (isExpanded) Res.string.cd_toggle_collapse
                            else Res.string.cd_toggle_expand
                        ),
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Expanded content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(durationMillis = 200)),
                exit = shrinkVertically(tween(durationMillis = 200))
            ) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Player statistics
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        StatRow(
                            label = stringResource(Res.string.game_tracker_game_stats_avg_per_round),
                            value = formatAverage(playerStats.averageScorePerRound)
                        )
                        if (playerStats.highestRoundScore != null) {
                            StatRow(
                                label = stringResource(Res.string.game_tracker_game_stats_highest_round),
                                value = playerStats.highestRoundScore.toString()
                            )
                        }
                        if (playerStats.lowestRoundScore != null) {
                            StatRow(
                                label = stringResource(Res.string.game_tracker_game_stats_lowest_round),
                                value = playerStats.lowestRoundScore.toString()
                            )
                        }
                    }

                    // Score distribution
                    if (playerStats.scoreDistribution.total() > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(Res.string.game_tracker_game_stats_score_distribution),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ScoreDistributionChart(
                            distribution = playerStats.scoreDistribution
                        )
                    }
                }
            }
        }
    }
}
