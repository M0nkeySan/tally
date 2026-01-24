package io.github.m0nkeysan.tally.ui.screens.tarot

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.platform.getCurrentDateTimeString
import io.github.m0nkeysan.tally.ui.components.FlexiblePlayerSelector
import io.github.m0nkeysan.tally.ui.screens.common.GameCreationTemplate
import io.github.m0nkeysan.tally.core.domain.GameConfig
import io.github.m0nkeysan.tally.ui.utils.generateRandomHexColor
import org.jetbrains.compose.resources.stringResource
import io.github.m0nkeysan.tally.generated.resources.game_creation_field_game_name
import io.github.m0nkeysan.tally.generated.resources.game_creation_new_tarot_title
import io.github.m0nkeysan.tally.generated.resources.game_creation_tarot_name_default
import io.github.m0nkeysan.tally.generated.resources.Res

@Composable
fun TarotGameCreationScreen(
    onBack: () -> Unit,
    onGameCreated: (String) -> Unit,
    viewModel: TarotGameViewModel = viewModel { TarotGameViewModel() }
) {
    var gameName by remember { mutableStateOf(getCurrentDateTimeString()) }
    var selectedPlayers by remember { mutableStateOf<List<Player?>>(emptyList()) }
    val allPlayers by viewModel.allPlayers.collectAsState(emptyList())

    val canCreate = gameName.isNotBlank() && selectedPlayers.size in GameConfig.TAROT_MIN_PLAYERS..GameConfig.TAROT_MAX_PLAYERS && selectedPlayers.all { it != null }

    val defaultGameName = stringResource(Res.string.game_creation_tarot_name_default)

    GameCreationTemplate(
        title = stringResource(Res.string.game_creation_new_tarot_title),
        onBack = onBack,
        onCreate = {
            val finalPlayers: List<Player> = selectedPlayers.filterNotNull()
            if (finalPlayers.size in GameConfig.TAROT_MIN_PLAYERS..GameConfig.TAROT_MAX_PLAYERS) {
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
                 minPlayers = GameConfig.TAROT_MIN_PLAYERS,
                 maxPlayers = GameConfig.TAROT_MAX_PLAYERS,
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
