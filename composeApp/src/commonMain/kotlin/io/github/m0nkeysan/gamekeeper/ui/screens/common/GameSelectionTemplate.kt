package io.github.m0nkeysan.gamekeeper.ui.screens.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.generated.resources.Res
import io.github.m0nkeysan.gamekeeper.generated.resources.action_back
import io.github.m0nkeysan.gamekeeper.generated.resources.action_cancel
import io.github.m0nkeysan.gamekeeper.generated.resources.action_delete_all
import io.github.m0nkeysan.gamekeeper.generated.resources.cd_menu
import io.github.m0nkeysan.gamekeeper.generated.resources.game_create
import io.github.m0nkeysan.gamekeeper.generated.resources.game_delete_all_confirm
import io.github.m0nkeysan.gamekeeper.generated.resources.game_delete_all_title
import io.github.m0nkeysan.gamekeeper.generated.resources.game_selection_cd_create
import io.github.m0nkeysan.gamekeeper.generated.resources.game_selection_cd_delete_all
import io.github.m0nkeysan.gamekeeper.generated.resources.game_selection_cd_delete_game
import io.github.m0nkeysan.gamekeeper.generated.resources.game_selection_cd_statistics
import io.github.m0nkeysan.gamekeeper.generated.resources.game_selection_empty
import io.github.m0nkeysan.gamekeeper.generated.resources.game_selection_loading
import io.github.m0nkeysan.gamekeeper.ui.components.EmptyState
import io.github.m0nkeysan.gamekeeper.ui.components.GameDisplay
import io.github.m0nkeysan.gamekeeper.ui.components.GameKeeperSnackbarHost
import io.github.m0nkeysan.gamekeeper.ui.components.GameSelectionCard
import io.github.m0nkeysan.gamekeeper.ui.components.LoadingState
import io.github.m0nkeysan.gamekeeper.ui.components.showErrorSnackbar
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors
import org.jetbrains.compose.resources.stringResource

/**
 * Reusable template for game selection screens.
 * Eliminates code duplication between Tarot and Yahtzee game selection screens.
 *
 * Features:
 * - Automatic handling of loading, empty, and error states
 * - Swipe-to-delete functionality with confirmation
 * - Floating action button for creating new games
 * - Error feedback via Snackbar notifications
 * - Consistent flat design with GameColors theme
 *
 * @param title Title displayed in TopAppBar
 * @param games List of games to display
 * @param onGameSelect Callback when game is selected
 * @param onCreateNew Callback when FAB is clicked
 * @param onDeleteGame Callback when game is swiped to delete
 * @param onBack Callback for back navigation
 * @param isLoading Whether data is loading
 * @param error Error message to display, if any
 * @param modifier Optional layout modifier
 *
 * Example usage:
 * ```
 * GameSelectionTemplate(
 *     title = "Tarot Games",
 *     games = games,
 *     onGameSelect = { viewModel.selectGame(it) },
 *     onCreateNew = { navController.navigate("create") },
 *     onDeleteGame = { viewModel.deleteGame(it) },
 *     onBack = { navController.popBackStack() },
 *     isLoading = state.isLoading,
 *     error = state.error
 * )
 * ```
 */
@Composable
fun GameSelectionTemplate(
    title: String,
    games: List<GameDisplay>,
    onGameSelect: (String) -> Unit,
    onCreateNew: () -> Unit,
    onDeleteGame: (GameDisplay) -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean,
    error: String?,
    modifier: Modifier = Modifier,
    onDeleteAllGames: (() -> Unit)? = null,
    onNavigateToStatistics: (() -> Unit)? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    // Show error in Snackbar
    LaunchedEffect(error) {
        if (error != null) {
            showErrorSnackbar(snackbarHostState, error)
        }
    }

    // Delete all confirmation dialog
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text(stringResource(Res.string.game_delete_all_title)) },
            text = { Text(stringResource(Res.string.game_delete_all_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteAllGames?.invoke()
                        showDeleteAllDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(Res.string.action_delete_all))
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
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
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
                    if (onNavigateToStatistics != null && games.isNotEmpty()) {
                        IconButton(onClick = {
                            onNavigateToStatistics()
                        }) {
                            Icon(
                                GameIcons.BarChart,
                                contentDescription = stringResource(Res.string.game_selection_cd_statistics)
                            )
                        }
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                GameIcons.MoreVert,
                                contentDescription = stringResource(Res.string.cd_menu)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.game_create)) },
                                onClick = {
                                    showMenu = false
                                    onCreateNew()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = stringResource(Res.string.game_selection_cd_create)
                                    )
                                }
                            )
                            if (onDeleteAllGames != null && games.isNotEmpty()) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(Res.string.game_delete_all_title)) },
                                    onClick = {
                                        showMenu = false
                                        showDeleteAllDialog = true
                                    },
                                    leadingIcon = {
                                        Icon(
                                            GameIcons.Delete,
                                            contentDescription = stringResource(Res.string.game_selection_cd_delete_all)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNew,
                containerColor = GameColors.Primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(Res.string.game_selection_cd_create)
                )
            }
        },
        snackbarHost = {
            GameKeeperSnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(
                    message = stringResource(Res.string.game_selection_loading),
                    modifier = Modifier.padding(paddingValues)
                )
            }

            games.isEmpty() -> {
                EmptyState(
                    message = stringResource(Res.string.game_selection_empty),
                    actionLabel = "Create Game",
                    onAction = onCreateNew,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(games, key = { it.id }) { game ->
                        val dismissState = rememberSwipeToDismissBoxState()
                        LaunchedEffect(dismissState.currentValue) {
                            if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                                onDeleteGame(game)
                                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                            }
                        }

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = GameIcons.Delete,
                                        contentDescription = stringResource(Res.string.game_selection_cd_delete_game),
                                        tint = GameColors.Error,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        ) {
                            GameSelectionCard(
                                game = game,
                                onClick = { onGameSelect(game.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
