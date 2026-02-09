package io.github.m0nkeysan.tally.ui.screens.gametracker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.action_cancel
import io.github.m0nkeysan.tally.generated.resources.action_delete
import io.github.m0nkeysan.tally.generated.resources.game_deletion_dialog_game_tracker_message
import io.github.m0nkeysan.tally.generated.resources.game_deletion_dialog_game_tracker_title
import io.github.m0nkeysan.tally.generated.resources.game_tracker_selection_title
import io.github.m0nkeysan.tally.ui.components.GameDisplay
import io.github.m0nkeysan.tally.ui.screens.common.GameSelectionTemplate
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameTrackerSelectionScreen(
    onBack: () -> Unit,
    onCreateNewGame: () -> Unit,
    onSelectGame: (String) -> Unit,
    onNavigateToStatistics: () -> Unit,
    viewModel: GameTrackerSelectionViewModel = viewModel { GameTrackerSelectionViewModel() }
) {
    val state by viewModel.selectionState.collectAsState()
    var gameToDelete by remember { mutableStateOf<GameTrackerDisplayModel?>(null) }

    if (gameToDelete != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { gameToDelete = null },
            title = { androidx.compose.material3.Text(stringResource(Res.string.game_deletion_dialog_game_tracker_title)) },
            text = { androidx.compose.material3.Text(stringResource(Res.string.game_deletion_dialog_game_tracker_message, gameToDelete?.name ?: "")) },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        gameToDelete?.let { viewModel.deleteGame(it.game) }
                        gameToDelete = null
                    },
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = androidx.compose.material3.MaterialTheme.colorScheme.error)
                ) {
                    androidx.compose.material3.Text(stringResource(Res.string.action_delete))
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { gameToDelete = null }) {
                    androidx.compose.material3.Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }

    val games = state.games.map { game ->
        GameDisplay(
            id = game.id,
            name = game.name,
            playerCount = game.playerCount,
            playerNames = game.playerNames,
            isFinished = game.isFinished,
            createdAt = game.createdAt,
            updatedAt = game.updatedAt
        )
    }

    GameSelectionTemplate(
        title = stringResource(Res.string.game_tracker_selection_title),
        games = games,
        onGameSelect = onSelectGame,
        onCreateNew = onCreateNewGame,
        onDeleteGame = { game ->
            gameToDelete = state.games.find { it.id == game.id }
        },
        onBack = onBack,
        isLoading = state.isLoading,
        error = state.error,
        onDeleteAllGames = { viewModel.deleteAllGames() },
        onNavigateToStatistics = onNavigateToStatistics
    )
}
