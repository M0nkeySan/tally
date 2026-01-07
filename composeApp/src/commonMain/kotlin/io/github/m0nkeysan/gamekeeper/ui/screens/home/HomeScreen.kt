package io.github.m0nkeysan.gamekeeper.ui.screens.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.navigation.Screen
import io.github.m0nkeysan.gamekeeper.ui.components.GameCard
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateTo: (String) -> Unit,
    viewModel: HomeViewModel = viewModel { HomeViewModel() }
) {
    val cardOrder by viewModel.cardOrder.collectAsState()
    val features = remember(cardOrder) {
        cardOrder.mapNotNull { id -> gameFeatureMap[id] }
    }

    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    val itemPositions = remember { mutableStateMapOf<Int, Offset>() }
    val itemSizes = remember { mutableStateMapOf<Int, IntSize>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GameKeeper") },
                actions = {
                    IconButton(onClick = { onNavigateTo(Screen.AddPlayer.route) }) {
                        Icon(GameIcons.Add, contentDescription = "Add Player")
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    IconButton(onClick = { onNavigateTo(Screen.History.route) }) {
                        Icon(GameIcons.History, contentDescription = "Statistics")
                    }
                }
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
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                userScrollEnabled = draggedItemIndex == null
            ) {
                itemsIndexed(
                    items = features,
                    key = { _, feature -> feature.id }
                ) { index, feature ->
                    val isDragging = draggedItemIndex == index
                    val scale by animateFloatAsState(if (isDragging) 1.05f else 1f)
                    val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp)

                    Box(
                        modifier = Modifier
                            .zIndex(if (isDragging) 1f else 0f)
                            .graphicsLayer {
                                if (isDragging) {
                                    translationX = dragOffset.x
                                    translationY = dragOffset.y
                                }
                            }
                            .scale(scale)
                            .shadow(elevation, shape = MaterialTheme.shapes.medium)
                            .onGloballyPositioned { coordinates ->
                                itemPositions[index] = coordinates.positionInParent()
                                itemSizes[index] = coordinates.size
                            }
                            .pointerInput(Unit) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        draggedItemIndex = index
                                        dragOffset = Offset.Zero
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffset += dragAmount

                                        // Find target position for swap
                                        val draggedPos = itemPositions[index] ?: return@detectDragGesturesAfterLongPress
                                        val currentCenter = Offset(
                                            draggedPos.x + dragOffset.x + (itemSizes[index]?.width ?: 0) / 2f,
                                            draggedPos.y + dragOffset.y + (itemSizes[index]?.height ?: 0) / 2f
                                        )

                                        // Check if we should swap with another item
                                        for ((otherIndex, otherPos) in itemPositions) {
                                            if (otherIndex == index) continue
                                            val otherSize = itemSizes[otherIndex] ?: continue

                                            val otherCenterX = otherPos.x + otherSize.width / 2f
                                            val otherCenterY = otherPos.y + otherSize.height / 2f

                                            val distance = sqrt(
                                                (currentCenter.x - otherCenterX) * (currentCenter.x - otherCenterX) +
                                                        (currentCenter.y - otherCenterY) * (currentCenter.y - otherCenterY)
                                            )

                                            // Swap if close enough to another item's center
                                            if (distance < otherSize.width / 2f) {
                                                val currentOrder = cardOrder.toMutableList()
                                                val draggedId = currentOrder.removeAt(index)
                                                currentOrder.add(otherIndex, draggedId)
                                                viewModel.updateCardOrder(currentOrder)
                                                draggedItemIndex = otherIndex
                                                dragOffset = Offset.Zero
                                                break
                                            }
                                        }
                                    },
                                    onDragEnd = {
                                        draggedItemIndex = null
                                        dragOffset = Offset.Zero
                                    },
                                    onDragCancel = {
                                        draggedItemIndex = null
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
                                if (draggedItemIndex == null) {
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
        icon = { Icon(GameIcons.Casino, contentDescription = null) },
        title = "Tarot",
        description = "Score Tarot games for 3, 4, or 5 players",
        route = Screen.Tarot.route
    ),
    "yahtzee" to GameFeature(
        id = "yahtzee",
        icon = { Icon(GameIcons.GridView, contentDescription = null) },
        title = "Yahtzee",
        description = "Complete Yahtzee scorecard with automatic bonuses",
        route = Screen.Yahtzee.route
    ),
    "counter" to GameFeature(
        id = "counter",
        icon = { Icon(GameIcons.Add, contentDescription = null) },
        title = "Counter",
        description = "Simple counter for any board game",
        route = Screen.Counter.route
    )
)
