package io.github.m0nkeysan.gamekeeper.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.core.model.sanitizePlayerName
import io.github.m0nkeysan.gamekeeper.core.model.playerNamesEqual

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSelectorField(
    label: String,
    selectedPlayer: Player?,
    allPlayers: List<Player>,
    onPlayerSelected: (Player) -> Unit,
    onNewPlayerCreated: (String) -> Unit,
    modifier: Modifier = Modifier,
    excludedPlayerIds: Set<String> = emptySet(),
    onReactivatePlayer: ((Player) -> Unit)? = null
) {
    var showSheet by remember { mutableStateOf(false) }
    var lastCreatedPlayerName by remember { mutableStateOf<String?>(null) }
    
    // Auto-select newly created player when it appears in allPlayers
    LaunchedEffect(lastCreatedPlayerName, allPlayers) {
        if (lastCreatedPlayerName != null) {
            val newPlayer = allPlayers.find { it.name == lastCreatedPlayerName && it.isActive }
            if (newPlayer != null && newPlayer.id !in excludedPlayerIds) {
                onPlayerSelected(newPlayer)
                lastCreatedPlayerName = null
                showSheet = false
            }
        }
    }
    
    val playerColor = selectedPlayer?.let { remember(it.avatarColor) { parseColor(it.avatarColor) } } ?: MaterialTheme.colorScheme.surface
    val isSelected = selectedPlayer != null
    val contentColor = if (isSelected) {
        if (playerColor.luminance() > 0.5f) Color.Black.copy(alpha = 0.8f) else Color.White
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showSheet = true },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = playerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 0.dp),
        border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedPlayer != null) {
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp),
                    color = contentColor.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = selectedPlayer.name.firstOrNull()?.uppercase() ?: "P",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label, 
                        style = MaterialTheme.typography.labelSmall, 
                        color = contentColor.copy(alpha = 0.7f)
                    )
                    Text(
                        text = selectedPlayer.name, 
                        style = MaterialTheme.typography.bodyLarge, 
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    text = "Select $label", 
                    style = MaterialTheme.typography.bodyLarge, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
            
            TextButton(
                onClick = { showSheet = true },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isSelected) contentColor else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isSelected) "CHANGE" else "SELECT", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false }
        ) {
            PlayerSelectorContent(
                allPlayers = allPlayers,
                excludedPlayerIds = excludedPlayerIds,
                onSelect = { 
                    onPlayerSelected(it)
                    showSheet = false
                },
                onCreate = { name ->
                    lastCreatedPlayerName = name
                    onNewPlayerCreated(name)
                },
                onReactivate = onReactivatePlayer
            )
        }
    }
}

@Composable
fun PlayerAvatar(color: String, letter: String, size: androidx.compose.ui.unit.Dp = 40.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(parseColor(color)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.uppercase(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSelectorContent(
    allPlayers: List<Player>,
    excludedPlayerIds: Set<String> = emptySet(),
    onSelect: (Player) -> Unit,
    onCreate: (String) -> Unit,
    onReactivate: ((Player) -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredPlayers = remember(searchQuery, allPlayers, excludedPlayerIds) {
        val activeList = allPlayers.filter { it.isActive }
        val baseList = activeList.filter { it.id !in excludedPlayerIds }
        if (searchQuery.isBlank()) baseList
        else baseList.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    // Helper function to handle player creation or reactivation
    val handleCreateOrReactivate = { name: String ->
        sanitizePlayerName(name)?.let { sanitized ->
            // Look for deactivated player with matching sanitized name
            val deactivatedPlayer = allPlayers.find { existingPlayer ->
                !existingPlayer.isActive && playerNamesEqual(sanitized, existingPlayer.name)
            }
            
            if (deactivatedPlayer != null && onReactivate != null) {
                // Reactivate existing player
                onReactivate(deactivatedPlayer)
                onSelect(deactivatedPlayer)
            } else {
                // Create new player with sanitized name
                onCreate(sanitized)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .heightIn(max = 500.dp)
    ) {
        Text(text = "Choose Player", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search or add new...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                val sanitized = sanitizePlayerName(searchQuery)
                if (sanitized != null && !allPlayers.any { it.isActive && playerNamesEqual(sanitized, it.name) }) {
                    IconButton(onClick = { 
                        handleCreateOrReactivate(searchQuery)
                        searchQuery = ""
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add New")
                    }
                }
            },
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val sanitizedQuery = sanitizePlayerName(searchQuery)
            if (sanitizedQuery != null && !allPlayers.any { it.isActive && playerNamesEqual(sanitizedQuery, it.name) }) {
                item {
                    // Check if there's a deactivated player with this sanitized name
                    val deactivatedPlayer = allPlayers.find { existingPlayer ->
                        !existingPlayer.isActive && playerNamesEqual(sanitizedQuery, existingPlayer.name)
                    }
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                handleCreateOrReactivate(searchQuery)
                                searchQuery = ""
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Text(
                                text = if (deactivatedPlayer != null && onReactivate != null) {
                                    "Reactivate \"$searchQuery\""
                                } else {
                                    "Create \"$searchQuery\""
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            items(filteredPlayers) { player ->
                val playerColor = remember(player.avatarColor) { parseColor(player.avatarColor) }
                val contentColor = if (playerColor.luminance() > 0.5f) Color.Black.copy(alpha = 0.8f) else Color.White

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(player) },
                    colors = CardDefaults.cardColors(
                        containerColor = playerColor,
                        contentColor = contentColor
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp),
                            color = contentColor.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = player.name.firstOrNull()?.uppercase() ?: "P",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor
                                )
                            }
                        }
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}
