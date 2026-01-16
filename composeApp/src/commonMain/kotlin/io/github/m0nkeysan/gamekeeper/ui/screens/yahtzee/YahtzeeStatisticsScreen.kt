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
                            "Statistics",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
                        Text("No data available")
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
        YahtzeeStatisticsViewModel.GLOBAL_ID -> "⭐ Global Statistics"
        else -> selectedPlayer?.name ?: "Select Player"
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
                                "⭐ Global Statistics",
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
                text = "📊 Overall Performance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            StatisticRow(
                label = "Total Games",
                value = statistics.totalGames.toString()
            )

            StatisticRow(
                label = "Finished Games",
                value = statistics.finishedGames.toString()
            )

            StatisticRow(
                label = "Wins",
                value = "${statistics.wins} (${formatPercentage(statistics.winRate)})",
                valueColor = GameColors.Success
            )

            StatisticRow(
                label = "Average Score",
                value = formatAverage(statistics.averageScore),
                valueColor = GameColors.Primary
            )

            StatisticRow(
                label = "Personal Best",
                value = statistics.highScore.toString(),
                valueColor = GameColors.Success
            )

            StatisticRow(
                label = "Total Yahtzees",
                value = statistics.totalYahtzees.toString(),
                valueColor = GameColors.TrophyGold
            )

            StatisticRow(
                label = "Yahtzee Rate",
                value = formatAverage(statistics.yahtzeeRate) + " per game",
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
                text = "📈 Score Box Performance",
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
                        text = "Upper Section",
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
                        text = "Lower Section",
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
                        text = "Bonus Rate",
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
                text = "🎮 Recent Games",
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
                text = "📊 Overall Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            StatisticRow("Total Games", statistics.totalGames.toString())
            StatisticRow("Finished Games", statistics.finishedGames.toString())
            StatisticRow("Total Players", statistics.totalPlayers.toString())
            
            if (statistics.mostActivePlayer != null) {
                StatisticRow(
                    "Most Active Player",
                    "${statistics.mostActivePlayer.playerName} (${statistics.mostActivePlayer.gamesPlayed} games)"
                )
            }
            
            HorizontalDivider()
            
            if (statistics.allTimeHighScore != null) {
                StatisticRow(
                    "All-Time High Score",
                    "${statistics.allTimeHighScore.score} by ${statistics.allTimeHighScore.playerName}",
                    valueColor = GameColors.TrophyGold
                )
            }
            
            StatisticRow(
                "Average Score",
                formatAverage(statistics.averageScore),
                valueColor = GameColors.Primary
            )
            
            StatisticRow(
                "Total Yahtzees",
                statistics.totalYahtzees.toString(),
                valueColor = GameColors.Success
            )
            
            StatisticRow(
                "Yahtzee Rate",
                "${formatAverage(statistics.yahtzeeRate)} per game"
            )
            
            if (statistics.mostYahtzeesInGame != null && statistics.mostYahtzeesInGame.count > 0) {
                StatisticRow(
                    "Most Yahtzees (Single Game)",
                    "${statistics.mostYahtzeesInGame.count} by ${statistics.mostYahtzeesInGame.playerName}",
                    valueColor = GameColors.TrophyGold
                )
            }
            
            StatisticRow("Upper Bonus Rate", formatPercentage(statistics.upperBonusRate))
            
            HorizontalDivider()
            
            Text(
                text = "🎲 Fun Facts",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            StatisticRow("Estimated Dice Rolls", statistics.estimatedDiceRolls.toString())
            StatisticRow("Total Points Scored", statistics.totalPointsScored.toString())
            StatisticRow("Avg Players/Game", formatAverage(statistics.averagePlayersPerGame))
            
            if (statistics.luckiestPlayer != null) {
                StatisticRow(
                    "Luckiest Player",
                    "${statistics.luckiestPlayer.playerName} (${formatAverage(statistics.luckiestPlayer.metric)}/game)"
                )
            }
            
            if (statistics.mostConsistentPlayer != null) {
                StatisticRow("Most Consistent", statistics.mostConsistentPlayer.playerName)
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
                text = "🏆 Leaderboards",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Most Wins
            Text(
                text = "Most Wins",
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
                text = "Highest Scores",
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
                text = "Most Yahtzees",
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
                1 -> "🥇"
                2 -> "🥈"
                3 -> "🥉"
                else -> "${entry.rank}."
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
                text = "📈 Category Performance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            GlobalCategoryHeatmap(categoryStats = statistics.categoryStats)

            Spacer(modifier = Modifier.height(12.dp))

            if (statistics.mostScoredCategory != null) {
                StatisticRow(
                    "Most Scored Category",
                    statistics.mostScoredCategory.displayName
                )
            }
            
            if (statistics.leastScoredCategory != null) {
                StatisticRow(
                    "Least Scored Category",
                    statistics.leastScoredCategory.displayName
                )
            }
            
            if (statistics.highestCategoryAverage != null) {
                StatisticRow(
                    "Best Average Category",
                    "${statistics.highestCategoryAverage.category.displayName} (${formatAverage(statistics.highestCategoryAverage.average)})"
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
                text = "🎮 Recent Games",
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
                text = "Winner: ${game.winnerName}",
                style = MaterialTheme.typography.bodySmall,
                color = GameColors.Success
            )
            Text(
                text = "${game.playerCount} players",
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
