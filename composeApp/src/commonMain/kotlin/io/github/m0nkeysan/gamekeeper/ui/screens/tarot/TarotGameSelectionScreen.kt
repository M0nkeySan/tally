package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.data.local.database.TarotGameEntity

@OptIn(ExperimentalMaterial3Api::class)
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
        AlertDialog(
            onDismissRequest = { gameToDelete = null },
            title = { Text("Delete Game") },
            text = { Text("Are you sure you want to delete the game '${gameToDelete?.name}'? This will also delete all rounds in this game.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        gameToDelete?.let { viewModel.deleteGame(it.rawEntity) }
                        gameToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { gameToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tarot Games") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNewGame) {
                Icon(Icons.Default.Add, contentDescription = "New Game")
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.games.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No games yet. Create one!")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = state.games,
                    key = { it.id }
                ) { game ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                gameToDelete = game
                            }
                            false 
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            val color = when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                                else -> Color.Transparent
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color, shape = MaterialTheme.shapes.medium)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    ) {
                        TarotGameCard(
                            game = game, 
                            onClick = { onSelectGame(game.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TarotGameCard(
    game: TarotGameDisplayModel, 
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = game.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${game.playerCount} players", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = game.playerNames,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
