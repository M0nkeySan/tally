package io.github.m0nkeysan.tally.ui.screens.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.GameIcons
import io.github.m0nkeysan.tally.core.navigation.CounterRoute
import io.github.m0nkeysan.tally.core.navigation.DiceRollerRoute
import io.github.m0nkeysan.tally.core.navigation.FingerSelectorRoute
import io.github.m0nkeysan.tally.core.navigation.Route
import io.github.m0nkeysan.tally.core.navigation.TarotRoute
import io.github.m0nkeysan.tally.core.navigation.YahtzeeRoute
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.desc_counter
import io.github.m0nkeysan.tally.generated.resources.desc_dice
import io.github.m0nkeysan.tally.generated.resources.desc_finger_selector
import io.github.m0nkeysan.tally.generated.resources.desc_tarot
import io.github.m0nkeysan.tally.generated.resources.desc_yahtzee
import io.github.m0nkeysan.tally.generated.resources.game_counter
import io.github.m0nkeysan.tally.generated.resources.game_dice
import io.github.m0nkeysan.tally.generated.resources.game_tarot
import io.github.m0nkeysan.tally.generated.resources.game_yahtzee
import io.github.m0nkeysan.tally.generated.resources.home_cd_counter
import io.github.m0nkeysan.tally.generated.resources.home_cd_dice
import io.github.m0nkeysan.tally.generated.resources.home_cd_finger_selector
import io.github.m0nkeysan.tally.generated.resources.home_title
import io.github.m0nkeysan.tally.generated.resources.home_title_finger_selector
import io.github.m0nkeysan.tally.ui.components.GameCard
import io.github.m0nkeysan.tally.ui.components.TarotIcon
import io.github.m0nkeysan.tally.ui.components.YahtzeeIcon
import io.github.m0nkeysan.tally.ui.theme.resolveIsDarkTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateTo: (Route) -> Unit,
    viewModel: HomeViewModel = viewModel { HomeViewModel() }
) {
    val cardOrder by viewModel.cardOrder.collectAsState()
    val themePreference by viewModel.themePreference.collectAsState()
    val isDarkTheme = resolveIsDarkTheme(themePreference)

    var localCardOrder by remember { mutableStateOf<List<String>?>(null) }

    val activeOrder = localCardOrder ?: cardOrder

    val gameFeatureMap = getGameFeatureMap()
    val features = remember(activeOrder, gameFeatureMap) {
        activeOrder.mapNotNull { id -> gameFeatureMap[id] }
    }

    // Track by ID, not Index
    var draggedItemId by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    val gridState = rememberLazyGridState()

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.home_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.shadow(elevation = 2.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Adaptive(minSize = 140.dp),
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

                                            val currentList = localCardOrder
                                                ?: return@detectDragGesturesAfterLongPress
                                            val activeId =
                                                draggedItemId
                                                    ?: return@detectDragGesturesAfterLongPress

                                            // 1. Find the current index of the item being dragged
                                            val currentIdx = currentList.indexOf(activeId)
                                            if (currentIdx == -1) return@detectDragGesturesAfterLongPress

                                            // 2. Get layout info for all visible items
                                            val visibleItems = gridState.layoutInfo.visibleItemsInfo
                                            val currentItemInfo =
                                                visibleItems.find { it.index == currentIdx }
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
                                                val offsetDiff =
                                                    targetItem.offset - currentItemInfo.offset
                                                dragOffset -= Offset(
                                                    offsetDiff.x.toFloat(),
                                                    offsetDiff.y.toFloat()
                                                )
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
                                },
                                borderColor = if (isDarkTheme) {
                                    feature.colors.borderDark
                                } else {
                                    feature.colors.borderLight
                                },
                                backgroundColor = if (isDarkTheme) {
                                    feature.colors.backgroundDark.copy(alpha = 0.3f)
                                } else {
                                    feature.colors.backgroundLight.copy(alpha = 0.2f)
                                },
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = if (isDarkTheme) 2.dp else 0.dp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Color scheme for game cards in light and dark themes
 */
data class GameColors(
    val borderLight: Color,
    val backgroundLight: Color,
    val borderDark: Color,
    val backgroundDark: Color
)

/**
 * Predefined color schemes for each game card
 */
private object GameCardColors {
    val fingerSelector = GameColors(
        borderLight = Color(0xFF9333EA),      // Purple-600
        backgroundLight = Color(0xFFF3E8FF),  // Purple-100
        borderDark = Color(0xFF9333EA),       // Purple-600
        backgroundDark = Color(0xFF581C87)    // Purple-900
    )
    
    val tarot = GameColors(
        borderLight = Color(0xFF4F46E5),      // Indigo-600
        backgroundLight = Color(0xFFE0E7FF),  // Indigo-100
        borderDark = Color(0xFF6366F1),       // Indigo-500 (brighter)
        backgroundDark = Color(0xFF312E81)    // Indigo-900
    )
    
    val yahtzee = GameColors(
        borderLight = Color(0xFFDC2626),      // Red-600
        backgroundLight = Color(0xFFFEE2E2),  // Red-100
        borderDark = Color(0xFFEF4444),       // Red-500 (brighter)
        backgroundDark = Color(0xFF7F1D1D)    // Red-900
    )
    
    val counter = GameColors(
        borderLight = Color(0xFF10B981),      // Emerald-500
        backgroundLight = Color(0xFFD1FAE5),  // Emerald-100
        borderDark = Color(0xFF10B981),       // Emerald-500
        backgroundDark = Color(0xFF064E3B)    // Emerald-900
    )
    
    val diceRoller = GameColors(
        borderLight = Color(0xFFF59E0B),      // Amber-500
        backgroundLight = Color(0xFFFEF3C7),  // Amber-100
        borderDark = Color(0xFFFCD34D),       // Amber-400 (brighter)
        backgroundDark = Color(0xFF78350F)    // Amber-900
    )
}

data class GameFeature(
    val id: String,
    val icon: @Composable () -> Unit,
    val title: String,
    val description: String,
    val route: Route,
    val colors: GameColors
)

@Composable
private fun getGameFeatureMap() = mapOf(
    "finger_selector" to GameFeature(
        id = "finger_selector",
        icon = {
            Icon(
                GameIcons.TouchApp,
                contentDescription = stringResource(Res.string.home_cd_finger_selector),
                Modifier
                    .width(64.dp)
                    .height(64.dp)
            )
        },
        title = stringResource(Res.string.home_title_finger_selector),
        description = stringResource(Res.string.desc_finger_selector),
        route = FingerSelectorRoute,
        colors = GameCardColors.fingerSelector
    ),
    "tarot" to GameFeature(
        id = "tarot",
        icon = { TarotIcon() },
        title = stringResource(Res.string.game_tarot),
        description = stringResource(Res.string.desc_tarot),
        route = TarotRoute,
        colors = GameCardColors.tarot
    ),
    "yahtzee" to GameFeature(
        id = "yahtzee",
        icon = { YahtzeeIcon() },
        title = stringResource(Res.string.game_yahtzee),
        description = stringResource(Res.string.desc_yahtzee),
        route = YahtzeeRoute,
        colors = GameCardColors.yahtzee
    ),
    "counter" to GameFeature(
        id = "counter",
        icon = {
            Icon(
                GameIcons.AddBox,
                contentDescription = stringResource(Res.string.home_cd_counter),
                Modifier
                    .width(64.dp)
                    .height(64.dp)
            )
        },
        title = stringResource(Res.string.game_counter),
        description = stringResource(Res.string.desc_counter),
        route = CounterRoute,
        colors = GameCardColors.counter
    ),
    "dice_roller" to GameFeature(
        id = "dice_roller",
        icon = {
            Icon(
                GameIcons.Casino,
                contentDescription = stringResource(Res.string.home_cd_dice),
                Modifier
                    .width(64.dp)
                    .height(64.dp)
            )
        },
        title = stringResource(Res.string.game_dice),
        description = stringResource(Res.string.desc_dice),
        route = DiceRollerRoute,
        colors = GameCardColors.diceRoller
    )
)
