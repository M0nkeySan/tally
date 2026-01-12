# Plan: Remove Counter History Database Persistence

**Date:** January 12, 2025  
**Project:** GameKeeper  
**Scope:** Convert counter history from persistent database to in-memory session storage + Add UI notice

---

## Executive Summary

Convert counter history from persistent SQLite storage (Room) to in-memory session-based storage. History entries will:
- ✅ Persist for the duration of the app session
- ❌ Be lost when the app closes (no persistence between sessions)

Additionally, add a notice below the title in the Counter History screen explaining this behavior.

---

## Current State Analysis

### Existing Architecture
- **Database:** Room (SQLite), version 15
- **History Entity:** `CounterChangeEntity` stored in `counter_changes` table
- **History DAO:** `CounterChangeDao` with reactive Flow-based queries
- **Repository:** `CounterRepositoryImpl` manages change logging and history retrieval
- **ViewModel:** `CounterViewModel` displays merged history in UI
- **UI:** `CounterHistoryScreen` shows merged counter changes with timestamps

### Current Data Flow
```
CounterHistoryScreen (UI)
    ↓ collectAsState()
CounterViewModel.mergedHistory (StateFlow)
    ↓ getMergedCounterHistory()
CounterRepository (Interface)
    ↓ CounterRepositoryImpl
CounterChangeDao (Room DAO)
    ↓ @Query
counter_changes table (SQLite)
```

---

## Objectives

1. **Remove Database Persistence**
   - Delete `CounterChangeEntity` from Room
   - Delete `CounterChangeDao` interface
   - Bump database version (15 → 16) for schema migration
   - Replace with in-memory `CounterHistoryStore`

2. **Maintain Functionality**
   - Keep history during app session
   - Merge consecutive changes (existing algorithm)
   - Support clearing history manually
   - Preserve domain models and business logic

3. **Add UI Notice**
   - Display disclaimer below "Counter History" title
   - Explain history is cleared when app closes
   - Use appropriate styling and icons

---

## Implementation Plan

### Phase 1: Database Layer Removal

#### 1.1 Delete Persistent Storage Files
**Files to delete:**
- `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/core/data/local/database/CounterChangeEntity.kt`
- `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/core/data/local/database/CounterChangeDao.kt`

**Reason:** These define the Room entity and DAO for counter history persistence. No longer needed with in-memory store.

#### 1.2 Update Database Configuration
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/core/data/local/database/GameDatabase.kt`

**Changes:**
- Remove `CounterChangeEntity::class` from `@Database(entities = [...])`
- Increment version: `version = 15` → `version = 16`
- Keep all other tables and DAOs intact

**Result:** Counter history table will be dropped from SQLite on next app launch (destructive migration already enabled via `fallbackToDestructiveMigration(true)`).

---

### Phase 2: Create In-Memory History Store

#### 2.1 New File: CounterHistoryStore.kt
**Location:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/core/domain/`

**Implementation:**
```kotlin
class CounterHistoryStore {
    private val _history = MutableStateFlow<List<CounterChange>>(emptyList())
    val history: StateFlow<List<CounterChange>> = _history.asStateFlow()
    
    suspend fun addChange(change: CounterChange) {
        _history.value = _history.value + change
    }
    
    suspend fun deleteAllChanges() {
        _history.value = emptyList()
    }
    
    fun getHistory(): List<CounterChange> = _history.value
    
    fun getMergedHistory(): List<MergedCounterChange> {
        return mergeConsecutiveChanges(getHistory())
    }
    
    private fun mergeConsecutiveChanges(changes: List<CounterChange>): List<MergedCounterChange> {
        // Keep existing merge algorithm from CounterRepositoryImpl
        // ...existing implementation...
    }
}
```

**Key Features:**
- Scope: Application singleton (lifetime = app session)
- Thread-safe: Uses `MutableStateFlow`
- Reactive: Returns `StateFlow<List<CounterChange>>`
- Merging: Includes existing merge algorithm

---

### Phase 3: Update Repository Layer

#### 3.1 CounterRepository Interface
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/core/domain/repository/CounterRepository.kt`

**Changes:**
- Update documentation for history methods to note in-memory only
- No method signatures change (interface stays the same)

**Affected Methods:**
- `logCounterChange()` - Document: stores in memory only
- `logCounterDeletion()` - Document: stores in memory only
- `getCounterHistory()` - Document: returns session history
- `getMergedCounterHistory()` - Document: returns session history
- `clearCounterHistory()` - Unchanged

#### 3.2 CounterRepositoryImpl Implementation
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/core/data/local/repository/CounterRepositoryImpl.kt`

**Changes:**

1. **Update Constructor:**
   ```kotlin
   class CounterRepositoryImpl(
       private val dao: PersistentCounterDao,
       private val historyStore: CounterHistoryStore  // ← NEW
       // remove: private val changeDao: CounterChangeDao
   ) : CounterRepository
   ```

2. **Update logCounterChange():**
   ```kotlin
   override suspend fun logCounterChange(
       counterId: String,
       counterName: String,
       counterColor: Long,
       previousValue: Int,
       newValue: Int
   ) {
       val change = CounterChange(
           id = UUID.randomUUID().toString(),
           counterId = counterId,
           counterName = counterName,
           counterColor = counterColor,
           previousValue = previousValue,
           newValue = newValue,
           changeDelta = newValue - previousValue,
           isDeleted = false,
           timestamp = System.currentTimeMillis(),
           createdAt = System.currentTimeMillis()
       )
       historyStore.addChange(change)  // ← Changed from changeDao.insertChange()
   }
   ```

3. **Update logCounterDeletion():**
   ```kotlin
   override suspend fun logCounterDeletion(
       counterId: String,
       counterName: String,
       counterColor: Long
   ) {
       val change = CounterChange(
           id = UUID.randomUUID().toString(),
           counterId = counterId,
           counterName = counterName,
           counterColor = counterColor,
           previousValue = 0,
           newValue = 0,
           changeDelta = 0,
           isDeleted = true,
           timestamp = System.currentTimeMillis(),
           createdAt = System.currentTimeMillis()
       )
       historyStore.addChange(change)  // ← Changed from changeDao.insertChange()
   }
   ```

4. **Update getCounterHistory():**
   ```kotlin
   override fun getCounterHistory(): Flow<List<CounterChange>> {
       return historyStore.history  // ← Changed from changeDao.getAllChanges()
   }
   ```

5. **Update getMergedCounterHistory():**
   ```kotlin
   override fun getMergedCounterHistory(): Flow<List<MergedCounterChange>> {
       return historyStore.history.map { changes ->
           mergeConsecutiveChanges(changes)
       }
   }
   ```

6. **Update clearCounterHistory():**
   ```kotlin
   override suspend fun clearCounterHistory() {
       historyStore.deleteAllChanges()  // ← Changed from changeDao.deleteAllChanges()
   }
   ```

7. **Remove:**
   - DAO mapper functions (no longer needed)
   - Any `changeDao` references

---

### Phase 4: Update Dependency Injection

#### 4.1 Platform Repositories (Android)
**File:** `composeApp/src/androidMain/kotlin/io/github/m0nkeysan/gamekeeper/platform/PlatformRepositories.android.kt`

**Changes:**

1. **Add history store singleton:**
   ```kotlin
   private var historyStore: CounterHistoryStore? = null
   
   private fun getHistoryStore(): CounterHistoryStore {
       return historyStore ?: CounterHistoryStore().also {
           historyStore = it
       }
   }
   ```

2. **Update getCounterRepository():**
   ```kotlin
   actual fun getCounterRepository(): CounterRepository {
       return counterRepository ?: CounterRepositoryImpl(
           getDatabase().persistentCounterDao(),
           getHistoryStore()  // ← NEW dependency
       ).also {
           counterRepository = it
       }
   }
   ```

#### 4.2 Platform Repositories (Common)
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/platform/PlatformRepositories.kt`

**Changes:**
- Update documentation if needed
- May need to add history store initialization if shared initialization exists

---

### Phase 5: Update Database Configuration

#### 5.1 GameDatabase.kt
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/core/data/local/database/GameDatabase.kt`

**Changes:**
```kotlin
@Database(
    entities = [
        PlayerEntity::class,
        UserPreferencesEntity::class,
        PersistentCounterEntity::class,
        // CounterChangeEntity::class,  ← REMOVE THIS LINE
        TarotGameEntity::class,
        TarotRoundEntity::class,
        YahtzeeGameEntity::class,
        YahtzeeScoreEntity::class
    ],
    version = 16  // ← CHANGE FROM 15
)
abstract class GameDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun persistentCounterDao(): PersistentCounterDao
    // abstract fun counterChangeDao(): CounterChangeDao  ← REMOVE THIS LINE
    abstract fun tarotDao(): TarotDao
    abstract fun yahtzeeDao(): YahtzeeDao
}
```

---

### Phase 6: Clean Up Imports

**Action:** Search and remove imports of deleted files throughout codebase

**Commands to search:**
- `import.*CounterChangeEntity`
- `import.*CounterChangeDao`

**Files likely to have these imports:**
- `CounterRepositoryImpl.kt`
- `PlatformRepositories.android.kt`
- Any test files

---

### Phase 7: UI Enhancement - Add Notice

#### 7.1 CounterHistoryScreen
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/counter/CounterHistoryScreen.kt`

**Changes:**

1. **Add notice composable after TopAppBar, before content:**
   ```kotlin
   Surface(
       modifier = Modifier
           .fillMaxWidth()
           .padding(horizontal = 16.dp, vertical = 8.dp),
       color = Color.Transparent
   ) {
       Row(
           verticalAlignment = Alignment.CenterVertically,
           modifier = Modifier.padding(8.dp)
       ) {
           Icon(
               imageVector = Icons.Default.Info,
               contentDescription = null,
               modifier = Modifier.size(16.dp),
               tint = MaterialTheme.colorScheme.onSurfaceVariant
           )
           Spacer(modifier = Modifier.width(8.dp))
           Text(
               "History is cleared when you close the app",
               style = MaterialTheme.typography.bodySmall,
               color = MaterialTheme.colorScheme.onSurfaceVariant,
               textAlign = TextAlign.Start
           )
       }
   }
   ```

2. **Layout structure:**
   ```kotlin
   Scaffold(
       topBar = { TopAppBar(...) }
   ) { paddingValues ->
       Column(
           modifier = Modifier
               .fillMaxSize()
               .padding(paddingValues)
       ) {
           // ← INSERT NOTICE HERE
           
           if (mergedHistory.isEmpty()) {
               // Empty state
           } else {
               // History list
           }
       }
   }
   ```

3. **Optional: Extract as separate composable:**
   ```kotlin
   @Composable
   private fun CounterHistoryNotice() {
       Surface(
           modifier = Modifier
               .fillMaxWidth()
               .padding(horizontal = 16.dp, vertical = 8.dp),
           color = Color.Transparent
       ) {
           Row(
               verticalAlignment = Alignment.CenterVertically,
               modifier = Modifier.padding(8.dp)
           ) {
               Icon(...)
               Spacer(...)
               Text(...)
           }
       }
   }
   ```

**Styling Notes:**
- Works in both light and dark modes (uses theme colors)
- Info icon from `Icons.Default.Info` or can use custom emoji
- Subtle appearance (uses `onSurfaceVariant` color)
- Padding prevents edge alignment

---

## File Changes Summary

### Files to Delete (2)
1. `CounterChangeEntity.kt` - Room entity definition
2. `CounterChangeDao.kt` - Room DAO interface

### Files to Create (1)
1. `CounterHistoryStore.kt` - In-memory history manager

### Files to Modify (6)
1. `GameDatabase.kt` - Remove entity, bump version 15→16
2. `CounterRepository.kt` - Update documentation
3. `CounterRepositoryImpl.kt` - Replace DAO with store
4. `PlatformRepositories.android.kt` - Inject store
5. `PlatformRepositories.kt` - If needed, inject store
6. `CounterHistoryScreen.kt` - Add notice UI

### Imports to Clean (Throughout codebase)
- Remove `CounterChangeEntity` imports
- Remove `CounterChangeDao` imports

---

## Testing Checklist

### Compilation
- [ ] Project compiles without errors
- [ ] No unresolved references
- [ ] All imports cleaned up

### Functional Testing (During Session)
- [ ] Create counter and make changes
- [ ] Open Counter History screen
- [ ] Verify history entries appear
- [ ] Verify entries are merged correctly (consecutive changes grouped)
- [ ] Verify notice is visible and readable

### Persistence Testing
- [ ] Close app completely
- [ ] Reopen app
- [ ] Open Counter History screen
- [ ] **Verify history is EMPTY** (persistence removed) ✅
- [ ] Create new changes
- [ ] Verify new changes work normally

### Manual Operations
- [ ] Delete all histories while app open
- [ ] Verify dialog confirmation works
- [ ] Verify history clears immediately
- [ ] Verify can continue adding history

### UI Testing
- [ ] Notice displays in light mode
- [ ] Notice displays in dark mode
- [ ] Notice text is readable (no overlap, proper size)
- [ ] Notice icon displays correctly
- [ ] Notice doesn't interfere with history list

### Edge Cases
- [ ] Empty history state (should show "No counter changes yet" + notice)
- [ ] Large number of history entries (scroll performance)
- [ ] Create and delete counter (history entry remains with denormalized data)
- [ ] Multiple rapid changes (merging works correctly)

---

## Database Migration Notes

### Version Upgrade: 15 → 16
- **Migration Type:** Destructive (uses `fallbackToDestructiveMigration(true)`)
- **Impact:** `counter_changes` table will be dropped
- **Data Loss:** Counter history will be cleared on first app launch after update
- **User Impact:** Low - only in-session history is lost, counters themselves are preserved

### Destructive Migration Already Enabled
No additional migration files needed because:
- Project already uses `fallbackToDestructiveMigration(true)` in `PlatformRepositories.android.kt`
- Database is not heavily relied upon (user accepts data reset on schema changes)
- Counter data (not history) persists via `PersistentCounterEntity`

---

## Architecture Benefits

### Before (Persistent Storage)
```
❌ History persists indefinitely
❌ SQLite table growth over time
❌ Database complexity
❌ Migration management needed
```

### After (In-Memory)
```
✅ Clean session-based history
✅ Automatic cleanup (app close)
✅ Simpler architecture (no DAO)
✅ Less database complexity
✅ Better memory efficiency
✅ User understands temporary nature
✅ Clear UI notice explains behavior
```

---

## Risk Assessment

### Low Risk Changes
- ✅ In-memory StateFlow (proven pattern in codebase)
- ✅ No changes to UI layer (transparent to UI)
- ✅ No changes to ViewModel (works with same Flow interface)
- ✅ Destructive migration already enabled

### Testing Coverage
- ✅ Simple flow-based storage
- ✅ Merge algorithm unchanged (proven to work)
- ✅ Easy to test manually

---

## Implementation Notes

### MutableStateFlow Thread Safety
```kotlin
// Thread-safe operations
_history.value = _history.value + change  // Atomic update
_history.value = emptyList()              // Atomic reset
```

### Merge Algorithm Migration
- Existing `mergeConsecutiveChanges()` function moves from `CounterRepositoryImpl` to `CounterHistoryStore`
- No logic changes required
- Behavior identical

### CounterViewModel Compatibility
- No changes needed
- Still receives `StateFlow<List<CounterChange>>` from `getCounterHistory()`
- Still receives `StateFlow<List<MergedCounterChange>>` from `getMergedCounterHistory()`
- `collectAsState()` works identically

---

## Branch Strategy

**Branch Name:** `feature/session-only-counter-history`

**Workflow:**
1. Create branch from `main`
2. Implement all changes (Phases 1-7)
3. Compile and test
4. Create comprehensive commit messages
5. Push to remote
6. Create PR with plan reference
7. Merge to `main`

---

## Commit Message Strategy

```
feat: convert counter history to session-only in-memory storage

- Remove CounterChangeEntity and CounterChangeDao (no longer needed)
- Create CounterHistoryStore for in-memory session-based history
- Update CounterRepositoryImpl to use in-memory store instead of database
- Bump database version 15 → 16 (destructive migration)
- Update PlatformRepositories to inject history store
- Add notice in CounterHistoryScreen explaining session-only behavior

This change removes persistence of counter history from the database.
History now lives only for the duration of the app session and is
automatically cleared when the app closes.
```

---

**Plan Status:** ✅ Ready for Implementation
