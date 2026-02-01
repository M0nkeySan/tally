package io.github.m0nkeysan.tally.ui.screens.gametracker

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.GameIcons
import io.github.m0nkeysan.tally.generated.resources.*
import io.github.m0nkeysan.tally.platform.HapticType
import io.github.m0nkeysan.tally.platform.rememberHapticFeedbackController
import io.github.m0nkeysan.tally.ui.components.AppSnackbarHost
import io.github.m0nkeysan.tally.ui.components.FlatTextField
import io.github.m0nkeysan.tally.ui.components.showErrorSnackbar
import io.github.m0nkeysan.tally.ui.utils.parseColor
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameTrackerRoundAdditionScreen(
    gameId: String,
    roundNumber: Int,
    roundId: String?,
    onBack: () -> Unit,
    onRoundSaved: () -> Unit,
    viewModel: GameTrackerRoundAdditionViewModel = viewModel { GameTrackerRoundAdditionViewModel() }
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val hapticController = rememberHapticFeedbackController()
    val isEditMode = roundId != null

    var quickAdjustTarget by remember { mutableStateOf<io.github.m0nkeysan.tally.core.model.Player?>(null) }
    var initialIsAddition by remember { mutableStateOf(true) }
    var autoFocusModal by remember { mutableStateOf(false) }

    var scoreSetTarget by remember { mutableStateOf<io.github.m0nkeysan.tally.core.model.Player?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(gameId, roundNumber, roundId) {
        viewModel.loadGame(gameId, roundNumber, roundId)
    }

    // Show error in Snackbar
    LaunchedEffect(state.error) {
        state.error?.let { errorMessage ->
            showErrorSnackbar(snackbarHostState, errorMessage)
            viewModel.onErrorConsumed()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) {
                            stringResource(Res.string.game_tracker_round_title_edit, roundNumber)
                        } else {
                            stringResource(Res.string.game_tracker_round_title_add, roundNumber)
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            GameIcons.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                },
                modifier = Modifier.shadow(elevation = 2.dp)
            )
        },
        snackbarHost = {
            AppSnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        viewModel.saveRound(
                            onSaved = onRoundSaved,
                            onError = { error ->
                                // Error will be shown via state
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        stringResource(Res.string.game_tracker_round_action_save),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.players.forEach { player ->
                        val score = state.playerScores[player.id] ?: 0
                        PlayerCounterCard(
                            name = player.name,
                            count = score,
                            color = parseColor(player.avatarColor),
                            onIncrement = {
                                viewModel.adjustPlayerScore(player.id, 1)
                                hapticController.performHapticFeedback(HapticType.LIGHT)
                            },
                            onDecrement = {
                                viewModel.adjustPlayerScore(player.id, -1)
                                hapticController.performHapticFeedback(HapticType.LIGHT)
                            },
                            onLongPressPlus = {
                                hapticController.performHapticFeedback(HapticType.MEDIUM)
                                initialIsAddition = true
                                autoFocusModal = false
                                quickAdjustTarget = player
                            },
                            onLongPressMinus = {
                                hapticController.performHapticFeedback(HapticType.MEDIUM)
                                initialIsAddition = false
                                autoFocusModal = false
                                quickAdjustTarget = player
                            },
                            onScoreClick = {
                                hapticController.performHapticFeedback(HapticType.MEDIUM)
                                initialIsAddition = true
                                autoFocusModal = true
                                quickAdjustTarget = player
                            },
                            onScoreLongPress = {
                                hapticController.performHapticFeedback(HapticType.MEDIUM)
                                scoreSetTarget = player
                            }
                        )
                    }
                }
            }
        }

        if (quickAdjustTarget != null) {
            ModalBottomSheet(
                onDismissRequest = { quickAdjustTarget = null },
                sheetState = sheetState
            ) {
                val player = quickAdjustTarget!!
                RoundQuickAdjustContent(
                    playerName = player.name,
                    playerColor = parseColor(player.avatarColor),
                    currentScore = state.playerScores[player.id] ?: 0,
                    initialIsAddition = initialIsAddition,
                    autoFocus = autoFocusModal,
                    onAdjust = { amount ->
                        viewModel.adjustPlayerScore(player.id, amount)
                        hapticController.performHapticFeedback(HapticType.SUCCESS)
                        quickAdjustTarget = null
                    }
                )
            }
        }

        if (scoreSetTarget != null) {
            ModalBottomSheet(
                onDismissRequest = { scoreSetTarget = null },
                sheetState = sheetState
            ) {
                val player = scoreSetTarget!!
                RoundSetScoreContent(
                    playerName = player.name,
                    playerColor = parseColor(player.avatarColor),
                    currentScore = state.playerScores[player.id] ?: 0,
                    onSet = { newScore ->
                        viewModel.updatePlayerScore(player.id, newScore)
                        hapticController.performHapticFeedback(HapticType.SUCCESS)
                        scoreSetTarget = null
                    },
                    onCancel = { scoreSetTarget = null }
                )
            }
        }
    }
}

@Composable
private fun PlayerCounterCard(
    name: String,
    count: Int,
    color: Color,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onLongPressPlus: () -> Unit,
    onLongPressMinus: () -> Unit,
    onScoreClick: () -> Unit,
    onScoreLongPress: () -> Unit
) {
    val contentColor = if (color.luminance() > 0.5) Color.Black.copy(alpha = 0.8f) else Color.White

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.1f))
                        .combinedClickable(
                            onClick = onDecrement,
                            onLongClick = onLongPressMinus
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(GameIcons.Remove, contentDescription = stringResource(Res.string.counter_cd_decrease), tint = contentColor)
                }

                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .widthIn(min = 48.dp)
                        .combinedClickable(
                            onClick = onScoreClick,
                            onLongClick = onScoreLongPress
                        ),
                    textAlign = TextAlign.Center,
                    color = contentColor
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.1f))
                        .combinedClickable(
                            onClick = onIncrement,
                            onLongClick = onLongPressPlus
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(GameIcons.Add, contentDescription = stringResource(Res.string.counter_cd_increase), tint = contentColor)
                }
            }
        }
    }
}

@Composable
fun RoundQuickAdjustContent(
    playerName: String,
    playerColor: Color,
    currentScore: Int,
    initialIsAddition: Boolean,
    autoFocus: Boolean = false,
    onAdjust: (Int) -> Unit
) {
    var isAddition by remember { mutableStateOf(initialIsAddition) }
    var manualValue by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    val quickValues = listOf(5, 10, 15, 20, 50, 100, 200)

    LaunchedEffect(Unit) {
        if (autoFocus) {
            focusRequester.requestFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(playerColor)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$playerName: $currentScore",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = if (playerColor.luminance() > 0.5) Color.Black.copy(alpha = 0.8f) else Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                quickValues.chunked(4).forEach { rowValues ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowValues.forEach { value ->
                            Button(
                                onClick = { onAdjust(if (isAddition) value else -value) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isAddition) playerColor.copy(alpha = 0.8f) else MaterialTheme.colorScheme.errorContainer,
                                    contentColor = if (isAddition) (if (playerColor.luminance() > 0.5) Color.Black else Color.White) else MaterialTheme.colorScheme.onErrorContainer
                                ),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (isAddition) "+$value" else "-$value",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        repeat(4 - rowValues.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilterChip(
                    selected = !isAddition,
                    onClick = { isAddition = false },
                    label = {
                        Text(
                            stringResource(Res.string.counter_remove_label),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.error
                    )
                )
                FilterChip(
                    selected = isAddition,
                    onClick = { isAddition = true },
                    label = {
                        Text(
                            stringResource(Res.string.counter_add_label),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = playerColor,
                        selectedLabelColor = if (playerColor.luminance() > 0.5) Color.Black else Color.White
                    )
                )
            }

            FlatTextField(
                 value = manualValue,
                 onValueChange = {
                     if (it.all { char -> char.isDigit() || char == '-' }) manualValue = it
                 },
                 label = stringResource(Res.string.counter_dialog_adjust_label),
                 placeholder = stringResource(Res.string.counter_dialog_adjust_placeholder),
                 accentColor = playerColor,
                 focusRequester = focusRequester,
                 keyboardOptions = KeyboardOptions(
                     keyboardType = KeyboardType.Number,
                     imeAction = ImeAction.Done
                 ),
                 keyboardActions = KeyboardActions(
                     onDone = {
                         val value = manualValue.toIntOrNull() ?: 0
                         onAdjust(if (isAddition) value else -value)
                     }
                 )
             )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun RoundSetScoreContent(
    playerName: String,
    playerColor: Color,
    currentScore: Int,
    onSet: (Int) -> Unit,
    onCancel: () -> Unit
) {
    var scoreValue by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(playerColor)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$playerName: $currentScore",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = if (playerColor.luminance() > 0.5) Color.Black.copy(alpha = 0.8f) else Color.White
            )
        }

        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FlatTextField(
                 value = scoreValue,
                 onValueChange = {
                     if (it.isEmpty() || it == "-" || it.all { char -> char.isDigit() || char == '-' }) scoreValue =
                         it
                 },
                 placeholder = currentScore.toString(),
                 label = stringResource(Res.string.counter_dialog_set_score_label),
                 accentColor = playerColor,
                 focusRequester = focusRequester,
                 keyboardOptions = KeyboardOptions(
                     keyboardType = KeyboardType.Number,
                     imeAction = ImeAction.Done
                 ),
                 keyboardActions = KeyboardActions(
                     onDone = {
                         val value = scoreValue.toIntOrNull() ?: currentScore
                         onSet(value)
                     }
                 )
             )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                     onClick = onCancel,
                     modifier = Modifier.weight(1f),
                     shape = MaterialTheme.shapes.medium
                 ) {
                     Text(stringResource(Res.string.action_cancel))
                 }
                 Button(
                     onClick = {
                         val value = scoreValue.toIntOrNull() ?: currentScore
                         onSet(value)
                     },
                     modifier = Modifier.weight(1f),
                     colors = ButtonDefaults.buttonColors(
                         containerColor = playerColor,
                         contentColor = if (playerColor.luminance() > 0.5) Color.Black else Color.White
                     ),
                     shape = MaterialTheme.shapes.medium
                 ) {
                     Text(stringResource(Res.string.action_save), fontWeight = FontWeight.Bold)
                 }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
