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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.core.model.GameTrackerPlayerStatistics
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.action_back
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_active_games
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_average_score
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_avg_rounds_per_game
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_completed_games
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_games_played
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_games_won
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_global_title
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_highest_score
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_loading
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_lowest_score
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_no_data
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_player_stats
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_subtitle
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_title
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_total_games
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_total_rounds
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_win_rate
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameTrackerStatisticsScreen(
    onBack: () -> Unit,
    viewModel: GameTrackerStatisticsViewModel = viewModel { GameTrackerStatisticsViewModel() }
) {
    val statistics by viewModel.statistics.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(Res.string.game_tracker_stats_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(Res.string.game_tracker_stats_subtitle),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                CircularProgressIndicator()
                Text(
                    text = stringResource(Res.string.game_tracker_stats_loading),
                    modifier = Modifier.padding(top = 64.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else if (statistics.totalGames == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.game_tracker_stats_no_data),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Global Statistics Section
                item {
                    Text(
                        text = stringResource(Res.string.game_tracker_stats_global_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    GlobalStatisticsCard(
                        totalGames = statistics.totalGames,
                        completedGames = statistics.completedGames,
                        activeGames = statistics.activeGames,
                        totalRounds = statistics.totalRounds,
                        averageRoundsPerGame = statistics.averageRoundsPerGame
                    )
                }

                // Player Statistics Section
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(Res.string.game_tracker_stats_player_stats),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(statistics.playerStatistics) { playerStats ->
                    PlayerStatisticsCard(playerStats = playerStats)
                }
            }
        }
    }
}

@Composable
private fun GlobalStatisticsCard(
    totalGames: Int,
    completedGames: Int,
    activeGames: Int,
    totalRounds: Int,
    averageRoundsPerGame: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatRow(
                label = stringResource(Res.string.game_tracker_stats_total_games),
                value = totalGames.toString()
            )
            StatRow(
                label = stringResource(Res.string.game_tracker_stats_completed_games),
                value = completedGames.toString()
            )
            StatRow(
                label = stringResource(Res.string.game_tracker_stats_active_games),
                value = activeGames.toString()
            )
            StatRow(
                label = stringResource(Res.string.game_tracker_stats_total_rounds),
                value = totalRounds.toString()
            )
            StatRow(
                label = stringResource(Res.string.game_tracker_stats_avg_rounds_per_game),
                value = String.format("%.1f", averageRoundsPerGame)
            )
        }
    }
}

@Composable
private fun PlayerStatisticsCard(playerStats: GameTrackerPlayerStatistics) {
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
                            color = Color(android.graphics.Color.parseColor(playerStats.player.avatarColor)),
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

                Column {
                    Text(
                        text = playerStats.player.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = playerStats.getWinRatePercentage(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Player statistics
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StatRow(
                    label = stringResource(Res.string.game_tracker_stats_games_played),
                    value = playerStats.gamesPlayed.toString()
                )
                StatRow(
                    label = stringResource(Res.string.game_tracker_stats_games_won),
                    value = playerStats.gamesWon.toString()
                )
                StatRow(
                    label = stringResource(Res.string.game_tracker_stats_win_rate),
                    value = playerStats.getWinRatePercentage()
                )
                StatRow(
                    label = stringResource(Res.string.game_tracker_stats_average_score),
                    value = String.format("%.1f", playerStats.averageScore)
                )
                if (playerStats.highestGameScore != null) {
                    StatRow(
                        label = stringResource(Res.string.game_tracker_stats_highest_score),
                        value = playerStats.highestGameScore.toString()
                    )
                }
                if (playerStats.lowestGameScore != null) {
                    StatRow(
                        label = stringResource(Res.string.game_tracker_stats_lowest_score),
                        value = playerStats.lowestGameScore.toString()
                    )
                }
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
