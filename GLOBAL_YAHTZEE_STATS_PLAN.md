# 📋 Global Yahtzee Statistics - Implementation Plan

## ✅ Plan Confirmation

Based on requirements:
- ✅ Implement **all** proposed statistics
- ✅ Show **top 3** in leaderboards
- ✅ Only **finished games** included
- ✅ Screen layout: **Overview + Leaderboard + Category Analysis**
- ✅ **"Global"** at **top** of dropdown
- ✅ **All** statistics features included

Additional clarifications:
- ✅ Use constants for magic numbers (TOP_N, RECENT_GAMES, etc.)
- ✅ Display errors when no data/missing players
- ✅ Use Material3 `HorizontalDivider`
- ✅ Medal emojis (🥇🥈🥉) approved
- ✅ Modify existing `CategoryHeatmap` to handle both types

---

## 📐 Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                     User Interface                       │
│  YahtzeeStatisticsScreen.kt                             │
│  ├─ PlayerSelectorDropdown (Global at top)             │
│  ├─ GlobalStatisticsContent (when Global selected)      │
│  │   ├─ GlobalOverviewCard                             │
│  │   ├─ GlobalLeaderboardCard                          │
│  │   └─ GlobalCategoryAnalysisCard                     │
│  └─ StatisticsContent (when Player selected)            │
└─────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────┐
│                   State Management                       │
│  YahtzeeStatisticsViewModel.kt                          │
│  ├─ selectedPlayerId: String? ("GLOBAL" or playerId)   │
│  ├─ globalStatistics: YahtzeeGlobalStatistics?         │
│  ├─ loadGlobalStatistics()                             │
│  └─ selectPlayer(playerId)                             │
└─────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────┐
│                   Business Logic                         │
│  YahtzeeStatisticsEngine.kt                             │
│  └─ calculateGlobalStatistics() - Main calculation      │
│      ├─ Overall stats (games, players, scores)         │
│      ├─ Record finding (high score, most yahtzees)     │
│      ├─ Leaderboard building (wins, scores, yahtzees)  │
│      ├─ Category analysis (averages, zero rates)       │
│      └─ Fun stats (dice rolls, consistency)            │
└─────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────┐
│                    Data Access                           │
│  YahtzeeStatisticsRepository.kt/.Impl.kt                │
│  └─ getGlobalStatistics(): YahtzeeGlobalStatistics     │
└─────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────┐
│                      Database                            │
│  YahtzeeDao.kt                                          │
│  ├─ getAllFinishedGames()                              │
│  └─ getAllScoresFromFinishedGames()                    │
└─────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────┐
│                    Data Models                           │
│  YahtzeeStatistics.kt                                   │
│  ├─ YahtzeeGlobalStatistics (main model)               │
│  ├─ PlayerSummary, ScoreRecord, YahtzeeRecord          │
│  ├─ GlobalCategoryStat, CategoryRecord                 │
│  ├─ LeaderboardEntry, GlobalGameSummary                │
│  └─ Constants object (TOP_N, RECENT_GAMES, etc.)      │
└─────────────────────────────────────────────────────────┘
```

---

## 📝 Implementation Tasks

### **TASK 1: Add Constants & Data Models** (YahtzeeStatistics.kt)
- [x] Constants object with TOP_N, RECENT_GAMES, etc.
- [x] YahtzeeGlobalStatistics main model
- [x] Supporting data classes (PlayerSummary, ScoreRecord, etc.)

### **TASK 2: Add Database Queries** (YahtzeeDao.kt)
- [x] getAllFinishedGames()
- [x] getAllScoresFromFinishedGames()

### **TASK 3: Update Repository Interface** (YahtzeeStatisticsRepository.kt)
- [x] Add getGlobalStatistics() method

### **TASK 4: Implement Repository Method** (YahtzeeStatisticsRepositoryImpl.kt)
- [x] Implement getGlobalStatistics()

### **TASK 5: Create Global Statistics Engine** (YahtzeeStatisticsEngine.kt)
- [x] calculateGlobalStatistics() - Main orchestrator
- [x] calculateAllTimeHighScore()
- [x] calculateMostYahtzeesInGame()
- [x] buildWinsLeaderboard()
- [x] buildScoreLeaderboard()
- [x] buildYahtzeesLeaderboard()
- [x] calculateGlobalCategoryStats()
- [x] findMostActivePlayer()
- [x] findLuckiestPlayer()
- [x] findMostConsistentPlayer()
- [x] calculateGlobalRecentGames()
- [x] calculatePlayerGameScore()

### **TASK 6: Update ViewModel** (YahtzeeStatisticsViewModel.kt)
- [x] Add YahtzeeGlobalStatistics to UiState
- [x] Add GLOBAL_ID constant
- [x] Update loadAvailablePlayers() to default to Global
- [x] Update selectPlayer()
- [x] Add loadGlobalStatistics()

### **TASK 7: Update CategoryHeatmap Component** (CategoryHeatmap.kt)
- [x] Make component accept both CategoryStat and GlobalCategoryStat

### **TASK 8: Update Dropdown UI** (YahtzeeStatisticsScreen.kt)
- [x] Add imports for HorizontalDivider and global stats
- [x] Update PlayerSelectorDropdown to show Global at top
- [x] Add HorizontalDivider after Global option

### **TASK 9: Create Global Statistics UI Components** (YahtzeeStatisticsScreen.kt)
- [x] GlobalStatisticsContent
- [x] GlobalOverviewCard
- [x] GlobalLeaderboardCard
- [x] LeaderboardRow
- [x] GlobalCategoryAnalysisCard
- [x] GlobalRecentGamesCard
- [x] GlobalGameSummaryRow

### **TASK 10: Update Main Content Logic** (YahtzeeStatisticsScreen.kt)
- [x] Add condition for Global stats display

---

## 📊 File Changes Summary

| File | Action | Est. Lines |
|------|--------|------------|
| YahtzeeStatistics.kt | Add models | +140 |
| YahtzeeDao.kt | Add queries | +9 |
| YahtzeeStatisticsRepository.kt | Add method | +1 |
| YahtzeeStatisticsRepositoryImpl.kt | Implement | +10 |
| YahtzeeStatisticsEngine.kt | Add calculations | +500 |
| YahtzeeStatisticsViewModel.kt | Update logic | ~60 |
| CategoryHeatmap.kt | Make generic | ~20 |
| YahtzeeStatisticsScreen.kt | Add UI + update | +445 |

**Total estimated changes**: ~1,185 lines across 8 files

---

## 🧪 Testing Checklist

### **Initial Load**
- [ ] Global stats selected by default
- [ ] All cards display correctly
- [ ] No crashes with empty database

### **Data Accuracy**
- [ ] Leaderboards show correct top 3
- [ ] Category heatmap matches player stats
- [ ] All calculations are mathematically correct
- [ ] Recent games show last 10

### **UI/UX**
- [ ] Dropdown shows Global at top with separator
- [ ] Switching between Global and Player works
- [ ] Loading states work correctly
- [ ] Error messages display properly

### **Edge Cases**
- [ ] No finished games → appropriate error
- [ ] Single player → leaderboards handle gracefully
- [ ] Missing player data → doesn't crash
- [ ] Very large numbers → no overflow

---

## 🚀 Implementation Status

**Branch**: `feature/global-yahtzee-statistics`
**Status**: INFRASTRUCTURE COMPLETE ✅ (Tasks 1-5)
**Started**: 2026-01-16
**Latest Commit**: 03e5ce0

### Completed Tasks ✅
- [x] Task 1: Create YahtzeeGlobalStatistics data model with all metrics
- [x] Task 2: Add database queries for global stats  
- [x] Task 3: Update repository interface and implementation
- [x] Task 4: Implement calculateGlobalStatistics engine
- [x] Task 5: Update ViewModel for global stats support

### Remaining Tasks (UI Layer)
- [ ] Task 6: Make CategoryHeatmap generic for both stat types
- [ ] Task 7: Update dropdown UI with Global option at top
- [ ] Task 8: Create GlobalStatisticsContent composable
- [ ] Task 9: Create Global UI cards (Overview, Leaderboard, Category)
- [ ] Task 10: Update main content logic and test

### Build Status
✅ **BUILD SUCCESSFUL** (12s, 24 actionable tasks)
✅ **Zero compilation errors**
✅ **Only unrelated Beta warnings**

### Next Steps
1. Implement Tasks 6-10 (UI layer) in comprehensive update
2. Compile and verify
3. Manual testing with sample data
4. Commit and merge to main
