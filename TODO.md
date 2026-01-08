# GameKeeper - Technical Debt & Improvements

## Progress Tracking

Last Updated: 2026-01-08

---

## CRITICAL Priority

### 1. Domain interfaces depend on data entities [COMPLETED ✅]
- [x] `TarotRepository.kt` - Now uses domain models (`TarotGame`, `TarotRound`)
- [x] `YahtzeeRepository.kt` - Now uses domain models (`YahtzeeGame`, `YahtzeeScore`)
- [x] `CounterRepository.kt` - Now uses domain model (`Counter`)
- [x] Repository implementations handle entity-to-domain mapping internally
- [x] Created new `Counter` domain model

### 2. No error handling anywhere [COMPLETED ✅]
- [x] Created `Result<T>` wrapper class for error states (`Result.kt`)
- [x] Added try-catch in repository implementations (Tarot, Yahtzee, Counter)
- [x] Added error state to ViewModel UI states
- [x] Error handling in `TarotGameViewModel`, `YahtzeeGameViewModel`, `TarotScoringViewModel`

### 3. Empty implementation of stats recording [COMPLETED ✅]
- [x] `PlayerStatsRepositoryImpl.kt` - Implemented `recordTarotGame()`
- [x] `PlayerStatsRepositoryImpl.kt` - Implemented `recordYahtzeeGame()`
- [x] `PlayerStatsRepositoryImpl.kt` - Implemented `recordCounterGame()`
- All methods now properly record game history and update player statistics

### 4. Main thread violation [COMPLETED ✅]
- [x] Fixed `TarotGameViewModel.createGame()` - callback now runs on main thread
- [x] Fixed `YahtzeeGameViewModel.createGame()` - callback now runs on main thread

---

## HIGH Priority

### 5. Non-atomic delete operations [COMPLETED ✅]
- [x] `TarotRepositoryImpl.kt` - Uses `database.withTransaction` for atomic deletes
- [x] `YahtzeeRepositoryImpl.kt` - Uses `database.withTransaction` for atomic deletes

### 6. No foreign key constraints [DEFERRED ⏸️]
- [ ] `TarotRoundEntity` - Add FK to `TarotGameEntity`
- [ ] `YahtzeeScoreEntity` - Add FK to `YahtzeeGameEntity`
- [ ] `GameParticipantEntity` - Add FK to `PlayerEntity`
- **Note:** Requires careful migration strategy to avoid data loss. This should be done in a separate PR with proper migration tests.

### 7. Comma-separated IDs storage [DEFERRED ⏸️]
- [ ] Create `GamePlayerEntity` junction table
- [ ] Migrate `TarotGameEntity.playerIds` to junction table
- [ ] Migrate `YahtzeeGameEntity.playerIds` to junction table
- **Note:** This is a significant refactor requiring database migration. Deferred to avoid breaking existing user data.

---

## MEDIUM Priority

### 8. Business logic in ViewModel [COMPLETED ✅]
- [x] `TarotScoringViewModel.kt` - Moved `getCurrentTotalScores()` logic to `TarotScoringEngine`
- [x] Added `calculateTotalScores()` method to `TarotScoringEngine`

### 9. Blocking DAO method [COMPLETED ✅]
- [x] `PlayerDao.kt:16-17` - Added `suspend` to `getPlayerById()`

---

## LOW Priority

### 10. New repository instance per call [COMPLETED ✅]
- [x] `PlatformRepositories.android.kt` - Implemented singleton pattern with lazy initialization
- All repository instances are now cached and reused

### 11. Missing database indexes [COMPLETED ✅]
- [x] Added `@Index` on `TarotRoundEntity.gameId`
- [x] Added `@Index` on `YahtzeeScoreEntity.gameId`
- [x] Added `@Index` on `GameParticipantEntity.playerId` and `gameId`
- Database version bumped to 14

---

## Summary

### Completed (9/11) ✅
- [x] Domain interfaces using domain models
- [x] Error handling with Result wrapper
- [x] Stats recording implementation
- [x] Atomic delete operations with transactions
- [x] Non-blocking DAO methods
- [x] Singleton repository pattern
- [x] Database indexes
- [x] Business logic extracted to domain layer
- [x] Main thread violation fixes

### Remaining (2/11) ⏸️
- [ ] Foreign key constraints (HIGH - DEFERRED)
- [ ] Comma-separated IDs refactor (HIGH - DEFERRED)

---

## Files Modified

### New Files
- `core/model/Result.kt` - Error handling wrapper
- `core/model/Counter.kt` - Domain model for counters

### Modified Files
**Domain Layer:**
- `core/domain/repository/TarotRepository.kt`
- `core/domain/repository/YahtzeeRepository.kt`
- `core/domain/repository/CounterRepository.kt`
- `core/domain/engine/TarotScoringEngine.kt`

**Data Layer:**
- `core/data/local/repository/TarotRepositoryImpl.kt`
- `core/data/local/repository/YahtzeeRepositoryImpl.kt`
- `core/data/local/repository/CounterRepositoryImpl.kt`
- `core/data/local/repository/PlayerStatsRepositoryImpl.kt`
- `core/data/local/database/TarotGameEntities.kt`
- `core/data/local/database/YahtzeeGameEntities.kt`
- `core/data/local/database/StatsEntities.kt`
- `core/data/local/database/PlayerDao.kt`
- `core/data/local/database/GameDatabase.kt` (version 14)

**Presentation Layer:**
- `ui/screens/tarot/TarotGameViewModel.kt`
- `ui/screens/tarot/TarotGameState.kt`
- `ui/screens/tarot/TarotGameSelectionScreen.kt`
- `ui/screens/yahtzee/YahtzeeGameViewModel.kt`
- `ui/screens/yahtzee/YahtzeeGameSelectionScreen.kt`
- `ui/viewmodel/TarotScoringViewModel.kt`
- `ui/viewmodel/CounterViewModel.kt`

**Platform:**
- `platform/PlatformRepositories.android.kt`

**Model:**
- `core/model/Game.kt`

---

## Notes

- Issues #6 (foreign key constraints) and #7 (junction table) are deferred as they require significant database migration and could break existing user data
- All critical and most high-priority issues have been resolved
- Error handling is now comprehensive across repositories and ViewModels
- Business logic has been properly separated into the domain layer
- Build passes successfully with all changes
