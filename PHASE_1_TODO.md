# GameKeeper UI/UX Consistency - Phase 1 Implementation TODO

## Overview
AI Agent: Implement Phase 1 (Foundation) - Create reusable components and theme system.
Timeline: Week 1
**IMPORTANT: Commit after each task with the specified commit message.**

---

## PHASE 1: Foundation Implementation

### Task 1.1: Create GameColors.kt Theme System
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/theme/GameColors.kt`

**Objective:** Centralize all color definitions for flat design system

**Implementation:**
```kotlin
package io.github.m0nkeysan.gamekeeper.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Centralized color system for GameKeeper flat design.
 * All colors should be referenced from this object.
 */
object GameColors {
    // Primary Colors (Indigo - modern, professional)
    val Primary = Color(0xFF6366F1)
    val PrimaryLight = Color(0xFFE0E7FF)
    val PrimaryDark = Color(0xFF4F46E5)
    
    // Secondary Colors (Emerald - success)
    val Secondary = Color(0xFF10B981)
    val SecondaryLight = Color(0xFFD1FAE5)
    val SecondaryDark = Color(0xFF059669)
    
    // Tertiary Colors (Amber - warnings)
    val Tertiary = Color(0xFFF59E0B)
    val TertiaryLight = Color(0xFFFEF3C7)
    val TertiaryDark = Color(0xFFD97706)
    
    // Semantic Colors
    val Success = Color(0xFF10B981)
    val Error = Color(0xFFEF4444)
    val Warning = Color(0xFFF59E0B)
    val Info = Color(0xFF3B82F6)
    
    // Neutral Palette (Flat Design)
    val Surface0 = Color(0xFFFFFFFF)     // White
    val Surface1 = Color(0xFFF9FAFB)     // Almost white
    val Surface2 = Color(0xFFF3F4F6)     // Light gray
    val TextPrimary = Color(0xFF111827)   // Dark text
    val TextSecondary = Color(0xFF6B7280) // Medium gray
    val Divider = Color(0xFFE5E7EB)       // Light divider
    
    // Game-Specific Accent Colors
    val TarotAccent = Color(0xFF9333EA)   // Purple - mystical
    val YahtzeeAccent = Color(0xFF06B6D4) // Cyan - fun
    
    // Player Avatar Colors (8-color consistent palette)
    val PlayerAvatarColors = listOf(
        Color(0xFFFF6B6B), // Red
        Color(0xFF4ECDC4), // Teal
        Color(0xFF45B7D1), // Blue
        Color(0xFFFFA07A), // Salmon
        Color(0xFF98D8C8), // Mint
        Color(0xFFF7DC6F), // Yellow
        Color(0xFFBB8FCE), // Purple
        Color(0xFF85C1E2), // Sky
    )
    
    // Trophy/Achievement Colors
    val TrophyGold = Color(0xFFFFD700)
    val TrophySilver = Color(0xFFC0C0C0)
    val TrophyBronze = Color(0xFFCD7F32)
}
```

**Success Criteria:**
- File compiles without errors
- All colors defined in one place
- Can be imported and used: `import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors`

**Commit Message:** `feat: add centralized GameColors theme system for flat design`

---

### Task 1.2: Create StateDisplay.kt Components
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/components/StateDisplay.kt`

**Objective:** Create reusable components for loading, empty, and error states

**Implementation:**
```kotlin
package io.github.m0nkeysan.gamekeeper.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.InboxOutlined
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Loading state with spinner and optional message.
 */
@Composable
fun LoadingState(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = GameColors.Primary)
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = GameColors.TextSecondary
            )
        }
    }
}

/**
 * Empty state with icon, message, and optional action.
 */
@Composable
fun EmptyState(
    icon: ImageVector = Icons.Default.InboxOutlined,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = GameColors.TextSecondary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = GameColors.TextSecondary,
                textAlign = TextAlign.Center
            )
            if (actionLabel != null && onAction != null) {
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GameColors.Primary
                    )
                ) {
                    Text(actionLabel)
                }
            }
        }
    }
}

/**
 * Error state with error icon, message, and optional retry button.
 */
@Composable
fun ErrorState(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = GameColors.Error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = GameColors.TextPrimary,
                textAlign = TextAlign.Center
            )
            if (onRetry != null) {
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GameColors.Error
                    )
                ) {
                    Text("Retry")
                }
            }
        }
    }
}
```

**Success Criteria:**
- All three composables compile
- Use GameColors throughout
- Respect flat design (minimal shadows)
- Can be imported and used in screens

**Commit Message:** `feat: add reusable StateDisplay components (Loading, Empty, Error)`

---

### Task 1.3: Create GameSelectionCard.kt Component
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/components/GameSelectionCard.kt`

**Objective:** Unified card component for all game selections (Tarot & Yahtzee)

**Implementation:**
```kotlin
package io.github.m0nkeysan.gamekeeper.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Data class representing a game for display in selection screens.
 */
data class GameDisplay(
    val id: String,
    val name: String,
    val playerCount: Int,
    val playerNames: String,
    val isFinished: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Unified game selection card for all game types.
 * Uses flat design with minimal elevation.
 */
@Composable
fun GameSelectionCard(
    game: GameDisplay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (game.isFinished) 
                GameColors.Surface2 
            else 
                GameColors.Surface1
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = game.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (game.isFinished) 
                            GameColors.TextSecondary 
                        else 
                            GameColors.TextPrimary
                    )
                    
                    if (game.isFinished) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = GameColors.Success
                        ) {
                            Text(
                                text = "FINISHED",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = GameColors.Surface0,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${game.playerCount} player${if (game.playerCount > 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GameColors.TextSecondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = game.playerNames,
                    style = MaterialTheme.typography.bodySmall,
                    color = GameColors.TextSecondary
                )
            }
            
            if (game.isFinished) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Finished game",
                    tint = GameColors.TrophyGold,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
```

**Success Criteria:**
- Component compiles
- Displays all game information
- Shows status badge for finished games
- Trophy icon for finished games
- Flat design (0.dp elevation)

**Commit Message:** `feat: add unified GameSelectionCard component for all games`

---

### Task 1.4: Create ResultsCard.kt Component
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/components/ResultsCard.kt`

**Objective:** Unified card for displaying game results with ranking

**Implementation:**
```kotlin
package io.github.m0nkeysan.gamekeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Card displaying a player's rank and score in game results.
 * Highlights winners with indigo background.
 */
@Composable
fun ResultsCard(
    rank: Int,
    playerName: String,
    score: Int,
    isWinner: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isWinner) 
                GameColors.PrimaryLight 
            else 
                GameColors.Surface1
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isWinner) 2.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Rank badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (isWinner) GameColors.Primary else GameColors.Surface2
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank.toString(),
                    color = if (isWinner) Color.White else GameColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Player name
            Text(
                text = playerName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f),
                color = GameColors.TextPrimary
            )
            
            // Score
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = if (isWinner) GameColors.Primary else GameColors.TextPrimary
            )
        }
    }
}
```

**Success Criteria:**
- Component compiles
- Shows rank, player name, and score
- Winners highlighted with indigo background
- Flat design aesthetic

**Commit Message:** `feat: add unified ResultsCard component for game results display`

---

### Task 1.5: Create SnackbarManager.kt Component
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/components/SnackbarManager.kt`

**Objective:** Reusable Snackbar for error messages throughout app

**Implementation:**
```kotlin
package io.github.m0nkeysan.gamekeeper.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Standard Snackbar host configuration for GameKeeper.
 * - Duration: 5 seconds
 * - Position: Bottom center
 * - Action: Dismiss only (no retry)
 */
@Composable
fun GameKeeperSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { data ->
        Snackbar(
            snackbarData = data,
            containerColor = GameColors.Error,
            contentColor = GameColors.Surface0,
            actionColor = GameColors.Surface0
        )
    }
}

/**
 * Helper function to show error message in Snackbar.
 * Auto-dismisses after 5 seconds.
 */
suspend fun showErrorSnackbar(
    hostState: SnackbarHostState,
    message: String
) {
    hostState.showSnackbar(
        message = message,
        duration = SnackbarDuration.Long, // 5 seconds
        withDismissAction = true
    )
}
```

**Success Criteria:**
- Compiles without errors
- Can be integrated into Scaffold
- Shows for 5 seconds
- Bottom center position
- Dismiss-only (no retry action)

**Commit Message:** `feat: add SnackbarManager for error feedback (5s, bottom center, dismiss-only)`

---

### Task 1.6: Create FlexiblePlayerSelector.kt Component
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/components/FlexiblePlayerSelector.kt`

**Objective:** Reusable player selector for Tarot (3-5) and Yahtzee (2-8)

**Implementation:**
```kotlin
package io.github.m0nkeysan.gamekeeper.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors
import kotlin.random.Random

/**
 * Flexible player selector that supports min/max player constraints.
 * - Tarot: minPlayers=3, maxPlayers=5
 * - Yahtzee: minPlayers=2, maxPlayers=8
 */
@Composable
fun FlexiblePlayerSelector(
    minPlayers: Int,
    maxPlayers: Int,
    allPlayers: List<Player>,
    onPlayersChange: (List<Player?>) -> Unit,
    onCreatePlayer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPlayers by remember { mutableStateOf(List<Player?>(minPlayers) { null }) }
    var showError by remember { mutableStateOf(false) }
    
    // Update callback when players change
    LaunchedEffect(selectedPlayers) {
        onPlayersChange(selectedPlayers)
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Players (${selectedPlayers.size}/$maxPlayers)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GameColors.TextPrimary
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Remove button
                IconButton(
                    onClick = {
                        if (selectedPlayers.size > minPlayers) {
                            selectedPlayers = selectedPlayers.dropLast(1)
                            showError = false
                        } else {
                            showError = true
                        }
                    },
                    enabled = selectedPlayers.size > minPlayers
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Remove player",
                        tint = if (selectedPlayers.size > minPlayers) 
                            GameColors.Error 
                        else 
                            GameColors.TextSecondary
                    )
                }
                
                // Add button
                IconButton(
                    onClick = {
                        if (selectedPlayers.size < maxPlayers) {
                            selectedPlayers = selectedPlayers + null
                            showError = false
                        } else {
                            showError = true
                        }
                    },
                    enabled = selectedPlayers.size < maxPlayers
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add player",
                        tint = if (selectedPlayers.size < maxPlayers) 
                            GameColors.Success 
                        else 
                            GameColors.TextSecondary
                    )
                }
            }
        }
        
        // Validation error message
        if (showError) {
            Text(
                text = "Player count must be between $minPlayers and $maxPlayers",
                style = MaterialTheme.typography.bodySmall,
                color = GameColors.Error
            )
        }
        
        // Player selector fields
        selectedPlayers.forEachIndexed { index, player ->
            PlayerSelectorField(
                label = "Player ${index + 1}",
                selectedPlayer = player,
                allPlayers = allPlayers,
                onPlayerSelected = { newPlayer ->
                    selectedPlayers = selectedPlayers.toMutableList().apply {
                        set(index, newPlayer)
                    }
                },
                onNewPlayerCreated = { name ->
                    onCreatePlayer(name)
                },
                excludedPlayerIds = selectedPlayers
                    .filterNotNull()
                    .filter { it != player }
                    .map { it.id }
                    .toSet()
            )
        }
    }
}
```

**Success Criteria:**
- Compiles without errors
- Can add players up to maxPlayers
- Can remove players down to minPlayers
- Shows validation errors inline
- Calls callback when players change
- Uses PlayerSelectorField component

**Commit Message:** `feat: add FlexiblePlayerSelector for dynamic player count (min/max validation)`

---

### Task 1.7: Create GameSelectionTemplate.kt Screen Template
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/common/GameSelectionTemplate.kt`

**Objective:** Reusable screen template for all game selections

**Implementation:**
```kotlin
package io.github.m0nkeysan.gamekeeper.ui.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.ui.components.*
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Reusable template for game selection screens.
 * Handles loading, error, and empty states.
 * Provides consistent UI for Tarot and Yahtzee game selection.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSelectionTemplate(
    title: String,
    games: List<GameDisplay>,
    onGameSelect: (String) -> Unit,
    onCreateNew: () -> Unit,
    onDeleteGame: (GameDisplay) -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error in Snackbar
    LaunchedEffect(error) {
        if (error != null) {
            showErrorSnackbar(snackbarHostState, error)
        }
    }
    
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNew,
                containerColor = GameColors.Primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create new game")
            }
        },
        snackbarHost = {
            GameKeeperSnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(
                    message = "Loading games...",
                    modifier = Modifier.padding(paddingValues)
                )
            }
            games.isEmpty() -> {
                EmptyState(
                    message = "No games yet. Create one!",
                    actionLabel = "Create Game",
                    onAction = onCreateNew,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(games, key = { it.id }) { game ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    onDeleteGame(game)
                                }
                                false
                            }
                        )
                        
                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 20.dp)
                                )
                            }
                        ) {
                            GameSelectionCard(
                                game = game,
                                onClick = { onGameSelect(game.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
```

**Success Criteria:**
- Compiles without errors
- Shows loading state correctly
- Shows error in Snackbar
- Shows empty state when no games
- LazyColumn displays games
- FAB works
- Swipe-to-delete support

**Commit Message:** `feat: add GameSelectionTemplate for unified game selection screens`

---

### Task 1.8: Create GameCreationTemplate.kt Screen Template
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/common/GameCreationTemplate.kt`

**Objective:** Reusable screen template for all game creation flows

**Implementation:**
```kotlin
package io.github.m0nkeysan.gamekeeper.ui.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.ui.components.GameKeeperSnackbarHost
import io.github.m0nkeysan.gamekeeper.ui.components.showErrorSnackbar
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Reusable template for game creation screens.
 * Provides consistent layout with scrollable content and action buttons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCreationTemplate(
    title: String,
    onBack: () -> Unit,
    onCreate: () -> Unit,
    canCreate: Boolean,
    error: String?,
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error in Snackbar
    LaunchedEffect(error) {
        if (error != null) {
            showErrorSnackbar(snackbarHostState, error)
        }
    }
    
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = {
            GameKeeperSnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = onCreate,
                        enabled = canCreate,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GameColors.Primary,
                            disabledContainerColor = GameColors.Surface2
                        )
                    ) {
                        Text("Create Game")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            content()
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
```

**Success Criteria:**
- Compiles without errors
- Content renders correctly in scrollable area
- Buttons at bottom are sticky
- Error displays in Snackbar
- Create button disabled when canCreate=false

**Commit Message:** `feat: add GameCreationTemplate for unified game creation screens`

---

### Task 1.9: Create ResultsTemplate.kt Screen Template
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/common/ResultsTemplate.kt`

**Objective:** Reusable screen template for game results display

**Implementation:**
```kotlin
package io.github.m0nkeysan.gamekeeper.ui.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.m0nkeysan.gamekeeper.ui.components.ResultsCard
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Reusable template for game results/summary screens.
 * Displays winners and ranked results consistently.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsTemplate(
    winners: List<Pair<String, Int>>,
    allResults: List<Pair<String, Int>>,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Game Over", 
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        bottomBar = {
            Button(
                onClick = onHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GameColors.Primary
                )
            ) {
                Text("BACK TO HOME", fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Trophy icon
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = GameColors.TrophyGold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Winner announcement
            Text(
                text = if (winners.size > 1) "IT'S A TIE!" else "WE HAVE A WINNER!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = GameColors.Primary
            )
            
            Text(
                text = winners.joinToString(" & ") { it.first }.uppercase(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = GameColors.TextPrimary
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Final scores header
            Text(
                text = "FINAL SCORES",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = GameColors.TextSecondary,
                letterSpacing = 1.5.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Results list
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(allResults) { index, (name, score) ->
                    val isWinner = winners.any { it.first == name }
                    ResultsCard(
                        rank = index + 1,
                        playerName = name,
                        score = score,
                        isWinner = isWinner
                    )
                }
            }
        }
    }
}
```

**Success Criteria:**
- Compiles without errors
- Trophy icon displays correctly
- Winner names show (join with " & ")
- "IT'S A TIE!" shows when multiple winners
- All results displayed in ranked order
- Back button calls onHome

**Commit Message:** `feat: add ResultsTemplate for unified game results display`

---

### Task 1.10: Integration & Testing
**Objective:** Verify all Phase 1 components work together

**Actions:**
1. Build project:
```bash
./gradlew :composeApp:compileDebugKotlinAndroid
```

2. Verify:
- ✅ All 9 new files created
- ✅ No compilation errors
- ✅ No warnings in new code
- ✅ GameColors can be imported
- ✅ Components can be imported

3. Check file structure:
```bash
find composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui -name "*.kt" -type f | grep -E "(theme|components|common)" | sort
```

**Success Criteria:**
- ✅ Project builds successfully
- ✅ No errors in Phase 1 components
- ✅ All new files present
- ✅ Colors are importable and usable
- ✅ Components are importable

**Commit Message:** `build: verify Phase 1 foundation components compile successfully`

---

## GENERAL REQUIREMENTS

### Code Style
- Use Kotlin conventions
- Add KDoc comments to public functions
- Use meaningful variable names
- Keep functions focused

### Commit Strategy
**CRITICAL: Commit after each task (10 commits total)**
- Use present tense: "add", "create", "update"
- Reference file names
- Keep commits atomic

### Testing
- After each task, verify compilation
- Use `./gradlew :composeApp:compileDebugKotlinAndroid`

### Documentation
- Add KDoc comments to all public composables
- Include parameter descriptions

---

## Success Criteria for Phase 1

✅ All 9 new files created
✅ 10 commits made (one per task)
✅ Project compiles without errors
✅ Flat design throughout
✅ GameColors used everywhere
✅ Ready for Phase 2

---

## Notes for AI Agent

- Do NOT edit existing screens in Phase 1
- Do NOT modify ViewModels
- Focus on creating clean, reusable components
- Use GameColors for all colors (no hardcoded hex)
- **Commit after each task**

Good luck! 🚀
