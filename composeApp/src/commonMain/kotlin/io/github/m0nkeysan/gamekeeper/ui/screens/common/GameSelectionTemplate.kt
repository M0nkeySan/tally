package io.github.m0nkeysan.gamekeeper.ui.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.ui.components.*
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Reusable template for game selection screens.
 * Handles loading, error, and empty states.
 * Provides consistent UI for Tarot and Yahtzee game selection.
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
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error in Snackbar
    LaunchedEffect(error) {
        if (error != null) {
            showErrorSnackbar(snackbarHostState, error)
        }
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
                                        .padding(horizontal = 20.dp)
                                )
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
