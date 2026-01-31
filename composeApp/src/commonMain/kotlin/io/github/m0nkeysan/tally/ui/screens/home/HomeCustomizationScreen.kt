package io.github.m0nkeysan.tally.ui.screens.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.GameIcons
import io.github.m0nkeysan.tally.core.domain.model.HomeFeatureState
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.action_save
import io.github.m0nkeysan.tally.generated.resources.home_customize_discard
import io.github.m0nkeysan.tally.generated.resources.home_customize_keep_editing
import io.github.m0nkeysan.tally.generated.resources.home_customize_title
import io.github.m0nkeysan.tally.generated.resources.home_customize_unsaved_message
import io.github.m0nkeysan.tally.generated.resources.home_customize_unsaved_title
import io.github.m0nkeysan.tally.platform.HapticFeedbackController
import io.github.m0nkeysan.tally.platform.HapticType
import io.github.m0nkeysan.tally.platform.rememberHapticFeedbackController
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeCustomizationScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: HomeCustomizationViewModel = viewModel { HomeCustomizationViewModel() }
) {
    val featureStates by viewModel.featureStates.collectAsState()
    val hasChanges by viewModel.hasChanges.collectAsState()
    val scope = rememberCoroutineScope()
    val hapticController = rememberHapticFeedbackController()

    var showUnsavedDialog by remember { mutableStateOf(false) }

    // Handle back navigation with unsaved changes check
    val handleBack = {
        if (hasChanges) {
            showUnsavedDialog = true
        } else {
            onNavigateBack()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.home_customize_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = handleBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            FeatureList(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                featureStates = featureStates,
                onToggleFeature = viewModel::toggleFeature,
                onReorderFeatures = viewModel::reorderFeatures,
                hapticController = hapticController
            )

            if (hasChanges) {
                Button(
                    onClick = {
                        scope.launch {
                            val success = viewModel.saveChanges()
                            if (success) {
                                onSaveSuccess()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        GameIcons.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.action_save))
                }
            }
        }
    }

    // Unsaved changes dialog
    if (showUnsavedDialog) {
        UnsavedChangesDialog(
            onSave = {
                scope.launch {
                    val success = viewModel.saveChanges()
                    if (success) {
                        showUnsavedDialog = false
                        onSaveSuccess()
                    }
                }
            },
            onDiscard = {
                viewModel.discardChanges()
                showUnsavedDialog = false
                onNavigateBack()
            },
            onDismiss = {
                showUnsavedDialog = false
            }
        )
    }
}

@Composable
private fun FeatureList(
    modifier: Modifier = Modifier,
    featureStates: List<HomeFeatureState>,
    onToggleFeature: (String) -> Unit,
    onReorderFeatures: (List<String>) -> Unit,
    hapticController: HapticFeedbackController
) {
    val listState = rememberLazyListState()

    var draggedItemId by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    var previewOrder by remember { mutableStateOf<List<HomeFeatureState>?>(null) }

    val displayStates = previewOrder ?: featureStates
    val gameFeatureMap = getGameFeatureMap()

    LazyColumn(
        state = listState,
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = draggedItemId == null // Disable scrolling while dragging
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        itemsIndexed(
            items = displayStates,
            key = { _, state -> state.featureId }
        ) { _, state ->
            val isDragging = draggedItemId == state.featureId
            val scale by animateFloatAsState(if (isDragging) 1.05f else 1f)
            val elevation by animateDpAsState(if (isDragging) 12.dp else 0.dp)

            Box(
                modifier = Modifier
                    .zIndex(if (isDragging) 1f else 0f)
                    .graphicsLayer {
                        if (isDragging) {
                            translationY = dragOffset.y
                            rotationZ = 2f
                        }
                    }
                    .scale(scale)
                    .shadow(elevation, shape = MaterialTheme.shapes.medium)
                    .pointerInput(state.featureId) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                draggedItemId = state.featureId
                                previewOrder = featureStates.toList()
                                dragOffset = Offset.Zero
                                hapticController.performHapticFeedback(HapticType.MEDIUM)
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffset += dragAmount

                                val currentList = previewOrder ?: return@detectDragGesturesAfterLongPress
                                val activeId = draggedItemId ?: return@detectDragGesturesAfterLongPress

                                val currentIdx = currentList.indexOfFirst { it.featureId == activeId }
                                if (currentIdx == -1) return@detectDragGesturesAfterLongPress

                                val visibleItems = listState.layoutInfo.visibleItemsInfo

                                val currentItemInfo = visibleItems.find { it.index == currentIdx + 1 }
                                    ?: return@detectDragGesturesAfterLongPress

                                val currentItemCenterY = currentItemInfo.offset + dragOffset.y + (currentItemInfo.size / 2f)

                                val targetItem = visibleItems.find {
                                    it.index != (currentIdx + 1) &&
                                            it.index > 0 &&
                                            it.index <= currentList.size &&
                                            currentItemCenterY > it.offset &&
                                            currentItemCenterY < (it.offset + it.size)
                                }

                                if (targetItem != null) {
                                    val targetIdx = targetItem.index - 1

                                    val newOrder = currentList.toMutableList()
                                    val item = newOrder.removeAt(currentIdx)
                                    newOrder.add(targetIdx, item)

                                    previewOrder = newOrder

                                    val distance = targetItem.offset - currentItemInfo.offset
                                    dragOffset -= Offset(0f, distance.toFloat())

                                    hapticController.performHapticFeedback(HapticType.SELECTION)
                                }
                            },
                            onDragEnd = {
                                previewOrder?.let { finalOrder ->
                                    onReorderFeatures(finalOrder.map { it.featureId })
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
                FeatureItem(
                    state = state,
                    gameFeature = gameFeatureMap[state.featureId],
                    onToggle = { onToggleFeature(state.featureId) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun FeatureItem(
    modifier: Modifier = Modifier,
    state: HomeFeatureState,
    gameFeature: GameFeature?,
    onToggle: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (state.enabled) {
                MaterialTheme.colorScheme.surfaceContainerLow
            } else {
                MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Drag handle icon
            Icon(
                GameIcons.DragHandle,
                contentDescription = "Drag to reorder",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Feature icon
            if (gameFeature != null) {
                Box(modifier = Modifier.size(48.dp)) {
                    gameFeature.icon()
                }
            } else {
                Icon(
                    GameIcons.GridView,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Feature title with disabled indicator
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = gameFeature?.title ?: state.featureId,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (state.enabled) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    if (!state.enabled) {
                        Icon(
                            GameIcons.VisibilityOff,
                            contentDescription = "Hidden",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (gameFeature?.description != null) {
                    Text(
                        text = gameFeature.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Toggle switch
            Switch(
                checked = state.enabled,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@Composable
private fun UnsavedChangesDialog(
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(Res.string.home_customize_unsaved_title))
        },
        text = {
            Text(stringResource(Res.string.home_customize_unsaved_message))
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("Save")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onDiscard) {
                    Text(stringResource(Res.string.home_customize_discard))
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(Res.string.home_customize_keep_editing))
                }
            }
        }
    )
}
