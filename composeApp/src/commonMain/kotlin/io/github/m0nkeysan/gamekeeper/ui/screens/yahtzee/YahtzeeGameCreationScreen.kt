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
import io.github.m0nkeysan.gamekeeper.platform.getCurrentDateTimeString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YahtzeeGameCreationScreen(
    onBack: () -> Unit,
    onGameCreated: (String) -> Unit,
    viewModel: YahtzeeGameViewModel = viewModel { YahtzeeGameViewModel() }
) {
    var gameName by remember { mutableStateOf(getCurrentDateTimeString()) }
    val playerNames = remember { mutableStateListOf("Player 1") }
    val maxPlayers = 8

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
                    
                    if (playerNames.size < maxPlayers) {
                        TextButton(
                            onClick = { 
                                playerNames.add("Player ${playerNames.size + 1}")
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Add Player")
                        }
                    }
                }

                playerNames.forEachIndexed { index, name ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { playerNames[index] = it },
                            label = { Text("Player ${index + 1} Name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        
                        if (playerNames.size > 1) {
                            IconButton(onClick = { playerNames.removeAt(index) }) {
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
                    viewModel.createGame(
                        name = gameName.ifBlank { "Yahtzee Game" },
                        playerCount = playerNames.size,
                        playerNames = playerNames.toList(),
                        onCreated = onGameCreated
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = gameName.isNotBlank() && playerNames.all { it.isNotBlank() }
            ) {
                Text("Start Game")
            }
        }
    }
}
