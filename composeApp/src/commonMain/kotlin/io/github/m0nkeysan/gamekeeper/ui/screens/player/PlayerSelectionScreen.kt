package io.github.m0nkeysan.gamekeeper.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.model.Player

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSelectionScreen(
    onBack: () -> Unit,
    viewModel: PlayerSelectionViewModel = viewModel { PlayerSelectionViewModel() }
) {
    val players by viewModel.players.collectAsState()
    
    // State to manage dialogs
    var showAddDialog by remember { mutableStateOf(false) }
    var playerToDelete by remember { mutableStateOf<Player?>(null) }
    var playerToEdit by remember { mutableStateOf<Player?>(null) }

    if (showAddDialog) {
        PlayerDialog(
            existingPlayers = players,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, color ->
                viewModel.addPlayer(name, color)
                showAddDialog = false
            }
        )
    }

    // --- Delete Confirmation Dialog ---
    if (playerToDelete != null) {
        AlertDialog(
            onDismissRequest = { playerToDelete = null },
            title = { Text("Delete Player") },
            text = { Text("Are you sure you want to delete '${playerToDelete?.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        playerToDelete?.let { viewModel.deletePlayer(it) }
                        playerToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { playerToDelete = null }) { Text("Cancel") }
            }
        )
    }

    // --- Edit Player Dialog ---
    if (playerToEdit != null) {
        PlayerDialog(
            initialPlayer = playerToEdit,
            existingPlayers = players,
            onDismiss = { playerToEdit = null },
            onConfirm = { name, color ->
                playerToEdit?.let { viewModel.updatePlayer(it, name, color) }
                playerToEdit = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Players") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Player")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Player")
            }
        }
    ) { paddingValues ->
        if (players.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No players found. Add one!")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(players, key = { it.id }) { player ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                playerToDelete = player
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
                                    .background(
                                        MaterialTheme.colorScheme.errorContainer,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    ) {
                        PlayerCard(
                            player = player,
                            onClick = { playerToEdit = player }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerCard(player: Player, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            val color = remember(player.avatarColor) {
                try {
                    Color(0xFF000000 or player.avatarColor.removePrefix("#").toLong(16))
                } catch (e: Exception) {
                    Color.Gray
                }
            }
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color, CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.name.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Edit Icon hint
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}