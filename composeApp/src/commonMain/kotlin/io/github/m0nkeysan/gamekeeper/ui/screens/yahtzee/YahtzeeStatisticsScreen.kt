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
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.components.CategoryHeatmap
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.components.GameSummaryRow
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.components.GlobalCategoryHeatmap
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.components.StatisticRow
import io.github.m0nkeysan.gamekeeper.ui.strings.AppStrings
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

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
                            AppStrings.CD_SETTINGS,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = AppStrings.ACTION_BACK
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
                        Text(AppStrings.YAHTZEE_STATS_NO_DATA)
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
        YahtzeeStatisticsViewModel.GLOBAL_ID -> AppStrings.YAHTZEE_STATS_GLOBAL
        else -> selectedPlayer?.name ?: AppStrings.YAHTZEE_STATS_SELECT_PLAYER
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
                                AppStrings.YAHTZEE_STATS_GLOBAL,
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
                text = AppStrings.YAHTZEE_STATS_OVERALL_PERFORMANCE,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            StatisticRow(
                label = AppStrings.YAHTZEE_STATS_TOTAL_GAMES,
                value = statistics.totalGames.toString()
            )

            StatisticRow(
                label = AppStrings.YAHTZEE_STATS_FINISHED_GAMES,
                value = statistics.finishedGames.toString()
            )

            StatisticRow(
                label = AppStrings.YAHTZEE_STATS_WINS,
                value = String.format(AppStrings.YAHTZEE_FORMAT_WINS, statistics.wins, formatPercentage(statistics.winRate)),
                valueColor = GameColors.Success
            )

            StatisticRow(
                label = AppStrings.YAHTZEE_STATS_AVERAGE_SCORE,
                value = formatAverage(statistics.averageScore),
                valueColor = GameColors.Primary
            )

            StatisticRow(
                label = AppStrings.YAHTZEE_STATS_PERSONAL_BEST,
                value = statistics.highScore.toString(),
                valueColor = GameColors.Success
            )

            StatisticRow(
                label = AppStrings.YAHTZEE_STATS_TOTAL_YAHTZEES,
                value = statistics.totalYahtzees.toString(),
                valueColor = GameColors.TrophyGold
            )

            StatisticRow(
                label = AppStrings.YAHTZEE_STATS_YAHTZEE_RATE,
                value = String.format(AppStrings.YAHTZEE_FORMAT_YAHTZEE_RATE, formatAverage(statistics.yahtzeeRate)),
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
                text = AppStrings.YAHTZEE_STATS_SCORE_BOX,
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
                        text = AppStrings.YAHTZEE_STATS_UPPER_SECTION,
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
                        text = AppStrings.YAHTZEE_STATS_LOWER_SECTION,
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
                        text = AppStrings.YAHTZEE_STATS_BONUS_RATE,
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
                text = AppStrings.YAHTZEE_STATS_RECENT_GAMES,
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
                text = AppStrings.YAHTZEE_STATS_GLOBAL_OVERALL,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            StatisticRow(AppStrings.YAHTZEE_STATS_TOTAL_GAMES, statistics.totalGames.toString())
            StatisticRow(AppStrings.YAHTZEE_STATS_GLOBAL_FINISHED, statistics.finishedGames.toString())
            StatisticRow(AppStrings.YAHTZEE_STATS_GLOBAL_TOTAL_PLAYERS, statistics.totalPlayers.toString())
            
            if (statistics.mostActivePlayer != null) {
                StatisticRow(
                    AppStrings.YAHTZEE_STATS_GLOBAL_MOST_ACTIVE,
                    String.format(AppStrings.YAHTZEE_FORMAT_ACTIVE_PLAYER, statistics.mostActivePlayer.playerName, statistics.mostActivePlayer.gamesPlayed)
                )
            }
            
            HorizontalDivider()
            
            if (statistics.allTimeHighScore != null) {
                StatisticRow(
                    AppStrings.YAHTZEE_STATS_GLOBAL_HIGH_SCORE,
                    String.format(AppStrings.YAHTZEE_FORMAT_HIGH_SCORE, statistics.allTimeHighScore.score, statistics.allTimeHighScore.playerName),
                    valueColor = GameColors.TrophyGold
                )
            }
            
            StatisticRow(
                AppStrings.YAHTZEE_STATS_GLOBAL_AVERAGE_SCORE,
                formatAverage(statistics.averageScore),
                valueColor = GameColors.Primary
            )
            
            StatisticRow(
                AppStrings.YAHTZEE_STATS_GLOBAL_TOTAL_YAHTZEES,
                statistics.totalYahtzees.toString(),
                valueColor = GameColors.Success
            )
            
            StatisticRow(
                AppStrings.YAHTZEE_STATS_GLOBAL_YAHTZEE_RATE,
                String.format(AppStrings.YAHTZEE_FORMAT_YAHTZEE_RATE, formatAverage(statistics.yahtzeeRate))
            )
            
            if (statistics.mostYahtzeesInGame != null && statistics.mostYahtzeesInGame.count > 0) {
                StatisticRow(
                    AppStrings.YAHTZEE_STATS_GLOBAL_MOST_YAHTZEES_GAME,
                    String.format(AppStrings.YAHTZEE_FORMAT_MOST_YAHTZEES, statistics.mostYahtzeesInGame.count, statistics.mostYahtzeesInGame.playerName),
                    valueColor = GameColors.TrophyGold
                )
            }
            
            StatisticRow(AppStrings.YAHTZEE_STATS_GLOBAL_UPPER_BONUS, formatPercentage(statistics.upperBonusRate))
            
            HorizontalDivider()
            
            Text(
                text = AppStrings.YAHTZEE_STATS_GLOBAL_FUN_FACTS,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            StatisticRow(AppStrings.YAHTZEE_STATS_GLOBAL_DICE_ROLLS, statistics.estimatedDiceRolls.toString())
            StatisticRow(AppStrings.YAHTZEE_STATS_GLOBAL_POINTS_SCORED, statistics.totalPointsScored.toString())
            StatisticRow(AppStrings.YAHTZEE_STATS_GLOBAL_AVG_PLAYERS, formatAverage(statistics.averagePlayersPerGame))
            
            if (statistics.luckiestPlayer != null) {
                StatisticRow(
                    AppStrings.YAHTZEE_STATS_GLOBAL_LUCKIEST,
                    String.format(AppStrings.YAHTZEE_FORMAT_LUCKIEST_PLAYER, statistics.luckiestPlayer.playerName, formatAverage(statistics.luckiestPlayer.metric))
                )
            }
            
            if (statistics.mostConsistentPlayer != null) {
                StatisticRow(AppStrings.YAHTZEE_STATS_GLOBAL_MOST_CONSISTENT, statistics.mostConsistentPlayer.playerName)
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
                text = AppStrings.YAHTZEE_STATS_GLOBAL_LEADERBOARDS,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Most Wins
            Text(
                text = AppStrings.YAHTZEE_STATS_LEADERBOARD_MOST_WINS,
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
                text = AppStrings.YAHTZEE_STATS_LEADERBOARD_HIGHEST_SCORES,
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
                text = AppStrings.YAHTZEE_STATS_LEADERBOARD_MOST_YAHTZEES,
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
                1 -> AppStrings.YAHTZEE_RANK_FIRST
                2 -> AppStrings.YAHTZEE_RANK_SECOND
                3 -> AppStrings.YAHTZEE_RANK_THIRD
                else -> String.format(AppStrings.YAHTZEE_RANK_FORMAT, entry.rank)
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
                text = AppStrings.YAHTZEE_STATS_GLOBAL_CATEGORY,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            GlobalCategoryHeatmap(categoryStats = statistics.categoryStats)

            Spacer(modifier = Modifier.height(12.dp))

            if (statistics.mostScoredCategory != null) {
                StatisticRow(
                    AppStrings.YAHTZEE_STATS_GLOBAL_MOST_SCORED,
                    statistics.mostScoredCategory.displayName
                )
            }
            
            if (statistics.leastScoredCategory != null) {
                StatisticRow(
                    AppStrings.YAHTZEE_STATS_GLOBAL_LEAST_SCORED,
                    statistics.leastScoredCategory.displayName
                )
            }
            
            if (statistics.highestCategoryAverage != null) {
                StatisticRow(
                    AppStrings.YAHTZEE_STATS_GLOBAL_BEST_AVG,
                    String.format(AppStrings.YAHTZEE_FORMAT_CATEGORY_AVG, statistics.highestCategoryAverage.category.displayName, formatAverage(statistics.highestCategoryAverage.average))
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
                text = AppStrings.YAHTZEE_STATS_RECENT_GAMES,
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
                text = String.format(AppStrings.YAHTZEE_FORMAT_WINNER_SCORE, game.winnerName),
                style = MaterialTheme.typography.bodySmall,
                color = GameColors.Success
            )
            Text(
                text = String.format(AppStrings.YAHTZEE_FORMAT_PLAYER_COUNT, game.playerCount),
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
