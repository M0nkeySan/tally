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
import io.github.m0nkeysan.gamekeeper.core.domain.GameConfig
import io.github.m0nkeysan.gamekeeper.ui.utils.generateRandomHexColor
import io.github.m0nkeysan.gamekeeper.ui.strings.AppStrings

@Composable
fun YahtzeeGameCreationScreen(
    onBack: () -> Unit,
    onGameCreated: (String) -> Unit,
    viewModel: YahtzeeGameViewModel = viewModel { YahtzeeGameViewModel() }
) {
    var gameName by remember { mutableStateOf(getCurrentDateTimeString()) }
    var selectedPlayers by remember { mutableStateOf<List<Player?>>(emptyList()) }
    val allPlayers by viewModel.allPlayers.collectAsState(emptyList())

    val canCreate = gameName.isNotBlank() && selectedPlayers.size in GameConfig.YAHTZEE_MIN_PLAYERS..GameConfig.YAHTZEE_MAX_PLAYERS && selectedPlayers.all { it != null }

    GameCreationTemplate(
        title = AppStrings.YAHTZEE_NEW_GAME_TITLE,
        onBack = onBack,
        onCreate = {
            val finalPlayers = selectedPlayers.filterNotNull()
            if (finalPlayers.size in GameConfig.YAHTZEE_MIN_PLAYERS..GameConfig.YAHTZEE_MAX_PLAYERS) {
                viewModel.createGame(
                    name = gameName.ifBlank { AppStrings.YAHTZEE_GAME_NAME_DEFAULT },
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
                label = { Text(AppStrings.GAME_CREATION_FIELD_GAME_NAME) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

              FlexiblePlayerSelector(
                 minPlayers = GameConfig.YAHTZEE_MIN_PLAYERS,
                 maxPlayers = GameConfig.YAHTZEE_MAX_PLAYERS,
                allPlayers = allPlayers,
                onPlayersChange = { players ->
                    selectedPlayers = players
                },
                onCreatePlayer = { name ->
                    val newPlayer = Player.create(name, generateRandomHexColor())
                    viewModel.savePlayer(newPlayer)
                }
            )
        }
    )
}
