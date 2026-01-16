package io.github.m0nkeysan.gamekeeper.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors
import io.github.m0nkeysan.gamekeeper.ui.strings.AppStrings

/**
 * Flexible player selector that supports min/max player constraints.
 * - Tarot: minPlayers=3, maxPlayers=5
 * - Yahtzee: minPlayers=1, maxPlayers=8
 */
@Composable
fun FlexiblePlayerSelector(
    minPlayers: Int,
    maxPlayers: Int,
    allPlayers: List<Player>,
    onPlayersChange: (List<Player?>) -> Unit,
    onCreatePlayer: (String) -> Unit,
    modifier: Modifier = Modifier,
    onReactivatePlayer: ((Player) -> Unit)? = null
) {
    var selectedPlayers by remember { mutableStateOf(List<Player?>(minPlayers) { null }) }
    var showError by remember { mutableStateOf(false) }
    
    // Update callback when players change
    LaunchedEffect(selectedPlayers) {
        onPlayersChange(selectedPlayers)
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = AppStrings.PLAYERS_COUNT_FORMAT.format(selectedPlayers.size, maxPlayers),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Remove button
                IconButton(
                    onClick = {
                        if (selectedPlayers.size > minPlayers) {
                            selectedPlayers = selectedPlayers.dropLast(1)
                            showError = false
                        } else {
                            showError = true
                        }
                    },
                    enabled = selectedPlayers.size > minPlayers
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = AppStrings.CD_REMOVE_PLAYER,
                        tint = if (selectedPlayers.size > minPlayers) 
                            GameColors.Error 
                        else 
                            GameColors.TextSecondary
                    )
                }
                
                // Add button
                IconButton(
                    onClick = {
                        if (selectedPlayers.size < maxPlayers) {
                            selectedPlayers = selectedPlayers + null
                            showError = false
                        } else {
                            showError = true
                        }
                    },
                    enabled = selectedPlayers.size < maxPlayers
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = AppStrings.CD_ADD_PLAYER,
                        tint = if (selectedPlayers.size < maxPlayers) 
                            GameColors.Success 
                        else 
                            GameColors.TextSecondary
                    )
                }
            }
        }
        
        // Validation error message
        if (showError) {
            Text(
                text = AppStrings.ERROR_PLAYER_COUNT_RANGE.format(minPlayers, maxPlayers),
                style = MaterialTheme.typography.bodySmall,
                color = GameColors.Error
            )
        }
        
        // Player selector fields
        selectedPlayers.forEachIndexed { index, player ->
            PlayerSelectorField(
                label = "Player ${index + 1}",
                selectedPlayer = player,
                allPlayers = allPlayers,
                onPlayerSelected = { newPlayer ->
                    selectedPlayers = selectedPlayers.toMutableList().apply {
                        set(index, newPlayer)
                    }
                },
                onNewPlayerCreated = { name ->
                    onCreatePlayer(name)
                },
                excludedPlayerIds = selectedPlayers
                    .filterNotNull()
                    .filter { it != player }
                    .map { it.id }
                    .toSet(),
                onReactivatePlayer = onReactivatePlayer
            )
        }
    }
}
