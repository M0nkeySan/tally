package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.model.*
import io.github.m0nkeysan.gamekeeper.ui.components.parseColor
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarotRoundAdditionScreen(
    gameId: String,
    roundId: String? = null,
    onBack: () -> Unit,
    onRoundAdded: () -> Unit,
    viewModel: TarotScoringViewModel = viewModel { TarotScoringViewModel() }
) {
    val state by viewModel.state.collectAsState()
    
    var takerIndex by remember { mutableStateOf<Int?>(null) }
    var bid by remember { mutableStateOf(TarotBid.PRISE) }
    var bouts by remember { mutableStateOf(0) }
    var pointsAtq by remember { mutableStateOf("46") }
    var hasPetitAuBout by remember { mutableStateOf(false) }
    var hasPoignee by remember { mutableStateOf(false) }
    var poigneeLevel by remember { mutableStateOf(PoigneeLevel.SIMPLE) }
    var chelem by remember { mutableStateOf(ChelemType.NONE) }
    var calledPlayerIndex by remember { mutableStateOf<Int?>(null) }

    val isEditMode = roundId != null
    var isDataLoaded by remember { mutableStateOf(false) }

    // Pre-fill data if in edit mode
    LaunchedEffect(state.rounds) {
        if (isEditMode && !isDataLoaded && state.rounds.isNotEmpty()) {
            val round = state.rounds.find { it.id == roundId }
            if (round != null) {
                takerIndex = round.takerPlayerId.toIntOrNull()
                bid = round.bid
                bouts = round.bouts
                pointsAtq = round.pointsScored.toString()
                hasPetitAuBout = round.hasPetitAuBout
                hasPoignee = round.hasPoignee
                poigneeLevel = round.poigneeLevel ?: PoigneeLevel.SIMPLE
                chelem = round.chelem
                calledPlayerIndex = round.calledPlayerId?.toIntOrNull()
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
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Round" else "Add Round", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = {
                        takerIndex?.let { tIdx ->
                            viewModel.addRoundManual(
                                roundId = roundId,
                                takerIndex = tIdx,
                                bid = bid,
                                bouts = bouts,
                                pointsScored = pointsAtq.toIntOrNull() ?: 0,
                                hasPetitAuBout = hasPetitAuBout,
                                hasPoignee = hasPoignee,
                                poigneeLevel = if (hasPoignee) poigneeLevel else null,
                                chelem = chelem,
                                calledPlayerIndex = if (state.game?.playerCount == 5) calledPlayerIndex else null
                            )
                            onRoundAdded()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = statusColor),
                    enabled = takerIndex != null && 
                             (state.game?.playerCount != 5 || calledPlayerIndex != null) &&
                             pointsAtq.toFloatOrNull() != null
                ) {
                    Text("SAVE ROUND", fontWeight = FontWeight.Bold, color = Color.White)
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
                Section(title = "PLAYERS") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Taker
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("TAKER", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                state.players.forEachIndexed { index, player ->
                                    val isSelected = takerIndex == index
                                    val playerColor = remember(player.avatarColor) { parseColor(player.avatarColor) }
                                    val contentColor = if (playerColor.luminance() > 0.5f) Color.Black else Color.White

                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { takerIndex = index },
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
                                Text("CALLED PLAYER", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    state.players.forEachIndexed { index, player ->
                                        val isSelected = calledPlayerIndex == index
                                        val playerColor = remember(player.avatarColor) { parseColor(player.avatarColor) }
                                        val contentColor = if (playerColor.luminance() > 0.5f) Color.Black else Color.White

                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { calledPlayerIndex = index },
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
                Section(title = "BID") {
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
                Section(title = "BOUTS") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        (0..3).forEach { b ->
                            FilterChip(
                                selected = bouts == b,
                                onClick = { bouts = b },
                                label = { Text("$b Bouts") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 4. Points
                Section(title = "POINTS") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = pointsAtq,
                                onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) pointsAtq = it },
                                label = { Text("Attacker") },
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
                                Text("DEFENSE", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
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
                            text = if (isWinner) "CONTRACT WON (+${(pAtqFloat - targetPoints).toInt()} pts)" 
                                   else "CONTRACT LOST (${(pAtqFloat - targetPoints).toInt()} pts)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // 5. Announces
                Section(title = "ANNOUNCES") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Petit au bout
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = hasPetitAuBout, onCheckedChange = { hasPetitAuBout = it })
                            Text("Petit au bout")
                        }
                        
                        // Poignée
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = hasPoignee, onCheckedChange = { hasPoignee = it })
                                Text("Poignée")
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
                            Text("Chelem", style = MaterialTheme.typography.bodyMedium)
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
