package io.github.m0nkeysan.gamekeeper.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.alpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.ui.components.parseColor
import io.github.m0nkeysan.gamekeeper.ui.components.GameKeeperSnackbarHost
import io.github.m0nkeysan.gamekeeper.ui.components.showSuccessSnackbar
import io.github.m0nkeysan.gamekeeper.ui.strings.AppStrings
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSelectionScreen(
    onBack: () -> Unit,
    viewModel: PlayerSelectionViewModel = viewModel { PlayerSelectionViewModel() },
    showBackButton: Boolean = true
) {
    val allPlayers by viewModel.allPlayersIncludingInactive.collectAsState(emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // State to manage dialogs
    var showAddDialog by remember { mutableStateOf(false) }
    var playerToDelete by remember { mutableStateOf<Player?>(null) }
    var playerToEdit by remember { mutableStateOf<Player?>(null) }

    if (showAddDialog) {
        PlayerDialog(
            existingPlayers = allPlayers,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, color ->
                viewModel.addPlayer(name, color)
                showAddDialog = false
            }
        )
    }

    // --- Delete Confirmation Dialog ---
    if (playerToDelete != null) {
        var gameCount by remember { mutableStateOf(0) }
        LaunchedEffect(playerToDelete) {
            gameCount = viewModel.getGameCountForPlayer(playerToDelete!!.id)
        }
        
        AlertDialog(
            onDismissRequest = { playerToDelete = null },
            title = { 
                Text(
                    if (gameCount > 0) "Deactivate Player?" else "Delete Player?"
                ) 
            },
            text = { 
                Text(
                    if (gameCount > 0) {
                        "Linked to $gameCount game(s). Player will be deactivated to preserve game history."
                    } else {
                        "This player has no game history. They will be permanently deleted."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        playerToDelete?.let { player ->
                            viewModel.deletePlayer(player)
                            scope.launch {
                                val message = if (gameCount > 0) {
                                    "Player '${player.name}' deactivated"
                                } else {
                                    "Player '${player.name}' deleted"
                                }
                                showSuccessSnackbar(snackbarHostState, message)
                            }
                        }
                        playerToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { 
                    Text(if (gameCount > 0) "Deactivate" else "Delete") 
                }
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
            existingPlayers = allPlayers,
            onDismiss = { playerToEdit = null },
            onConfirm = { name, color ->
                playerToEdit?.let { viewModel.updatePlayer(it, name, color) }
                playerToEdit = null
            }
        )
    }

    Scaffold(
        topBar = {
            if (showBackButton) {
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
            } else {
                TopAppBar(
                    title = { Text("Players") },
                    actions = {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Player")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Player")
            }
        },
        snackbarHost = { GameKeeperSnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        val activePlayers = allPlayers.filter { it.isActive }
        val deactivatedPlayers = allPlayers.filter { !it.isActive }
        
        if (allPlayers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(AppStrings.PLAYERS_NO_PLAYERS)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ACTIVE PLAYERS SECTION
                if (activePlayers.isNotEmpty()) {
                    item {
                        Text("Players", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    items(activePlayers, key = { it.id }) { player ->
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
                                isActive = true,
                                onClick = { playerToEdit = player }
                            )
                        }
                    }
                }
                
                // DEACTIVATED PLAYERS SECTION
                if (deactivatedPlayers.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Deactivated", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    items(deactivatedPlayers, key = { it.id }) { player ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.StartToEnd) {
                                    viewModel.reactivatePlayer(player)
                                    scope.launch {
                                        showSuccessSnackbar(snackbarHostState, "Player '${player.name}' has been reactivated")
                                    }
                                }
                                false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromEndToStart = false,
                            backgroundContent = {
                                // Only show background when actively swiping
                                if (dismissState.progress > 0f) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                MaterialTheme.colorScheme.secondaryContainer,
                                                shape = MaterialTheme.shapes.medium
                                            )
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            null,
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                        ) {
                            PlayerCard(
                                player = player,
                                isActive = false,
                                onClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerCard(player: Player, isActive: Boolean = true, onClick: () -> Unit) {
    val color = remember(player.avatarColor) { parseColor(player.avatarColor) }
    val cardColor = if (isActive) color else color.copy(alpha = 0.5f)
    val contentColor = if (color.luminance() > 0.5f) Color.Black.copy(alpha = 0.8f) else Color.White
    val textAlpha = if (isActive) 1f else 0.6f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isActive, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor.copy(alpha = textAlpha),
                    textDecoration = if (isActive) TextDecoration.None else TextDecoration.LineThrough
                )
            }

            // Edit Icon hint (only for active players)
            if (isActive) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = contentColor
                )
            }
        }
    }
}