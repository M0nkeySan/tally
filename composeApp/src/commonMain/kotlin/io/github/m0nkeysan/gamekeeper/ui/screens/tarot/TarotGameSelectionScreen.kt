package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.ui.components.GameDisplay
import io.github.m0nkeysan.gamekeeper.ui.screens.common.GameSelectionTemplate
import org.jetbrains.compose.resources.stringResource
import io.github.m0nkeysan.gamekeeper.generated.resources.action_cancel
import io.github.m0nkeysan.gamekeeper.generated.resources.action_delete
import io.github.m0nkeysan.gamekeeper.generated.resources.game_deletion_dialog_tarot_message
import io.github.m0nkeysan.gamekeeper.generated.resources.game_deletion_dialog_tarot_title
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_scoring_game_title
import io.github.m0nkeysan.gamekeeper.generated.resources.Res

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
             title = { androidx.compose.material3.Text(stringResource(Res.string.game_deletion_dialog_tarot_title)) },
             text = { androidx.compose.material3.Text(stringResource(Res.string.game_deletion_dialog_tarot_message).format(gameToDelete?.name ?: "")) },
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
            isFinished = false, // Tarot games don't have explicit finished state like Yahtzee
            createdAt = game.createdAt,
            updatedAt = game.updatedAt
        )
    }

    GameSelectionTemplate(
        title = stringResource(Res.string.tarot_scoring_game_title),
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
