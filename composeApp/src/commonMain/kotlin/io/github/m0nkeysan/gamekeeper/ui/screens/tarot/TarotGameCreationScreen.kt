package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun TarotGameCreationScreen(
    onBack: () -> Unit,
    onGameCreated: (String) -> Unit,
    viewModel: TarotGameViewModel = viewModel { TarotGameViewModel() }
) {
    var gameName by remember { mutableStateOf(getCurrentDateTimeString()) }
    var playerCount by remember { mutableStateOf(4) }
    val playerNames = remember { mutableStateListOf("", "", "", "", "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Tarot Game") },
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

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Number of Players", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(3, 4, 5).forEach { count ->
                        FilterChip(
                            selected = playerCount == count,
                            onClick = { playerCount = count },
                            label = { Text("$count Players") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Players", style = MaterialTheme.typography.titleMedium)
                for (i in 0 until playerCount) {
                    OutlinedTextField(
                        value = playerNames[i],
                        onValueChange = { playerNames[i] = it },
                        label = { Text("Player ${i + 1} Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Button(
                onClick = {
                    val names = playerNames.take(playerCount).map { it.ifBlank { "Player" } }
                    viewModel.createGame(
                        name = gameName.ifBlank { "Tarot Game" },
                        playerCount = playerCount,
                        playerNames = names,
                        onCreated = onGameCreated
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = gameName.isNotBlank() && playerNames.take(playerCount).all { it.isNotBlank() }
            ) {
                Text("Start Game")
            }
        }
    }
}
