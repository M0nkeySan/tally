package io.github.m0nkeysan.gamekeeper.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.core.model.Player

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSelectorField(
    label: String,
    selectedPlayer: Player?,
    allPlayers: List<Player>,
    onPlayerSelected: (Player) -> Unit,
    onNewPlayerCreated: (String) -> Unit,
    modifier: Modifier = Modifier,
    excludedPlayerIds: Set<String> = emptySet()
) {
    var showSheet by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showSheet = true },
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedPlayer != null) {
                PlayerAvatar(color = selectedPlayer.avatarColor, letter = selectedPlayer.name.firstOrNull()?.toString() ?: "P")
                Column {
                    Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(text = selectedPlayer.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
                }
                Text(text = "Select $label", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            }
            
            Spacer(Modifier.weight(1f))
            
            TextButton(onClick = { showSheet = true }) {
                Text("CHANGE")
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
                    onNewPlayerCreated(name)
                    showSheet = false
                }
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
    onCreate: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredPlayers = remember(searchQuery, allPlayers, excludedPlayerIds) {
        val baseList = allPlayers.filter { it.id !in excludedPlayerIds }
        if (searchQuery.isBlank()) baseList
        else baseList.filter { it.name.contains(searchQuery, ignoreCase = true) }
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
                if (searchQuery.isNotBlank() && !allPlayers.any { it.name.equals(searchQuery, ignoreCase = true) }) {
                    IconButton(onClick = { onCreate(searchQuery) }) {
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
            if (searchQuery.isNotBlank() && !allPlayers.any { it.name.equals(searchQuery, ignoreCase = true) }) {
                item {
                    ListItem(
                        headlineContent = { Text("Create \"$searchQuery\"") },
                        leadingContent = { 
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, null, tint = Color.White)
                            }
                        },
                        modifier = Modifier.clickable { onCreate(searchQuery) }
                    )
                }
            }

            items(filteredPlayers) { player ->
                ListItem(
                    headlineContent = { Text(player.name) },
                    leadingContent = { 
                        PlayerAvatar(color = player.avatarColor, letter = player.name.firstOrNull()?.toString() ?: "P")
                    },
                    trailingContent = {
                        // Icon(Icons.Default.ChevronRight, null)
                    },
                    modifier = Modifier.clickable { onSelect(player) }
                )
            }
        }
    }
}
