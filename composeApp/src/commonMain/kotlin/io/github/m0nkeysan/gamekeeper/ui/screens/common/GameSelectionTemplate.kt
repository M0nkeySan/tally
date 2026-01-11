package io.github.m0nkeysan.gamekeeper.ui.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.ui.components.*
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

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
@OptIn(ExperimentalMaterial3Api::class)
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
    onDeleteAllGames: (() -> Unit)? = null
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
            title = { Text("Delete All Games") },
            text = { Text("Are you sure you want to delete all games? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteAllGames?.invoke()
                        showDeleteAllDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Text("⋯", fontSize = MaterialTheme.typography.headlineSmall.fontSize)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Create Game") },
                                onClick = {
                                    showMenu = false
                                    onCreateNew()
                                },
                                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) }
                            )
                            if (onDeleteAllGames != null && games.isNotEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Delete All Games") },
                                    onClick = {
                                        showMenu = false
                                        showDeleteAllDialog = true
                                    },
                                    leadingIcon = { Icon(GameIcons.Delete, contentDescription = null) }
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
                Icon(Icons.Default.Add, contentDescription = "Create new game")
            }
        },
        snackbarHost = {
            GameKeeperSnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(
                    message = "Loading games...",
                    modifier = Modifier.padding(paddingValues)
                )
            }
            games.isEmpty() -> {
                EmptyState(
                    message = "No games yet. Create one!",
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
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    onDeleteGame(game)
                                }
                                false
                            }
                        )
                        
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
                                        contentDescription = "Delete game",
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
