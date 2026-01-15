package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.BackHandler
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeCategory
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.ui.components.GameKeeperSnackbarHost
import io.github.m0nkeysan.gamekeeper.ui.components.showErrorSnackbar
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors
import io.github.m0nkeysan.gamekeeper.ui.strings.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YahtzeeScoringScreen(
    gameId: String,
    onBack: () -> Unit,
    onGameFinished: () -> Unit,
    viewModel: YahtzeeScoringViewModel = viewModel { YahtzeeScoringViewModel() }
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle system back gesture
    BackHandler {
        onBack()
    }
    
    LaunchedEffect(gameId) {
        viewModel.loadGame(gameId)
    }

    // Show error in Snackbar
    LaunchedEffect(state.error) {
        state.error?.let { errorMessage ->
            showErrorSnackbar(snackbarHostState, errorMessage)
        }
    }

    Scaffold(
        snackbarHost = {
            GameKeeperSnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        if (state.game == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            YahtzeeGameView(
                modifier = Modifier.padding(paddingValues),
                state = state,
                viewModel = viewModel,
                onGameFinished = onGameFinished
            )
        }
    }
}

@Composable
fun YahtzeeGameView(
    modifier: Modifier = Modifier,
    state: YahtzeeScoringState,
    viewModel: YahtzeeScoringViewModel,
    onGameFinished: () -> Unit
) {
    val game = state.game!!
    val players = viewModel.getPlayers()
    var selectedPlayerId by remember(game.currentPlayerId) { 
        mutableStateOf(game.currentPlayerId) 
    }
    var showPlayerDropdown by remember { mutableStateOf(false) }
    
    // Auto-switch to current player when turn changes
    LaunchedEffect(state.game) {
        state.game.let { updatedGame ->
            selectedPlayerId = updatedGame.currentPlayerId
        }
    }

    LaunchedEffect(state.scores) {
        if (viewModel.isGameFinished()) {
            viewModel.markAsFinished()
            onGameFinished()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { 
                        val currentIndex = players.indexOfFirst { it.id == selectedPlayerId }
                        val nextIndex = if (currentIndex > 0) currentIndex - 1 else players.size - 1
                        selectedPlayerId = players.getOrNull(nextIndex)?.id ?: selectedPlayerId
                    }
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.KeyboardArrowLeft, 
                        contentDescription = AppStrings.YAHTZEE_SCORING_CD_PREVIOUS
                    )
                }

                Box(contentAlignment = Alignment.Center) {
                    Column(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { showPlayerDropdown = true }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val isTurn = selectedPlayerId == game.currentPlayerId
                            if (isTurn) {
                                Text(
                                    text = "●",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp
                                )
                            }
                            
                            Text(
                                text = players.find { it.id == selectedPlayerId }?.name ?: "Unknown",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                                contentDescription = "Select player dropdown"
                            )
                        }
                        
                        val selectedPlayerTotal = remember(selectedPlayerId, state.scores) {
                            "Total: ${viewModel.calculateTotalScore(selectedPlayerId)}"
                        }
                        Text(
                            text = selectedPlayerTotal,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val playerTotals = remember(state.scores) {
                        players.associateBy { it.id }.mapValues { (id, _) -> viewModel.calculateTotalScore(id) }
                    }
                    DropdownMenu(
                        expanded = showPlayerDropdown,
                        onDismissRequest = { showPlayerDropdown = false }
                    ) {
                        players.forEach { player ->
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (player.id == game.currentPlayerId) {
                                            Text("● ", color = MaterialTheme.colorScheme.primary)
                                        }
                                        Text(
                                            text = player.name,
                                            fontWeight = if (player.id == selectedPlayerId) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Spacer(Modifier.weight(1f))
                                        Text(
                                            text = (playerTotals[player.id] ?: 0).toString(),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.Gray
                                        )
                                    }
                                },
                                onClick = {
                                    selectedPlayerId = player.id
                                    showPlayerDropdown = false
                                }
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { 
                        val currentIndex = players.indexOfFirst { it.id == selectedPlayerId }
                        val nextIndex = (currentIndex + 1) % players.size
                        selectedPlayerId = players.getOrNull(nextIndex)?.id ?: selectedPlayerId
                    }
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.KeyboardArrowRight, 
                        contentDescription = AppStrings.YAHTZEE_SCORING_CD_NEXT
                    )
                }
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            val isViewingSelf = selectedPlayerId == game.currentPlayerId
            val displayedName = players.find { it.id == selectedPlayerId }?.name ?: "Unknown"
            
            Text(
                text = if (isViewingSelf) "Your Turn" else "Viewing $displayedName's card",
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { SectionHeader(AppStrings.YAHTZEE_SECTION_UPPER) }
            items(YahtzeeCategory.entries.filter { it.isUpperSection() }) { category ->
                val currentScore = state.scores[selectedPlayerId]?.get(category)
                ScoreRow(
                    category = category,
                    score = currentScore,
                    onScoreSet = { score -> 
                        // Move turn if the CURRENT player (not just selected) is scoring for the first time
                        val isMoveTurn = game.currentPlayerId == selectedPlayerId && currentScore == null
                        viewModel.submitScore(selectedPlayerId, category, score, isMoveTurn) 
                    }
                )
            }
            item {
                UpperBonusRow(
                    score = viewModel.calculateUpperScore(selectedPlayerId)
                )
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { SectionHeader(AppStrings.YAHTZEE_SECTION_LOWER) }
            items(YahtzeeCategory.entries.filter { it.isLowerSection() }) { category ->
                val currentScore = state.scores[selectedPlayerId]?.get(category)
                ScoreRow(
                    category = category,
                    score = currentScore,
                    onScoreSet = { score -> 
                        // Move turn if the CURRENT player (not just selected) is scoring for the first time
                        val isMoveTurn = game.currentPlayerId == selectedPlayerId && currentScore == null
                        viewModel.submitScore(selectedPlayerId, category, score, isMoveTurn) 
                    }
                )
            }
            
            item {
                val selectedPlayerTotal = remember(selectedPlayerId, state.scores) {
                    viewModel.calculateTotalScore(selectedPlayerId)
                }
                TotalScoreRow(
                    total = selectedPlayerTotal
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Black,
        color = GameColors.TextSecondary,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun ScoreRow(
    category: YahtzeeCategory,
    score: Int?,
    onScoreSet: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val options = remember(category) {
        when (category) {
            YahtzeeCategory.ACES -> (0..5).map { it * 1 }
            YahtzeeCategory.TWOS -> (0..5).map { it * 2 }
            YahtzeeCategory.THREES -> (0..5).map { it * 3 }
            YahtzeeCategory.FOURS -> (0..5).map { it * 4 }
            YahtzeeCategory.FIVES -> (0..5).map { it * 5 }
            YahtzeeCategory.SIXES -> (0..5).map { it * 6 }
            YahtzeeCategory.FULL_HOUSE -> listOf(0, 25)
            YahtzeeCategory.SMALL_STRAIGHT -> listOf(0, 30)
            YahtzeeCategory.LARGE_STRAIGHT -> listOf(0, 40)
            YahtzeeCategory.YAHTZEE -> listOf(0, 50, 150, 250, 350, 450)
            YahtzeeCategory.THREE_OF_KIND, YahtzeeCategory.FOUR_OF_KIND, YahtzeeCategory.CHANCE -> null // Manual entry
        }
    }

    if (showDialog) {
        if (options != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Select score for ${category.displayName}") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        options.forEach { value ->
                            Button(
                                onClick = {
                                    onScoreSet(value)
                                    showDialog = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (score == value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (score == value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Text(value.toString())
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(AppStrings.ACTION_CANCEL)
                    }
                }
            )
        } else {
            var scoreInput by remember { mutableStateOf(score?.toString() ?: "") }
            val inputScore = scoreInput.toIntOrNull()
            val isInvalid = inputScore != null && inputScore > 30
            
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Enter score for ${category.displayName}") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(
                            value = scoreInput,
                            onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) scoreInput = it },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text(AppStrings.YAHTZEE_PLACEHOLDER_DICE_SUM) },
                            isError = isInvalid,
                            supportingText = {
                                if (isInvalid) {
                                    Text("Score cannot be higher than 30")
                                }
                            }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val value = scoreInput.toIntOrNull() ?: 0
                            if (value <= 30) {
                                onScoreSet(value)
                                showDialog = false
                            }
                        },
                        enabled = !isInvalid
                    ) {
                        Text(AppStrings.ACTION_OK)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(AppStrings.ACTION_CANCEL)
                    }
                }
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        colors = CardDefaults.cardColors(
            containerColor = if (score != null) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        border = if (score == null) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category.displayName, 
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            
            Surface(
                color = if (score != null) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = score?.toString() ?: "-",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = if (score != null) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun UpperBonusRow(score: Int) {
    val needed = (63 - score).coerceAtLeast(0)
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Upper Bonus (63+)", style = MaterialTheme.typography.bodySmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (needed > 0) {
                Text("-$needed", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = if (score >= 63) "+35" else "0",
                fontWeight = FontWeight.Bold,
                color = if (score >= 63) GameColors.Success else GameColors.TextSecondary
            )
        }
    }
}

@Composable
fun TotalScoreRow(total: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(AppStrings.YAHTZEE_LABEL_TOTAL_SCORE, fontWeight = FontWeight.Black)
            Text(
                text = total.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black
            )
        }
    }
}
