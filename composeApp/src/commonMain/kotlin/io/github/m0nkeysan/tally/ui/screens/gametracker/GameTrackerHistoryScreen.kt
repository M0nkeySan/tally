package io.github.m0nkeysan.tally.ui.screens.gametracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.GameIcons
import io.github.m0nkeysan.tally.core.model.GameTrackerScoreChange
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.action_back
import io.github.m0nkeysan.tally.generated.resources.action_cancel
import io.github.m0nkeysan.tally.generated.resources.counter_dialog_delete_message
import io.github.m0nkeysan.tally.generated.resources.dialog_delete_all_title
import io.github.m0nkeysan.tally.generated.resources.game_tracker_history_cd_delete
import io.github.m0nkeysan.tally.generated.resources.game_tracker_history_empty
import io.github.m0nkeysan.tally.generated.resources.game_tracker_history_round_format
import io.github.m0nkeysan.tally.generated.resources.game_tracker_history_subtitle
import io.github.m0nkeysan.tally.generated.resources.game_tracker_history_title
import io.github.m0nkeysan.tally.platform.formatTimestamp
import io.github.m0nkeysan.tally.ui.theme.LocalCustomColors
import io.github.m0nkeysan.tally.ui.utils.parseColor
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameTrackerHistoryScreen(
    onBack: () -> Unit,
    viewModel: GameTrackerHistoryViewModel = viewModel { GameTrackerHistoryViewModel() }
) {
    val history by viewModel.history.collectAsState()
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    // Delete all confirmation dialog
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text(stringResource(Res.string.game_tracker_history_cd_delete)) },
            text = { Text(stringResource(Res.string.counter_dialog_delete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory()
                        showDeleteAllDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(Res.string.dialog_delete_all_title))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(Res.string.game_tracker_history_title),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                stringResource(Res.string.game_tracker_history_subtitle),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            GameIcons.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                },
                actions = {
                    if (history.isNotEmpty()) {
                        IconButton(onClick = { showDeleteAllDialog = true }) {
                            Icon(
                                GameIcons.Delete,
                                contentDescription = stringResource(Res.string.game_tracker_history_cd_delete)
                            )
                        }
                    }
                },
                modifier = Modifier.shadow(elevation = 2.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (history.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(Res.string.game_tracker_history_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(history.size) { index ->
                        ScoreHistoryItem(history[index])
                        if (index < history.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant,
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreHistoryItem(scoreChange: GameTrackerScoreChange) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Player avatar circle
        val avatarColor = remember(scoreChange.playerAvatarColor) { 
            parseColor(scoreChange.playerAvatarColor) 
        }
        val contentColor = if (avatarColor.luminance() > 0.5f) 
            Color.Black.copy(alpha = 0.8f) 
        else 
            Color.White
        
        Surface(
            shape = CircleShape,
            modifier = Modifier.size(32.dp),
            color = avatarColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = scoreChange.playerName.take(1).uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }

        // Player name and round info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = scoreChange.playerName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "${stringResource(Res.string.game_tracker_history_round_format, scoreChange.roundNumber)} • ${formatTimestamp(scoreChange.timestamp)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Score display
        Text(
            text = if (scoreChange.score >= 0) "+${scoreChange.score}" else "${scoreChange.score}",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = if (scoreChange.score > 0) {
                LocalCustomColors.current.success
            } else {
                MaterialTheme.colorScheme.error
            }
        )
    }
}
