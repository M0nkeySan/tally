package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.platform.getCurrentDateTimeString
import io.github.m0nkeysan.gamekeeper.ui.components.PlayerSelectorField
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YahtzeeGameCreationScreen(
    onBack: () -> Unit,
    onGameCreated: (String) -> Unit,
    viewModel: YahtzeeGameViewModel = viewModel { YahtzeeGameViewModel() }
) {
    var gameName by remember { mutableStateOf(getCurrentDateTimeString()) }
    val selectedPlayers = remember { mutableStateListOf<Player?>(null) }
    val allPlayers by viewModel.allPlayers.collectAsState(emptyList())
    val maxPlayers = 8

    fun generateRandomColor(): String {
        val color = Random.nextInt(0xFFFFFF)
        return "#${color.toString(16).padStart(6, '0')}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Yahtzee Game") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            OutlinedTextField(
                value = gameName,
                onValueChange = { gameName = it },
                label = { Text("Game Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Players", style = MaterialTheme.typography.titleMedium)
                    
                    if (selectedPlayers.size < maxPlayers) {
                        TextButton(
                            onClick = { 
                                selectedPlayers.add(null)
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Add Player")
                        }
                    }
                }

                selectedPlayers.forEachIndexed { index, player ->
                    val excludedIds = selectedPlayers.filterIndexed { i, p -> i != index && p != null }.map { it!!.id }.toSet()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PlayerSelectorField(
                            label = "Player ${index + 1}",
                            selectedPlayer = player,
                            allPlayers = allPlayers,
                            excludedPlayerIds = excludedIds,
                            onPlayerSelected = { selectedPlayers[index] = it },
                            onNewPlayerCreated = { name ->
                                val newPlayer = Player.create(name, generateRandomColor())
                                selectedPlayers[index] = newPlayer
                            },
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (selectedPlayers.size > 1) {
                            IconButton(onClick = { selectedPlayers.removeAt(index) }) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Remove Player",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val finalPlayers = selectedPlayers.filterNotNull()
                    if (finalPlayers.isNotEmpty()) {
                        viewModel.createGame(
                            name = gameName.ifBlank { "Yahtzee Game" },
                            playerCount = finalPlayers.size,
                            players = finalPlayers,
                            onCreated = onGameCreated
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = gameName.isNotBlank() && selectedPlayers.all { it != null }
            ) {
                Text("Start Game")
            }
        }
    }
}
