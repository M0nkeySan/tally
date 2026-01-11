package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.ui.components.GameDisplay
import io.github.m0nkeysan.gamekeeper.ui.screens.common.GameSelectionTemplate

@Composable
fun TarotGameSelectionScreen(
    onBack: () -> Unit,
    onCreateNewGame: () -> Unit,
    onSelectGame: (String) -> Unit,
    viewModel: TarotGameViewModel = viewModel { TarotGameViewModel() }
) {
    val state by viewModel.selectionState.collectAsState()
    var gameToDelete by remember { mutableStateOf<TarotGameDisplayModel?>(null) }

    if (gameToDelete != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { gameToDelete = null },
            title = { androidx.compose.material3.Text("Delete Game") },
            text = { androidx.compose.material3.Text("Are you sure you want to delete the game '${gameToDelete?.name}'? This will also delete all rounds in this game.") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        gameToDelete?.let { viewModel.deleteGame(it.game) }
                        gameToDelete = null
                    },
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = androidx.compose.material3.MaterialTheme.colorScheme.error)
                ) {
                    androidx.compose.material3.Text("Delete")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { gameToDelete = null }) {
                    androidx.compose.material3.Text("Cancel")
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
            isFinished = false, // Tarot games don't have explicit finished state like Yahtzee
            createdAt = game.createdAt,
            updatedAt = game.updatedAt
        )
    }

    GameSelectionTemplate(
        title = "Tarot Games",
        games = games,
        onGameSelect = onSelectGame,
        onCreateNew = onCreateNewGame,
        onDeleteGame = { game ->
            gameToDelete = state.games.find { it.id == game.id }
        },
        onBack = onBack,
        isLoading = state.isLoading,
        error = state.error,
        onDeleteAllGames = { viewModel.deleteAllGames() }
    )
}
