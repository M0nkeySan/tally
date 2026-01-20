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
import org.jetbrains.compose.resources.stringResource
import io.github.m0nkeysan.gamekeeper.generated.resources.game_creation_field_game_name
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_game_name_default
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_new_game_title
import io.github.m0nkeysan.gamekeeper.generated.resources.Res

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

    val defaultGameName = stringResource(Res.string.yahtzee_game_name_default)

    GameCreationTemplate(
        title = stringResource(Res.string.yahtzee_new_game_title),
        onBack = onBack,
        onCreate = {
            val finalPlayers = selectedPlayers.filterNotNull()
            if (finalPlayers.size in GameConfig.YAHTZEE_MIN_PLAYERS..GameConfig.YAHTZEE_MAX_PLAYERS) {
                viewModel.createGame(
                    name = gameName.ifBlank { defaultGameName },
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
                label = { Text(stringResource(Res.string.game_creation_field_game_name)) },
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
