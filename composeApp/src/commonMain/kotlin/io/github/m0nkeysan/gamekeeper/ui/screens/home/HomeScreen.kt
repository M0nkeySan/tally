package io.github.m0nkeysan.gamekeeper.ui.screens.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.navigation.Screen
import io.github.m0nkeysan.gamekeeper.ui.components.GameCard
import io.github.m0nkeysan.gamekeeper.ui.strings.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateTo: (String) -> Unit,
    viewModel: HomeViewModel = viewModel { HomeViewModel() }
) {
    val cardOrder by viewModel.cardOrder.collectAsState()

    var localCardOrder by remember { mutableStateOf<List<String>?>(null) }

    val activeOrder = localCardOrder ?: cardOrder

    val features = remember(activeOrder) {
        activeOrder.mapNotNull { id -> gameFeatureMap[id] }
    }

    // Track by ID, not Index
    var draggedItemId by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    val gridState = rememberLazyGridState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(AppStrings.HOME_TITLE) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                // Disable scrolling while dragging
                userScrollEnabled = draggedItemId == null
            ) {
                itemsIndexed(
                    items = features,
                    key = { _, feature -> feature.id }
                ) { _, feature ->
                    val isDragging = draggedItemId == feature.id
                    val scale by animateFloatAsState(if (isDragging) 1.05f else 1f)
                    val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp)
                    val zIndex = if (isDragging) 1f else 0f

                    Box(
                        modifier = Modifier
                            .zIndex(zIndex)
                            .graphicsLayer {
                                if (isDragging) {
                                    translationX = dragOffset.x
                                    translationY = dragOffset.y
                                }
                            }
                            .scale(scale)
                            .shadow(elevation, shape = MaterialTheme.shapes.medium)
                            .pointerInput(feature.id) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        draggedItemId = feature.id
                                        localCardOrder = cardOrder.toList()
                                        dragOffset = Offset.Zero
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffset += dragAmount

                                        val currentList = localCardOrder ?: return@detectDragGesturesAfterLongPress
                                        val activeId = draggedItemId ?: return@detectDragGesturesAfterLongPress

                                        // 1. Find the current index of the item being dragged
                                        val currentIdx = currentList.indexOf(activeId)
                                        if (currentIdx == -1) return@detectDragGesturesAfterLongPress

                                        // 2. Get layout info for all visible items
                                        val visibleItems = gridState.layoutInfo.visibleItemsInfo
                                        val currentItemInfo = visibleItems.find { it.index == currentIdx }
                                            ?: return@detectDragGesturesAfterLongPress

                                        // 3. Calculate absolute center of the dragged item
                                        // (Item Offset + Drag Offset + Size/2)
                                        val currentItemCenter = Offset(
                                            x = currentItemInfo.offset.x + dragOffset.x + (currentItemInfo.size.width / 2f),
                                            y = currentItemInfo.offset.y + dragOffset.y + (currentItemInfo.size.height / 2f)
                                        )

                                        // 4. Check for overlap with other items
                                        val targetItem = visibleItems.find { item ->
                                            item.index != currentIdx &&
                                                    currentItemCenter.x > item.offset.x &&
                                                    currentItemCenter.x < (item.offset.x + item.size.width) &&
                                                    currentItemCenter.y > item.offset.y &&
                                                    currentItemCenter.y < (item.offset.y + item.size.height)
                                        }

                                        if (targetItem != null) {
                                            val targetIdx = targetItem.index

                                            // 5. Swap data
                                            val newOrder = currentList.toMutableList()
                                            val idToMove = newOrder.removeAt(currentIdx)
                                            newOrder.add(targetIdx, idToMove)

                                            localCardOrder = newOrder

                                            // 6. Compensate Offset
                                            // Calculate the physical distance between the two slots
                                            val offsetDiff = targetItem.offset - currentItemInfo.offset
                                            dragOffset -= Offset(offsetDiff.x.toFloat(), offsetDiff.y.toFloat())
                                        }
                                    },
                                    onDragEnd = {
                                        // Commit changes to ViewModel
                                        localCardOrder?.let { finalOrder ->
                                            viewModel.updateCardOrder(finalOrder)
                                        }
                                        draggedItemId = null
                                        localCardOrder = null
                                        dragOffset = Offset.Zero
                                    },
                                    onDragCancel = {
                                        draggedItemId = null
                                        localCardOrder = null
                                        dragOffset = Offset.Zero
                                    }
                                )
                            }
                    ) {
                        GameCard(
                            icon = feature.icon,
                            title = feature.title,
                            description = feature.description,
                            onClick = {
                                if (draggedItemId == null) {
                                    onNavigateTo(feature.route)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

data class GameFeature(
    val id: String,
    val icon: @Composable () -> Unit,
    val title: String,
    val description: String,
    val route: String
)

private val gameFeatureMap = mapOf(
    "finger_selector" to GameFeature(
        id = "finger_selector",
        icon = { Icon(GameIcons.TouchApp, contentDescription = null) },
        title = "Finger Selector",
        description = "Randomly select a starting player with multi-touch",
        route = Screen.FingerSelector.route
    ),
    "tarot" to GameFeature(
        id = "tarot",
        icon = { Icon(GameIcons.Tarot, contentDescription = null) },
        title = AppStrings.GAME_TAROT,
        description = AppStrings.DESC_TAROT,
        route = Screen.Tarot.route
    ),
    "yahtzee" to GameFeature(
        id = "yahtzee",
        icon = { Icon(GameIcons.Casino, contentDescription = null ) },
        title = AppStrings.GAME_YAHTZEE,
        description = AppStrings.DESC_YAHTZEE,
        route = Screen.Yahtzee.route
    ),
    "counter" to GameFeature(
        id = "counter",
        icon = { Icon(GameIcons.Add, contentDescription = null) },
        title = AppStrings.GAME_COUNTER,
        description = AppStrings.DESC_COUNTER,
        route = Screen.Counter.route
    )
)
