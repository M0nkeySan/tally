package io.github.m0nkeysan.gamekeeper.ui.screens.fingerselector

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.platform.HapticType
import io.github.m0nkeysan.gamekeeper.platform.rememberHapticFeedbackController
import kotlinx.coroutines.delay
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
    WINNERS, GROUPS
}

data class SelectionConfig(
    val mode: SelectionMode = SelectionMode.WINNERS,
    val count: Int = 1 // Number of winners OR number of groups
)

data class FingerData(
    val position: Offset,
    val color: Color,
    val orbitPhase: Float = Random.nextFloat() * 360f,
    val groupId: Int? = null // For group mode
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FingerSelectorScreen(onBack: () -> Unit) {
    var showSettings by remember { mutableStateOf(false) }
    var config by remember { mutableStateOf(SelectionConfig()) }
    val sheetState = rememberModalBottomSheetState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Finger Selector")
                        Text(
                            text = when(config.mode) {
                                SelectionMode.WINNERS -> "${config.count} Winner${if(config.count > 1) "s" else ""}"
                                SelectionMode.GROUPS -> "${config.count} Groups"
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(GameIcons.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            FingerSelectorGame(
                config = config,
                onBack = onBack
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
            text = "Selector Settings",
            style = MaterialTheme.typography.headlineMedium
        )
        
        // Mode Selection
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Mode", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = config.mode == SelectionMode.WINNERS,
                    onClick = { onConfigChange(config.copy(mode = SelectionMode.WINNERS, count = 1)) },
                    label = { Text("Winners") },
                    leadingIcon = { Icon(GameIcons.TouchApp, null) }
                )
                FilterChip(
                    selected = config.mode == SelectionMode.GROUPS,
                    onClick = { onConfigChange(config.copy(mode = SelectionMode.GROUPS, count = 2)) },
                    label = { Text("Groups") },
                    leadingIcon = { Icon(GameIcons.Group, null) }
                )
            }
        }
        
        // Count Selection
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val title = if (config.mode == SelectionMode.WINNERS) "Number of Winners" else "Number of Groups"
            val min = 1
            val max = 5
            
            Text("$title: ${config.count}", style = MaterialTheme.typography.titleMedium)
            
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
    onBack: () -> Unit
) {
    val fingers = remember { mutableStateMapOf<Long, FingerData>() }
    // We use a set for winners to support multiple winners
    var winners by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var countdownSeconds by remember { mutableStateOf<Int?>(null) }
    var isSelecting by remember { mutableStateOf(false) }
    var lastFingerCount by remember { mutableStateOf(0) }
    var countdownStartTime by remember { mutableStateOf<Long?>(null) }
    
    val hapticController = rememberHapticFeedbackController()
    
    // Reset state when config changes
    LaunchedEffect(config) {
        winners = emptySet()
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
    
    // Expansion animation for the winner (only used for single winner)
    val expansionRadius by animateFloatAsState(
        targetValue = if (winners.isNotEmpty() && config.mode == SelectionMode.WINNERS && config.count == 1) 2000f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
    )

    // Countdown logic
    LaunchedEffect(fingers.size) {
        val minFingers = if (config.mode == SelectionMode.GROUPS) config.count else config.count + 1
        
        if (fingers.size >= minFingers && winners.isEmpty() && !isSelecting) {
            if (fingers.size != lastFingerCount) {
                countdownStartTime = System.currentTimeMillis()
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
        
        if (countdownStartTime != null && fingers.size >= minFingers && winners.isEmpty() && !isSelecting) {
            delay(500)
            
            for (i in 5 downTo 1) {
                if (fingers.size < minFingers || winners.isNotEmpty() || isSelecting) {
                    countdownSeconds = null
                    return@LaunchedEffect
                }
                countdownSeconds = i
                hapticController.performHapticFeedback(HapticType.LIGHT)
                delay(1000)
            }
            
            if (fingers.size >= minFingers && winners.isEmpty()) {
                isSelecting = true
                countdownSeconds = null
                
                if (config.mode == SelectionMode.WINNERS) {
                    // Select N random winners
                    val currentFingers = fingers.keys.toList()
                    winners = currentFingers.shuffled().take(config.count).toSet()
                } else {
                    // Assign groups
                    val currentFingers = fingers.keys.toList().shuffled()
                    val groupAssignments = mutableMapOf<Long, FingerData>()
                    
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
                    // Mark all as "winners" so they stay visible
                    winners = fingers.keys.toSet()
                }
                
                hapticController.performHapticFeedback(HapticType.SUCCESS)
                
                delay(5000)
                
                // Reset
                winners = emptySet()
                isSelecting = false
                // Clean up fingers that might still be detected if user held down
                fingers.clear()
                
                // Reset colors for groups mode logic handled by clear()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(winners, isSelecting) {
                // Lock touch input if we have winners or are in selection process
                if (winners.isNotEmpty() || isSelecting) return@pointerInput
                
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
                                        val availableColors = fingerColors.filter { it !in usedColors }
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
                                        fingers[change.id.value] = existing.copy(position = change.position)
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
                val isWinner = id in winners
                
                // In Winners mode: hide losers
                if (config.mode == SelectionMode.WINNERS && winners.isNotEmpty() && !isWinner) return@forEach
                
                val baseRadius = 80f
                var radius = baseRadius
                var alpha = 1f
                
                if (winners.isNotEmpty()) {
                    if (config.mode == SelectionMode.WINNERS && config.count == 1 && isWinner) {
                        // Single Winner: Expand to fill screen
                        drawCircle(
                            color = fingerData.color.copy(alpha = 0.8f),
                            center = fingerData.position,
                            radius = expansionRadius
                        )
                        radius = baseRadius + 40f
                    } else if (isWinner) {
                        // Multiple Winners OR Groups: Slight expansion/pulse
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
                // Hide orbit only for single winner focus
                val hideOrbit = config.mode == SelectionMode.WINNERS && winners.isNotEmpty()
                
                if (!hideOrbit) {
                    val orbitRadius = radius + 30f
                    val currentAngle = orbitAngle + fingerData.orbitPhase
                    val orbitX = fingerData.position.x + orbitRadius * cos(Math.toRadians(currentAngle.toDouble())).toFloat()
                    val orbitY = fingerData.position.y + orbitRadius * sin(Math.toRadians(currentAngle.toDouble())).toFloat()
                    
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
        if (countdownSeconds != null && winners.isEmpty()) {
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
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Place your fingers",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (config.mode == SelectionMode.GROUPS) 
                            "Wait for group assignment" 
                        else 
                            "${config.count} winner${if(config.count > 1) "s" else ""} will be chosen",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Show "Need more fingers" if not enough
            val minFingers = if (config.mode == SelectionMode.GROUPS) config.count else config.count + 1
            if (fingers.size < minFingers && winners.isEmpty()) {
                 Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        text = "Need at least $minFingers fingers",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
