# Dice Roller Feature - Implementation Plan

**Date**: January 13, 2026  
**Branch**: `feature/dice-roller`  
**Status**: Implementation in progress  

---

## Requirements Summary

✅ **Data Persistence**: Save settings between sessions  
✅ **Animation**: 2D rotation/spinning  
✅ **Custom Dice**: Allow any value 2-99  
✅ **Results**: Current roll only (no history)  
✅ **Home Integration**: Add to home screen game cards  
✅ **Shake Detection**: Use reasonable default sensitivity  

---

## Feature Overview

A dedicated Dice Roller screen with:
- **Large central dice** displaying roll results
- **Configuration display** at top showing selected dice count & type (e.g., "1d6")
- **Settings modal** (long-press) to configure:
  - Number of dice: 1-5 (slider)
  - Dice type: d4, d6, d8, d10, d12, d20, or custom (2-99)
  - Animation toggle (2D rotation)
  - Shake-to-roll toggle
- **Results below dice**: Individual results and total
- **Interactions**:
  - Tap dice to roll
  - Long-press configuration to open settings
  - Shake device to roll (if enabled)

---

## Implementation Phases

### Phase 1: Foundation & Data Models (1-2 hours)
- [x] Create data models (DiceConfiguration, DiceType, DiceRoll)
- [x] Extend UserPreferencesRepository
- [x] Add string resources

### Phase 2: Platform-Specific Features (2-3 hours)
- [ ] Shake detection common interface
- [ ] Android shake detection implementation

### Phase 3: ViewModel (1-2 hours)
- [ ] Create DiceRollerViewModel
- [ ] Implement roll logic
- [ ] Handle persistence

### Phase 4: UI Components (4-5 hours)
- [ ] DiceView component with 2D rotation animation
- [ ] D6 dot pattern display
- [ ] DiceSettingsDialog
- [ ] CustomDiceDialog

### Phase 5: Main Screen (2-3 hours)
- [ ] DiceRollerScreen with tap/long-press gestures
- [ ] Single vs multiple dice layout
- [ ] Results display
- [ ] Shake detection integration

### Phase 6: Navigation & Integration (1 hour)
- [ ] Add to Screen.kt
- [ ] Update NavGraph
- [ ] Add to home screen game cards
- [ ] Update home screen card order

### Phase 7: Testing & Polish (2-3 hours)
- [ ] Functional testing all dice types
- [ ] Animation and persistence testing
- [ ] Shake detection on device
- [ ] UI/UX on different screen sizes
- [ ] Accessibility verification

---

## File Structure

### New Files (8 total)
```
composeApp/src/
├── commonMain/kotlin/io/github/m0nkeysan/gamekeeper/
│   ├── core/model/
│   │   └── Dice.kt (NEW)
│   ├── platform/
│   │   └── ShakeDetector.kt (NEW - expect)
│   └── ui/screens/dice/
│       ├── DiceRollerScreen.kt (NEW)
│       ├── DiceRollerViewModel.kt (NEW)
│       ├── DiceView.kt (NEW)
│       ├── DiceSettingsDialog.kt (NEW)
│       └── CustomDiceDialog.kt (NEW)
└── androidMain/kotlin/io/github/m0nkeysan/gamekeeper/
    └── platform/
        └── ShakeDetector.android.kt (NEW - actual)
```

### Modified Files (7 total)
```
- core/navigation/Screen.kt
- core/domain/repository/UserPreferencesRepository.kt
- core/data/local/repository/UserPreferencesRepositoryImpl.kt
- ui/strings/AppStrings.kt
- ui/screens/home/HomeScreen.kt
- ui/screens/home/HomeViewModel.kt
- Navigation graph configuration
```

---

## Data Models

### DiceConfiguration
```kotlin
data class DiceConfiguration(
    val numberOfDice: Int = 1,
    val diceType: DiceType = DiceType.D6,
    val animationEnabled: Boolean = true,
    val shakeEnabled: Boolean = false
)
```

### DiceType
```kotlin
sealed class DiceType(val sides: Int, val displayName: String) {
    object D4 : DiceType(4, "d4")
    object D6 : DiceType(6, "d6")
    object D8 : DiceType(8, "d8")
    object D10 : DiceType(10, "d10")
    object D12 : DiceType(12, "d12")
    object D20 : DiceType(20, "d20")
    data class Custom(val customSides: Int) : DiceType(customSides, "d$customSides")
}
```

### DiceRoll
```kotlin
data class DiceRoll(
    val individualResults: List<Int>,
    val total: Int
)
```

---

## Technical Decisions

| Aspect | Decision | Rationale |
|--------|----------|-----------|
| **Animation** | 2D rotation (360°, 800ms) | Smooth, performant, matches user requirement |
| **D6 Display** | Dot patterns | Traditional dice aesthetic |
| **Persistence** | JSON in UserPreferences | Reuses existing infrastructure |
| **Shake Threshold** | 12f acceleration | Reasonable sensitivity, prevents false positives |
| **Shake Cooldown** | 500ms | Prevents accidental multiple rolls |
| **Grid Layout** | Single large / responsive grid | Optimal for 1 dice vs 2-5 dice |
| **Custom Range** | 2-99 | Covers all practical RPG dice |

---

## String Resources

New strings to add to `AppStrings.kt`:

```kotlin
// Dice Roller
const val GAME_DICE = "Dice Roller"
const val DESC_DICE = "Roll customizable dice for any board game"
const val DICE_SETTINGS_TITLE = "Dice Settings"
const val DICE_NUMBER_OF_DICE = "Number of Dice"
const val DICE_TYPE = "Dice Type"
const val DICE_ENABLE_ANIMATION = "Enable Animation"
const val DICE_SHAKE_TO_ROLL = "Shake to Roll"
const val DICE_CUSTOM_SIDES = "Custom Sides"
const val DICE_CUSTOM_SIDES_HINT = "Enter number of sides (2-99)"
const val DICE_TOTAL = "Total"
const val DICE_TAP_TO_ROLL = "Tap to roll"
const val DICE_TAP_OR_SHAKE = "Tap or shake to roll"
const val DICE_ERROR_INVALID = "Please enter a valid number"
const val DICE_ERROR_MIN = "Minimum is 2 sides"
const val DICE_ERROR_MAX = "Maximum is 99 sides"
```

---

## Testing Checklist

### Functional Testing
- [ ] All dice types roll correctly (d4, d6, d8, d10, d12, d20, custom)
- [ ] Custom dice validation (2-99, error handling)
- [ ] Results match configuration (e.g., 3d6 has 3 values)
- [ ] Total calculation is correct
- [ ] Configuration persists across app sessions

### Animation Testing
- [ ] 2D rotation animation smooth (800ms, FastOutSlowInEasing)
- [ ] Animation can be disabled without errors
- [ ] D6 dot patterns display correctly
- [ ] Other dice show numeric values

### Shake Detection Testing (Physical Device Required)
- [ ] Shake detection works on Android device
- [ ] Reasonable threshold (not too sensitive/insensitive)
- [ ] Cooldown prevents multiple rolls
- [ ] Can be enabled/disabled via toggle
- [ ] Haptic feedback on shake detection

### UI/UX Testing
- [ ] Layouts work on different screen sizes
- [ ] Tap and long-press gestures work
- [ ] Settings dialog opens correctly
- [ ] Custom dice dialog validates input
- [ ] Haptic feedback on all interactions
- [ ] Accessibility: all icons have contentDescription

### Edge Cases
- [ ] Rapid tapping prevented (isRolling state)
- [ ] Settings dialog while rolling handled gracefully
- [ ] App backgrounding during roll
- [ ] Very fast shake movements
- [ ] Minimum/maximum custom values

---

## Commits Strategy

Each phase will be committed separately for clear history:

1. `feat: Add Dice model and persistence layer`
2. `feat: Add shake detection platform feature`
3. `feat: Add DiceRollerViewModel with roll logic`
4. `feat: Add DiceView and animation components`
5. `feat: Add DiceSettings and CustomDice dialogs`
6. `feat: Add DiceRollerScreen main UI`
7. `feat: Integrate Dice Roller into navigation and home screen`

---

## Build Verification

After each phase, verify:
```bash
./gradlew compileDebugKotlin -x test
```

---

## Implementation Notes

### Shake Detection
- Requires Android 5.0+ (API 21+) - standard accelerometer
- No additional permissions needed
- Will gracefully no-op on non-Android platforms

### Persistence Format
Custom JSON serialization (no JSON library dependency):
```
{numberOfDice:X,diceType:TYPE_NAME,customSides:Y,animation:true,shake:false}
```

### Animation Performance
- 2D rotation uses `graphicsLayer` for GPU acceleration
- 800ms animation at 60fps = smooth on most devices
- Can be disabled for lower-end devices

### Accessibility
- All interactive elements have haptic feedback
- All icons have contentDescription
- Touch targets meet 48dp minimum
- Error messages are clear and actionable

---

## Known Limitations & Future Enhancements

### Current Scope
- Single player tool (not multiplayer)
- No roll history (as requested)
- No custom animations beyond 2D rotation
- Android shake detection only (other platforms: no-op)

### Future Enhancements (Out of scope)
- Roll history with statistics
- Custom dice colors
- Sound effects
- Quick-roll presets (e.g., "common RPG set")
- Share roll results
- Multi-language support (already i18n ready)

---

## Dependencies

No new external dependencies required. Uses existing:
- Compose Material 3
- Jetpack ViewModel
- Room (for persistence)
- Android SensorManager (standard API)
- Kotlin stdlib (Random)

---

## Success Criteria

✅ Feature complete when:
1. All 7 dice types roll correctly
2. Settings persist across sessions
3. 2D rotation animation plays smoothly
4. Shake detection works on physical Android device
5. UI is responsive on 4" to 7" screens
6. All interactions provide haptic feedback
7. No crashes or warnings in build
8. All accessibility requirements met

---

## Progress Tracking

| Phase | Task | Status | Completed | Duration |
|-------|------|--------|-----------|----------|
| 1 | Foundation & Data Models | ⏳ | - | - |
| 2 | Platform Features | ⏳ | - | - |
| 3 | ViewModel | ⏳ | - | - |
| 4 | UI Components | ⏳ | - | - |
| 5 | Main Screen | ⏳ | - | - |
| 6 | Navigation & Integration | ⏳ | - | - |
| 7 | Testing & Polish | ⏳ | - | - |

---

## Branch Info

**Branch Name**: `feature/dice-roller`  
**Base**: `main`  
**Expected PR**: After all 7 phases complete  

---

**Last Updated**: January 13, 2026  
**Implementation Start**: TBD  
**Expected Completion**: TBD
