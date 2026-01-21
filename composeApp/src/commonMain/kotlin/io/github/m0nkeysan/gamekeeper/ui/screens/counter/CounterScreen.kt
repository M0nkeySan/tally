package io.github.m0nkeysan.gamekeeper.ui.screens.counter

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.generated.resources.Res
import io.github.m0nkeysan.gamekeeper.generated.resources.action_back
import io.github.m0nkeysan.gamekeeper.generated.resources.action_cancel
import io.github.m0nkeysan.gamekeeper.generated.resources.action_reset
import io.github.m0nkeysan.gamekeeper.generated.resources.action_save
import io.github.m0nkeysan.gamekeeper.generated.resources.cd_menu
import io.github.m0nkeysan.gamekeeper.generated.resources.cd_settings
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_action_close
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_action_delete
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_add_label
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_cd_add
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_cd_decrease
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_cd_delete_all
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_cd_history
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_cd_increase
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_cd_reset_all
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_delete_everything
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_dialog_adjust_label
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_dialog_adjust_placeholder
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_dialog_delete_message
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_dialog_delete_title
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_dialog_reset_message
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_dialog_reset_title
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_dialog_set_score_label
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_dialog_settings_label
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_dialog_settings_title
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_empty_state
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_leader_display_format
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_reinitialize_all
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_remove_label
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_settings
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_settings_option_least
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_settings_option_most
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_title
import io.github.m0nkeysan.gamekeeper.platform.HapticType
import io.github.m0nkeysan.gamekeeper.platform.rememberHapticFeedbackController
import io.github.m0nkeysan.gamekeeper.ui.components.FlatTextField
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(
    onBack: () -> Unit,
    onEditCounter: (String, String, Int, Long) -> Unit,
    onNavigateToHistory: () -> Unit = {},
    viewModel: CounterViewModel = viewModel { CounterViewModel() }
) {
    val state by viewModel.state.collectAsState()
    val hapticController = rememberHapticFeedbackController()
    val listState = rememberLazyListState()

    var draggedItemId by remember { mutableStateOf<Any?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    // Preview order during drag
    var previewOrder by remember { mutableStateOf<List<CounterItem>?>(null) }

    val displayCounters = previewOrder ?: state.counters

    var quickAdjustTarget by remember { mutableStateOf<CounterItem?>(null) }
    var initialIsAddition by remember { mutableStateOf(true) }
    var autoFocusModal by remember { mutableStateOf(false) }

    var scoreSetTarget by remember { mutableStateOf<CounterItem?>(null) }

    var showMenu by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showResetConfirmation by remember { mutableStateOf(false) }
    var showDeleteAllConfirmation by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val leader = remember(state.counters, state.displayMode) {
        if (state.displayMode == CounterDisplayMode.MOST_POINTS) {
            state.counters.maxByOrNull { it.count }
        } else {
            state.counters.minByOrNull { it.count }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (leader != null && state.counters.isNotEmpty()) {
                            val emoji =
                                if (state.displayMode == CounterDisplayMode.MOST_POINTS) "📈" else "📉"
                            Text(stringResource(Res.string.counter_leader_display_format, emoji, leader.name), fontWeight = FontWeight.ExtraBold)
                        } else {
                            Text(
                                stringResource(Res.string.counter_title),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = stringResource(Res.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.addRandomCounter()
                        hapticController.performHapticFeedback(HapticType.LIGHT)
                    }) {
                        Icon(GameIcons.Add, contentDescription = stringResource(Res.string.counter_cd_add))
                    }
                    IconButton(onClick = { onNavigateToHistory() }) {
                        Icon(GameIcons.History, contentDescription = stringResource(Res.string.counter_cd_history))
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(GameIcons.MoreVert, contentDescription = stringResource(Res.string.cd_menu))
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.counter_settings)) },
                                onClick = {
                                    showMenu = false
                                    showSettingsDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        GameIcons.Settings,
                                        contentDescription = stringResource(Res.string.cd_settings)
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.counter_reinitialize_all)) },
                                onClick = {
                                    showMenu = false
                                    showResetConfirmation = true
                                },
                                leadingIcon = {
                                    Icon(
                                        GameIcons.Refresh,
                                        contentDescription = stringResource(Res.string.counter_cd_reset_all)
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.counter_delete_everything)) },
                                onClick = {
                                    showMenu = false
                                    showDeleteAllConfirmation = true
                                },
                                leadingIcon = {
                                    Icon(
                                        GameIcons.Delete,
                                        contentDescription = stringResource(Res.string.counter_cd_delete_all)
                                    )
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.counters.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        GameIcons.Add,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(Res.string.counter_empty_state),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                userScrollEnabled = draggedItemId == null
            ) {
                itemsIndexed(
                    items = displayCounters,
                    key = { _, counter -> counter.id }
                ) { _, counter ->
                    val isDragging = draggedItemId == counter.id
                    val scale by animateFloatAsState(if (isDragging) 1.05f else 1f)
                    val elevation by animateDpAsState(if (isDragging) 12.dp else 0.dp)
                    val zIndex = if (isDragging) 1f else 0f

                    Box(
                        modifier = Modifier
                            .zIndex(zIndex)
                            .graphicsLayer {
                                if (isDragging) {
                                    translationY = dragOffset.y
                                    rotationZ = 2f
                                }
                            }
                            .scale(scale)
                            .shadow(elevation, shape = MaterialTheme.shapes.medium)
                            .pointerInput(counter.id) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        draggedItemId = counter.id
                                        previewOrder = state.counters.toList()
                                        dragOffset = Offset.Zero
                                        hapticController.performHapticFeedback(HapticType.MEDIUM)
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffset += dragAmount

                                        val currentList =
                                            previewOrder ?: return@detectDragGesturesAfterLongPress
                                        val activeId =
                                            draggedItemId ?: return@detectDragGesturesAfterLongPress

                                        val currentIdx =
                                            currentList.indexOfFirst { it.id == activeId }
                                        if (currentIdx == -1) return@detectDragGesturesAfterLongPress

                                        val visibleItems = listState.layoutInfo.visibleItemsInfo
                                        val currentItemInfo =
                                            visibleItems.find { it.index == currentIdx }
                                                ?: return@detectDragGesturesAfterLongPress

                                        // Calculate center using layout info + drag offset
                                        val currentItemCenterY =
                                            currentItemInfo.offset + dragOffset.y + (currentItemInfo.size / 2f)

                                        // Find swap target
                                        val targetItem = visibleItems.find {
                                            it.index != currentIdx &&
                                                    currentItemCenterY > it.offset &&
                                                    currentItemCenterY < (it.offset + it.size)
                                        }

                                        if (targetItem != null) {
                                            val targetIdx = targetItem.index

                                            // Perform swap
                                            val newOrder = currentList.toMutableList()
                                            val item = newOrder.removeAt(currentIdx)
                                            newOrder.add(targetIdx, item)

                                            previewOrder = newOrder

                                            // Offset compensation
                                            val distance =
                                                targetItem.offset - currentItemInfo.offset
                                            dragOffset -= Offset(0f, distance.toFloat())

                                            hapticController.performHapticFeedback(HapticType.SELECTION)
                                        }
                                    },
                                    onDragEnd = {
                                        previewOrder?.let { finalOrder ->
                                            viewModel.reorderCounters(finalOrder.map { it.id })
                                        }
                                        draggedItemId = null
                                        previewOrder = null
                                        dragOffset = Offset.Zero
                                    },
                                    onDragCancel = {
                                        draggedItemId = null
                                        previewOrder = null
                                        dragOffset = Offset.Zero
                                    }
                                )
                            }
                    ) {
                        CounterCard(
                            name = counter.name,
                            count = counter.count,
                            color = Color(counter.color),
                            contentColor = Color.Black.copy(alpha = 0.8f),
                            onIncrement = {
                                viewModel.incrementCount(counter.id)
                                hapticController.performHapticFeedback(HapticType.LIGHT)
                            },
                            onDecrement = {
                                viewModel.decrementCount(counter.id)
                                hapticController.performHapticFeedback(HapticType.LIGHT)
                            },
                            onLongPressPlus = {
                                hapticController.performHapticFeedback(HapticType.MEDIUM)
                                initialIsAddition = true
                                autoFocusModal = false
                                quickAdjustTarget = counter
                            },
                            onLongPressMinus = {
                                hapticController.performHapticFeedback(HapticType.MEDIUM)
                                initialIsAddition = false
                                autoFocusModal = false
                                quickAdjustTarget = counter
                            },
                            onScoreClick = {
                                hapticController.performHapticFeedback(HapticType.MEDIUM)
                                initialIsAddition = true
                                autoFocusModal = true
                                quickAdjustTarget = counter
                            },
                            onScoreLongPress = {
                                hapticController.performHapticFeedback(HapticType.MEDIUM)
                                scoreSetTarget = counter
                            },
                            onClick = {
                                if (draggedItemId == null) {
                                    onEditCounter(
                                        counter.id,
                                        counter.name,
                                        counter.count,
                                        counter.color
                                    )
                                }
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
                QuickAdjustContent(
                    counter = quickAdjustTarget!!,
                    initialIsAddition = initialIsAddition,
                    autoFocus = autoFocusModal,
                    onAdjust = { amount ->
                        viewModel.adjustCount(quickAdjustTarget!!.id, amount)
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
                SetScoreContent(
                    counter = scoreSetTarget!!,
                    onSet = { newScore ->
                        viewModel.setCount(scoreSetTarget!!.id, newScore)
                        hapticController.performHapticFeedback(HapticType.SUCCESS)
                        scoreSetTarget = null
                    },
                    onCancel = { scoreSetTarget = null }
                )
            }
        }

        // --- Settings Dialog ---
        if (showSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showSettingsDialog = false },
                title = { Text(stringResource(Res.string.counter_dialog_settings_title)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            stringResource(Res.string.counter_dialog_settings_label),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                             SettingsOption(
                                 text = stringResource(Res.string.counter_settings_option_most),
                                 isSelected = state.displayMode == CounterDisplayMode.MOST_POINTS,
                                 onClick = { viewModel.setDisplayMode(CounterDisplayMode.MOST_POINTS) }
                             )

                             SettingsOption(
                                 text = stringResource(Res.string.counter_settings_option_least),
                                 isSelected = state.displayMode == CounterDisplayMode.LEAST_POINTS,
                                 onClick = { viewModel.setDisplayMode(CounterDisplayMode.LEAST_POINTS) }
                             )
                         }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSettingsDialog = false }) {
                        Text(stringResource(Res.string.counter_action_close))
                    }
                }
            )
        }

        // --- Reset All Confirmation ---
         if (showResetConfirmation) {
             AlertDialog(
                 onDismissRequest = { showResetConfirmation = false },
                 title = { Text(stringResource(Res.string.counter_dialog_reset_title)) },
                 text = { Text(stringResource(Res.string.counter_dialog_reset_message)) },
                 confirmButton = {
                     TextButton(
                         onClick = {
                             viewModel.resetAll()
                             showResetConfirmation = false
                         }
                     ) {
                         Text(stringResource(Res.string.action_reset))
                     }
                 },
                 dismissButton = {
                     TextButton(onClick = { showResetConfirmation = false }) {
                         Text(stringResource(Res.string.action_cancel))
                     }
                 }
             )
         }

        // --- Delete All Confirmation ---
         if (showDeleteAllConfirmation) {
             AlertDialog(
                 onDismissRequest = { showDeleteAllConfirmation = false },
                 title = { Text(stringResource(Res.string.counter_dialog_delete_title)) },
                 text = { Text(stringResource(Res.string.counter_dialog_delete_message)) },
                 confirmButton = {
                     TextButton(
                         onClick = {
                             viewModel.deleteAll()
                             showDeleteAllConfirmation = false
                         },
                         colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                     ) {
                         Text(stringResource(Res.string.counter_action_delete))
                     }
                 },
                 dismissButton = {
                     TextButton(onClick = { showDeleteAllConfirmation = false }) {
                         Text(stringResource(Res.string.action_cancel))
                     }
                 }
             )
         }
    }
}

@Composable
fun QuickAdjustContent(
    counter: CounterItem,
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
                .background(Color(counter.color))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${counter.name}: ${counter.count}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = Color.Black.copy(alpha = 0.8f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 120.dp)
            ) {
                items(quickValues) { value ->
                    Button(
                        onClick = { onAdjust(if (isAddition) value else -value) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAddition) Color(counter.color).copy(alpha = 0.8f) else MaterialTheme.colorScheme.errorContainer,
                            contentColor = if (isAddition) Color.Black else MaterialTheme.colorScheme.onErrorContainer
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = if (isAddition) "+$value" else "-$value",
                            fontWeight = FontWeight.Bold
                        )
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
                        selectedContainerColor = Color(counter.color),
                        selectedLabelColor = Color.Black
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
                 accentColor = Color(counter.color),
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
fun SetScoreContent(
    counter: CounterItem,
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
        // Header with Background Color
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(counter.color))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${counter.name}: ${counter.count}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = Color.Black.copy(alpha = 0.8f)
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
                 placeholder = counter.count.toString(),
                 label = stringResource(Res.string.counter_dialog_set_score_label),
                 accentColor = Color(counter.color),
                 focusRequester = focusRequester,
                 keyboardOptions = KeyboardOptions(
                     keyboardType = KeyboardType.Number,
                     imeAction = ImeAction.Done
                 ),
                 keyboardActions = KeyboardActions(
                     onDone = {
                         val value = scoreValue.toIntOrNull() ?: counter.count
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
                         val value = scoreValue.toIntOrNull() ?: counter.count
                         onSet(value)
                     },
                     modifier = Modifier.weight(1f),
                     colors = ButtonDefaults.buttonColors(
                         containerColor = Color(counter.color),
                         contentColor = Color.Black
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


@Composable
fun SettingsOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = if (isSelected) GameColors.Surface0 else GameColors.Surface1,
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CounterCard(
    name: String,
    count: Int,
    color: Color,
    contentColor: Color,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onLongPressPlus: () -> Unit,
    onLongPressMinus: () -> Unit,
    onScoreClick: () -> Unit,
    onScoreLongPress: () -> Unit,
    onClick: () -> Unit
) {
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
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(onClick = onClick)
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }

            Row(
                modifier = Modifier
                    .padding(end = 16.dp, top = 16.dp, bottom = 16.dp),
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