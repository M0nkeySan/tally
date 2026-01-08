# GameKeeper UI/UX Consistency - Phase 2 Implementation TODO

## Overview
AI Agent: Implement Phase 2 (Integration) - Refactor existing game screens to use new templates.
Timeline: Week 2
**IMPORTANT: Commit after each task with the specified commit message.**

## Prerequisites
✅ Phase 1 must be completed first
✅ All Phase 1 components compile successfully
✅ GameColors, StateDisplay, templates available

---

## PHASE 2: Integration & Refactoring

### Task 2.1: Refactor TarotGameSelectionScreen
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/tarot/TarotGameSelectionScreen.kt`

**Objective:** Replace custom implementation with GameSelectionTemplate

**Current Issues:**
- Custom card layout (~157 lines)
- Hardcoded styling
- No error display to user
- Duplicate code

**Implementation Steps:**
1. Read current TarotGameSelectionScreen
2. Map `TarotGameDisplayModel` to `GameDisplay`
3. Replace entire screen content with `GameSelectionTemplate`
4. Pass proper parameters:
   - title = "Tarot Games"
   - games = mapped from TarotGameDisplayModel
   - isLoading = state.isLoading
   - error = state.error
   - onGameSelect, onCreateNew, onDeleteGame, onBack
5. Remove old TarotGameCard composable
6. Build and test

**Expected Result:**
- File reduced from ~157 lines to ~60 lines
- Uses GameSelectionTemplate
- Shows errors in Snackbar
- Consistent with flat design

**Success Criteria:**
- Compiles without errors
- Game selection works
- Error display works
- Delete functionality works

**Commit Message:** `refactor: use GameSelectionTemplate in TarotGameSelectionScreen (157→60 lines)`

---

### Task 2.2: Refactor YahtzeeGameSelectionScreen
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/yahtzee/YahtzeeGameSelectionScreen.kt`

**Objective:** Replace custom implementation with GameSelectionTemplate

**Current Issues:**
- Different card layout than Tarot (~210 lines)
- Hardcoded colors
- No error display
- Trophy icon logic duplicated

**Implementation Steps:**
1. Read current YahtzeeGameSelectionScreen
2. Map `YahtzeeGameDisplayModel` to `GameDisplay`
3. Replace entire screen content with `GameSelectionTemplate`
4. Pass proper parameters:
   - title = "Yahtzee Games"
   - games = mapped from YahtzeeGameDisplayModel
   - isLoading = state.isLoading
   - error = state.error
   - onGameSelect, onCreateNew, onDeleteGame, onBack
5. Remove old YahtzeeGameCard composable
6. Build and test

**Expected Result:**
- File reduced from ~210 lines to ~60 lines
- Uses GameSelectionTemplate
- Matches Tarot selection screen layout
- Shows errors in Snackbar

**Success Criteria:**
- Compiles without errors
- Game selection works
- Error display works
- Trophy icon shows for finished games

**Commit Message:** `refactor: use GameSelectionTemplate in YahtzeeGameSelectionScreen (210→60 lines)`

---

### Task 2.3: Refactor TarotGameCreationScreen
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/tarot/TarotGameCreationScreen.kt`

**Objective:** Use GameCreationTemplate and FlexiblePlayerSelector

**Current Issues:**
- Uses FilterChip for player count (~150+ lines)
- Fixed player slots
- Different UX than Yahtzee

**Implementation Steps:**
1. Read current TarotGameCreationScreen
2. Replace Scaffold with `GameCreationTemplate`
3. Replace player selection with `FlexiblePlayerSelector`:
   - minPlayers = 3
   - maxPlayers = 5
   - Pass allPlayers from ViewModel
   - Handle onPlayersChange callback
4. Keep game name field in content lambda
5. Validation: Enable "Create" button only when:
   - Game name is not blank
   - All player slots filled
   - Player count is 3-5
6. Remove FilterChip logic
7. Remove repeat(playerCount) logic
8. Build and test

**Expected Result:**
- File reduced to ~80 lines
- Uses GameCreationTemplate
- Dynamic player addition (like Yahtzee)
- Validation for 3-5 players
- Shows error if validation fails

**Success Criteria:**
- Compiles without errors
- Can add players (up to 5)
- Can remove players (down to 3)
- Validation works
- Create button enabled/disabled correctly

**Commit Message:** `refactor: use GameCreationTemplate and FlexiblePlayerSelector in TarotGameCreationScreen`

---

### Task 2.4: Refactor YahtzeeGameCreationScreen
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/yahtzee/YahtzeeGameCreationScreen.kt`

**Objective:** Use GameCreationTemplate and FlexiblePlayerSelector

**Current Issues:**
- Custom Add/Remove buttons (~150+ lines)
- mutableStateListOf management
- Different from Tarot

**Implementation Steps:**
1. Read current YahtzeeGameCreationScreen
2. Replace Scaffold with `GameCreationTemplate`
3. Replace player selection with `FlexiblePlayerSelector`:
   - minPlayers = 2
   - maxPlayers = 8
   - Pass allPlayers from ViewModel
   - Handle onPlayersChange callback
4. Keep game name field in content lambda
5. Validation: Enable "Create" button only when:
   - Game name is not blank
   - All player slots filled
   - Player count is 2-8
6. Remove custom Add/Remove button logic
7. Build and test

**Expected Result:**
- File reduced to ~80 lines
- Uses GameCreationTemplate
- Matches Tarot creation screen
- Validation for 2-8 players

**Success Criteria:**
- Compiles without errors
- Can add players (up to 8)
- Can remove players (down to 2)
- Validation works
- Same UX as Tarot creation

**Commit Message:** `refactor: use GameCreationTemplate and FlexiblePlayerSelector in YahtzeeGameCreationScreen`

---

### Task 2.5: Add Error Display to TarotScoringScreen
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/tarot/TarotScoringScreen.kt`

**Objective:** Display error messages from ViewModel

**Current Issues:**
- Error state exists in ViewModel (TarotScoringState.error)
- Not displayed to user
- Silent failures

**Implementation Steps:**
1. Read current TarotScoringScreen
2. Add `snackbarHostState` to Scaffold
3. Add `GameKeeperSnackbarHost` to Scaffold
4. Add LaunchedEffect to watch `state.error`:
   ```kotlin
   LaunchedEffect(state.error) {
       if (state.error != null) {
           showErrorSnackbar(snackbarHostState, state.error)
       }
   }
   ```
5. Import GameKeeperSnackbarHost and showErrorSnackbar
6. Build and test

**Expected Result:**
- Errors now visible to user
- Snackbar shows at bottom for 5 seconds
- User knows why operations failed

**Success Criteria:**
- Compiles without errors
- Snackbar appears when error occurs
- Error message is clear
- Auto-dismisses after 5 seconds

**Commit Message:** `feat: add error Snackbar display to TarotScoringScreen`

---

### Task 2.6: Add Error Display to YahtzeeScoringScreen
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/yahtzee/YahtzeeScoringScreen.kt`

**Objective:** Display error messages from ViewModel

**Current Issues:**
- Error state exists in ViewModel
- Not displayed to user
- Silent failures

**Implementation Steps:**
1. Read current YahtzeeScoringScreen
2. Add `snackbarHostState` to Scaffold
3. Add `GameKeeperSnackbarHost` to Scaffold
4. Add LaunchedEffect to watch error state
5. Import necessary components
6. Build and test

**Expected Result:**
- Errors now visible to user
- Consistent with TarotScoringScreen
- Same Snackbar behavior

**Success Criteria:**
- Compiles without errors
- Snackbar appears when error occurs
- Matches TarotScoringScreen behavior

**Commit Message:** `feat: add error Snackbar display to YahtzeeScoringScreen`

---

### Task 2.7: Refactor YahtzeeSummaryScreen
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/yahtzee/YahtzeeSummaryScreen.kt`

**Objective:** Use ResultsTemplate and ResultsCard components

**Current Issues:**
- Custom results display (~186 lines)
- Custom ScoreRankItem component
- Duplicates future Tarot results logic

**Implementation Steps:**
1. Read current YahtzeeSummaryScreen
2. Replace Scaffold + content with `ResultsTemplate`
3. Pass winners and allResults:
   - winners = viewModel.getWinners()
   - allResults = viewModel.getAllPlayerScores().sortedByDescending { it.second }
4. Remove custom ScoreRankItem composable (now use ResultsCard from template)
5. Build and test

**Expected Result:**
- File reduced from ~186 lines to ~60 lines
- Uses ResultsTemplate
- Uses ResultsCard
- Consistent with flat design
- Trophy icon positioned correctly

**Success Criteria:**
- Compiles without errors
- Winners display correctly
- Tie message shows when appropriate
- All scores ranked correctly
- Back button works

**Commit Message:** `refactor: use ResultsTemplate in YahtzeeSummaryScreen (186→60 lines)`

---

### Task 2.8: Update Color Usage Throughout
**Objective:** Replace hardcoded colors with GameColors

**Files to Update:**
1. `TarotScoringScreen.kt` - Replace any hardcoded colors
2. `YahtzeeScoringScreen.kt` - Replace any hardcoded colors
3. `FingerSelectorScreen.kt` - Harmonize fingerColors with GameColors palette (optional, keep vibrant but consider using GameColors)

**Implementation Steps:**
1. Search for hardcoded Color(0xFFxxxxxx) in scoring screens
2. Replace with GameColors equivalents:
   - Trophy gold: Use `GameColors.TrophyGold`
   - Primary colors: Use `GameColors.Primary`
   - Error colors: Use `GameColors.Error`
   - etc.
3. Update imports to include GameColors
4. Build and test

**Success Criteria:**
- No hardcoded colors in updated screens
- All use GameColors
- Colors still look correct

**Commit Message:** `refactor: replace hardcoded colors with GameColors in scoring screens`

---

### Task 2.9: Add Back Button to CounterScreen
**File:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/screens/counter/CounterScreen.kt`

**Objective:** Add consistent navigation (back button in TopBar)

**Current Issues:**
- No back button
- Inconsistent with other screens
- User can't navigate back easily

**Implementation Steps:**
1. Read current CounterScreen
2. Find TopAppBar in Scaffold
3. Add navigationIcon with back button:
   ```kotlin
   navigationIcon = {
       IconButton(onClick = onBack) {
           Icon(GameIcons.ArrowBack, contentDescription = "Back")
       }
   }
   ```
4. Add `onBack: () -> Unit` parameter to CounterScreen function
5. Update navigation call site (in NavGraph.kt) to pass onBack
6. Build and test

**Expected Result:**
- Counter screen now has back button
- Consistent with other screens
- Easy navigation

**Success Criteria:**
- Compiles without errors
- Back button visible in TopBar
- Back button navigates correctly
- No visual regression

**Commit Message:** `feat: add back button to CounterScreen for consistent navigation`

---

### Task 2.10: Integration Testing & Build Verification
**Objective:** Verify all Phase 2 changes work together

**Actions:**
1. Full build:
```bash
./gradlew :composeApp:assembleDebug
```

2. Verify:
- ✅ All screens compile
- ✅ No runtime errors
- ✅ Error Snackbars work
- ✅ Game selection consistent (Tarot & Yahtzee)
- ✅ Game creation consistent (Tarot & Yahtzee)
- ✅ Results screen uses template
- ✅ Counter has back button
- ✅ No visual regressions

3. Test flows:
- Create Tarot game (3-5 players)
- Create Yahtzee game (2-8 players)
- Select existing game
- Delete game
- View results
- Navigate Counter

4. Check code reduction:
```bash
# Count lines before/after
# Should see ~480 line reduction overall
```

**Success Criteria:**
- ✅ Full app builds successfully
- ✅ All game flows work
- ✅ Error display works
- ✅ Consistent UX across games
- ✅ Flat design throughout
- ✅ ~480 lines of code removed

**Commit Message:** `build: verify Phase 2 integration - all screens refactored successfully`

---

## GENERAL REQUIREMENTS

### Code Style
- Maintain existing Kotlin conventions
- Preserve existing logic
- Only change UI/structure

### Commit Strategy
**CRITICAL: Commit after each task (10 commits total)**
- Use present tense
- Reference file names
- Note line count reduction where applicable

### Testing
- After each task, verify compilation
- Test affected screens manually if possible
- Check for visual regressions

### Migration Strategy
- Keep ViewModels unchanged
- Keep business logic unchanged
- Only change UI layer
- Preserve existing functionality

---

## Success Criteria for Phase 2

✅ All game selection screens use GameSelectionTemplate
✅ All game creation screens use GameCreationTemplate
✅ Results screen uses ResultsTemplate
✅ Error Snackbars added to scoring screens
✅ Counter has back button
✅ Colors unified with GameColors
✅ ~480 lines of code removed
✅ Consistent UX across all games
✅ 10 commits made

---

## Notes for AI Agent

- Preserve all existing functionality
- Do NOT change ViewModel logic
- Do NOT change repository code
- Focus on UI layer only
- Test each screen after refactoring
- **Commit after each task**
- If a task fails, document the error and continue

Ready for Phase 3 after completion! 🚀
