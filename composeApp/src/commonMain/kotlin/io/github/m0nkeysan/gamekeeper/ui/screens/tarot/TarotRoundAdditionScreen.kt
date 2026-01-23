package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.core.model.ChelemType
import io.github.m0nkeysan.gamekeeper.core.model.PoigneeLevel
import io.github.m0nkeysan.gamekeeper.core.model.TarotBid
import io.github.m0nkeysan.gamekeeper.generated.resources.Res
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_round_action_save
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_round_announce_petit_au_bout
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_round_announce_poignee
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_round_contract_lost
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_round_contract_won
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_round_field_attacker_score
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_round_label_bouts
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_round_label_called_player
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_round_label_chelem
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_round_label_defense
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_round_label_taker
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_round_section_announces
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_round_section_bid
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_round_section_bouts
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_round_section_players
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_round_section_points
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors
import io.github.m0nkeysan.gamekeeper.ui.utils.parseColor
import org.jetbrains.compose.resources.stringResource

@Composable
fun TarotRoundAdditionScreen(
    gameId: String,
    roundId: String? = null,
    onBack: () -> Unit,
    onRoundAdded: () -> Unit,
    viewModel: TarotScoringViewModel = viewModel { TarotScoringViewModel() }
) {
    val state by viewModel.state.collectAsState()
    
    var takerPlayerId by remember { mutableStateOf<String?>(null) }
    var bid by remember { mutableStateOf(TarotBid.PRISE) }
    var bouts by remember { mutableIntStateOf(0) }
    var pointsAtq by remember { mutableStateOf("46") }
    var hasPetitAuBout by remember { mutableStateOf(false) }
    var hasPoignee by remember { mutableStateOf(false) }
    var poigneeLevel by remember { mutableStateOf(PoigneeLevel.SIMPLE) }
    var chelem by remember { mutableStateOf(ChelemType.NONE) }
    var calledPlayerId by remember { mutableStateOf<String?>(null) }

    val isEditMode = roundId != null
    var isDataLoaded by remember { mutableStateOf(false) }

    // Handle system back gesture
    BackHandler {
        onBack()
    }

    // Pre-fill data if in edit mode
    LaunchedEffect(state.rounds) {
        if (isEditMode && !isDataLoaded && state.rounds.isNotEmpty()) {
            val round = state.rounds.find { it.id == roundId }
            if (round != null) {
                takerPlayerId = round.takerPlayerId
                bid = round.bid
                bouts = round.bouts
                pointsAtq = round.pointsScored.toString()
                hasPetitAuBout = round.hasPetitAuBout
                hasPoignee = round.hasPoignee
                poigneeLevel = round.poigneeLevel ?: PoigneeLevel.SIMPLE
                chelem = round.chelem
                calledPlayerId = round.calledPlayerId
                isDataLoaded = true
            }
        }
    }

    LaunchedEffect(gameId) {
        viewModel.loadGame(gameId)
    }

    val pAtqFloat = pointsAtq.toFloatOrNull() ?: 0f
    val targetPoints = when (bouts) {
        0 -> 56f
        1 -> 51f
        2 -> 41f
        3 -> 36f
        else -> 56f
    }
    val isWinner = pAtqFloat >= targetPoints
    val statusColor = if (isWinner) GameColors.Success else GameColors.Error

    Scaffold(
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = {
                        takerPlayerId?.let { tId ->
                            viewModel.addRoundManual(
                                roundId = roundId,
                                takerPlayerId = tId,
                                bid = bid,
                                bouts = bouts,
                                pointsScored = pointsAtq.toIntOrNull() ?: 0,
                                hasPetitAuBout = hasPetitAuBout,
                                hasPoignee = hasPoignee,
                                poigneeLevel = if (hasPoignee) poigneeLevel else null,
                                chelem = chelem,
                                calledPlayerId = if (state.game?.playerCount == 5) calledPlayerId else null
                            )
                            onRoundAdded()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = statusColor),
                    enabled = takerPlayerId != null && 
                             (state.game?.playerCount != 5 || calledPlayerId != null) &&
                             pointsAtq.toFloatOrNull() != null
                ) {
                    Text(stringResource(Res.string.tarot_round_action_save), fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        if (state.game == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // 1. Taker & Called Player
                Section(title = stringResource(Res.string.tarot_round_section_players)) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Taker
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(Res.string.tarot_round_label_taker), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                state.players.forEach { player ->
                                    val isSelected = takerPlayerId == player.id
                                    val playerColor = remember(player.avatarColor) { parseColor(player.avatarColor) }
                                    val contentColor = if (playerColor.luminance() > 0.5f) Color.Black else Color.White

                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { takerPlayerId = player.id },
                                        label = { Text(player.name) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = playerColor,
                                            selectedLabelColor = contentColor
                                        )
                                    )
                                }
                            }
                        }

                        // Called Player (5 players only)
                        if (state.game?.playerCount == 5) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(stringResource(Res.string.tarot_round_label_called_player), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    state.players.forEach { player ->
                                        val isSelected = calledPlayerId == player.id
                                        val playerColor = remember(player.avatarColor) { parseColor(player.avatarColor) }
                                        val contentColor = if (playerColor.luminance() > 0.5f) Color.Black else Color.White

                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { calledPlayerId = player.id },
                                            label = { Text(player.name) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = playerColor,
                                                selectedLabelColor = contentColor
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Bid Selection
                Section(title = stringResource(Res.string.tarot_round_section_bid)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TarotBid.entries.forEach { b ->
                            FilterChip(
                                selected = bid == b,
                                onClick = { bid = b },
                                label = { 
                                    Text(
                                        text = b.displayName.replace(" ", "\n"),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                        lineHeight = 14.sp,
                                        style = MaterialTheme.typography.labelSmall
                                    ) 
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 3. Bouts
                Section(title = stringResource(Res.string.tarot_round_section_bouts)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        (0..3).forEach { b ->
                             FilterChip(
                                 selected = bouts == b,
                                 onClick = { bouts = b },
                                 label = { Text(stringResource(Res.string.tarot_round_label_bouts, b)) },
                                 modifier = Modifier.weight(1f)
                             )
                         }
                    }
                }

                // 4. Points
                Section(title = stringResource(Res.string.tarot_round_section_points)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = pointsAtq,
                                onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) pointsAtq = it },
                                label = { Text(stringResource(Res.string.tarot_round_field_attacker_score)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = statusColor,
                                    focusedLabelColor = statusColor
                                )
                            )
                            
                            val pAtq = pointsAtq.toIntOrNull() ?: 0
                            val pDef = (91 - pAtq).coerceAtLeast(0)
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(Res.string.tarot_round_label_defense), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                Text(
                                    text = pDef.toString(),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = statusColor.copy(alpha = 0.6f)
                                )
                            }
                        }
                        
                        Slider(
                            value = pointsAtq.toFloatOrNull() ?: 0f,
                            onValueChange = { pointsAtq = it.toInt().toString() },
                            valueRange = 0f..91f,
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = statusColor,
                                activeTrackColor = statusColor
                            )
                        )
                        
                        Text(
                             text = if (isWinner) stringResource(Res.string.tarot_round_contract_won, (pAtqFloat - targetPoints).toInt())
                                    else stringResource(Res.string.tarot_round_contract_lost, (pAtqFloat - targetPoints).toInt()),
                             style = MaterialTheme.typography.labelMedium,
                             fontWeight = FontWeight.Bold,
                             color = statusColor,
                             modifier = Modifier.fillMaxWidth(),
                             textAlign = TextAlign.Center
                         )
                    }
                }

                // 5. Announces
                Section(title = stringResource(Res.string.tarot_round_section_announces)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Petit au bout
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = hasPetitAuBout, onCheckedChange = { hasPetitAuBout = it })
                            Text(stringResource(Res.string.tarot_round_announce_petit_au_bout))
                        }
                        
                        // Poignée
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = hasPoignee, onCheckedChange = { hasPoignee = it })
                                Text(stringResource(Res.string.tarot_round_announce_poignee))
                            }
                            if (hasPoignee) {
                                Row(
                                    modifier = Modifier.padding(start = 32.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PoigneeLevel.entries.forEach { level ->
                                        FilterChip(
                                            selected = poigneeLevel == level,
                                            onClick = { poigneeLevel = level },
                                            label = { Text(level.displayName) }
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Chelem
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(Res.string.tarot_round_label_chelem), style = MaterialTheme.typography.bodyMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ChelemType.entries.forEach { type ->
                                    FilterChip(
                                        selected = chelem == type,
                                        onClick = { chelem = type },
                                        label = { Text(type.displayName) }
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Black,
            color = Color.Gray,
            letterSpacing = 1.2.sp
        )
        content()
    }
}
