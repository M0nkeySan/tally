package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.platform.getCurrentDateTimeString
import io.github.m0nkeysan.gamekeeper.ui.components.FlexiblePlayerSelector
import io.github.m0nkeysan.gamekeeper.ui.screens.common.GameCreationTemplate
import io.github.m0nkeysan.gamekeeper.ui.theme.GameConfig
import kotlin.random.Random

@Composable
fun YahtzeeGameCreationScreen(
    onBack: () -> Unit,
    onGameCreated: (String) -> Unit,
    viewModel: YahtzeeGameViewModel = viewModel { YahtzeeGameViewModel() }
) {
    var gameName by remember { mutableStateOf(getCurrentDateTimeString()) }
    var selectedPlayers by remember { mutableStateOf<List<Player?>>(emptyList()) }
    val allPlayers by viewModel.allPlayers.collectAsState(emptyList())

    fun generateRandomColor(): String {
        val color = Random.nextInt(0xFFFFFF)
        return "#${color.toString(16).padStart(6, '0')}"
    }

    val canCreate = gameName.isNotBlank() && selectedPlayers.size in GameConfig.yahtzeeMinPlayers..GameConfig.yahtzeeMaxPlayers && selectedPlayers.all { it != null }

    GameCreationTemplate(
        title = "New Yahtzee Game",
        onBack = onBack,
        onCreate = {
            val finalPlayers = selectedPlayers.filterNotNull()
            if (finalPlayers.size in GameConfig.yahtzeeMinPlayers..GameConfig.yahtzeeMaxPlayers) {
                viewModel.createGame(
                    name = gameName.ifBlank { "Yahtzee Game" },
                    playerCount = finalPlayers.size,
                    players = finalPlayers,
                    onCreated = onGameCreated
                )
            }
        },
        canCreate = canCreate,
        error = null,
        content = {
            OutlinedTextField(
                value = gameName,
                onValueChange = { gameName = it },
                label = { Text("Game Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

             FlexiblePlayerSelector(
                minPlayers = GameConfig.yahtzeeMinPlayers,
                maxPlayers = GameConfig.yahtzeeMaxPlayers,
                allPlayers = allPlayers,
                onPlayersChange = { players ->
                    selectedPlayers = players
                },
                onCreatePlayer = { name ->
                    val newPlayer = Player.create(name, generateRandomColor())
                    viewModel.savePlayer(newPlayer)
                }
            )
        }
    )
}
