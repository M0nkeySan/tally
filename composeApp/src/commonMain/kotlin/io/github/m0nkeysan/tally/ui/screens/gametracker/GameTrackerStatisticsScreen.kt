package io.github.m0nkeysan.tally.ui.screens.gametracker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.core.model.GameTrackerLeaderboards
import io.github.m0nkeysan.tally.core.model.GameTrackerPlayerStatistics
import io.github.m0nkeysan.tally.core.model.GameTrackerRecords
import io.github.m0nkeysan.tally.core.model.GameTrackerLeaderboardEntry
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.action_back
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_active_games
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_average_score
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_avg_rounds_per_game
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_cd_collapse_player
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_cd_collapse_section
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_cd_expand_player
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_cd_expand_section
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_completed_games
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_games_played
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_games_won
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_global_title
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_highest_score
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_leaderboard_empty
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_leaderboard_most_games
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_leaderboard_win_rate
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_leaderboard_win_rate_min
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_leaderboards_title
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_loading
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_lowest_score
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_no_data
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_player_count
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_record_by_in
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_record_highest_score
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_record_longest_game
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_record_lowest_score
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_record_rounds
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_record_shortest_game
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_records_empty
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_records_title
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_subtitle
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_title
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_total_games
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_total_rounds
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_total_rounds_played
import io.github.m0nkeysan.tally.generated.resources.game_tracker_stats_win_rate
import io.github.m0nkeysan.tally.ui.utils.formatAverage
import io.github.m0nkeysan.tally.ui.utils.parseColor
import org.jetbrains.compose.resources.stringResource

// ---------------------------------------------------------------------------
// Auto-collapse threshold: player section starts collapsed when there are more
// players than this.
// ---------------------------------------------------------------------------
private const val PLAYER_AUTO_COLLAPSE_THRESHOLD = 5

// ---------------------------------------------------------------------------
// Medal emojis for top-3 leaderboard / compact-row badges
// ---------------------------------------------------------------------------
private fun medalEmoji(rank: Int): String = when (rank) {
    1 -> "\uD83E\uDD47" // 🥇
    2 -> "\uD83E\uDD48" // 🥈
    3 -> "\uD83E\uDD49" // 🥉
    else -> ""
}

// ===========================================================================
// Main screen
// ===========================================================================
@Composable
fun GameTrackerStatisticsScreen(
    onBack: () -> Unit,
    viewModel: GameTrackerStatisticsViewModel = viewModel { GameTrackerStatisticsViewModel() }
) {
    val statistics by viewModel.statistics.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // ---------- collapsible-section state (reset to default every recomposition
    // triggered by a new statistics payload, which is the "reset on reload"
    // behaviour requested) ----------
    var globalExpanded     by remember { mutableStateOf(true) }
    var leaderboardsExpanded by remember { mutableStateOf(true) }
    var recordsExpanded    by remember { mutableStateOf(true) }
    var playersExpanded    by remember {
        mutableStateOf(statistics.playerStatistics.size <= PLAYER_AUTO_COLLAPSE_THRESHOLD)
    }

    // Which single player card is expanded (null = none)
    var expandedPlayerId by remember { mutableStateOf<String?>(null) }

    // Pre-compute the set of player IDs that deserve a medal (top 3 by win rate,
    // using completed-game count as tie-breaker).  We keep this as a map
    // playerId → rank so compact rows can look it up in O(1).
    val medalMap: Map<String, Int> = remember(statistics) {
        statistics.playerStatistics
            .sortedWith(compareByDescending<GameTrackerPlayerStatistics> { it.winRate }
                .thenByDescending { it.gamesWon })
            .take(3)
            .mapIndexed { index, stats -> stats.player.id to (index + 1) }
            .toMap()
    }

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
        when {
            // ── loading ──────────────────────────────────────────────
            isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(
                        text = stringResource(Res.string.game_tracker_stats_loading),
                        modifier = Modifier.padding(top = 12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // ── empty ────────────────────────────────────────────────
            statistics.totalGames == 0 -> Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.game_tracker_stats_no_data),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ── content ──────────────────────────────────────────────
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── Global Statistics ─────────────────────────────────
                item {
                    CollapsibleSectionHeader(
                        title = stringResource(Res.string.game_tracker_stats_global_title),
                        isExpanded = globalExpanded,
                        onToggle = { globalExpanded = !globalExpanded }
                    )
                }
                if (globalExpanded) {
                    item {
                        GlobalStatisticsCard(
                            totalGames = statistics.totalGames,
                            completedGames = statistics.completedGames,
                            activeGames = statistics.activeGames,
                            totalRounds = statistics.totalRounds,
                            averageRoundsPerGame = statistics.averageRoundsPerGame
                        )
                    }
                }

                // ── Leaderboards ──────────────────────────────────────
                item {
                    CollapsibleSectionHeader(
                        title = stringResource(Res.string.game_tracker_stats_leaderboards_title),
                        isExpanded = leaderboardsExpanded,
                        onToggle = { leaderboardsExpanded = !leaderboardsExpanded }
                    )
                }
                if (leaderboardsExpanded) {
                    item {
                        LeaderboardsCard(leaderboards = statistics.leaderboards)
                    }
                }

                // ── Records ───────────────────────────────────────────
                item {
                    CollapsibleSectionHeader(
                        title = stringResource(Res.string.game_tracker_stats_records_title),
                        isExpanded = recordsExpanded,
                        onToggle = { recordsExpanded = !recordsExpanded }
                    )
                }
                if (recordsExpanded) {
                    item {
                        RecordsCard(records = statistics.records)
                    }
                }

                // ── Player Statistics header ──────────────────────────
                item {
                    CollapsibleSectionHeader(
                        title = stringResource(
                            Res.string.game_tracker_stats_player_count,
                            statistics.playerStatistics.size
                        ),
                        isExpanded = playersExpanded,
                        onToggle = {
                            playersExpanded = !playersExpanded
                            if (!playersExpanded) expandedPlayerId = null
                        }
                    )
                }

                // ── Player Statistics rows ────────────────────────────
                if (playersExpanded) {
                    items(
                        statistics.playerStatistics.sortedByDescending { it.winRate },
                        key = { it.player.id }
                    ) { playerStats ->
                        val isExpanded = expandedPlayerId == playerStats.player.id
                        CompactPlayerRow(
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

// ===========================================================================
// Collapsible section header
// ===========================================================================
@Composable
private fun CollapsibleSectionHeader(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = stringResource(
                if (isExpanded) Res.string.game_tracker_stats_cd_collapse_section
                else Res.string.game_tracker_stats_cd_expand_section
            ),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// ===========================================================================
// Global Statistics Card  (unchanged content, just extracted)
// ===========================================================================
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
            StatRow(label = stringResource(Res.string.game_tracker_stats_total_games),         value = totalGames.toString())
            StatRow(label = stringResource(Res.string.game_tracker_stats_completed_games),     value = completedGames.toString())
            StatRow(label = stringResource(Res.string.game_tracker_stats_active_games),        value = activeGames.toString())
            StatRow(label = stringResource(Res.string.game_tracker_stats_total_rounds),        value = totalRounds.toString())
            StatRow(label = stringResource(Res.string.game_tracker_stats_avg_rounds_per_game), value = formatAverage(averageRoundsPerGame))
        }
    }
}

// ===========================================================================
// Leaderboards Card
// ===========================================================================
@Composable
private fun LeaderboardsCard(leaderboards: GameTrackerLeaderboards) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (leaderboards.mostGamesPlayed.isEmpty() && leaderboards.highestWinRate.isEmpty()) {
                Text(
                    text = stringResource(Res.string.game_tracker_stats_leaderboard_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                return@Column
            }

            // Most Games Played
            if (leaderboards.mostGamesPlayed.isNotEmpty()) {
                LeaderboardSection(
                    title = stringResource(Res.string.game_tracker_stats_leaderboard_most_games),
                    subtitle = null,
                    entries = leaderboards.mostGamesPlayed
                )
            }

            // divider between the two sub-sections
            if (leaderboards.mostGamesPlayed.isNotEmpty() && leaderboards.highestWinRate.isNotEmpty()) {
                HorizontalDivider()
            }

            // Highest Win Rate
            if (leaderboards.highestWinRate.isNotEmpty()) {
                LeaderboardSection(
                    title = stringResource(Res.string.game_tracker_stats_leaderboard_win_rate),
                    subtitle = stringResource(Res.string.game_tracker_stats_leaderboard_win_rate_min),
                    entries = leaderboards.highestWinRate
                )
            }
        }
    }
}

@Composable
private fun LeaderboardSection(
    title: String,
    subtitle: String?,
    entries: List<GameTrackerLeaderboardEntry>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        entries.forEach { entry ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Medal emoji
                    Text(
                        text = medalEmoji(entry.rank),
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Player avatar
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(parseColor(entry.player.avatarColor), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = entry.player.name.first().uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = entry.player.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = entry.value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ===========================================================================
// Records Card
// ===========================================================================
@Composable
private fun RecordsCard(records: GameTrackerRecords) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val hasAny = records.highestScoreInHighGames != null ||
                         records.lowestScoreInLowGames   != null ||
                         records.longestGame            != null ||
                         records.shortestCompletedGame  != null

            if (!hasAny) {
                Text(
                    text = stringResource(Res.string.game_tracker_stats_records_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                return@Column
            }

            var first = true

            records.highestScoreInHighGames?.let { record ->
                if (!first) HorizontalDivider()
                first = false
                RecordItem(
                    label = stringResource(Res.string.game_tracker_stats_record_highest_score),
                    value = record.score.toString(),
                    details = stringResource(Res.string.game_tracker_stats_record_by_in, record.playerName, record.gameName)
                )
            }

            records.lowestScoreInLowGames?.let { record ->
                if (!first) HorizontalDivider()
                first = false
                RecordItem(
                    label = stringResource(Res.string.game_tracker_stats_record_lowest_score),
                    value = record.score.toString(),
                    details = stringResource(Res.string.game_tracker_stats_record_by_in, record.playerName, record.gameName)
                )
            }

            records.longestGame?.let { record ->
                if (!first) HorizontalDivider()
                first = false
                RecordItem(
                    label = stringResource(Res.string.game_tracker_stats_record_longest_game),
                    value = stringResource(Res.string.game_tracker_stats_record_rounds, record.rounds),
                    details = "${record.gameName} • ${record.getDisplayPlayerNames()}"
                )
            }

            records.shortestCompletedGame?.let { record ->
                if (!first) HorizontalDivider()
                @Suppress("UNUSED_VALUE")
                first = false
                RecordItem(
                    label = stringResource(Res.string.game_tracker_stats_record_shortest_game),
                    value = stringResource(Res.string.game_tracker_stats_record_rounds, record.rounds),
                    details = "${record.gameName} • ${record.getDisplayPlayerNames()}"
                )
            }
        }
    }
}

@Composable
private fun RecordItem(label: String, value: String, details: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = details,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ===========================================================================
// Compact player row  (collapsed = one line; expanded = full detail card)
// ===========================================================================
@Composable
private fun CompactPlayerRow(
    playerStats: GameTrackerPlayerStatistics,
    rank: Int?,           // 1-3 for medal, null otherwise
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
        )
    ) {
        Column {
            // ── compact header row (always visible) ──────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(parseColor(playerStats.player.avatarColor), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = playerStats.player.name.first().uppercase(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Name
                    Text(
                        text = playerStats.player.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Right side: games | win%  +  expand icon
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${playerStats.gamesPlayed}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "|",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = playerStats.getWinRatePercentage(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = stringResource(
                            if (isExpanded) Res.string.game_tracker_stats_cd_collapse_player
                            else Res.string.game_tracker_stats_cd_expand_player
                        ),
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── expanded detail block ────────────────────────────────
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(durationMillis = 200)),
                exit  = shrinkVertically(tween(durationMillis = 200))
            ) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 4.dp))
                    StatRow(label = stringResource(Res.string.game_tracker_stats_games_played),      value = playerStats.gamesPlayed.toString())
                    StatRow(label = stringResource(Res.string.game_tracker_stats_games_won),         value = playerStats.gamesWon.toString())
                    StatRow(label = stringResource(Res.string.game_tracker_stats_win_rate),          value = playerStats.getWinRatePercentage())
                    StatRow(label = stringResource(Res.string.game_tracker_stats_total_rounds_played), value = playerStats.totalRoundsPlayed.toString())
                    StatRow(label = stringResource(Res.string.game_tracker_stats_average_score),     value = formatAverage(playerStats.averageScore))
                    if (playerStats.highestGameScore != null) {
                        StatRow(label = stringResource(Res.string.game_tracker_stats_highest_score), value = playerStats.highestGameScore.toString())
                    }
                    if (playerStats.lowestGameScore != null) {
                        StatRow(label = stringResource(Res.string.game_tracker_stats_lowest_score),  value = playerStats.lowestGameScore.toString())
                    }
                }
            }
        }
    }
}

// ===========================================================================
// Shared helpers
// ===========================================================================
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
