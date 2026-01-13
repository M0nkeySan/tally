# GameKeeper Project - Comprehensive Improvement Plan

**Date:** January 12, 2026  
**Project:** GameKeeper  
**Scope:** Full project analysis and improvement roadmap

---

## Executive Summary

A comprehensive analysis of the GameKeeper codebase identified **62 improvement opportunities** across 5 categories:

| Category | Critical | High | Medium | Low | Total |
|----------|----------|------|--------|-----|-------|
| **Code Cleanup** | 1 | 3 | 4 | 5 | 13 |
| **Consistency** | 0 | 4 | 6 | 3 | 13 |
| **Performance** | 0 | 3 | 3 | 2 | 8 |
| **Accessibility** | 0 | 17 | 2 | 0 | 19 |
| **Architecture** | 0 | 2 | 4 | 3 | 9 |
| **Total** | **1** | **29** | **19** | **13** | **62** |

### User Preferences
- Accessibility: Include all fixes
- Database changes: Allowed (app not published)
- Internationalization: Move strings to AppStrings
- Architecture: Set up DI and base classes

---

## Table of Contents

1. [Critical Issues](#1-critical-issues)
2. [Code Cleanup](#2-code-cleanup)
3. [Consistency Issues](#3-consistency-issues)
4. [Performance Improvements](#4-performance-improvements)
5. [Accessibility](#5-accessibility)
6. [Architecture Improvements](#6-architecture-improvements)
7. [Database Design](#7-database-design)
8. [Implementation Plan](#8-implementation-plan)
9. [File Reference Index](#9-file-reference-index)

---

## 1. Critical Issues

### 1.1 Blocking UI Thread with runBlocking

**File:** `composeApp/src/commonMain/kotlin/.../ui/screens/player/PlayerSelectionViewModel.kt`  
**Lines:** 89-92

**Current Code:**
```kotlin
fun getGameCountForPlayer(playerId: String): Int {
    return runBlocking {  // BLOCKS UI THREAD!
        gameQueryHelper.getGameCountForPlayer(playerId)
    }
}
```

**Problem:** `runBlocking` blocks the main/UI thread, which can freeze the app on slow devices or during slow database operations.

**Solution:** Convert to suspend function or use StateFlow:
```kotlin
// Option 1: Suspend function
suspend fun getGameCountForPlayer(playerId: String): Int {
    return gameQueryHelper.getGameCountForPlayer(playerId)
}

// Option 2: Expose as StateFlow
private val _gameCountsMap = MutableStateFlow<Map<String, Int>>(emptyMap())
val gameCountsMap: StateFlow<Map<String, Int>> = _gameCountsMap.asStateFlow()
```

**Impact:** App freeze/ANR on slow devices  
**Priority:** CRITICAL  
**Effort:** Low (30 min)

---

## 2. Code Cleanup

### 2.1 Duplicate Code - generateRandomColor()

**Duplicated in:**
- `ui/screens/tarot/TarotGameCreationScreen.kt` (lines 26-29)
- `ui/screens/yahtzee/YahtzeeGameCreationScreen.kt` (lines 26-29)

**Current Code (duplicated):**
```kotlin
fun generateRandomColor(): String {
    val color = Random.nextInt(0xFFFFFF)
    return "#${color.toString(16).padStart(6, '0')}"
}
```

**Solution:** Create `ui/utils/ColorUtils.kt`:
```kotlin
package io.github.m0nkeysan.gamekeeper.ui.utils

import kotlin.random.Random

object ColorUtils {
    /**
     * Generate a random hex color string (e.g., "#FF5733")
     */
    fun generateRandomHexColor(): String {
        val color = Random.nextInt(0xFFFFFF)
        return "#${color.toString(16).padStart(6, '0')}"
    }
    
    /**
     * Generate a random pastel color as Long (ARGB format)
     */
    fun generateRandomPastelColor(): Long {
        val hue = Random.nextFloat() * 360
        val saturation = 0.4f + Random.nextFloat() * 0.2f
        val lightness = 0.7f + Random.nextFloat() * 0.15f
        // Convert HSL to RGB, then to Long
        // ... implementation
        return 0xFFFFFFFF
    }
}
```

---

### 2.2 System.currentTimeMillis() in Common Code

**Count:** 13 usages in `commonMain/`  
**Problem:** Platform-specific code in shared module

**Files affected:**
| File | Lines |
|------|-------|
| `CounterRepositoryImpl.kt` | 67, 68, 87, 88 |
| `CounterViewModel.kt` | 125 |
| `FingerSelectorScreen.kt` | 236 |
| `GameSelectionCard.kt` | 106, 107, 123, 124 |
| `YahtzeeScoringViewModel.kt` | 88, 111 |
| `PlayerDao.kt` | 38 |

**Solution:** Use existing `getCurrentTimeMillis()` from `Player.kt`:
```kotlin
// Already exists in Player.kt
expect fun getCurrentTimeMillis(): Long

// Replace all usages:
// System.currentTimeMillis() -> getCurrentTimeMillis()
```

---

### 2.3 Suppress Warnings to Address

| File | Line | Suppression | Action |
|------|------|-------------|--------|
| `HapticFeedback.android.kt` | 22 | `@Suppress("DEPRECATION")` | Keep (needed for older Android) |

---

### 2.4 Unused Navigation Screen

**File:** `core/navigation/Screen.kt`  
**Line:** 30

```kotlin
object AddPlayer : Screen("add_player")  // Never used in NavGraph.kt
```

**Action:** Remove `Screen.AddPlayer` if not needed

---

## 3. Consistency Issues

### 3.1 Inconsistent Error Handling in ViewModels

| ViewModel | Has Error Handling | Pattern |
|-----------|-------------------|---------|
| `TarotGameViewModel` | Yes | try-catch with state update |
| `TarotScoringViewModel` | Yes | try-catch with state update |
| `YahtzeeGameViewModel` | Yes | try-catch with state update |
| `YahtzeeScoringViewModel` | Yes | try-catch with state update |
| `CounterViewModel` | No | No error handling |
| `HomeViewModel` | No | No error handling |
| `PlayerSelectionViewModel` | No | No error handling |

**Solution:** Add consistent error handling:
```kotlin
// Add to CounterViewModel
private val _error = MutableStateFlow<String?>(null)
val error: StateFlow<String?> = _error.asStateFlow()

fun clearError() {
    _error.value = null
}

// Wrap operations:
viewModelScope.launch(Dispatchers.IO) {
    try {
        // operation
    } catch (e: Exception) {
        _error.value = "Failed to perform action: ${e.message}"
    }
}
```

---

### 3.2 Inline Imports in Selection Screens

**Files:**
- `ui/screens/tarot/TarotGameSelectionScreen.kt` (lines 19-37)
- `ui/screens/yahtzee/YahtzeeGameSelectionScreen.kt` (lines 19-37)

**Current (Bad):**
```kotlin
androidx.compose.material3.AlertDialog(
    onDismissRequest = { gameToDelete = null },
    title = { androidx.compose.material3.Text("Delete Game") },
    text = { androidx.compose.material3.Text("Are you sure...") },
    // ...
)
```

**Solution:** Add proper imports at top of file:
```kotlin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
```

---

### 3.3 Hardcoded Colors (Not Using Theme)

| File | Lines | Current | Replace With |
|------|-------|---------|--------------|
| `CounterHistoryScreen.kt` | 168, 224 | `Color(0xFFCCCCCC)` | `MaterialTheme.colorScheme.outline` |
| `CounterHistoryScreen.kt` | 234 | `Color(0xFF4CAF50)` | `GameColors.Success` |
| `CounterHistoryScreen.kt` | 236 | `Color(0xFFF44336)` | `GameColors.Error` |
| `CounterScreen.kt` | 663 | `Color(0xFFFBFBFB)` | `GameColors.Surface0` |
| `CommonFields.kt` | 55-56 | `Color(0xFFFBFBFB)` | `GameColors.Surface0` |
| `YahtzeeScoringScreen.kt` | 289 | `Color.Gray` | `GameColors.TextSecondary` |
| `ColorSelectorRow.kt` | 92 | `Color(0xFF4CAF50)` | `GameColors.Success` |

**Note:** `FingerSelectorScreen.kt` (lines 29-41) has 12 hardcoded finger colors. These are intentional for the black-background selector but could be moved to `GameColors.fingerColors`.

---

### 3.4 Hardcoded Strings (Move to AppStrings)

#### CounterScreen.kt
| Line | Current | AppStrings Key |
|------|---------|----------------|
| 98 | `"Counter"` | `COUNTER_TITLE` |
| 126 | `"Settings"` | `COUNTER_SETTINGS` |
| 134 | `"Reinitialise all"` | `COUNTER_REINITIALIZE_ALL` |
| 142 | `"Delete everything"` | `COUNTER_DELETE_EVERYTHING` |
| 170-174 | `"Tap + to add a counter"` | `COUNTER_EMPTY_STATE` |

#### GameSelectionTemplate.kt
| Line | Current | AppStrings Key |
|------|---------|----------------|
| 81 | `"Delete All Games"` | `GAME_DELETE_ALL_TITLE` |
| 82 | `"Are you sure..."` | `GAME_DELETE_ALL_CONFIRM` |
| 129 | `"Create Game"` | `GAME_CREATE` |
| 166 | `"Loading games..."` | `GAME_LOADING` |
| 172 | `"No games yet. Create one!"` | `GAME_EMPTY_STATE` |

#### GameCreationTemplate.kt
| Line | Current | AppStrings Key |
|------|---------|----------------|
| Various | `"Cancel"` | `ACTION_CANCEL` |
| Various | `"Create"` | `ACTION_CREATE` |

#### HomeScreen.kt
| Line | Current | AppStrings Key |
|------|---------|----------------|
| 206 | `"Finger Selector"` | `GAME_FINGER_SELECTOR` |
| 207 | `"Randomly select..."` | `DESC_FINGER_SELECTOR` |

#### Common Strings
| String | AppStrings Key |
|--------|----------------|
| `"Back"` | `ACTION_BACK` |
| `"Delete"` | `ACTION_DELETE` |
| `"Save"` | `ACTION_SAVE` |
| `"Confirm"` | `ACTION_CONFIRM` |
| `"Game Name"` | `GAME_NAME_LABEL` |

**New AppStrings.kt additions:**
```kotlin
object AppStrings {
    // Existing...
    
    // Actions
    const val ACTION_BACK = "Back"
    const val ACTION_CANCEL = "Cancel"
    const val ACTION_CREATE = "Create"
    const val ACTION_DELETE = "Delete"
    const val ACTION_SAVE = "Save"
    const val ACTION_CONFIRM = "Confirm"
    
    // Counter
    const val COUNTER_TITLE = "Counter"
    const val COUNTER_SETTINGS = "Settings"
    const val COUNTER_REINITIALIZE_ALL = "Reinitialise all"
    const val COUNTER_DELETE_EVERYTHING = "Delete everything"
    const val COUNTER_EMPTY_STATE = "Tap + to add a counter"
    
    // Game Selection
    const val GAME_CREATE = "Create Game"
    const val GAME_LOADING = "Loading games..."
    const val GAME_EMPTY_STATE = "No games yet. Create one!"
    const val GAME_DELETE_ALL_TITLE = "Delete All Games"
    const val GAME_DELETE_ALL_CONFIRM = "Are you sure you want to delete all games? This cannot be undone."
    const val GAME_NAME_LABEL = "Game Name"
    
    // Finger Selector
    const val GAME_FINGER_SELECTOR = "Finger Selector"
    const val DESC_FINGER_SELECTOR = "Randomly select a starting player with multi-touch"
}
```

---

### 3.5 Naming Convention Inconsistencies

**Constants should use SCREAMING_SNAKE_CASE:**

**Current (GameConfig.kt):**
```kotlin
const val yahtzeeMinPlayers = 2
const val yahtzeeMaxPlayers = 8
const val tarotMinPlayers = 3
const val tarotMaxPlayers = 5
```

**Recommended:**
```kotlin
const val YAHTZEE_MIN_PLAYERS = 2
const val YAHTZEE_MAX_PLAYERS = 8
const val TAROT_MIN_PLAYERS = 3
const val TAROT_MAX_PLAYERS = 5
```

---

### 3.6 ViewModel State Management Inconsistency

**Current patterns:**
| ViewModel | State Type |
|-----------|-----------|
| `CounterViewModel` | `CounterUiState` data class |
| `HomeViewModel` | Simple `List<String>` |
| `TarotGameViewModel` | `TarotGameSelectionState` data class |
| `PlayerSelectionViewModel` | Multiple separate StateFlows |

**Recommendation:** Standardize on data class approach for all ViewModels:
```kotlin
// Every ViewModel should have:
data class XxxUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val data: T? = null
)
```

---

## 4. Performance Improvements

### 4.1 N+1 Query Pattern (HIGH PRIORITY)

**Files:** `TarotGameViewModel.kt`, `YahtzeeGameViewModel.kt`

**Problem:** Loading player names creates N database queries per game:
```kotlin
repository.getAllGames().collect { games ->
    val displayModels = games.map { game ->
        val names = game.playerIds.split(",").map { id ->
            playerRepository.getPlayerById(id)?.name ?: "Unknown"  // N queries!
        }.joinToString(", ")
        // ...
    }
}
```

For 10 games with 4 players each = 1 + 40 database queries.

**Solution:** Add batch loading to PlayerRepository:
```kotlin
// PlayerRepository.kt
suspend fun getPlayersByIds(ids: List<String>): Map<String, Player>

// PlayerRepositoryImpl.kt
override suspend fun getPlayersByIds(ids: List<String>): Map<String, Player> {
    return dao.getPlayersByIds(ids)
        .map { it.toDomain() }
        .associateBy { it.id }
}

// PlayerDao.kt
@Query("SELECT * FROM players WHERE id IN (:ids)")
suspend fun getPlayersByIds(ids: List<String>): List<PlayerEntity>

// Usage in ViewModel:
val allPlayerIds = games.flatMap { it.playerIds.split(",") }.distinct()
val playersMap = playerRepository.getPlayersByIds(allPlayerIds)  // Single query!

val displayModels = games.map { game ->
    val names = game.playerIds.split(",")
        .mapNotNull { playersMap[it]?.name }
        .joinToString(", ")
    // ...
}
```

---

### 4.2 Sequential Delete Operations

**Problem:** `deleteAllGames` deletes games sequentially:
```kotlin
fun deleteAllGames(onError: (String) -> Unit = {}) {
    viewModelScope.launch {
        selectionState.value.games.forEach { displayModel ->
            deleteGame(displayModel.game)  // Sequential, not batched
        }
    }
}
```

**Solution:** Add batch delete to DAOs:
```kotlin
// TarotDao.kt
@Query("DELETE FROM tarot_games")
suspend fun deleteAllGames()

@Query("DELETE FROM tarot_rounds WHERE gameId IN (SELECT id FROM tarot_games)")
suspend fun deleteAllRounds()

// TarotRepository.kt
suspend fun deleteAllGames() {
    dao.deleteAllRounds()
    dao.deleteAllGames()
}
```

---

### 4.3 CounterHistoryStore Efficiency

**Problem:** Creates new list on every change:
```kotlin
suspend fun addChange(change: CounterChange) {
    _history.value = _history.value + change  // Creates new list each time
}
```

**Solution:** Add size limit and use more efficient operations:
```kotlin
class CounterHistoryStore {
    companion object {
        const val MAX_HISTORY_SIZE = 1000
    }
    
    suspend fun addChange(change: CounterChange) {
        _history.update { currentList ->
            val newList = currentList + change
            if (newList.size > MAX_HISTORY_SIZE) {
                newList.drop(newList.size - MAX_HISTORY_SIZE)
            } else {
                newList
            }
        }
    }
}
```

---

### 4.4 Missing Flow Operators

**Problem:** Some ViewModels collect raw flows without optimization:
```kotlin
viewModelScope.launch {
    counterRepository.getAllCounters().collect { counters -> ... }
}
```

**Solution:** Use `stateIn` with `WhileSubscribed`:
```kotlin
val counters: StateFlow<List<Counter>> = counterRepository.getAllCounters()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
```

---

### 4.5 Thread-Unsafe Repository Singletons

**Problem:** `PlatformRepositories` uses nullable singletons with manual null checks:
```kotlin
private var playerRepository: PlayerRepository? = null

actual fun getPlayerRepository(): PlayerRepository {
    return playerRepository ?: PlayerRepositoryImpl(...).also {
        playerRepository = it
    }
}
```

**Solution:** Use `lazy` initialization for thread safety:
```kotlin
actual object PlatformRepositories {
    private lateinit var database: GameDatabase
    
    val playerRepository: PlayerRepository by lazy {
        PlayerRepositoryImpl(database.playerDao(), gameQueryHelper)
    }
}
```

---

## 5. Accessibility

### 5.1 Missing contentDescription on Icons (17 locations)

| File | Line | Icon | Recommended Description |
|------|------|------|------------------------|
| `HomeScreen.kt` | 205 | TouchApp | "Finger Selector game" |
| `HomeScreen.kt` | 212 | Tarot | "Tarot game" |
| `HomeScreen.kt` | 219 | Casino | "Yahtzee game" |
| `HomeScreen.kt` | 226 | Add | "Counter game" |
| `CounterScreen.kt` | 131 | Settings | "Settings menu" |
| `CounterScreen.kt` | 139 | Reset | "Reset all counters" |
| `CounterScreen.kt` | 147 | Delete | "Delete all counters" |
| `CounterScreen.kt` | 165 | Add | "Add new counter" |
| `GameSelectionTemplate.kt` | 134 | Add | "Create new game" |
| `GameSelectionTemplate.kt` | 143 | Delete | "Delete all games" |
| `FingerSelectorScreen.kt` | 444 | TouchApp | "Touch here to place finger" |
| `YahtzeeScoringScreen.kt` | 155 | ArrowDropDown | "Select player dropdown" |
| `StateDisplay.kt` | 94 | Folder | "" (decorative) |
| `StateDisplay.kt` | 153 | Error | "" (decorative) |
| `PlayerSelector.kt` | 118 | Person | "Player avatar" |
| `FlexiblePlayerSelector.kt` | Various | Add/Remove | "Add/Remove player" |
| `GameCard.kt` | Various | Game icons | Context-dependent |

**Pattern to apply:**
```kotlin
// Interactive icons - meaningful description
Icon(
    imageVector = GameIcons.Add,
    contentDescription = "Add new counter"
)

// Decorative icons - empty string
Icon(
    imageVector = Icons.Default.Folder,
    contentDescription = ""  // Screen reader will skip
)
```

---

### 5.2 Color-Only Information

**Problem:** Positive/negative changes indicated only by color (green/red)

**File:** `CounterHistoryScreen.kt` (lines 234-236)

**Current:**
```kotlin
color = if (mergedChange.totalDelta > 0) {
    Color(0xFF4CAF50)  // Green
} else if (mergedChange.totalDelta < 0) {
    Color(0xFFF44336)  // Red
}
```

**Solution:** Already includes `+` prefix. Ensure consistent usage:
```kotlin
text = "${if (mergedChange.totalDelta > 0) "+" else ""}${mergedChange.totalDelta}"
```

---

### 5.3 Touch Target Sizes

**Audit needed:** Ensure all clickable areas have minimum 48.dp touch target.

Most touch targets appear adequate (e.g., counter buttons use 40.dp minimum).

---

## 6. Architecture Improvements

### 6.1 Dependency Injection with Koin

**Current:** Direct singleton access everywhere:
```kotlin
private val repository = PlatformRepositories.getTarotRepository()
private val playerRepository = PlatformRepositories.getPlayerRepository()
```

**Problems:**
- Hard to test (can't mock repositories)
- No lifecycle management
- Tight coupling

**Solution:** Set up Koin (already in dependencies):

**File:** `di/AppModule.kt` (new)
```kotlin
package io.github.m0nkeysan.gamekeeper.di

import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {
    // Repositories
    single { PlatformRepositories.getPlayerRepository() }
    single { PlatformRepositories.getTarotRepository() }
    single { PlatformRepositories.getYahtzeeRepository() }
    single { PlatformRepositories.getCounterRepository() }
    single { PlatformRepositories.getUserPreferencesRepository() }
    single { PlatformRepositories.getGameQueryHelper() }
    
    // ViewModels
    viewModel { HomeViewModel(get()) }
    viewModel { CounterViewModel(get(), get()) }
    viewModel { TarotGameViewModel(get(), get()) }
    viewModel { YahtzeeGameViewModel(get(), get()) }
    viewModel { PlayerSelectionViewModel(get(), get()) }
    // ... etc
}
```

**File:** `App.kt` or `MainActivity.kt`:
```kotlin
class GameKeeperApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GameKeeperApplication)
            modules(appModule)
        }
    }
}
```

**Updated ViewModel:**
```kotlin
class TarotGameViewModel(
    private val repository: TarotRepository,
    private val playerRepository: PlayerRepository
) : ViewModel() {
    // ...
}

// Usage in Composable:
val viewModel: TarotGameViewModel = koinViewModel()
```

---

### 6.2 Base Game ViewModel

**Problem:** `TarotGameViewModel` and `YahtzeeGameViewModel` share ~70% identical code.

**Solution:** Create abstract base class:

**File:** `ui/viewmodel/BaseGameViewModel.kt` (new)
```kotlin
package io.github.m0nkeysan.gamekeeper.ui.viewmodel

abstract class BaseGameViewModel<G : Any, S : GameSelectionState<G>>(
    protected val playerRepository: PlayerRepository
) : ViewModel() {
    
    protected abstract val _selectionState: MutableStateFlow<S>
    abstract val selectionState: StateFlow<S>
    
    val allPlayers: StateFlow<List<Player>> = playerRepository.getAllPlayers()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    fun savePlayer(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                playerRepository.addPlayer(player)
            } catch (e: Exception) {
                updateError("Failed to save player: ${e.message}")
            }
        }
    }
    
    protected abstract fun updateError(message: String)
    protected abstract fun updateLoading(isLoading: Boolean)
    
    abstract fun loadGames()
    abstract fun createGame(/* params */)
    abstract fun deleteGame(game: G)
    abstract fun deleteAllGames(onError: (String) -> Unit = {})
}

interface GameSelectionState<G> {
    val games: List<GameDisplayModel<G>>
    val isLoading: Boolean
    val error: String?
}

data class GameDisplayModel<G>(
    val game: G,
    val playerNames: String,
    val displayDate: String
)
```

**Updated TarotGameViewModel:**
```kotlin
class TarotGameViewModel(
    private val repository: TarotRepository,
    playerRepository: PlayerRepository
) : BaseGameViewModel<TarotGame, TarotGameSelectionState>(playerRepository) {
    
    override val _selectionState = MutableStateFlow(TarotGameSelectionState())
    override val selectionState = _selectionState.asStateFlow()
    
    override fun updateError(message: String) {
        _selectionState.update { it.copy(error = message) }
    }
    
    override fun updateLoading(isLoading: Boolean) {
        _selectionState.update { it.copy(isLoading = isLoading) }
    }
    
    // Implement abstract methods...
}
```

---

### 6.3 Extract Reusable Drag-and-Drop Component

**Problem:** Similar drag-and-drop logic duplicated in:
- `HomeScreen.kt` (lines 94-175)
- `CounterScreen.kt` (lines 197-270)

**Solution:** Create reusable component:

**File:** `ui/components/ReorderableList.kt` (new)
```kotlin
@Composable
fun <T> ReorderableLazyGrid(
    items: List<T>,
    key: (T) -> Any,
    columns: GridCells,
    onReorder: (fromIndex: Int, toIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T, Boolean) -> Unit  // item, isDragging
) {
    var draggedItemKey by remember { mutableStateOf<Any?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    val gridState = rememberLazyGridState()
    
    LazyVerticalGrid(
        state = gridState,
        columns = columns,
        modifier = modifier,
        userScrollEnabled = draggedItemKey == null
    ) {
        itemsIndexed(items, key = { _, item -> key(item) }) { index, item ->
            val isDragging = draggedItemKey == key(item)
            
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        if (isDragging) {
                            translationX = dragOffset.x
                            translationY = dragOffset.y
                        }
                    }
                    .pointerInput(key(item)) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { draggedItemKey = key(item) },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffset += dragAmount
                                // Handle swap detection...
                            },
                            onDragEnd = {
                                draggedItemKey = null
                                dragOffset = Offset.Zero
                            },
                            onDragCancel = {
                                draggedItemKey = null
                                dragOffset = Offset.Zero
                            }
                        )
                    }
            ) {
                itemContent(item, isDragging)
            }
        }
    }
}
```

---

### 6.4 Extract Delete Confirmation Dialog

**Problem:** Identical delete confirmation dialogs in multiple files.

**Solution:** Create reusable component:

**File:** `ui/components/ConfirmationDialog.kt` (new)
```kotlin
@Composable
fun DeleteConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = AppStrings.ACTION_DELETE,
    dismissText: String = AppStrings.ACTION_CANCEL
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}
```

---

## 7. Database Design

### 7.1 Missing Indices

**Add indices to improve query performance:**

**PlayerEntity.kt:**
```kotlin
@Entity(
    tableName = "players",
    indices = [
        Index(value = ["isActive"]),
        Index(value = ["name"])
    ]
)
data class PlayerEntity(...)
```

**PersistentCounterEntity.kt:**
```kotlin
@Entity(
    tableName = "counters",
    indices = [Index(value = ["sortOrder"])]
)
data class PersistentCounterEntity(...)
```

**TarotGameEntity.kt:**
```kotlin
@Entity(
    tableName = "tarot_games",
    indices = [Index(value = ["updatedAt"])]
)
data class TarotGameEntity(...)
```

**YahtzeeGameEntity.kt:**
```kotlin
@Entity(
    tableName = "yahtzee_games",
    indices = [Index(value = ["updatedAt"])]
)
data class YahtzeeGameEntity(...)
```

---

### 7.2 Missing Foreign Key Constraints

**YahtzeeScoreEntity.kt:**
```kotlin
@Entity(
    tableName = "yahtzee_scores",
    indices = [Index(value = ["gameId"])],
    foreignKeys = [
        ForeignKey(
            entity = YahtzeeGameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class YahtzeeScoreEntity(...)
```

**TarotRoundEntity.kt:**
```kotlin
@Entity(
    tableName = "tarot_rounds",
    indices = [Index(value = ["gameId"])],
    foreignKeys = [
        ForeignKey(
            entity = TarotGameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TarotRoundEntity(...)
```

**Benefits:**
- Automatic cascade deletes
- Data integrity at database level
- Removes need for manual transaction handling

---

### 7.3 Inconsistent Repository Error Handling

**Current patterns:**
| Repository | Pattern |
|------------|---------|
| `PlayerRepositoryImpl` | try-catch with boolean return |
| `TarotRepositoryImpl` | try-catch with null return (swallows errors) |
| `CounterRepositoryImpl` | No error handling |

**Solution:** Use Result wrapper consistently:
```kotlin
sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val exception: Exception, val message: String) : Result<T>()
}

// Usage:
override suspend fun addCounter(counter: Counter): Result<Unit> {
    return try {
        dao.insertCounter(counter.toEntity())
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e, "Failed to add counter: ${e.message}")
    }
}
```

---

## 8. Implementation Plan

### Phase 1: Quick Wins (1-2 hours)
**Goal:** Remove dead code, clean up structure

- [ ] Remove 5 empty directories
- [ ] Remove 4 unused files (Greeting.kt, Platform.kt, BaseViewModel.kt, GameRepository.kt)
- [ ] Rename `AppDimensions.kt` to `GameConfig.kt`
- [ ] Remove `Screen.AddPlayer` from navigation
- [ ] Remove deprecated `AppDimensions` alias
- [ ] Extract `generateRandomColor()` to `ColorUtils.kt`

### Phase 2: Critical & High Priority Fixes (2-3 hours)
**Goal:** Fix bugs and performance issues

- [ ] Fix `runBlocking` in `PlayerSelectionViewModel`
- [ ] Replace `System.currentTimeMillis()` with `getCurrentTimeMillis()` (13 locations)
- [ ] Add error handling to `CounterViewModel`, `HomeViewModel`, `PlayerSelectionViewModel`
- [ ] Fix N+1 query pattern with batch player loading
- [ ] Add proper imports to selection screens

### Phase 3: Accessibility (1-2 hours)
**Goal:** Make app accessible

- [ ] Add `contentDescription` to 17+ icons
- [ ] Audit touch target sizes

### Phase 4: Consistency (3-4 hours)
**Goal:** Standardize patterns across codebase

- [ ] Replace hardcoded colors with `GameColors` references
- [ ] Move hardcoded strings to `AppStrings.kt`
- [ ] Rename constants to `SCREAMING_SNAKE_CASE`
- [ ] Remove `@Suppress("UNCHECKED_CAST")` with proper typing

### Phase 5: Database (1-2 hours)
**Goal:** Improve database design

- [ ] Add indices to entities
- [ ] Add foreign key constraints
- [ ] Add batch delete methods to DAOs

### Phase 6: Architecture (4-6 hours)
**Goal:** Improve maintainability and testability

- [ ] Set up Koin dependency injection
- [ ] Create `BaseGameViewModel` abstract class
- [ ] Extract `ReorderableList` component
- [ ] Extract `DeleteConfirmationDialog` component
- [ ] Add batch loading to repositories
- [ ] Standardize Result wrapper for error handling

---

## 9. File Reference Index

### Files to Delete
| File | Reason |
|------|--------|
| `Greeting.kt` | Unused boilerplate |
| `Platform.kt` | Unused |
| `ui/viewmodel/BaseViewModel.kt` | Unused |
| `core/domain/repository/GameRepository.kt` | Unused interface |

### Directories to Delete
| Directory | Reason |
|-----------|--------|
| `core/data/mapper/` | Empty |
| `ui/screens/history/` | Empty |
| `ui/screens/tarot/components/` | Empty |
| `ui/screens/yahtzee/components/` | Empty |
| `utils/` (root) | Empty |

### Files to Create
| File | Purpose |
|------|---------|
| `ui/utils/ColorUtils.kt` | Color generation utilities |
| `ui/components/ConfirmationDialog.kt` | Reusable delete confirmation |
| `ui/components/ReorderableList.kt` | Drag-and-drop list |
| `ui/viewmodel/BaseGameViewModel.kt` | Abstract base for game VMs |
| `di/AppModule.kt` | Koin DI module |

### Files to Modify (Major Changes)
| File | Changes |
|------|---------|
| `PlayerSelectionViewModel.kt` | Fix runBlocking |
| `CounterViewModel.kt` | Add error handling |
| `HomeViewModel.kt` | Add error handling |
| `TarotGameViewModel.kt` | Extend BaseGameViewModel |
| `YahtzeeGameViewModel.kt` | Extend BaseGameViewModel |
| `AppStrings.kt` | Add ~30 new strings |
| `GameConfig.kt` | Rename constants |
| `PlayerDao.kt` | Add batch loading |
| `PlayerRepository.kt` | Add batch loading |
| All entities | Add indices and FK constraints |

### Files to Modify (Minor Changes)
| File | Changes |
|------|---------|
| `CounterHistoryScreen.kt` | Replace hardcoded colors |
| `CounterScreen.kt` | Replace strings, colors |
| `GameSelectionTemplate.kt` | Replace strings |
| `TarotGameSelectionScreen.kt` | Fix imports |
| `YahtzeeGameSelectionScreen.kt` | Fix imports |
| `TarotGameCreationScreen.kt` | Use ColorUtils |
| `YahtzeeGameCreationScreen.kt` | Use ColorUtils |
| 13 files | Replace System.currentTimeMillis |
| 10+ files | Add contentDescription |

---

## Summary

This improvement plan addresses 62 issues across the GameKeeper codebase:

| Priority | Count | Status |
|----------|-------|--------|
| Critical | 1 | Ready to fix |
| High | 29 | Ready to fix |
| Medium | 19 | Ready to fix |
| Low | 13 | Ready to fix |

**Estimated Total Effort:** 12-18 hours

**Recommended Approach:**
1. Start with Phase 1-2 (Quick wins + Critical fixes)
2. Complete Phase 3 (Accessibility)
3. Progressively work through Phase 4-6

The codebase has a solid foundation. These improvements will enhance:
- **Reliability:** Error handling, null safety
- **Performance:** N+1 queries, database indices
- **Accessibility:** Screen reader support
- **Maintainability:** DI, base classes, code reuse
- **Consistency:** Naming, patterns, theme usage

---

**Document Status:** Ready for Implementation  
**Last Updated:** January 12, 2026
