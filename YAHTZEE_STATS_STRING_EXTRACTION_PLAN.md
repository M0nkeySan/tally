# Yahtzee Statistics String Extraction Implementation Plan

## 📋 Overview

This document outlines the plan to extract all hardcoded strings from Yahtzee statistics screens and move them to a centralized `AppStrings.kt` file for better maintainability and future i18n support.

**Status**: Ready for Implementation  
**Branch**: `feature/yahtzee-stats-string-extraction`  
**Estimated Duration**: 1.5-2 hours  
**Commits**: 1 atomic commit

---

## 🎯 Decisions Made

1. **Naming Convention**: `YAHTZEE_STATS_` (shorter, consistent with existing patterns)
2. **Format Strings**: Keep simple with `String.format()` calls (no helper functions)
3. **Commit Strategy**: Single atomic commit (Option A)

---

## 📍 Implementation Phases

### **Phase 1: Add Strings to AppStrings.kt** (15-20 min)

**Goal**: Add ~80-100 new string constants organized in logical sections

**File**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/strings/AppStrings.kt`

**String Categories** (with examples):

#### Screen & Navigation
```kotlin
const val YAHTZEE_STATS_TITLE = "Yahtzee Statistics"
const val YAHTZEE_STATS_BACK = "Back"
const val YAHTZEE_STATS_GLOBAL = "⭐ Global Statistics"
const val YAHTZEE_STATS_SELECT_PLAYER = "Select Player"
const val YAHTZEE_STATS_NO_DATA = "No statistics available"
```

#### Player Statistics Sections
```kotlin
const val YAHTZEE_STATS_OVERALL_PERFORMANCE = "📊 Overall Performance"
const val YAHTZEE_STATS_SCORE_BOX = "📦 Score Breakdown"
const val YAHTZEE_STATS_RECENT_GAMES = "🕐 Recent Games"
const val YAHTZEE_STATS_UPPER_SECTION = "Upper Section"
const val YAHTZEE_STATS_LOWER_SECTION = "Lower Section"
```

#### Player Statistics Labels
```kotlin
const val YAHTZEE_STATS_TOTAL_GAMES = "Total Games"
const val YAHTZEE_STATS_WINS = "Wins"
const val YAHTZEE_STATS_AVERAGE_SCORE = "Average Score"
const val YAHTZEE_STATS_YAHTZEE_RATE = "Yahtzee Rate"
const val YAHTZEE_STATS_UPPER_AVERAGE = "Upper Average"
const val YAHTZEE_STATS_LOWER_AVERAGE = "Lower Average"
// ... additional labels
```

#### Global Statistics Sections
```kotlin
const val YAHTZEE_STATS_GLOBAL_OVERALL = "📊 Overall Performance"
const val YAHTZEE_STATS_GLOBAL_FUN_FACTS = "🎲 Fun Facts"
const val YAHTZEE_STATS_GLOBAL_LEADERBOARDS = "🏆 Leaderboards"
const val YAHTZEE_STATS_GLOBAL_CATEGORY = "📈 Category Analysis"
const val YAHTZEE_STATS_GLOBAL_RECENT = "🕐 Recent Games"
```

#### Format Strings (with placeholders)
```kotlin
const val YAHTZEE_FORMAT_WINS = "%s (%s)"
const val YAHTZEE_FORMAT_SCORE_BY_PLAYER = "%d by %s"
const val YAHTZEE_FORMAT_PLAYER_COUNT = "%d players"
const val YAHTZEE_FORMAT_WINNING_SCORE = "Winner: %s"
// ... additional format strings
```

#### Leaderboards & Ranks
```kotlin
const val YAHTZEE_STATS_MOST_WINS = "Most Wins"
const val YAHTZEE_STATS_HIGHEST_SCORES = "Highest Scores"
const val YAHTZEE_STATS_MOST_YAHTZEES = "Most Yahtzees"
const val YAHTZEE_RANK_FIRST = "🥇"
const val YAHTZEE_RANK_SECOND = "🥈"
const val YAHTZEE_RANK_THIRD = "🥉"
```

#### Error Messages
```kotlin
const val YAHTZEE_ERROR_LOAD_FAILED = "Failed to load statistics: %s"
const val YAHTZEE_ERROR_STATS_FAILED = "Failed to calculate statistics: %s"
const val YAHTZEE_ERROR_GLOBAL_FAILED = "Failed to load global statistics: %s"
```

---

### **Phase 2: Update YahtzeeStatisticsScreen.kt** (30-40 min)

**Goal**: Replace ~75-80 hardcoded strings with `AppStrings.*` references

**File**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/yahtzee/YahtzeeStatisticsScreen.kt`

**Actions**:
1. Add import: `import io.github.m0nkeysan.gamekeeper.ui.strings.AppStrings`
2. Find all hardcoded strings (look for quoted text in the UI)
3. Replace with `AppStrings.YAHTZEE_STATS_*` references
4. Convert format strings to `String.format()` calls

**Example Replacements**:
```kotlin
// Before:
Text("${wins} (${rate})")

// After:
Text(String.format(AppStrings.YAHTZEE_FORMAT_WINS, wins, rate))

// Before:
Text("📊 Overall Performance")

// After:
Text(AppStrings.YAHTZEE_STATS_OVERALL_PERFORMANCE)
```

---

### **Phase 3: Update YahtzeeStatisticsViewModel.kt** (5-10 min)

**Goal**: Replace 3-4 error messages with `AppStrings.*` references

**File**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/yahtzee/YahtzeeStatisticsViewModel.kt`

**Actions**:
1. Add import
2. Find error messages
3. Replace with formatted `AppStrings.YAHTZEE_ERROR_*` references

**Example**:
```kotlin
// Before:
uiState.value = YahtzeeStatisticsUiState.Error("Failed to load: ${e.message}")

// After:
uiState.value = YahtzeeStatisticsUiState.Error(
    String.format(AppStrings.YAHTZEE_ERROR_LOAD_FAILED, e.message ?: "Unknown")
)
```

---

### **Phase 4: Update GameSummaryRow.kt** (5-10 min)

**Goal**: Replace 6-7 hardcoded strings with `AppStrings.*` references

**File**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/yahtzee/components/GameSummaryRow.kt`

**Actions**:
1. Add import
2. Find hardcoded strings (date format, player count, rank indicators)
3. Replace with `AppStrings.*` references

---

### **Phase 5: Verify Other Components** (5-10 min)

**Files to Check**:
- `StatisticRow.kt`
- `CategoryHeatmap.kt`

**Actions**:
1. Read each file
2. Look for hardcoded user-facing strings
3. Replace if found
4. Note any changes

---

### **Phase 6: Testing & Verification** (15-20 min)

**Actions**:
1. Run build: `./gradlew build -x test`
2. Verify no compilation errors
3. Check for remaining hardcoded strings: 
   ```bash
   grep -rn '"[A-Z]' composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/yahtzee/ \
     | grep -v "import\|package\|AppStrings\|@\|//"
   ```
4. Test UI visually (if possible)
5. Verify all text displays correctly
6. Verify no visual regressions

**Success Criteria**:
- ✅ Zero hardcoded user-facing strings in Yahtzee stats files
- ✅ All strings follow `YAHTZEE_STATS_*` naming convention
- ✅ Build successful
- ✅ All text displays correctly
- ✅ No compilation errors

---

## 📊 Expected Changes

**Files Modified**: 4-5
- `AppStrings.kt` - Add ~80-100 new string constants
- `YahtzeeStatisticsScreen.kt` - Replace ~75-80 strings
- `YahtzeeStatisticsViewModel.kt` - Replace 3-4 error messages
- `GameSummaryRow.kt` - Replace 6-7 strings
- `StatisticRow.kt` or `CategoryHeatmap.kt` - (if strings found)

**Lines Changed**:
- Added: ~100-120 (AppStrings.kt)
- Modified: ~80-90 (other files)
- Net: +200-210 lines

---

## 🔄 Git Strategy

**Branch**: `feature/yahtzee-stats-string-extraction`  
**Base**: `feature/yahtzee-statistics-performance-optimization`

**Final Commit Message**:
```
refactor: Extract Yahtzee statistics strings to AppStrings

- Add ~80-100 new string constants to AppStrings.kt
- Replace all hardcoded strings in YahtzeeStatisticsScreen.kt
- Replace error messages in YahtzeeStatisticsViewModel.kt
- Replace format strings in GameSummaryRow.kt
- Use String.format() for complex formatting
- Maintain emoji icons with text for consistency

Benefits:
- Centralized string management
- Foundation for future i18n support
- Consistent terminology throughout app
- Easier maintenance and updates

Files modified: 4-5
String constants added: ~80-100
Hardcoded strings removed: ~85-90
```

---

## 🚀 Execution Checklist

- [ ] Create branch from `feature/yahtzee-statistics-performance-optimization`
- [ ] Phase 1: Extract and add strings to AppStrings.kt
- [ ] Phase 2: Update YahtzeeStatisticsScreen.kt
- [ ] Phase 3: Update YahtzeeStatisticsViewModel.kt
- [ ] Phase 4: Update GameSummaryRow.kt
- [ ] Phase 5: Verify other components
- [ ] Phase 6: Build and test
- [ ] Create atomic commit
- [ ] Push to remote (optional, wait for user)

---

## 📚 References

**Key Files**:
- `AppStrings.kt`: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/strings/AppStrings.kt`
- Yahtzee Stats Screen: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/yahtzee/`

**Related Documentation**:
- `YAHTZEE_STATS_PERFORMANCE_OPTIMIZATION.md` - Previous performance work
- Project Structure: `/Users/thibaut.farcin/Documents/perso/GameKeeper`

---

## ⏱️ Timeline

**Total Estimated Time**: 1.5-2 hours

- Phase 1: 15-20 min
- Phase 2: 30-40 min
- Phase 3: 5-10 min
- Phase 4: 5-10 min
- Phase 5: 5-10 min
- Phase 6: 15-20 min
- Buffer: 10-15 min

