package io.github.m0nkeysan.tally.ui.screens.gametracker

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.core.model.PlayerRoundStats
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.action_back
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_avg_per_round
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_current_leader
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_current_streak
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_graph_title
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_highest_round
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_lead_changes
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_loading
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_lowest_round
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_no_rounds
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_player_stats
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_quick_stats
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_rounds_played
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_score_distribution
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_title
import io.github.m0nkeysan.tally.ui.components.ProgressLineChart
import io.github.m0nkeysan.tally.ui.components.ScoreDistributionChart
import io.github.m0nkeysan.tally.ui.components.StreakBadge
import io.github.m0nkeysan.tally.ui.utils.parseColor
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameTrackerGameStatisticsScreen(
    gameId: String,
    onBack: () -> Unit,
    viewModel: GameTrackerGameStatisticsViewModel = viewModel { GameTrackerGameStatisticsViewModel() }
) {
    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(gameId) {
        viewModel.loadGameStats(gameId)
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                },
                modifier = Modifier.shadow(elevation = 2.dp)
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(Res.string.game_tracker_game_stats_loading),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else if (stats == null || stats?.roundsPlayed == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.game_tracker_game_stats_no_rounds),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            stats?.let { gameStats ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Progress Graph Section
                    item {
                        Text(
                            text = stringResource(Res.string.game_tracker_game_stats_graph_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                            )
                        ) {
                            ProgressLineChart(
                                progressData = gameStats.progressData,
                                players = gameStats.playerStats.map { it.player },
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    // Quick Stats Section
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(Res.string.game_tracker_game_stats_quick_stats),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        QuickStatsCard(
                            roundsPlayed = gameStats.roundsPlayed,
                            currentLeader = gameStats.playerStats.find { it.player.id == gameStats.currentLeader }?.player?.name,
                            leadChanges = gameStats.leadChanges
                        )
                    }

                    // Player Statistics Section
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(Res.string.game_tracker_game_stats_player_stats),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(gameStats.playerStats) { playerStats ->
                        PlayerStatsCard(playerStats = playerStats)
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
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
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
private fun PlayerStatsCard(playerStats: PlayerRoundStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Player header with avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = parseColor(playerStats.player.avatarColor),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = playerStats.player.name.first().uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = playerStats.player.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(Res.string.game_tracker_game_stats_rounds_played) + ": ${playerStats.roundsPlayed}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Streak badge
                StreakBadge(streak = playerStats.currentStreak)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Player statistics
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StatRow(
                    label = stringResource(Res.string.game_tracker_game_stats_avg_per_round),
                    value = String.format("%.1f", playerStats.averageScorePerRound)
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

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
