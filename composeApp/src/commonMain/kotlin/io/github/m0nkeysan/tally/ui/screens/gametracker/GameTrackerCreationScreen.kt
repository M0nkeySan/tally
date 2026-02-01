package io.github.m0nkeysan.tally.ui.screens.gametracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.core.domain.model.DurationMode
import io.github.m0nkeysan.tally.core.domain.model.ScoringLogic
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.generated.resources.*
import io.github.m0nkeysan.tally.platform.getCurrentDateTimeString
import io.github.m0nkeysan.tally.ui.components.FlexiblePlayerSelector
import io.github.m0nkeysan.tally.ui.screens.common.GameCreationTemplate
import io.github.m0nkeysan.tally.ui.utils.generateRandomHexColor
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameTrackerCreationScreen(
    onBack: () -> Unit,
    onGameCreated: (String) -> Unit,
    viewModel: GameTrackerSelectionViewModel = viewModel { GameTrackerSelectionViewModel() }
) {
    var gameName by remember { mutableStateOf(getCurrentDateTimeString()) }
    var selectedPlayers by remember { mutableStateOf<List<Player?>>(emptyList()) }
    var scoringLogic by remember { mutableStateOf(ScoringLogic.HIGH_SCORE_WINS) }
    var enableTargetScore by remember { mutableStateOf(false) }
    var targetScoreText by remember { mutableStateOf("") }
    var durationMode by remember { mutableStateOf(DurationMode.INFINITE) }
    var roundCountText by remember { mutableStateOf("10") }
    
    val allPlayers by viewModel.allPlayers.collectAsState(emptyList())
    val defaultGameName = stringResource(Res.string.game_tracker_creation_name_default)

    // Validation
    val hasPlayers = selectedPlayers.isNotEmpty() && selectedPlayers.all { it != null }
    val targetScoreValid = !enableTargetScore || targetScoreText.toIntOrNull() != null
    val roundCountValid = durationMode != DurationMode.FIXED_ROUNDS || 
                          (roundCountText.toIntOrNull() != null && roundCountText.toInt() > 0)
    val canCreate = gameName.isNotBlank() && hasPlayers && targetScoreValid && roundCountValid

    GameCreationTemplate(
        title = stringResource(Res.string.game_tracker_creation_title),
        onBack = onBack,
        onCreate = {
            val finalPlayers: List<Player> = selectedPlayers.filterNotNull()
            if (finalPlayers.isNotEmpty()) {
                viewModel.createGame(
                    name = gameName.ifBlank { defaultGameName },
                    players = finalPlayers,
                    scoringLogic = scoringLogic,
                    targetScore = if (enableTargetScore) targetScoreText.toIntOrNull() else null,
                    durationMode = durationMode,
                    fixedRoundCount = if (durationMode == DurationMode.FIXED_ROUNDS) roundCountText.toIntOrNull() else null,
                    onCreated = onGameCreated
                )
            }
        },
        canCreate = canCreate,
        error = null,
        content = {
            // Game Name
            OutlinedTextField(
                value = gameName,
                onValueChange = { gameName = it },
                label = { Text(stringResource(Res.string.game_creation_field_game_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Player Selection
            FlexiblePlayerSelector(
                minPlayers = 1,
                maxPlayers = 10,
                allPlayers = allPlayers,
                onPlayersChange = { players ->
                    selectedPlayers = players
                },
                onCreatePlayer = { name ->
                    val newPlayer = Player.create(name, generateRandomHexColor())
                    viewModel.savePlayer(newPlayer)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Scoring Logic
            Text(
                text = stringResource(Res.string.game_tracker_creation_scoring_logic),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = scoringLogic == ScoringLogic.HIGH_SCORE_WINS,
                    onClick = { scoringLogic = ScoringLogic.HIGH_SCORE_WINS },
                    label = { Text(stringResource(Res.string.game_tracker_scoring_high)) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = scoringLogic == ScoringLogic.LOW_SCORE_WINS,
                    onClick = { scoringLogic = ScoringLogic.LOW_SCORE_WINS },
                    label = { Text(stringResource(Res.string.game_tracker_scoring_low)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Target Score (Optional)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.game_tracker_creation_target_score),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = enableTargetScore,
                    onCheckedChange = { enableTargetScore = it }
                )
            }

            if (enableTargetScore) {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = targetScoreText,
                    onValueChange = { targetScoreText = it },
                    label = { Text(stringResource(Res.string.game_tracker_creation_target_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = targetScoreText.isNotEmpty() && targetScoreText.toIntOrNull() == null
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Duration Mode
            Text(
                text = stringResource(Res.string.game_tracker_creation_duration_mode),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = durationMode == DurationMode.INFINITE,
                    onClick = { durationMode = DurationMode.INFINITE },
                    label = { Text(stringResource(Res.string.game_tracker_duration_infinite)) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = durationMode == DurationMode.FIXED_ROUNDS,
                    onClick = { durationMode = DurationMode.FIXED_ROUNDS },
                    label = { Text(stringResource(Res.string.game_tracker_duration_fixed)) },
                    modifier = Modifier.weight(1f)
                )
            }

            if (durationMode == DurationMode.FIXED_ROUNDS) {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = roundCountText,
                    onValueChange = { roundCountText = it },
                    label = { Text(stringResource(Res.string.game_tracker_creation_round_count_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = roundCountText.isEmpty() || roundCountText.toIntOrNull() == null || roundCountText.toInt() <= 0
                )
            }
        }
    )
}
