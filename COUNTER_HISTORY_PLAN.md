# Counter History Implementation Plan

## Overview
Implement a counter change history screen that displays all point additions/subtractions to counters with consecutive changes from the same counter merged into single lines. Changes are logged immediately to the database and displayed in real-time. History is auto-cleared when "Reinitialize All" or "Delete All" is pressed.

## Design Decisions
1. **Change logging timing**: Immediate persistence to database
2. **Merging display**: Only show total delta (±X), keep it simple
3. **History persistence**: Auto-clear when Reinitialize All or Delete All is pressed
4. **Navigation**: History button already in counter screen top bar

---

## Phase 1: Database Layer Design

### CounterChangeEntity Schema
```kotlin
@Entity(tableName = "counter_changes")
data class CounterChangeEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    val counterId: String,           // FK to Counter
    val counterName: String,         // Denormalized for display (in case counter deleted)
    val counterColor: Int,           // ARGB format, denormalized
    
    val previousValue: Int,          // State before change
    val newValue: Int,               // State after change
    val changeDelta: Int,            // newValue - previousValue
    
    val timestamp: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
```

**Why denormalize?**
- Counter names/colors can change → keep historical accuracy
- Deleted counters still show in history
- Avoids JOIN queries and improves performance

### CounterChangeDao Methods
```kotlin
@Dao
interface CounterChangeDao {
    @Query("SELECT * FROM counter_changes ORDER BY timestamp DESC")
    fun getAllChanges(): Flow<List<CounterChangeEntity>>
    
    @Query("SELECT * FROM counter_changes WHERE counterId = ? ORDER BY timestamp DESC")
    fun getChangesForCounter(counterId: String): Flow<List<CounterChangeEntity>>
    
    @Insert
    suspend fun insertChange(change: CounterChangeEntity)
    
    @Delete
    suspend fun deleteChange(change: CounterChangeEntity)
    
    @Query("DELETE FROM counter_changes")
    suspend fun deleteAllChanges()
    
    @Query("DELETE FROM counter_changes WHERE counterId = ?")
    suspend fun deleteChangesForCounter(counterId: String)
}
```

---

## Phase 2: Domain Layer

### CounterChange Model
```kotlin
data class CounterChange(
    val id: String,
    val counterId: String,
    val counterName: String,
    val counterColor: Int,
    val previousValue: Int,
    val newValue: Int,
    val changeDelta: Int,
    val timestamp: Long,
    val createdAt: Long
)
```

### MergedCounterChange Model (for display)
```kotlin
data class MergedCounterChange(
    val counterId: String,
    val counterName: String,
    val counterColor: Int,
    val totalDelta: Int,              // Sum of all consecutive deltas
    val count: Int,                   // How many changes were merged
    val firstTimestamp: Long,         // Earliest change in group
    val lastTimestamp: Long,          // Latest change in group
    val changes: List<CounterChange>  // Original changes for reference
)
```

**Merging Logic:**
- Group consecutive changes by `counterId`
- Sum all `changeDelta` values to get `totalDelta`
- Keep timestamps for range display
- Display only `totalDelta` in UI (simple as requested)

---

## Phase 3: Repository Layer

### Extend CounterRepository Interface
```kotlin
interface CounterRepository {
    // Existing methods...
    
    // New methods for history tracking
    suspend fun logCounterChange(
        counterId: String,
        counterName: String,
        counterColor: Int,
        previousValue: Int,
        newValue: Int
    )
    
    fun getCounterHistory(): Flow<List<CounterChange>>
    
    suspend fun clearCounterHistory()
}
```

### Implementation Details in CounterRepositoryImpl
1. **logCounterChange()**: Insert `CounterChangeEntity` immediately after any value update
2. **getCounterHistory()**: Read all changes from DAO, ordered newest-first
3. **clearCounterHistory()**: Delete all changes when "Reinitialize All" or "Delete All" pressed
4. **mergeConsecutiveChanges()**: Algorithm to group by counter and sum deltas

---

## Phase 4: ViewModel Changes

### CounterViewModel Modifications

**For each value-changing method:**
```kotlin
suspend fun incrementCount(id: String) {
    val counter = currentCounters.value.find { it.id == id } ?: return
    val newCount = counter.count + 1
    
    // Log BEFORE updating
    counterRepository.logCounterChange(
        counterId = id,
        counterName = counter.name,
        counterColor = counter.color,
        previousValue = counter.count,
        newValue = newCount
    )
    
    // Then update
    counterRepository.updateCount(id, newCount)
}
```

**Apply to:**
- `incrementCount(id)` → log +1
- `decrementCount(id)` → log -1
- `adjustCount(id, amount)` → log ±amount
- `setCount(id, newCount)` → log delta (newCount - currentCount)
- `updateCounter()` → log if count changed

**Clear history on:**
- `resetAll()` → call `counterRepository.clearCounterHistory()`
- `deleteAll()` → call `counterRepository.clearCounterHistory()`

---

## Phase 5: UI Layer

### CounterHistoryScreen (NEW)
Display all merged counter changes in reverse chronological order with:
- Counter color indicator
- Counter name
- Total delta (±X)
- Count of merged changes (if > 1)
- Swipe-back navigation support

### CounterScreen Updates
- Wire history button onClick to navigate to history screen
- Add navigation route handling

### Navigation Updates
- Define `Screen.History` route (if not already)
- Add NavGraph composition for history screen

---

## Phase 6: Special Considerations

### 1. Timestamp Management
- Use `System.currentTimeMillis()` for all timestamps
- Display in format: "HH:mm:ss"

### 2. Update Counter Detection
In `updateCounter()` method, only log if count changed

### 3. Database Migration
- Room auto-migration handles new table creation
- No manual migration script needed

### 4. Performance
- Changes persist immediately (no buffering)
- Use `Flow` for reactive updates
- Consider pagination if history gets very large (future enhancement)

---

## Execution Order

1. Create `CounterChangeEntity.kt`
2. Create `CounterChangeDao.kt`
3. Register new entity and DAO in Room Database
4. Create `CounterChange.kt` domain model
5. Create `MergedCounterChange.kt` domain model
6. Extend `CounterRepository` interface with new methods
7. Implement in `CounterRepositoryImpl`
8. Update `CounterViewModel` to log changes
9. Create `CounterHistoryScreen.kt`
10. Update `CounterScreen.kt` history button navigation
11. Update `NavGraph.kt` with History route
12. Build and test end-to-end
13. Commit with descriptive message

---

## Key Files to Modify/Create

| File | Action | Purpose |
|------|--------|---------|
| `CounterChangeEntity.kt` | CREATE | Database entity for tracking changes |
| `CounterChangeDao.kt` | CREATE | DAO for counter changes |
| `GameKeeperDatabase.kt` | MODIFY | Register new entity and DAO |
| `CounterChange.kt` | CREATE | Domain model |
| `MergedCounterChange.kt` | CREATE | Domain model for display |
| `CounterRepository.kt` | MODIFY | Add new interface methods |
| `CounterRepositoryImpl.kt` | MODIFY | Implement history tracking |
| `CounterViewModel.kt` | MODIFY | Log changes and clear history |
| `CounterHistoryScreen.kt` | CREATE | New screen for history display |
| `CounterScreen.kt` | MODIFY | Wire history button navigation |
| `NavGraph.kt` | MODIFY | Add history route |

---

## Integration Points

### Data Flow
```
User Action (UI Click)
    ↓
ViewModel Method (incrementCount, setCount, etc.)
    ↓
Log change to repository
    ↓
Repository Method (updateCount, etc.)
    ↓
DAO Method (updateCount, insertChange, etc.)
    ↓
SQLite Database (UPDATE/INSERT)
    ↓
DAO Flow emits updated list
    ↓
ViewModel updates StateFlow
    ↓
CounterScreen/CounterHistoryScreen observes and recomposes
```

### Change Interception Points
- `incrementCount()` → log change before update
- `decrementCount()` → log change before update
- `adjustCount()` → log change before update
- `setCount()` → log change before update
- `updateCounter()` → log change if count changed
- `resetAll()` → log changes then clear history
- `deleteAll()` → clear history
- `deleteCounter()` → clear history for that counter (optional)

---

## Testing Strategy

1. **Unit Tests**: Test merging logic with various change sequences
2. **Integration Tests**: Test logging during counter updates
3. **UI Tests**: Test history screen rendering
4. **E2E Tests**: 
   - Create counter → increment → check history
   - Multiple increments → verify merging
   - Reset all → verify history cleared
   - Delete all → verify history cleared

---

## Future Enhancements (Out of Scope)

- Pagination for large histories
- Filter by counter
- Export history as CSV
- Delete individual history entries
- Search/sort options
- Undo/redo functionality
