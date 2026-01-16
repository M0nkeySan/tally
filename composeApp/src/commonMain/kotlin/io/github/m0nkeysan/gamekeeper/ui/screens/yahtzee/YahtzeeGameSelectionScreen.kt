package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.ui.components.GameDisplay
import io.github.m0nkeysan.gamekeeper.ui.screens.common.GameSelectionTemplate
import io.github.m0nkeysan.gamekeeper.ui.strings.AppStrings

@Composable
fun YahtzeeGameSelectionScreen(
    onBack: () -> Unit,
    onCreateNewGame: () -> Unit,
    onSelectGame: (String) -> Unit,
    onNavigateToStatistics: () -> Unit,
    viewModel: YahtzeeGameViewModel = viewModel { YahtzeeGameViewModel() }
) {
    val state by viewModel.selectionState.collectAsState()
    var gameToDelete by remember { mutableStateOf<YahtzeeGameDisplayModel?>(null) }

    if (gameToDelete != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { gameToDelete = null },
            title = { Text(AppStrings.GAME_DELETION_DIALOG_YAHTZEE_TITLE) },
             text = { Text(AppStrings.GAME_DELETION_DIALOG_YAHTZEE_MESSAGE.format(gameToDelete?.name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        gameToDelete?.let { viewModel.deleteGame(it.game) }
                        gameToDelete = null
                    },
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = androidx.compose.material3.MaterialTheme.colorScheme.error)
                ) {
                    Text(AppStrings.ACTION_DELETE)
                }
            },
            dismissButton = {
                TextButton(onClick = { gameToDelete = null }) {
                    Text(AppStrings.ACTION_DELETE)
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
        title = AppStrings.YAHTZEE_GAME_TITLE,
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
