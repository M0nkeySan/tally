package io.github.m0nkeysan.tally.ui.screens.fingerselector

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.m0nkeysan.tally.GameIcons
import io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.action_back
import io.github.m0nkeysan.tally.generated.resources.cd_settings
import io.github.m0nkeysan.tally.generated.resources.error_min_fingers
import io.github.m0nkeysan.tally.generated.resources.finger_selector_cd_touch
import io.github.m0nkeysan.tally.generated.resources.finger_selector_dialog_title
import io.github.m0nkeysan.tally.generated.resources.finger_selector_finger_count
import io.github.m0nkeysan.tally.generated.resources.finger_selector_instruction_place
import io.github.m0nkeysan.tally.generated.resources.finger_selector_group_count
import io.github.m0nkeysan.tally.generated.resources.finger_selector_group_instruction_wait
import io.github.m0nkeysan.tally.generated.resources.finger_selector_finger_instruction_wait
import io.github.m0nkeysan.tally.generated.resources.finger_selector_label_fingers
import io.github.m0nkeysan.tally.generated.resources.finger_selector_label_groups
import io.github.m0nkeysan.tally.generated.resources.finger_selector_mode_fingers
import io.github.m0nkeysan.tally.generated.resources.finger_selector_mode_groups
import io.github.m0nkeysan.tally.generated.resources.finger_selector_section_mode
import io.github.m0nkeysan.tally.generated.resources.finger_selector_slider_value_format
import io.github.m0nkeysan.tally.generated.resources.game_finger_selector
import io.github.m0nkeysan.tally.platform.HapticType
import io.github.m0nkeysan.tally.platform.rememberHapticFeedbackController
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Vibrant colors for fingers
private val fingerColors = listOf(
    Color(0xFFE91E63), // Pink
    Color(0xFF9C27B0), // Purple
    Color(0xFF673AB7), // Deep Purple
    Color(0xFF3F51B5), // Indigo
    Color(0xFF2196F3), // Blue
    Color(0xFF00BCD4), // Cyan
    Color(0xFF009688), // Teal
    Color(0xFF4CAF50), // Green
    Color(0xFFFFEB3B), // Yellow
    Color(0xFFFF9800), // Orange
    Color(0xFFFF5722), // Deep Orange
    Color(0xFF795548), // Brown
)

enum class SelectionMode {
    FINGERS, GROUPS
}

data class SelectionConfig(
    val mode: SelectionMode = SelectionMode.FINGERS,
    val count: Int = 1 // Number of fingers OR number of groups
)

data class FingerData(
    val position: Offset,
    val color: Color,
    val orbitPhase: Float = Random.nextFloat() * 360f,
    val groupId: Int? = null // For group mode
)

@Composable
fun FingerSelectorScreen(onBack: () -> Unit) {
    var showSettings by remember { mutableStateOf(false) }
    var config by remember { mutableStateOf(SelectionConfig()) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                stringResource(Res.string.game_finger_selector),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = when (config.mode) {
                                    SelectionMode.FINGERS -> pluralStringResource(Res.plurals.finger_selector_finger_count, config.count, config.count)
                                    SelectionMode.GROUPS -> stringResource(Res.string.finger_selector_group_count, config.count)
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            GameIcons.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            GameIcons.Settings,
                            contentDescription = stringResource(Res.string.cd_settings)
                        )
                    }
                },
                modifier = Modifier.shadow(elevation = 2.dp)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            FingerSelectorGame(
                config = config
            )
        }

        if (showSettings) {
            ModalBottomSheet(
                onDismissRequest = { showSettings = false },
                sheetState = sheetState
            ) {
                SelectionSettingsSheet(
                    config = config,
                    onConfigChange = { config = it }
                )
            }
        }
    }
}

@Composable
fun SelectionSettingsSheet(
    config: SelectionConfig,
    onConfigChange: (SelectionConfig) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = stringResource(Res.string.finger_selector_dialog_title),
            style = MaterialTheme.typography.headlineMedium
        )

        // Mode Selection
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(Res.string.finger_selector_section_mode),
                style = MaterialTheme.typography.titleMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = config.mode == SelectionMode.FINGERS,
                    onClick = {
                        onConfigChange(
                            config.copy(
                                mode = SelectionMode.FINGERS,
                                count = 1
                            )
                        )
                    },
                    label = { Text(stringResource(Res.string.finger_selector_mode_fingers)) },
                    leadingIcon = { Icon(GameIcons.TouchApp, "Fingers filter") }
                )
                FilterChip(
                    selected = config.mode == SelectionMode.GROUPS,
                    onClick = {
                        onConfigChange(
                            config.copy(
                                mode = SelectionMode.GROUPS,
                                count = 2
                            )
                        )
                    },
                    label = { Text(stringResource(Res.string.finger_selector_mode_groups)) },
                    leadingIcon = { Icon(GameIcons.Group, null) }
                )
            }
        }

        // Count Selection
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val title =
                if (config.mode == SelectionMode.FINGERS) stringResource(Res.string.finger_selector_label_fingers) else stringResource(
                    Res.string.finger_selector_label_groups
                )
            val min = 1
            val max = 5

            Text(
                stringResource(
                    Res.string.finger_selector_slider_value_format,
                    title,
                    config.count
                ), style = MaterialTheme.typography.titleMedium
            )

            Slider(
                value = config.count.toFloat(),
                onValueChange = { onConfigChange(config.copy(count = it.toInt())) },
                valueRange = min.toFloat()..max.toFloat(),
                steps = max - min - 1
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (min..max).forEach { i ->
                    Text(
                        text = i.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun FingerSelectorGame(
    config: SelectionConfig,
) {
    val fingers = remember { mutableStateMapOf<Long, FingerData>() }
    // We use a set for selected to support multiple selected fingers
    var selected by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var countdownSeconds by remember { mutableStateOf<Int?>(null) }
    var isSelecting by remember { mutableStateOf(false) }
    var lastFingerCount by remember { mutableIntStateOf(0) }
    var countdownStartTime by remember { mutableStateOf<Long?>(null) }

    val hapticController = rememberHapticFeedbackController()

    // Reset state when config changes
    LaunchedEffect(config) {
        fingers.clear()
        selected = emptySet()
        isSelecting = false
        countdownSeconds = null
        countdownStartTime = null
    }

    // Infinite rotation animation for orbiting circles
    val infiniteTransition = rememberInfiniteTransition()
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Expansion animation for the selected finger (only used for single selection)
    val expansionRadius by animateFloatAsState(
        targetValue = if (selected.isNotEmpty() && config.mode == SelectionMode.FINGERS && config.count == 1) 2000f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
    )

    // Countdown logic
    LaunchedEffect(fingers.size) {
        val minFingers = if (config.mode == SelectionMode.GROUPS) config.count else config.count + 1

        if (fingers.size >= minFingers && selected.isEmpty() && !isSelecting) {
            if (fingers.size != lastFingerCount) {
                countdownStartTime = getCurrentTimeMillis()
                lastFingerCount = fingers.size
            }
        } else {
            countdownSeconds = null
            countdownStartTime = null
            lastFingerCount = fingers.size
        }
    }

    // Countdown timer
    LaunchedEffect(countdownStartTime) {
        val minFingers = if (config.mode == SelectionMode.GROUPS) config.count else config.count + 1

        if (countdownStartTime != null && fingers.size >= minFingers && selected.isEmpty() && !isSelecting) {
            delay(500)

            for (i in 3 downTo 1) {
                if (fingers.size < minFingers || selected.isNotEmpty() || isSelecting) {
                    countdownSeconds = null
                    return@LaunchedEffect
                }
                countdownSeconds = i
                hapticController.performHapticFeedback(HapticType.LIGHT)
                delay(1000)
            }

            if (fingers.size >= minFingers && selected.isEmpty()) {
                isSelecting = true
                countdownSeconds = null

                if (config.mode == SelectionMode.FINGERS) {
                    // Select N random fingers
                    val currentFingers = fingers.keys.toList()
                    selected = currentFingers.shuffled().take(config.count).toSet()
                } else {
                    // Assign groups
                    val currentFingers = fingers.keys.toList().shuffled()

                    currentFingers.forEachIndexed { index, id ->
                        val groupId = index % config.count
                        fingers[id]?.let { data ->
                            // Update color based on group
                            val groupColor = fingerColors[groupId % fingerColors.size]
                            fingers[id] = data.copy(
                                groupId = groupId,
                                color = groupColor
                            )
                        }
                    }
                    // Mark all as "selected" so they stay visible
                    selected = fingers.keys.toSet()
                }

                hapticController.performHapticFeedback(HapticType.SUCCESS)

                delay(5000)

                // Reset
                selected = emptySet()
                isSelecting = false
                // Clean up fingers that might still be detected if user held down
                fingers.clear()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(selected, isSelecting) {
                // Lock touch input if we have selected or are in selection process
                if (selected.isNotEmpty() || isSelecting) return@pointerInput

                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        event.changes.forEach { change ->
                            if (change.pressed) {
                                if (!fingers.containsKey(change.id.value)) {
                                    // Assign color: White for groups, Random for winners
                                    val color = if (config.mode == SelectionMode.GROUPS) {
                                        Color.White
                                    } else {
                                        val usedColors = fingers.values.map { it.color }.toSet()
                                        val availableColors =
                                            fingerColors.filter { it !in usedColors }
                                        if (availableColors.isNotEmpty()) {
                                            availableColors.random()
                                        } else {
                                            fingerColors.random()
                                        }
                                    }

                                    fingers[change.id.value] = FingerData(
                                        position = change.position,
                                        color = color
                                    )
                                    hapticController.performHapticFeedback(HapticType.LIGHT)
                                } else {
                                    fingers[change.id.value]?.let { existing ->
                                        fingers[change.id.value] =
                                            existing.copy(position = change.position)
                                    }
                                }
                            } else {
                                if (!isSelecting) {
                                    fingers.remove(change.id.value)
                                }
                            }
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            fingers.forEach { (id, fingerData) ->
                val isSelected = id in selected

                // In Fingers mode: hide unselected
                if (config.mode == SelectionMode.FINGERS && selected.isNotEmpty() && !isSelected) return@forEach

                val baseRadius = 80f
                var radius = baseRadius

                if (selected.isNotEmpty()) {
                    if (config.mode == SelectionMode.FINGERS && config.count == 1) {
                        // Single Finger: Expand to fill screen
                        drawCircle(
                            color = fingerData.color.copy(alpha = 0.8f),
                            center = fingerData.position,
                            radius = expansionRadius
                        )
                        radius = baseRadius + 40f
                    } else if (isSelected) {
                        // Multiple Selected OR Groups: Slight expansion/pulse
                        radius = baseRadius + 20f
                    }
                }

                // Main circle
                drawCircle(
                    color = fingerData.color,
                    center = fingerData.position,
                    radius = radius
                )

                // Inner glow / white center
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f),
                    center = fingerData.position,
                    radius = radius * 0.5f
                )

                // Details (orbit) - show if game is running or if we're in groups mode (to keep it lively)
                // Hide orbit only for single finger focus
                val hideOrbit = config.mode == SelectionMode.FINGERS && selected.isNotEmpty()

                if (!hideOrbit) {
                    val orbitRadius = radius + 30f
                    val currentAngle = orbitAngle + fingerData.orbitPhase
                    val angleInRadians = currentAngle.toDouble() * (PI / 180.0)

                    val orbitX =
                        fingerData.position.x + orbitRadius * cos(angleInRadians).toFloat()
                    val orbitY =
                        fingerData.position.y + orbitRadius * sin(angleInRadians).toFloat()

                    drawCircle(
                        color = fingerData.color.copy(alpha = 0.3f),
                        center = fingerData.position,
                        radius = orbitRadius,
                        style = Stroke(width = 2f)
                    )

                    drawCircle(
                        color = Color.White,
                        center = Offset(orbitX, orbitY),
                        radius = 12f
                    )
                }
            }
        }

        // Countdown
        if (countdownSeconds != null && selected.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = countdownSeconds.toString(),
                    fontSize = 120.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Instructions
        if (fingers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = GameIcons.TouchApp,
                        contentDescription = stringResource(Res.string.finger_selector_cd_touch),
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = stringResource(Res.string.finger_selector_instruction_place),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (config.mode == SelectionMode.GROUPS)
                            stringResource(Res.string.finger_selector_group_instruction_wait)
                        else
                            pluralStringResource(Res.plurals.finger_selector_finger_instruction_wait, config.count, config.count),
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Show "Need more fingers" if not enough
            val minFingers =
                if (config.mode == SelectionMode.GROUPS) config.count else config.count + 1
            if (fingers.size < minFingers && selected.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        text = stringResource(Res.string.error_min_fingers, minFingers),
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
