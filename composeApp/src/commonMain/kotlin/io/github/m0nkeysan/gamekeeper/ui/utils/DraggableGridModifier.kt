package io.github.m0nkeysan.gamekeeper.ui.utils

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntSize
import kotlin.math.sqrt

/**
 * Drag detection mode for items
 */
enum class DragDetectionMode {
    /**
     * 2D distance-based detection: Swaps when dragged item center is close to another item's center
     * Suitable for grid layouts (HomeScreen)
     */
    GRID_2D,

    /**
     * 1D vertical position detection: Swaps when dragged item center crosses vertical bounds
     * Suitable for vertical list layouts (CounterScreen)
     */
    LIST_VERTICAL
}

/**
 * Configuration for drag-and-drop behavior
 */
data class DragConfig(
    val detectionMode: DragDetectionMode = DragDetectionMode.GRID_2D,
    val swapThresholdFraction: Float = 0.5f, // For GRID_2D: fraction of item width/height
    val onHapticFeedback: ((HapticFeedbackType) -> Unit)? = null
)

enum class HapticFeedbackType {
    MEDIUM,
    SELECTION
}

/**
 * Reusable modifier for drag-and-drop functionality supporting both grid and list layouts.
 *
 * Usage:
 * ```
 * val itemPositions = remember { mutableStateMapOf<ItemId, Offset>() }
 * val itemSizes = remember { mutableStateMapOf<ItemId, IntSize>() }
 * var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
 * var dragOffset by remember { mutableStateOf(Offset.Zero) }
 *
 * Modifier.draggableGridItem(
 *     itemIndex = index,
 *     draggedItemIndex = draggedItemIndex,
 *     dragOffset = dragOffset,
 *     itemPositions = itemPositions,
 *     itemSizes = itemSizes,
 *     items = items,
 *     config = DragConfig(detectionMode = DragDetectionMode.GRID_2D),
 *     onDragStart = { draggedItemIndex = index; dragOffset = Offset.Zero },
 *     onSwap = { fromIndex, toIndex -> /* reorder items */ },
 *     onDragEnd = { draggedItemIndex = null; dragOffset = Offset.Zero },
 *     onDragOffsetChange = { dragOffset = it }
 * )
 * ```
 */
fun <T> Modifier.draggableGridItem(
    itemIndex: Int,
    draggedItemIndex: Int?,
    dragOffset: Offset,
    itemPositions: MutableMap<Int, Offset>,
    itemSizes: MutableMap<Int, IntSize>,
    items: List<T>,
    config: DragConfig = DragConfig(),
    onDragStart: () -> Unit = {},
    onSwap: (fromIndex: Int, toIndex: Int) -> Unit,
    onDragEnd: () -> Unit = {},
    onDragOffsetChange: (Offset) -> Unit,
    onPositionUpdate: (Int, Offset, IntSize) -> Unit = { _, _, _ -> }
): Modifier = this.pointerInput(items) {
    detectDragGesturesAfterLongPress(
        onDragStart = {
            onDragStart()
            config.onHapticFeedback?.invoke(HapticFeedbackType.MEDIUM)
        },
        onDrag = { change, dragAmount ->
            change.consume()
            val newDragOffset = dragOffset + dragAmount
            onDragOffsetChange(newDragOffset)

            // Perform swap detection based on mode
            when (config.detectionMode) {
                DragDetectionMode.GRID_2D -> performGrid2DDetection(
                    draggedItemIndex ?: itemIndex,
                    itemIndex,
                    newDragOffset,
                    itemPositions,
                    itemSizes,
                    config,
                    onSwap,
                    onDragOffsetChange
                )

                DragDetectionMode.LIST_VERTICAL -> performListVerticalDetection(
                    draggedItemIndex ?: itemIndex,
                    itemIndex,
                    newDragOffset,
                    itemPositions,
                    itemSizes,
                    items,
                    config,
                    onSwap,
                    onDragOffsetChange,
                    { config.onHapticFeedback?.invoke(HapticFeedbackType.SELECTION) }
                )
            }
        },
        onDragEnd = {
            onDragEnd()
        },
        onDragCancel = {
            onDragEnd()
        }
    )
}

/**
 * Track item position and size for drag detection
 */
fun Modifier.trackItemPosition(
    itemIndex: Int,
    itemPositions: MutableMap<Int, Offset>,
    itemSizes: MutableMap<Int, IntSize>
): Modifier = onGloballyPositioned { coordinates ->
    itemPositions[itemIndex] = coordinates.positionInParent()
    itemSizes[itemIndex] = coordinates.size
}

/**
 * Alternative tracking function for when items are keyed by a string ID
 */
fun <T> Modifier.trackItemPositionById(
    itemId: T,
    itemPositions: MutableMap<T, Offset>,
    itemSizes: MutableMap<T, IntSize>
): Modifier = onGloballyPositioned { coordinates ->
    itemPositions[itemId] = coordinates.positionInParent()
    itemSizes[itemId] = coordinates.size
}

// ============ INTERNAL DETECTION IMPLEMENTATIONS ============

/**
 * Grid 2D detection: Uses Euclidean distance to determine swap targets
 * Suitable for grid layouts where items can be swapped in any direction
 */
private fun performGrid2DDetection(
    currentDraggedIndex: Int,
    currentItemIndex: Int,
    dragOffset: Offset,
    itemPositions: Map<Int, Offset>,
    itemSizes: Map<Int, IntSize>,
    config: DragConfig,
    onSwap: (fromIndex: Int, toIndex: Int) -> Unit,
    onDragOffsetChange: (Offset) -> Unit
) {
    val draggedPos = itemPositions[currentDraggedIndex] ?: return
    val draggedSize = itemSizes[currentDraggedIndex] ?: return

    val currentCenter = Offset(
        draggedPos.x + dragOffset.x + draggedSize.width / 2f,
        draggedPos.y + dragOffset.y + draggedSize.height / 2f
    )

    // Find target for swap
    for ((otherIndex, otherPos) in itemPositions) {
        if (otherIndex == currentDraggedIndex) continue
        val otherSize = itemSizes[otherIndex] ?: continue

        val otherCenterX = otherPos.x + otherSize.width / 2f
        val otherCenterY = otherPos.y + otherSize.height / 2f

        val distance = sqrt(
            (currentCenter.x - otherCenterX) * (currentCenter.x - otherCenterX) +
                    (currentCenter.y - otherCenterY) * (currentCenter.y - otherCenterY)
        )

        val threshold = maxOf(otherSize.width, otherSize.height) * config.swapThresholdFraction
        if (distance < threshold) {
            onSwap(currentDraggedIndex, otherIndex)
            onDragOffsetChange(Offset.Zero)
            return
        }
    }
}

/**
 * List vertical detection: Uses vertical position intersection to determine swap targets
 * Suitable for list layouts where items are arranged vertically
 */
private fun <T> performListVerticalDetection(
    currentDraggedIndex: Int,
    currentItemIndex: Int,
    dragOffset: Offset,
    itemPositions: Map<Int, Offset>,
    itemSizes: Map<Int, IntSize>,
    items: List<T>,
    config: DragConfig,
    onSwap: (fromIndex: Int, toIndex: Int) -> Unit,
    onDragOffsetChange: (Offset) -> Unit,
    onSwapHaptic: () -> Unit
) {
    val currentDraggedId = currentItemIndex
    val currentPos = itemPositions[currentDraggedId] ?: return
    val currentSize = itemSizes[currentDraggedId] ?: return

    val currentCenterY = currentPos.y + dragOffset.y + currentSize.height / 2f

    var targetIndex = -1
    for (i in items.indices) {
        if (i == currentDraggedIndex) continue
        val otherId = i
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
        onSwap(currentDraggedIndex, targetIndex)
        onSwapHaptic()

        // Adjust drag offset to maintain visual continuity
        val oldY = itemPositions[currentDraggedIndex]?.y ?: 0f
        val targetY = itemPositions[targetIndex]?.y ?: 0f
        onDragOffsetChange(dragOffset.copy(y = dragOffset.y - (targetY - oldY)))
    }
}
