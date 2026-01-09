package io.github.m0nkeysan.gamekeeper.ui.screens.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.navigation.Screen
import io.github.m0nkeysan.gamekeeper.ui.components.GameCard
import io.github.m0nkeysan.gamekeeper.ui.strings.AppStrings
import io.github.m0nkeysan.gamekeeper.ui.utils.DragConfig
import io.github.m0nkeysan.gamekeeper.ui.utils.DragDetectionMode
import io.github.m0nkeysan.gamekeeper.ui.utils.draggableGridItem
import io.github.m0nkeysan.gamekeeper.ui.utils.trackItemPosition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateTo: (String) -> Unit,
    viewModel: HomeViewModel = viewModel { HomeViewModel() },
    modifier: Modifier = Modifier
) {
    val cardOrder by viewModel.cardOrder.collectAsState()
    val features = remember(cardOrder) {
        cardOrder.mapNotNull { id -> gameFeatureMap[id] }
    }

    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    val itemPositions = remember { mutableStateMapOf<Int, Offset>() }
    val itemSizes = remember { mutableStateMapOf<Int, IntSize>() }

    // Clean up stale position/size entries when features list changes
    LaunchedEffect(features.size) {
        val validIndices = features.indices.toSet()
        itemPositions.keys.removeAll { it !in validIndices }
        itemSizes.keys.removeAll { it !in validIndices }
    }

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
                            .trackItemPosition(index, itemPositions, itemSizes)
                            .draggableGridItem(
                                itemIndex = index,
                                draggedItemIndex = draggedItemIndex,
                                dragOffset = dragOffset,
                                itemPositions = itemPositions,
                                itemSizes = itemSizes,
                                items = features,
                                config = DragConfig(detectionMode = DragDetectionMode.GRID_2D),
                                onDragStart = {
                                    draggedItemIndex = index
                                    dragOffset = Offset.Zero
                                },
                                onSwap = { fromIndex, toIndex ->
                                    val currentOrder = cardOrder.toMutableList()
                                    val draggedId = currentOrder.removeAt(fromIndex)
                                    currentOrder.add(toIndex, draggedId)
                                    viewModel.updateCardOrder(currentOrder)
                                    draggedItemIndex = toIndex
                                },
                                onDragEnd = {
                                    draggedItemIndex = null
                                    dragOffset = Offset.Zero
                                },
                                onDragOffsetChange = { newOffset ->
                                    dragOffset = newOffset
                                }
                            )
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
        title = AppStrings.GAME_TAROT,
        description = AppStrings.DESC_TAROT,
        route = Screen.Tarot.route
    ),
    "yahtzee" to GameFeature(
        id = "yahtzee",
        icon = { Icon(GameIcons.GridView, contentDescription = null) },
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
