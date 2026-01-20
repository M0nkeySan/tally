package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeCategory
import io.github.m0nkeysan.gamekeeper.ui.components.GameKeeperSnackbarHost
import io.github.m0nkeysan.gamekeeper.ui.components.showErrorSnackbar
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors
import org.jetbrains.compose.resources.stringResource
import io.github.m0nkeysan.gamekeeper.generated.resources.action_cancel
import io.github.m0nkeysan.gamekeeper.generated.resources.action_ok
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_dialog_enter_score
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_dialog_select_score
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_error_score_too_high
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_label_bonus_earned
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_label_bonus_needed
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_label_total_score
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_label_upper_bonus
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_placeholder_dice_sum
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_scoring_cd_dropdown
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_scoring_cd_next
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_scoring_cd_previous
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_scoring_fallback_name
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_scoring_total_format
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_scoring_turn_indicator
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_scoring_turn_label
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_scoring_viewing_label
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_section_lower
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_section_upper
import io.github.m0nkeysan.gamekeeper.generated.resources.Res

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
    LaunchedEffect(game.currentPlayerId) {
        selectedPlayerId = game.currentPlayerId
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
                        contentDescription = stringResource(Res.string.yahtzee_scoring_cd_previous)
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
                                     text = stringResource(Res.string.yahtzee_scoring_turn_indicator),
                                     color = MaterialTheme.colorScheme.primary,
                                     fontSize = 12.sp
                                 )
                             }
                             
                             Text(
                                 text = players.find { it.id == selectedPlayerId }?.name ?: stringResource(Res.string.yahtzee_scoring_fallback_name),
                                 style = MaterialTheme.typography.titleLarge,
                                 fontWeight = FontWeight.ExtraBold
                             )
                            
                            Icon(
                                 imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                                 contentDescription = stringResource(Res.string.yahtzee_scoring_cd_dropdown)
                             )
                        }
                        
                        val totalFormat = stringResource(Res.string.yahtzee_scoring_total_format)
                        val selectedPlayerTotal = remember(selectedPlayerId, state.scores, totalFormat) {
                             totalFormat.format(viewModel.calculateTotalScore(selectedPlayerId))
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
                        contentDescription = stringResource(Res.string.yahtzee_scoring_cd_next)
                    )
                }
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            val isViewingSelf = selectedPlayerId == game.currentPlayerId
             val displayedName = players.find { it.id == selectedPlayerId }?.name ?: stringResource(Res.string.yahtzee_scoring_fallback_name)
             
             Text(
                 text = if (isViewingSelf) stringResource(Res.string.yahtzee_scoring_turn_label) else stringResource(Res.string.yahtzee_scoring_viewing_label).format(displayedName),
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
            item { SectionHeader(stringResource(Res.string.yahtzee_section_upper)) }
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
            item { SectionHeader(stringResource(Res.string.yahtzee_section_lower)) }
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
                 title = { Text(stringResource(Res.string.yahtzee_dialog_select_score).format(category.displayName)) },
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
                        Text(stringResource(Res.string.action_cancel))
                    }
                }
            )
        } else {
            var scoreInput by remember { mutableStateOf(score?.toString() ?: "") }
            val inputScore = scoreInput.toIntOrNull()
            val isInvalid = inputScore != null && inputScore > 30
            
            AlertDialog(
                 onDismissRequest = { showDialog = false },
                 title = { Text(stringResource(Res.string.yahtzee_dialog_enter_score).format(category.displayName)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(
                            value = scoreInput,
                            onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) scoreInput = it },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text(stringResource(Res.string.yahtzee_placeholder_dice_sum)) },
                            isError = isInvalid,
                            supportingText = {
                                 if (isInvalid) {
                                     Text(stringResource(Res.string.yahtzee_error_score_too_high))
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
                        Text(stringResource(Res.string.action_ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(stringResource(Res.string.action_cancel))
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
        Text(stringResource(Res.string.yahtzee_label_upper_bonus), style = MaterialTheme.typography.bodySmall)
         Row(verticalAlignment = Alignment.CenterVertically) {
             if (needed > 0) {
                 Text(stringResource(Res.string.yahtzee_label_bonus_needed).format(needed), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                 Spacer(modifier = Modifier.width(8.dp))
             }
             Text(
                 text = if (score >= 63) stringResource(Res.string.yahtzee_label_bonus_earned) else "0",
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
            Text(stringResource(Res.string.yahtzee_label_total_score), fontWeight = FontWeight.Black)
            Text(
                text = total.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black
            )
        }
    }
}
