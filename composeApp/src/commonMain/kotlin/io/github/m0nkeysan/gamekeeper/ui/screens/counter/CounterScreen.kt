package io.github.m0nkeysan.gamekeeper.ui.screens.counter

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.platform.HapticType
import io.github.m0nkeysan.gamekeeper.platform.rememberHapticFeedbackController
import io.github.m0nkeysan.gamekeeper.ui.viewmodel.CounterViewModel
import io.github.m0nkeysan.gamekeeper.ui.viewmodel.CounterItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(
    onBack: () -> Unit,
    onEditCounter: (String, String, Int, Long) -> Unit,
    viewModel: CounterViewModel = viewModel { CounterViewModel() }
) {
    val state by viewModel.state.collectAsState()
    val hapticController = rememberHapticFeedbackController()

    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    
    val itemPositions = remember { mutableStateMapOf<String, androidx.compose.ui.geometry.Offset>() }
    val itemSizes = remember { mutableStateMapOf<String, IntSize>() }

    // Quick Adjust Modal State
    var quickAdjustTarget by remember { mutableStateOf<CounterItem?>(null) }
    var initialIsAddition by remember { mutableStateOf(true) }
    var autoFocusModal by remember { mutableStateOf(false) }
    
    // Direct Score Set Modal State
    var scoreSetTarget by remember { mutableStateOf<CounterItem?>(null) }
    
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Counter") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        viewModel.addRandomCounter()
                        hapticController.performHapticFeedback(HapticType.LIGHT)
                    }) {
                        Icon(GameIcons.Add, contentDescription = "Add Counter")
                    }
                    IconButton(onClick = { /* TODO: Navigate to history */ }) {
                        Icon(GameIcons.History, contentDescription = "History")
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
                        text = "Tap + to add a counter",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                userScrollEnabled = draggedItemIndex == null
            ) {
                itemsIndexed(
                    items = state.counters,
                    key = { _, counter -> counter.id }
                ) { index, counter ->
                    val isDragging = draggedItemIndex == index
                    val scale by animateFloatAsState(if (isDragging) 1.05f else 1f)
                    val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp)

                    Box(
                        modifier = Modifier
                            .zIndex(if (isDragging) 1f else 0f)
                            .graphicsLayer {
                                if (isDragging) {
                                    translationY = dragOffset.y
                                }
                            }
                            .scale(scale)
                            .shadow(elevation, shape = MaterialTheme.shapes.medium)
                            .onGloballyPositioned { coordinates ->
                                itemPositions[counter.id] = coordinates.positionInParent()
                                itemSizes[counter.id] = coordinates.size
                            }
                            .pointerInput(state.counters) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        draggedItemIndex = index
                                        dragOffset = androidx.compose.ui.geometry.Offset.Zero
                                        hapticController.performHapticFeedback(HapticType.MEDIUM)
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffset += dragAmount

                                        val currentDraggedId = state.counters[draggedItemIndex ?: index].id
                                        val currentPos = itemPositions[currentDraggedId] ?: return@detectDragGesturesAfterLongPress
                                        val currentCenterY = currentPos.y + dragOffset.y + (itemSizes[currentDraggedId]?.height ?: 0) / 2f

                                        var targetIndex = -1
                                        for (i in state.counters.indices) {
                                            if (i == (draggedItemIndex ?: index)) continue
                                            val otherId = state.counters[i].id
                                            val otherPos = itemPositions[otherId] ?: continue
                                            val otherSize = itemSizes[otherId] ?: continue
                                            
                                            val otherTop = otherPos.y
                                            val otherBottom = otherPos.y + otherSize.height
                                            
                                            if (currentCenterY in otherTop..otherBottom) {
                                                targetIndex = i
                                                break
                                            }
                                        }

                                        if (targetIndex != -1) {
                                            val currentIndex = draggedItemIndex ?: index
                                            val newOrder = state.counters.toMutableList()
                                            val movedItem = newOrder.removeAt(currentIndex)
                                            newOrder.add(targetIndex, movedItem)
                                            
                                            val oldY = itemPositions[state.counters[currentIndex].id]?.y ?: 0f
                                            val targetY = itemPositions[state.counters[targetIndex].id]?.y ?: 0f
                                            dragOffset = dragOffset.copy(y = dragOffset.y - (targetY - oldY))

                                            viewModel.reorderCounters(newOrder.map { it.id })
                                            draggedItemIndex = targetIndex
                                            hapticController.performHapticFeedback(HapticType.SELECTION)
                                        }
                                    },
                                    onDragEnd = {
                                        draggedItemIndex = null
                                        dragOffset = androidx.compose.ui.geometry.Offset.Zero
                                    },
                                    onDragCancel = {
                                        draggedItemIndex = null
                                        dragOffset = androidx.compose.ui.geometry.Offset.Zero
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
                                if (draggedItemIndex == null) {
                                    onEditCounter(counter.id, counter.name, counter.count, counter.color)
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
        // Header with Background Color (Matching SetScoreContent design)
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
                    label = { Text("REMOVE (-)", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.error
                    )
                )
                FilterChip(
                    selected = isAddition,
                    onClick = { isAddition = true },
                    label = { Text("ADD (+)", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(counter.color),
                        selectedLabelColor = Color.Black
                    )
                )
            }

            OutlinedTextField(
                value = manualValue,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '-' }) manualValue = it },
                label = { Text("Manual Value") },
                placeholder = { Text("0") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val value = manualValue.toIntOrNull() ?: 0
                        onAdjust(if (isAddition) value else -value)
                    }
                ),
                singleLine = true
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
            OutlinedTextField(
                value = scoreValue,
                onValueChange = { if (it.isEmpty() || it == "-" || it.all { char -> char.isDigit() || char == '-' }) scoreValue = it },
                placeholder = { Text(counter.count.toString()) },
                label = { Text("Set New Score") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val value = scoreValue.toIntOrNull() ?: counter.count
                        onSet(value)
                    }
                ),
                singleLine = true
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
                    Text("CANCEL")
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
                    Text("EDIT", fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Decrement Button
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
                    Icon(GameIcons.Remove, contentDescription = "Decrease", tint = contentColor)
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

                // Increment Button
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
                    Icon(GameIcons.Add, contentDescription = "Increase", tint = contentColor)
                }
            }
        }
    }
}
