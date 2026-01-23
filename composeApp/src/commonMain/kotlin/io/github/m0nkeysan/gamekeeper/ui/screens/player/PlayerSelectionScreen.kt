package io.github.m0nkeysan.gamekeeper.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.generated.resources.Res
import io.github.m0nkeysan.gamekeeper.generated.resources.action_back
import io.github.m0nkeysan.gamekeeper.generated.resources.action_cancel
import io.github.m0nkeysan.gamekeeper.generated.resources.cd_add_player
import io.github.m0nkeysan.gamekeeper.generated.resources.dialog_deactivate_message
import io.github.m0nkeysan.gamekeeper.generated.resources.dialog_deactivate_player
import io.github.m0nkeysan.gamekeeper.generated.resources.dialog_deactivate_title
import io.github.m0nkeysan.gamekeeper.generated.resources.dialog_delete_message
import io.github.m0nkeysan.gamekeeper.generated.resources.dialog_delete_player
import io.github.m0nkeysan.gamekeeper.generated.resources.dialog_delete_title
import io.github.m0nkeysan.gamekeeper.generated.resources.player_cd_edit
import io.github.m0nkeysan.gamekeeper.generated.resources.player_deactivated_message
import io.github.m0nkeysan.gamekeeper.generated.resources.player_deleted_message
import io.github.m0nkeysan.gamekeeper.generated.resources.player_reactivated_message
import io.github.m0nkeysan.gamekeeper.generated.resources.player_section_deactivated
import io.github.m0nkeysan.gamekeeper.generated.resources.player_section_players
import io.github.m0nkeysan.gamekeeper.generated.resources.players_no_players
import io.github.m0nkeysan.gamekeeper.ui.components.GameKeeperSnackbarHost
import io.github.m0nkeysan.gamekeeper.ui.components.showSuccessSnackbar
import io.github.m0nkeysan.gamekeeper.ui.utils.parseColor
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlayerSelectionScreen(
    onBack: () -> Unit,
    viewModel: PlayerSelectionViewModel = viewModel { PlayerSelectionViewModel() },
    showBackButton: Boolean = true,
    triggerAddDialog: Boolean = false,
    onAddDialogHandled: () -> Unit = {}
) {
    val allPlayers by viewModel.allPlayersIncludingInactive.collectAsState(emptyList())
    val snackbarHostState = remember { SnackbarHostState() }

    // State to manage dialogs
    var showAddDialog by remember { mutableStateOf(false) }
    var playerToDelete by remember { mutableStateOf<Player?>(null) }
    var playerToEdit by remember { mutableStateOf<Player?>(null) }
    var playerToReactivate by remember { mutableStateOf<Player?>(null) }
    var snackbarMessage by remember { mutableStateOf("") }

    // Handle external trigger from parent Scaffold FAB
    LaunchedEffect(triggerAddDialog) {
        if (triggerAddDialog) {
            showAddDialog = true
            onAddDialogHandled()
        }
    }

    // --- Prepare Snackbar Message for Reactivation ---
    // We resolve the string here in the composition because stringResource is Composable
    val reactivateMsg = if (playerToReactivate != null) {
        stringResource(Res.string.player_reactivated_message, playerToReactivate!!.name)
    } else ""

    LaunchedEffect(playerToReactivate) {
        if (playerToReactivate != null) {
            showSuccessSnackbar(
                snackbarHostState,
                reactivateMsg // Use the pre-calculated string
            )
            playerToReactivate = null
        }
    }

    // Show generic snackbar for deletion/deactivation (set by dialog)
    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage.isNotEmpty()) {
            showSuccessSnackbar(snackbarHostState, snackbarMessage)
            snackbarMessage = ""
        }
    }

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
        val targetPlayer = playerToDelete!!
        var gameCount by remember { mutableIntStateOf(0) }
        LaunchedEffect(targetPlayer) {
            gameCount = viewModel.getGameCountForPlayer(targetPlayer.id)
        }

        // We calculate the success message here using stringResource with arguments
        // This avoids calling .format() manually
        val successMessage = if (gameCount > 0) {
            stringResource(Res.string.player_deactivated_message, targetPlayer.name)
        } else {
            stringResource(Res.string.player_deleted_message, targetPlayer.name)
        }

        AlertDialog(
            onDismissRequest = { playerToDelete = null },
            title = {
                Text(
                    if (gameCount > 0) {
                        stringResource(Res.string.dialog_deactivate_title)
                    } else {
                        stringResource(Res.string.dialog_delete_title)
                    }
                )
            },
            text = {
                Text(
                    if (gameCount > 0) {
                        stringResource(Res.string.dialog_deactivate_message, gameCount)
                    } else {
                        stringResource(Res.string.dialog_delete_message)
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePlayer(targetPlayer)
                        // Assign the string we resolved above
                        snackbarMessage = successMessage
                        playerToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(
                        if (gameCount > 0) stringResource(Res.string.dialog_deactivate_player)
                        else stringResource(Res.string.dialog_delete_player)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    playerToDelete = null
                }) { Text(stringResource(Res.string.action_cancel)) }
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
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(Res.string.player_section_players))
                        }
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
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(Res.string.cd_add_player)
                            )
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(Res.string.player_section_players))
                        }
                    },
                    actions = {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(Res.string.cd_add_player)
                            )
                        }
                    }
                )
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
                Text(stringResource(Res.string.players_no_players))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ACTIVE PLAYERS SECTION
                if (activePlayers.isNotEmpty()) {
                    item {
                        Text(
                            stringResource(Res.string.player_section_players),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(activePlayers, key = { it.id }) { player ->
                        val dismissState = rememberSwipeToDismissBoxState()

                        LaunchedEffect(dismissState.currentValue) {
                            if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                                playerToDelete = player
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
                        Text(
                            stringResource(Res.string.player_section_deactivated),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(deactivatedPlayers, key = { it.id }) { player ->

                        val dismissState = rememberSwipeToDismissBoxState()
                        LaunchedEffect(dismissState.currentValue) {
                            if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
                                viewModel.reactivatePlayer(player)
                                playerToReactivate = player
                                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                            }
                        }

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromEndToStart = false,
                            backgroundContent = {
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
    val contentColor = if (color.luminance() > 0.5f) Color.Black.copy(alpha = 0.8f) else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isActive, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = color,
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
                    color = contentColor,
                    textDecoration = if (isActive) TextDecoration.None else TextDecoration.LineThrough
                )
            }

            // Edit Icon hint (only for active players)
            if (isActive) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(Res.string.player_cd_edit),
                    tint = contentColor
                )
            }
        }
    }
}