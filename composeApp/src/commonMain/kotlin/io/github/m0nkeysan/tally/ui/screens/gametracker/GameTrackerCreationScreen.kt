package io.github.m0nkeysan.tally.ui.screens.gametracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.GameIcons
import io.github.m0nkeysan.tally.core.domain.model.DurationMode
import io.github.m0nkeysan.tally.core.domain.model.ScoringLogic
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.cd_toggle_collapse
import io.github.m0nkeysan.tally.generated.resources.cd_toggle_expand
import io.github.m0nkeysan.tally.generated.resources.game_creation_field_game_name
import io.github.m0nkeysan.tally.generated.resources.game_tracker_creation_duration_mode
import io.github.m0nkeysan.tally.generated.resources.game_tracker_creation_name_default
import io.github.m0nkeysan.tally.generated.resources.game_tracker_creation_round_count_hint
import io.github.m0nkeysan.tally.generated.resources.game_tracker_creation_scoring_logic
import io.github.m0nkeysan.tally.generated.resources.game_tracker_creation_settings
import io.github.m0nkeysan.tally.generated.resources.game_tracker_creation_target_hint
import io.github.m0nkeysan.tally.generated.resources.game_tracker_creation_target_score
import io.github.m0nkeysan.tally.generated.resources.game_tracker_creation_title
import io.github.m0nkeysan.tally.generated.resources.game_tracker_duration_fixed
import io.github.m0nkeysan.tally.generated.resources.game_tracker_duration_infinite
import io.github.m0nkeysan.tally.generated.resources.game_tracker_scoring_high
import io.github.m0nkeysan.tally.generated.resources.game_tracker_scoring_low
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
    var isSettingsExpanded by remember { mutableStateOf(false) }
    
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

            // Collapsible Game Settings Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Card Header (always visible - clickable)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isSettingsExpanded = !isSettingsExpanded }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(Res.string.game_tracker_creation_settings),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (isSettingsExpanded) GameIcons.ExpandLess else GameIcons.ExpandMore,
                            contentDescription = if (isSettingsExpanded) 
                                stringResource(Res.string.cd_toggle_collapse) 
                                else stringResource(Res.string.cd_toggle_expand)
                        )
                    }
                    
                    // Expanded content (conditional)
                    if (isSettingsExpanded) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Scoring Logic
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(Res.string.game_tracker_creation_scoring_logic),
                                style = MaterialTheme.typography.titleMedium
                            )

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
                            
                            // Duration Mode
                            Text(
                                text = stringResource(Res.string.game_tracker_creation_duration_mode),
                                style = MaterialTheme.typography.titleMedium
                            )
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
                    }
                }
            }

            // Player Selection
            FlexiblePlayerSelector(
                minPlayers = 2,
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
        }
    )
}
