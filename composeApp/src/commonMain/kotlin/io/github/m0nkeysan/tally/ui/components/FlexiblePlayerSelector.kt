package io.github.m0nkeysan.tally.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.cd_add_player
import io.github.m0nkeysan.tally.generated.resources.cd_remove_player
import io.github.m0nkeysan.tally.generated.resources.error_player_count_range
import io.github.m0nkeysan.tally.generated.resources.players_count_format
import io.github.m0nkeysan.tally.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource

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
                text = stringResource(Res.string.players_count_format, selectedPlayers.size, maxPlayers),
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
                        contentDescription = stringResource(Res.string.cd_remove_player),
                        tint = if (selectedPlayers.size > minPlayers) 
                            MaterialTheme.colorScheme.error 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
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
                        contentDescription = stringResource(
                            Res.string.cd_add_player),
                        tint = if (selectedPlayers.size < maxPlayers) 
                            LocalCustomColors.current.success 
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Validation error message
        if (showError) {
            Text(
                text = stringResource(Res.string.error_player_count_range, minPlayers, maxPlayers),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
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
