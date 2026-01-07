package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeGameEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YahtzeeGameSelectionScreen(
    onBack: () -> Unit,
    onCreateNewGame: () -> Unit,
    onSelectGame: (String) -> Unit,
    viewModel: YahtzeeGameViewModel = viewModel { YahtzeeGameViewModel() }
) {
    val state by viewModel.selectionState.collectAsState()
    var gameToDelete by remember { mutableStateOf<YahtzeeGameEntity?>(null) }

    if (gameToDelete != null) {
        AlertDialog(
            onDismissRequest = { gameToDelete = null },
            title = { Text("Delete Game") },
            text = { Text("Are you sure you want to delete '${gameToDelete?.name}'? All scores will be lost.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        gameToDelete?.let { viewModel.deleteGame(it) }
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
                title = { Text("Yahtzee Games") },
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
                Text("No games yet. Start one!")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.games, key = { it.id }) { game ->
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
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.medium)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                    ) {
                        YahtzeeGameCard(game = game, onClick = { onSelectGame(game.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun YahtzeeGameCard(game: YahtzeeGameEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = if (game.isFinished) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = game.name, 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.Bold,
                        color = if (game.isFinished) Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                    if (game.isFinished) {
                        Spacer(Modifier.width(8.dp))
                        SuggestionChip(
                            onClick = { },
                            label = { Text("FINISHED", fontSize = 10.sp) },
                            shape = CircleShape,
                            modifier = Modifier.height(20.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                if (game.isFinished && game.winnerName != null) {
                    Text(
                        text = "Winner: ${game.winnerName}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "${game.playerCount} players", 
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = game.playerNames.split(",").joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (game.isFinished) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFFFFD700), // Gold
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
