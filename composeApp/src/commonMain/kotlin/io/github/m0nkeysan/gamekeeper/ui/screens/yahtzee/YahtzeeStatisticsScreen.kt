package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeePlayerStatistics
import io.github.m0nkeysan.gamekeeper.core.model.getLocalizedName
import io.github.m0nkeysan.gamekeeper.generated.resources.Res
import io.github.m0nkeysan.gamekeeper.generated.resources.action_back
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_format_active_player
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_format_category_avg
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_format_high_score
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_format_luckiest_player
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_format_most_yahtzees
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_format_player_count
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_format_winner_score
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_format_wins
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_format_yahtzee_rate
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_rank_first
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_rank_format
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_rank_second
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_rank_third
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_average_score
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_bonus_rate
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_finished_games
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_average_score
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_avg_players
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_best_avg
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_category
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_dice_rolls
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_finished
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_fun_facts
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_high_score
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_leaderboards
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_least_scored
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_luckiest
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_most_active
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_most_consistent
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_most_scored
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_most_yahtzees_game
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_overall
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_points_scored
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_total_players
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_total_yahtzees
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_upper_bonus
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_global_yahtzee_rate
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_leaderboard_highest_scores
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_leaderboard_most_wins
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_leaderboard_most_yahtzees
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_lower_section
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_no_data
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_overall_performance
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_personal_best
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_recent_games
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_score_box
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_select_player
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_title
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_total_games
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_total_yahtzees
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_upper_section
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_wins
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_stats_yahtzee_rate
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.components.CategoryHeatmap
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.components.GameSummaryRow
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.components.GlobalCategoryHeatmap
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.components.StatisticRow
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YahtzeeStatisticsScreen(
    onBack: () -> Unit,
    viewModel: YahtzeeStatisticsViewModel = viewModel {
        YahtzeeStatisticsViewModel(
            statsRepository = PlatformRepositories.getYahtzeeStatisticsRepository()
        )
    }
) {
    val state by viewModel.uiState.collectAsState()
    var showPlayerDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(Res.string.yahtzee_stats_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
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
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Player selector dropdown below top bar
            if (state.availablePlayers.isNotEmpty()) {
                PlayerSelectorDropdown(
                    players = state.availablePlayers,
                    selectedPlayerId = state.selectedPlayerId,
                    onPlayerSelect = { playerId ->
                        viewModel.selectPlayer(playerId)
                        showPlayerDropdown = false
                    },
                    isDropdownOpen = showPlayerDropdown,
                    onDropdownOpenChange = { showPlayerDropdown = it }
                )
            }

            // Content area
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = state.error!!,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                state.selectedPlayerId == YahtzeeStatisticsViewModel.GLOBAL_ID && state.globalStatistics != null -> {
                    GlobalStatisticsContent(statistics = state.globalStatistics!!)
                }

                state.statistics != null -> {
                    StatisticsContent(
                        statistics = state.statistics!!
                    )
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(Res.string.yahtzee_stats_no_data))
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerSelectorDropdown(
    players: List<io.github.m0nkeysan.gamekeeper.core.model.Player>,
    selectedPlayerId: String?,
    onPlayerSelect: (String) -> Unit,
    isDropdownOpen: Boolean,
    onDropdownOpenChange: (Boolean) -> Unit
) {
    val selectedPlayer = players.find { it.id == selectedPlayerId }
    val displayName = when (selectedPlayerId) {
        YahtzeeStatisticsViewModel.GLOBAL_ID -> stringResource(Res.string.yahtzee_stats_global)
        else -> selectedPlayer?.name ?: stringResource(Res.string.yahtzee_stats_select_player)
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Button(
                    onClick = { onDropdownOpenChange(!isDropdownOpen) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    Text(
                        text = displayName,
                        modifier = Modifier
                            .weight(1f),
                        textAlign = TextAlign.Start
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                DropdownMenu(
                    expanded = isDropdownOpen,
                    onDismissRequest = { onDropdownOpenChange(false) },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    // Global option at top
                    DropdownMenuItem(
                        text = { 
                            Text(
                                stringResource(Res.string.yahtzee_stats_global),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        onClick = {
                            onPlayerSelect(YahtzeeStatisticsViewModel.GLOBAL_ID)
                            onDropdownOpenChange(false)
                        }
                    )
                    
                    // Divider
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    
                    // Individual players
                    players.forEach { player ->
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    player.name
                                )
                            },
                            onClick = {
                                onPlayerSelect(player.id)
                                onDropdownOpenChange(false)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticsContent(
    statistics: YahtzeePlayerStatistics
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Overall Performance Card
        item {
            OverallPerformanceCard(statistics = statistics)
        }

        // Score Box Analysis Card
        item {
            ScoreBoxAnalysisCard(statistics = statistics)
        }

        // Recent Games Card
        if (statistics.recentGames.isNotEmpty()) {
            item {
                RecentGamesCard(games = statistics.recentGames)
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun OverallPerformanceCard(statistics: YahtzeePlayerStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.yahtzee_stats_overall_performance),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            StatisticRow(
                label = stringResource(Res.string.yahtzee_stats_total_games),
                value = statistics.totalGames.toString()
            )

            StatisticRow(
                label = stringResource(Res.string.yahtzee_stats_finished_games),
                value = statistics.finishedGames.toString()
            )

            StatisticRow(
                label = stringResource(Res.string.yahtzee_stats_wins),
                value = stringResource(Res.string.yahtzee_format_wins, statistics.wins, formatPercentage(statistics.winRate)),
                valueColor = GameColors.Success
            )

            StatisticRow(
                label = stringResource(Res.string.yahtzee_stats_average_score),
                value = formatAverage(statistics.averageScore),
                valueColor = GameColors.Primary
            )

            StatisticRow(
                label = stringResource(Res.string.yahtzee_stats_personal_best),
                value = statistics.highScore.toString(),
                valueColor = GameColors.Success
            )

            StatisticRow(
                label = stringResource(Res.string.yahtzee_stats_total_yahtzees),
                value = statistics.totalYahtzees.toString(),
                valueColor = GameColors.TrophyGold
            )

            StatisticRow(
                label = stringResource(Res.string.yahtzee_stats_yahtzee_rate),
                value = stringResource(Res.string.yahtzee_format_yahtzee_rate, formatAverage(statistics.yahtzeeRate)),
                valueColor = GameColors.TrophyGold
            )
        }
    }
}

@Composable
private fun ScoreBoxAnalysisCard(statistics: YahtzeePlayerStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.yahtzee_stats_score_box),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            CategoryHeatmap(categoryStats = statistics.categoryStats)

            Spacer(modifier = Modifier.height(8.dp))

            // Upper/Lower section stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.yahtzee_stats_upper_section),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatAverage(statistics.upperSectionAverage),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = GameColors.Primary
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.yahtzee_stats_lower_section),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatAverage(statistics.lowerSectionAverage),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = GameColors.Primary
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.yahtzee_stats_bonus_rate),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatPercentage(statistics.upperBonusRate),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = GameColors.Success
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentGamesCard(games: List<io.github.m0nkeysan.gamekeeper.core.model.GameSummary>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.yahtzee_stats_recent_games),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            games.forEach { game ->
                GameSummaryRow(game = game)
            }
        }
    }
}

// ============================================================================
// GLOBAL STATISTICS COMPOSABLES
// ============================================================================

@Composable
private fun GlobalStatisticsContent(
    statistics: io.github.m0nkeysan.gamekeeper.core.model.YahtzeeGlobalStatistics
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Overall Performance Card
        item {
            GlobalOverviewCard(statistics = statistics)
        }

        // Leaderboard Card
        item {
            GlobalLeaderboardCard(statistics = statistics)
        }

        // Category Analysis Card
        item {
            GlobalCategoryAnalysisCard(statistics = statistics)
        }

        // Recent Games Card
        if (statistics.recentGames.isNotEmpty()) {
            item {
                GlobalRecentGamesCard(games = statistics.recentGames)
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GlobalOverviewCard(statistics: io.github.m0nkeysan.gamekeeper.core.model.YahtzeeGlobalStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.yahtzee_stats_global_overall),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            StatisticRow(stringResource(Res.string.yahtzee_stats_total_games), statistics.totalGames.toString())
            StatisticRow(stringResource(Res.string.yahtzee_stats_global_finished), statistics.finishedGames.toString())
            StatisticRow(stringResource(Res.string.yahtzee_stats_global_total_players), statistics.totalPlayers.toString())
            
            if (statistics.mostActivePlayer != null) {
                StatisticRow(
                    stringResource(Res.string.yahtzee_stats_global_most_active),
                    stringResource(Res.string.yahtzee_format_active_player, statistics.mostActivePlayer.playerName, statistics.mostActivePlayer.gamesPlayed)
                )
            }
            
            HorizontalDivider()
            
            if (statistics.allTimeHighScore != null) {
                StatisticRow(
                    stringResource(Res.string.yahtzee_stats_global_high_score),
                    stringResource(Res.string.yahtzee_format_high_score, statistics.allTimeHighScore.score, statistics.allTimeHighScore.playerName),
                    valueColor = GameColors.TrophyGold
                )
            }
            
            StatisticRow(
                stringResource(Res.string.yahtzee_stats_global_average_score),
                formatAverage(statistics.averageScore),
                valueColor = GameColors.Primary
            )
            
            StatisticRow(
                stringResource(Res.string.yahtzee_stats_global_total_yahtzees),
                statistics.totalYahtzees.toString(),
                valueColor = GameColors.Success
            )
            
            StatisticRow(
                stringResource(Res.string.yahtzee_stats_global_yahtzee_rate),
                stringResource(Res.string.yahtzee_format_yahtzee_rate, formatAverage(statistics.yahtzeeRate))
            )
            
            if (statistics.mostYahtzeesInGame != null && statistics.mostYahtzeesInGame.count > 0) {
                StatisticRow(
                    stringResource(Res.string.yahtzee_stats_global_most_yahtzees_game),
                    stringResource(Res.string.yahtzee_format_most_yahtzees, statistics.mostYahtzeesInGame.count, statistics.mostYahtzeesInGame.playerName),
                    valueColor = GameColors.TrophyGold
                )
            }
            
            StatisticRow(stringResource(Res.string.yahtzee_stats_global_upper_bonus), formatPercentage(statistics.upperBonusRate))
            
            HorizontalDivider()
            
            Text(
                text = stringResource(Res.string.yahtzee_stats_global_fun_facts),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            StatisticRow(stringResource(Res.string.yahtzee_stats_global_dice_rolls), statistics.estimatedDiceRolls.toString())
            StatisticRow(stringResource(Res.string.yahtzee_stats_global_points_scored), statistics.totalPointsScored.toString())
            StatisticRow(stringResource(Res.string.yahtzee_stats_global_avg_players), formatAverage(statistics.averagePlayersPerGame))
            
            if (statistics.luckiestPlayer != null) {
                StatisticRow(
                    stringResource(Res.string.yahtzee_stats_global_luckiest),
                    stringResource(Res.string.yahtzee_format_luckiest_player, statistics.luckiestPlayer.playerName, formatAverage(statistics.luckiestPlayer.metric))
                )
            }
            
            if (statistics.mostConsistentPlayer != null) {
                StatisticRow(stringResource(Res.string.yahtzee_stats_global_most_consistent), statistics.mostConsistentPlayer.playerName)
            }
        }
    }
}

@Composable
private fun GlobalLeaderboardCard(statistics: io.github.m0nkeysan.gamekeeper.core.model.YahtzeeGlobalStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.yahtzee_stats_global_leaderboards),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Most Wins
            Text(
                text = stringResource(Res.string.yahtzee_stats_leaderboard_most_wins),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            )
            statistics.topPlayersByWins.forEach { entry ->
                GlobalLeaderboardRow(entry)
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            
            // Highest Scores
            Text(
                text = stringResource(Res.string.yahtzee_stats_leaderboard_highest_scores),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            statistics.topPlayersByScore.forEach { entry ->
                GlobalLeaderboardRow(entry)
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            
            // Most Yahtzees
            Text(
                text = stringResource(Res.string.yahtzee_stats_leaderboard_most_yahtzees),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            statistics.topPlayersByYahtzees.forEach { entry ->
                GlobalLeaderboardRow(entry)
            }
        }
    }
}

@Composable
private fun GlobalLeaderboardRow(entry: io.github.m0nkeysan.gamekeeper.core.model.LeaderboardEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            val medal = when (entry.rank) {
                1 -> stringResource(Res.string.yahtzee_rank_first)
                2 -> stringResource(Res.string.yahtzee_rank_second)
                3 -> stringResource(Res.string.yahtzee_rank_third)
                else -> stringResource(Res.string.yahtzee_rank_format, entry.rank)
            }
            Text(
                text = medal,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            Text(
                text = entry.playerName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
        
        Text(
            text = entry.value.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = GameColors.Primary
        )
    }
}

@Composable
private fun GlobalCategoryAnalysisCard(statistics: io.github.m0nkeysan.gamekeeper.core.model.YahtzeeGlobalStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.yahtzee_stats_global_category),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            GlobalCategoryHeatmap(categoryStats = statistics.categoryStats)

            Spacer(modifier = Modifier.height(12.dp))

            if (statistics.mostScoredCategory != null) {
                StatisticRow(
                    stringResource(Res.string.yahtzee_stats_global_most_scored),
                    statistics.mostScoredCategory.getLocalizedName()
                )
            }
            
            if (statistics.leastScoredCategory != null) {
                StatisticRow(
                    stringResource(Res.string.yahtzee_stats_global_least_scored),
                    statistics.leastScoredCategory.getLocalizedName()
                )
            }
            
            if (statistics.highestCategoryAverage != null) {
                StatisticRow(
                    stringResource(Res.string.yahtzee_stats_global_best_avg),
                    stringResource(Res.string.yahtzee_format_category_avg, statistics.highestCategoryAverage.category.getLocalizedName(), formatAverage(statistics.highestCategoryAverage.average))
                )
            }
        }
    }
}

@Composable
private fun GlobalRecentGamesCard(games: List<io.github.m0nkeysan.gamekeeper.core.model.GlobalGameSummary>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.yahtzee_stats_recent_games),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            games.forEach { game ->
                GlobalGameSummaryRow(game = game)
            }
        }
    }
}

@Composable
private fun GlobalGameSummaryRow(game: io.github.m0nkeysan.gamekeeper.core.model.GlobalGameSummary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = game.gameName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stringResource(Res.string.yahtzee_format_winner_score, game.winnerName),
                style = MaterialTheme.typography.bodySmall,
                color = GameColors.Success
            )
            Text(
                text = stringResource(Res.string.yahtzee_format_player_count, game.playerCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = game.winnerScore.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = GameColors.Primary
        )
    }
}
