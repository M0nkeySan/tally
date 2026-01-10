# Soft Delete Player Implementation Plan - FINAL

**Status**: Ready for Implementation  
**Date**: January 10, 2025  
**Branch**: `feature/soft-delete-players`

---

## Overview

Implement soft delete for players with system-wide deactivation allowing players to be hidden instead of permanently deleted. When a player is deleted, they are marked as inactive and remain in the database to maintain game history integrity.

### Key Features

- **Deactivation**: Players marked as inactive instead of deleted from database
- **Game Link Warning**: Displays "Linked to X games" when deactivating a player
- **Reactivation by Name**: Creating a new player with a deactivated player's name reactivates the original
- **UI Treatment**: 
  - Deactivated players don't appear in Player Selector dialogs
  - Appear faded (alpha=0.5) with strikethrough text in management screen
  - Separate section at bottom of management screen
- **Swipe Gestures**:
  - Active players: Swipe right-to-left to deactivate
  - Deactivated players: Swipe left-to-right to reactivate
- **Notifications**: Toast messages shown on deactivation and reactivation
- **Scope**: Tarot & Yahtzee games only (Counter & FingerSelector unaffected)
- **Migration**: Room auto-migration (fields default to true/null for existing data)

---

## Phase 1: Database Schema Updates

### 1.1 PlayerEntity.kt

**Location**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/core/data/local/database/PlayerEntity.kt`

**Changes**:
```kotlin
@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey
    val id: String = Uuid.random().toString(),
    val name: String,
    val avatarColor: String,
    val createdAt: Long,
    val isActive: Boolean = true,           // NEW - defaults to true
    val deactivatedAt: Long? = null         // NEW - defaults to null
)
```

**Rationale**:
- `isActive: Boolean = true` - Simple flag for filtering active/inactive players
- `deactivatedAt: Long? = null` - Timestamp for when player was deactivated (useful for future features)
- Default values enable Room auto-migration (no explicit migration script needed)
- Existing players automatically have `isActive = true` and `deactivatedAt = null`

### 1.2 PlayerDao.kt

**Location**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/core/data/local/database/PlayerDao.kt`

**New Query Methods**:
```kotlin
// Get only active players (used by Player Selector)
@Query("SELECT * FROM players WHERE isActive = true ORDER BY name ASC")
suspend fun getAllActivePlayers(): List<PlayerEntity>

// Get all players including inactive (used by Management Screen)
@Query("SELECT * FROM players ORDER BY isActive DESC, name ASC")
suspend fun getAllPlayersIncludingInactive(): List<PlayerEntity>

// Get active player by exact name (for creating new)
@Query("SELECT * FROM players WHERE name = ? AND isActive = true LIMIT 1")
suspend fun getActivePlayerByName(name: String): PlayerEntity?

// Get ANY player by exact name - active OR inactive (for reactivation)
@Query("SELECT * FROM players WHERE name = ? ORDER BY isActive DESC LIMIT 1")
suspend fun getPlayerByName(name: String): PlayerEntity?

// Soft delete - mark as inactive
@Query("UPDATE players SET isActive = false, deactivatedAt = :timestamp WHERE id = :id")
suspend fun softDeletePlayer(id: String, timestamp: Long = System.currentTimeMillis())

// Reactivate - mark as active
@Query("UPDATE players SET isActive = true, deactivatedAt = null WHERE id = :id")
suspend fun reactivatePlayer(id: String)
```

**Query Purpose**:
- `getAllActivePlayers()` - Used by Player Selector to show only active players
- `getAllPlayersIncludingInactive()` - Used by Management Screen to show all players
- `getPlayerByName()` - Critical for reactivation-by-name logic (finds ANY player matching name)
- `softDeletePlayer()` - Soft delete updates `isActive` and timestamp instead of hard delete
- `reactivatePlayer()` - Resets both flags to make player active again

**Modifications to Existing Methods**:
- Remove or replace `@Delete suspend fun deletePlayer(player: PlayerEntity)` 
- Now soft delete instead via `softDeletePlayer()` query

### 1.3 TarotDao.kt

**Location**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/core/data/local/database/TarotDao.kt`

**New Query Method**:
```kotlin
@Query("SELECT COUNT(*) FROM tarot_games WHERE playerIds LIKE '%' || ? || '%'")
suspend fun countGamesWithPlayer(playerId: String): Int
```

**Purpose**: Count how many Tarot games contain this player ID (used in deactivation warning dialog)

### 1.4 YahtzeeDao.kt

**Location**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/core/data/local/database/YahtzeeDao.kt`

**New Query Method**:
```kotlin
@Query("SELECT COUNT(*) FROM yahtzee_games WHERE playerIds LIKE '%' || ? || '%'")
suspend fun countGamesWithPlayer(playerId: String): Int
```

**Purpose**: Count how many Yahtzee games contain this player ID (used in deactivation warning dialog)

---

## Phase 2: Domain Layer Updates

### 2.1 Player.kt

**Location**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/core/model/Player.kt`

**Changes to data class**:
```kotlin
data class Player(
    val id: String = Uuid.random().toString(),
    val name: String,
    val avatarColor: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,           // NEW
    val deactivatedAt: Long? = null         // NEW
) {
    companion object {
        fun create(name: String, color: String): Player {
            return Player(
                name = name,
                avatarColor = color,
                isActive = true,
                deactivatedAt = null
            )
        }
    }
}
```

**Changes to Extension Functions**:
```kotlin
// Update PlayerEntity.toDomain() conversion
fun PlayerEntity.toDomain(): Player = Player(
    id = id,
    name = name,
    avatarColor = avatarColor,
    createdAt = createdAt,
    isActive = isActive,
    deactivatedAt = deactivatedAt
)

// Update Player.toEntity() conversion
fun Player.toEntity(): PlayerEntity = PlayerEntity(
    id = id,
    name = name,
    avatarColor = avatarColor,
    createdAt = createdAt,
    isActive = isActive,
    deactivatedAt = deactivatedAt
)
```

**Location of extensions**: Same file or PlayerEntity.kt (check existing pattern in codebase)

### 2.2 PlayerRepository.kt (Interface)

**Location**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/core/domain/repository/PlayerRepository.kt`

**New Methods**:
```kotlin
interface PlayerRepository {
    // Existing methods (behavior unchanged)
    fun getAllPlayers(): Flow<List<Player>>
    suspend fun getPlayerById(id: String): Player?
    suspend fun insertPlayer(player: Player)
    suspend fun updatePlayer(player: Player)
    suspend fun deletePlayer(player: Player)  // Now soft delete via Dao change
    
    // NEW methods
    fun getAllPlayersIncludingInactive(): Flow<List<Player>>
    suspend fun getPlayerByName(name: String): Player?
    suspend fun reactivatePlayer(player: Player)
}
```

**Method Purposes**:
- `getAllPlayersIncludingInactive()` - Flow for management screen to show all players
- `getPlayerByName()` - Find any player by name (active or inactive) for reactivation logic
- `reactivatePlayer()` - Mark player as active again

### 2.3 PlayerRepositoryImpl.kt

**Location**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/core/data/local/repository/PlayerRepositoryImpl.kt`

**New Implementations**:
```kotlin
override fun getAllPlayersIncludingInactive(): Flow<List<Player>> =
    playerDao.getAllPlayersIncludingInactive().map { entities ->
        entities.map { it.toDomain() }
    }

override suspend fun getPlayerByName(name: String): Player? =
    playerDao.getPlayerByName(name)?.toDomain()

override suspend fun deletePlayer(player: Player) {
    playerDao.softDeletePlayer(player.id)  // Changed from hard delete
}

override suspend fun reactivatePlayer(player: Player) {
    playerDao.reactivatePlayer(player.id)
}
```

**Important**: 
- `getAllPlayers()` still returns only active players (existing behavior preserved)
- `deletePlayer()` now calls `softDeletePlayer()` instead of hard delete
- All other existing methods remain unchanged

---

## Phase 3: Data Layer - Game Link Detection

### 3.1 Create GameQueryHelper.kt (NEW FILE)

**Location**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/core/data/local/repository/GameQueryHelper.kt`

**Purpose**: Detect if a player is linked to any games

```kotlin
package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.TarotDao
import io.github.m0nkeysan.gamekeeper.core.data.local.database.YahtzeeDao

/**
 * Helper class for querying game-player relationships.
 * Used to detect if a player is linked to any active games.
 */
class GameQueryHelper(
    private val tarotDao: TarotDao,
    private val yahtzeeDao: YahtzeeDao
) {
    /**
     * Get total count of games (Tarot + Yahtzee) that contain this player.
     * Used in deactivation warning dialog to show user how many games will be affected.
     *
     * @param playerId The ID of the player to check
     * @return Count of games containing this player
     */
    suspend fun getGameCountForPlayer(playerId: String): Int {
        val tarotCount = tarotDao.countGamesWithPlayer(playerId)
        val yahtzeeCount = yahtzeeDao.countGamesWithPlayer(playerId)
        return tarotCount + yahtzeeCount
    }
}
```

**Integration Points**:
- Instantiate in `PlatformRepositories.kt` or similar singleton
- Inject into `PlayerSelectionViewModel` for use in deactivation dialog

---

## Phase 4: UI Layer - Player Selector Update

### 4.1 PlayerSelectorContent.kt

**Location**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/components/PlayerSelectorContent.kt`

**Change 1: Filter to Active Players Only**

```kotlin
val filteredPlayers = remember(searchQuery, allPlayers, excludedPlayerIds) {
    val activeList = allPlayers.filter { it.isActive }  // NEW: Filter to active only
    val baseList = activeList.filter { it.id !in excludedPlayerIds }
    if (searchQuery.isBlank()) baseList
    else baseList.filter { it.name.contains(searchQuery, ignoreCase = true) }
}
```

**Change 2: Reactivation-by-Name Logic**

When user tries to create a new player, check if a deactivated player with that name exists:

```kotlin
// In the trailing icon button (Create button) of the TextField
trailingIcon = {
    if (searchQuery.isNotBlank() && !allPlayers.any { 
        it.isActive && it.name.equals(searchQuery, ignoreCase = true) 
    }) {
        IconButton(onClick = {
            // Check if a deactivated player with this name exists
            val deactivatedPlayer = allPlayers.find {
                !it.isActive && it.name.equals(searchQuery, ignoreCase = true)
            }
            
            if (deactivatedPlayer != null) {
                // Reactivate existing player instead of creating new
                viewModel.reactivatePlayer(deactivatedPlayer)
                onSelect(deactivatedPlayer)  // Auto-select for the game
                showToast("Player '${deactivatedPlayer.name}' has been reactivated")
            } else {
                // Create new player
                val newPlayer = Player.create(searchQuery, generateRandomColor())
                viewModel.savePlayer(newPlayer)
                onSelect(newPlayer)
            }
            searchQuery = ""
        }) {
            Icon(Icons.Default.Add, contentDescription = "Add New")
        }
    }
}
```

**Also apply same logic to the "Create [name]" card** that appears in the list when user types a new name.

---

## Phase 5: UI Layer - Player Management Screen

### 5.1 PlayerSelectionScreen.kt

**Location**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/player/PlayerSelectionScreen.kt`

**Change 1: Use allPlayersIncludingInactive Flow**

```kotlin
@Composable
fun PlayerSelectionScreen(...) {
    val allPlayers by viewModel.allPlayersIncludingInactive.collectAsState(emptyList())
    // ... rest of composable
}
```

**Change 2: Separate Active and Deactivated Sections**

Replace the current single list with two sections:

```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
) {
    // ACTIVE PLAYERS SECTION
    if (activePlayers.isNotEmpty()) {
        Text("Players", style = MaterialTheme.typography.titleMedium)
        activePlayers.forEach { player ->
            PlayerCard(
                player = player,
                isActive = true,
                onDelete = { playerToDelete = player },
                onReactivate = {}  // Not used for active players
            )
        }
    }
    
    // DEACTIVATED PLAYERS SECTION
    val deactivatedPlayers = allPlayers.filter { !it.isActive }
    if (deactivatedPlayers.isNotEmpty()) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("Deactivated", style = MaterialTheme.typography.titleMedium)
        deactivatedPlayers.forEach { player ->
            PlayerCard(
                player = player,
                isActive = false,
                onDelete = {},  // Not used for deactivated players
                onReactivate = {
                    viewModel.reactivatePlayer(player)
                    showToast("Player '${player.name}' has been reactivated")
                }
            )
        }
    }
}
```

**Change 3: PlayerCard Composable**

Create or update a reusable PlayerCard that handles both active and deactivated appearance:

```kotlin
@Composable
private fun PlayerCard(
    player: Player,
    isActive: Boolean,
    onDelete: () -> Unit,
    onReactivate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isActive) 1f else 0.5f)  // Fade deactivated players
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar (with adjusted opacity for inactive)
            Surface(
                shape = CircleShape,
                modifier = Modifier.size(40.dp),
                color = parseColor(player.avatarColor)
                    .copy(alpha = if (isActive) 1f else 0.5f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = player.name.firstOrNull()?.uppercase() ?: "P",
                        color = if (parseColor(player.avatarColor).luminance() > 0.5f) 
                            Color.Black else Color.White
                    )
                }
            }
            
            // Player Name (with strikethrough if inactive)
            Text(
                text = player.name,
                textDecoration = if (isActive) 
                    TextDecoration.None 
                else 
                    TextDecoration.LineThrough,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
```

**Change 4: Deactivation Confirmation Dialog**

```kotlin
var playerToDelete by remember { mutableStateOf<Player?>(null) }

if (playerToDelete != null) {
    // Load game count asynchronously
    var gameCount by remember { mutableStateOf(0) }
    LaunchedEffect(playerToDelete) {
        gameCount = viewModel.getGameCountForPlayer(playerToDelete!!.id)
    }
    
    AlertDialog(
        onDismissRequest = { playerToDelete = null },
        title = { Text("Deactivate Player?") },
        text = { 
            Text(
                if (gameCount > 0) {
                    "Linked to $gameCount game(s)"
                } else {
                    "Are you sure?"
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    playerToDelete?.let { player ->
                        viewModel.deletePlayer(player)
                        showToast("Player '${player.name}' deactivated")
                    }
                    playerToDelete = null
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) { Text("Deactivate") }
        },
        dismissButton = {
            TextButton(onClick = { playerToDelete = null }) { Text("Cancel") }
        }
    )
}
```

---

## Phase 6: ViewModel Updates

### 6.1 PlayerSelectionViewModel.kt

**Location**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/player/PlayerSelectionViewModel.kt`

**New Properties and Methods**:

```kotlin
class PlayerSelectionViewModel : ViewModel() {
    private val playerRepository = PlatformRepositories.getPlayerRepository()
    private val gameQueryHelper = PlatformRepositories.getGameQueryHelper()  // NEW
    
    // For Player Selector (only active players)
    val allPlayers: Flow<List<Player>> = playerRepository.getAllPlayers()
    
    // For Management Screen (all players including deactivated)
    val allPlayersIncludingInactive: Flow<List<Player>> = 
        playerRepository.getAllPlayersIncludingInactive()
    
    // Existing method (now soft deletes via repository change)
    fun deletePlayer(player: Player) {
        viewModelScope.launch {
            playerRepository.deletePlayer(player)
        }
    }
    
    // NEW method - Reactivate a player
    fun reactivatePlayer(player: Player) {
        viewModelScope.launch {
            playerRepository.reactivatePlayer(player)
        }
    }
    
    // NEW method - Get count of games for a player
    fun getGameCountForPlayer(playerId: String): Int {
        return runBlocking {
            gameQueryHelper.getGameCountForPlayer(playerId)
        }
    }
}
```

**Integration**:
- `allPlayers` - Used by Player Selector (only active)
- `allPlayersIncludingInactive` - Used by Management Screen (all players)
- `deletePlayer()` - Now triggers soft delete via repository
- `reactivatePlayer()` - New method to reactivate deactivated players
- `getGameCountForPlayer()` - Used to populate deactivation warning dialog

---

## Phase 7: Toast Notifications

### 7.1 Toast Implementation

**Options**:
1. Use existing Toast system if already in codebase
2. Use Material3 `Snackbar` with `ScaffoldState`
3. Create simple Toast helper composable

**Usage Locations**:

In `PlayerSelectionScreen.kt`:
```kotlin
// When player is deactivated
showToast("Player '${player.name}' deactivated")

// When player is reactivated via swipe
showToast("Player '${player.name}' has been reactivated")
```

In `PlayerSelectorContent.kt`:
```kotlin
// When player is reactivated by name during game creation
showToast("Player '${deactivatedPlayer.name}' has been reactivated")
```

---

## Implementation Checklist

### Database Layer
- [ ] Update PlayerEntity.kt - Add `isActive` and `deactivatedAt` fields
- [ ] Update PlayerDao.kt - Add 6 new query methods
- [ ] Update TarotDao.kt - Add `countGamesWithPlayer()` query
- [ ] Update YahtzeeDao.kt - Add `countGamesWithPlayer()` query
- [ ] Verify Room auto-migration works

### Domain Layer
- [ ] Update Player.kt - Add new properties and update extension functions
- [ ] Update PlayerRepository.kt interface - Add new method signatures
- [ ] Update PlayerRepositoryImpl.kt - Implement new methods
- [ ] Verify no compilation errors

### Data Layer
- [ ] Create GameQueryHelper.kt (NEW FILE)
- [ ] Register GameQueryHelper in PlatformRepositories

### UI Layer - Player Selector
- [ ] Update PlayerSelectorContent.kt - Filter to active only
- [ ] Implement reactivation-by-name logic
- [ ] Test in game creation screens

### UI Layer - Management Screen
- [ ] Update PlayerSelectionScreen.kt - Separate sections
- [ ] Create PlayerCard composable with dual appearance
- [ ] Update deactivation confirmation dialog
- [ ] Implement toast notifications

### ViewModel
- [ ] Update PlayerSelectionViewModel.kt - Add new properties and methods
- [ ] Integrate with GameQueryHelper
- [ ] Test state flows

### Testing
- [ ] Build and verify compilation
- [ ] Test deactivation of player with games (should show count)
- [ ] Test deactivation of player without games
- [ ] Test reactivation by name in game creation
- [ ] Test reactivation via management screen
- [ ] Verify deactivated players don't appear in selectors
- [ ] Verify deactivated players appear in management screen
- [ ] Test toast notifications
- [ ] Test dark theme compatibility

---

## File Summary

### New Files (1)
| File | Lines | Purpose |
|------|-------|---------|
| GameQueryHelper.kt | 30 | Query game-player links |

### Modified Files (10)
| File | Path | Changes |
|------|------|---------|
| PlayerEntity.kt | `core/data/local/database/` | +2 properties |
| PlayerDao.kt | `core/data/local/database/` | +6 queries |
| TarotDao.kt | `core/data/local/database/` | +1 query |
| YahtzeeDao.kt | `core/data/local/database/` | +1 query |
| Player.kt | `core/model/` | +2 properties, update converters |
| PlayerRepository.kt | `core/domain/repository/` | +3 methods |
| PlayerRepositoryImpl.kt | `core/data/local/repository/` | +4 implementations |
| PlayerSelectorContent.kt | `ui/components/` | +25 lines |
| PlayerSelectionScreen.kt | `ui/screens/player/` | +80 lines |
| PlayerSelectionViewModel.kt | `ui/screens/player/` | +20 lines |

**Total**: ~176 lines of new code, 11 files modified

---

## Implementation Sequence

### Step 1: Foundation (Database & Domain)
1. Update PlayerEntity.kt
2. Update PlayerDao.kt
3. Update TarotDao.kt & YahtzeeDao.kt
4. Update Player.kt
5. Update PlayerRepository.kt & PlayerRepositoryImpl.kt
6. Create GameQueryHelper.kt
7. **Build & verify** - database/domain layer complete

### Step 2: Data Integration
8. Register GameQueryHelper in PlatformRepositories
9. Update PlayerSelectorContent.kt
10. **Build & verify** - Player selector filtering works

### Step 3: UI Implementation
11. Update PlayerSelectionScreen.kt
12. Update PlayerSelectionViewModel.kt
13. Implement toast notifications
14. **Build & verify** - full integration

### Step 4: Testing & Polish
15. Test all user flows
16. Test edge cases
17. Verify dark theme
18. Final build successful

---

## Key Implementation Notes

### Auto-Migration
- Room automatically migrates when fields have default values
- `isActive: Boolean = true` defaults all existing players to active
- `deactivatedAt: Long? = null` defaults all existing players to null
- No explicit migration script needed
- No data loss for existing players

### Query Performance
- `playerIds LIKE '%' || ? || '%'` search is not indexed but acceptable for small datasets
- Future enhancement: Add `player_games` junction table for better performance
- Current solution is simple and sufficient for typical game counts

### Swipe Gestures
- Active player: Swipe right-to-left (EndToStart) = Deactivate
- Deactivated player: Swipe left-to-right (StartToEnd) = Reactivate
- Opposite directions prevent accidental actions
- Use existing `SwipeToDismissBox` component

### Styling Details
- Deactivated players: `alpha = 0.5f` for entire card
- Deactivated players: `TextDecoration.LineThrough` on name
- Deactivated players: No badge (per user preference)
- Section separation with `Spacer(height = 24.dp)`

### Toast Implementation
- Show on deactivation: "Player deactivated"
- Show on reactivation by name: "Player '[name]' has been reactivated"
- Show on reactivation by swipe: "Player '[name]' has been reactivated"

---

## Scope & Limitations

### In Scope
- ✅ Tarot games
- ✅ Yahtzee games
- ✅ Player management screen
- ✅ Game creation screens
- ✅ Soft delete (mark as inactive)
- ✅ Reactivation by name
- ✅ Game link detection

### Out of Scope
- ❌ Counter game (not linked to player management)
- ❌ FingerSelector game (not linked to player management)
- ❌ Hard delete (admin cleanup)
- ❌ Bulk operations
- ❌ Export/backup before deletion
- ❌ Player merge functionality

---

## Future Enhancements

These features can be added in future iterations:
- Hard delete option for admin users
- Archive cleanup tool (delete deactivated players after X days)
- Statistics for deactivated players
- Bulk reactivation
- Undo deactivation feature (undo stack)
- Export/backup before deletion
- Player merge (combine two players)
- Animated transitions when toggling sections

---

## Questions Resolved

✅ **Visual treatment**: Alpha 0.5 + strikethrough, no badge  
✅ **Reactivation control**: Swipe left (opposite direction from delete)  
✅ **Notifications**: Toast on both deactivate and reactivate  
✅ **Game warning**: Show "Linked to X games"  
✅ **Scope**: Tarot & Yahtzee only  
✅ **Migration**: Room auto-migration  

---

## Status & Next Steps

**Current Status**: Planning complete, ready for implementation

**Next Steps**:
1. Create git branch `feature/soft-delete-players`
2. Implement Phase 1: Database schema
3. Implement Phase 2: Domain layer
4. Implement Phase 3: Data layer
5. Implement Phase 4: UI layer (selector)
6. Implement Phase 5: UI layer (management)
7. Implement Phase 6: ViewModel
8. Implement Phase 7: Notifications
9. Test all flows
10. Merge to main

**Estimated Time**: 2-3 hours with testing

---

## References

- Room Database: https://developer.android.com/training/data-storage/room
- Compose Material3: https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary
- SwipeToDismissBox: https://developer.android.com/reference/kotlin/androidx/compose/material3/SwipeToDismissBoxKt

---

**Document Version**: 1.0  
**Last Updated**: January 10, 2025  
**Author**: Claude Code Assistant
