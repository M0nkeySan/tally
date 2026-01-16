# GameKeeper String Management Guidelines

## 📋 Overview

All user-facing text in the GameKeeper application **MUST** be defined in `AppStrings.kt`. This document outlines the rules, standards, and best practices for introducing and managing strings in the codebase.

**Key Principle:** No hardcoded strings in UI code. Ever.

---

## ⚠️ The Golden Rule

```
ANY NEW USER-FACING TEXT MUST BE ADDED TO AppStrings.kt FIRST
BEFORE BEING USED IN THE CODEBASE
```

---

## 🎯 Types of Strings That Must Be Centralized

### ✅ MUST Go in AppStrings.kt

1. **Button Labels**
   - ✅ "Delete", "Cancel", "Save", "Back", "Reset"
   - ❌ `Text("Delete")` - Use `AppStrings.ACTION_DELETE`

2. **Dialog Messages & Titles**
   - ✅ Confirmation dialogs, warnings, errors
   - ❌ Hardcoded confirmation text in composables

3. **Display Labels**
   - ✅ "Settings", "History", "Players", section headers
   - ❌ Inline text in UI components

4. **Error & Validation Messages**
   - ✅ "Need at least 2 players", "Maximum 5 sides"
   - ❌ Hardcoded error messages in catch blocks or UI

5. **Accessibility Descriptions**
   - ✅ `contentDescription = AppStrings.CD_MENU`
   - ❌ `contentDescription = "Menu"` (inline)

6. **Format Strings**
   - ✅ `"%d player%s"`, `"%s: %d"` - Use `.format()`
   - ❌ `"$count player${if (count > 1) "s" else ""}"`

7. **Game-Specific Content**
   - ✅ Yahtzee category names, Tarot terms, Counter labels
   - ❌ Inline game terminology

---

## ❌ Exceptions: What Does NOT Need AppStrings

### Dynamic Content (Calculate/Generate)
- Player names: `leader.name` (user input)
- Game counts: `playerCount.toString()` (calculated value)
- Scores: `"${player.score}"` (dynamic data)

### Code Comments & Documentation
```kotlin
// This is a comment - no need for AppStrings
/** Documentation comments are fine */
```

### Visual/Decorative Elements (Already Constant)
```kotlin
// Bullet point already in constant
Text(AppStrings.YAHTZEE_SCORING_TURN_INDICATOR) // "●"
```

### Format Placeholders
- Use format strings with `%s`, `%d`, `%f` for dynamic data
- Example: `"Players (%d/%d)".format(current, max)`

---

## 📝 Naming Convention

### Pattern: `[SCREEN/FEATURE]_[TYPE]_[DESCRIPTION]`

### Action Buttons (Global)
```kotlin
const val ACTION_DELETE = "Delete"
const val ACTION_CANCEL = "Cancel"
const val ACTION_SAVE = "Save"
const val ACTION_BACK = "Back"
const val ACTION_DELETE_ALL = "Delete All"
```

### Screen-Specific Buttons
```kotlin
const val COUNTER_CD_SETTINGS_MENU = "Settings menu"
const val PLAYER_CD_ADD = "Add Player"
```

### Accessibility Descriptions (CD_*)
```kotlin
const val CD_BACK = "Back"
const val CD_SETTINGS = "Settings"
const val CD_MENU = "Menu"
const val CD_ADD_PLAYER = "Add Player"
```

### Dialogs
```kotlin
const val DIALOG_DELETE_COUNTER_TITLE = "Delete Counter"
const val DIALOG_DELETE_COUNTER_MESSAGE = "Are you sure..."
const val GAME_DELETION_DIALOG_YAHTZEE_MESSAGE = "Are you sure..."
```

### Format Strings (Use %s, %d, %f)
```kotlin
const val PLAYERS_COUNT_FORMAT = "Players (%d/%d)"
const val COUNTER_LEADER_DISPLAY_FORMAT = "%s %s"  // emoji + name
const val YAHTZEE_FORMAT_WINS = "%s (%s)"
```

### Error Messages
```kotlin
const val ERROR_MIN_FINGERS = "Need at least %d fingers"
const val ERROR_PLAYER_COUNT_RANGE = "Player count must be between %d and %d"
const val YAHTZEE_ERROR_SCORE_TOO_HIGH = "Score cannot be higher than 30"
```

### Screen-Specific Strings
```kotlin
const val COUNTER_TITLE = "Counter"
const val YAHTZEE_STATS_TITLE = "Statistics"
const val TAROT_SCORING_SCREEN_TITLE = "Game Scoring"
```

---

## 🏗️ How to Add a New String

### Step 1: Identify the String
```kotlin
// ❌ BAD - Hardcoded in UI
Text("Delete All")

// ✅ GOOD - Add to AppStrings first
const val ACTION_DELETE_ALL = "Delete All"
```

### Step 2: Choose the Right Location in AppStrings.kt

**Location by Type:**
- Actions: After line 30 (Common Actions section)
- Counter-specific: Around line 140 (Counter - Main Screen section)
- Yahtzee-specific: Around line 185 (Yahtzee - Scoring section)
- Dialogs: Around line 100 (Common Dialogs section)
- Accessibility: Around line 67 (Common Content Descriptions section)
- Errors: Around line 380 (Validation Messages section)

### Step 3: Follow the Naming Convention
```kotlin
// ✅ GOOD
const val ACTION_DELETE_ALL = "Delete All"
const val COUNTER_LEADER_DISPLAY_FORMAT = "%s %s"
const val ERROR_PLAYER_COUNT_RANGE = "Player count must be between %d and %d"

// ❌ BAD
const val DELETE_ALL_BTN = "Delete All"  // Poor naming
const val FORMAT_LEADER = "%s %s"  // Unclear
const val ERR_PLAYERS = "Player count must be between %d and %d"  // Vague
```

### Step 4: Use in Code with .format()
```kotlin
// ❌ BAD - String concatenation
Text("Players (${current}/${max})")

// ✅ GOOD - Use AppStrings.format()
Text(AppStrings.PLAYERS_COUNT_FORMAT.format(current, max))
```

### Step 5: Test the Change
```bash
./gradlew build -x test
```

---

## 📋 String Type Reference

### Format String Patterns

#### Simple Replacement
```kotlin
// Single value
const val COUNTER_TITLE = "Counter"
const val YAHTZEE_STATS_TITLE = "Statistics"
```

#### Single Parameter (%d = integer, %s = string)
```kotlin
// Integer: finger count
const val ERROR_MIN_FINGERS = "Need at least %d fingers"
Text(ERROR_MIN_FINGERS.format(minFingers))

// String: game name
const val GAME_DELETION_DIALOG_YAHTZEE_MESSAGE = "Are you sure you want to delete '%s'?"
Text(GAME_DELETION_DIALOG_YAHTZEE_MESSAGE.format(gameName))
```

#### Multiple Parameters
```kotlin
// Two integers
const val ERROR_PLAYER_COUNT_RANGE = "Player count must be between %d and %d"
Text(ERROR_PLAYER_COUNT_RANGE.format(minPlayers, maxPlayers))

// String and integer
const val FINGER_SELECTOR_SLIDER_VALUE_FORMAT = "%s: %d"
Text(FINGER_SELECTOR_SLIDER_VALUE_FORMAT.format(title, config.count))

// String and string (emoji + name)
const val COUNTER_LEADER_DISPLAY_FORMAT = "%s %s"
Text(COUNTER_LEADER_DISPLAY_FORMAT.format(emoji, leader.name))
```

---

## ✅ Pre-Commit Checklist

Before committing code with new strings, verify:

- [ ] All user-facing text is in AppStrings.kt
- [ ] Naming follows convention: `[SCREEN/FEATURE]_[TYPE]_[DESCRIPTION]`
- [ ] No hardcoded `Text("...")` strings in UI files
- [ ] Format strings use `%s`, `%d`, `%f` with `.format()`
- [ ] Strings are placed in the correct section
- [ ] Build succeeds: `./gradlew build -x test`
- [ ] No compilation errors
- [ ] Imports are correct (if using direct imports like in StateDisplay.kt)

---

## 🔍 How to Find Hardcoded Strings

### Search for Violations
```bash
# Find all Text() calls NOT using AppStrings
grep -rn 'Text("' composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui --include="*.kt" | grep -v "AppStrings\." | grep -v "//"

# Result should only show:
# - Comments
# - Dynamic content (variables like player.name)
# - Visual elements already in constants
```

### What's Okay to Find
```kotlin
// ✅ OKAY - Variable content
Text(leader.name)
Text("$emoji ${leader.name}")  // If emoji is a variable

// ✅ OKAY - Comments
// TODO: Add string to AppStrings

// ❌ NEVER - Hardcoded strings
Text("Delete")  // Should use AppStrings.ACTION_DELETE
Text("Players (${count}/${max})")  // Should use format string
```

---

## 📚 Current AppStrings Structure

### Sections (In Order)
1. **Home Screen** (1 constant)
2. **Counter Screen** (12+ constants)
3. **Common Actions** (8 constants)
4. **Loading and States** (2 constants)
5. **Game Specific** (4 constants)
6. **Descriptions** (3 constants)
7. **Game Operations** (4 constants)
8. **Dice Roller** (9+ constants)
9. **Common Actions Extended** (2 constants)
10. **Accessibility (CD_*)** (12+ constants)
11. **Common Dialogs** (5 constants)
12. **Tarot Strings** (70+ constants)
13. **Yahtzee Strings** (85+ constants)
14. **Player Management** (10+ constants)
15. **Home & Results** (10+ constants)
16. **Validation & Display Formats** (8 constants)
17. **Error Handling** (2 constants)

**Total: 281 constants organized for easy navigation**

---

## 🚀 Best Practices

### 1. Group Related Strings
```kotlin
// ✅ GOOD - Related strings grouped by feature
const val COUNTER_TITLE = "Counter"
const val COUNTER_CD_HISTORY = "History"
const val COUNTER_CD_SETTINGS_MENU = "Settings menu"

// ❌ BAD - Random placement
const val COUNTER_TITLE = "Counter"
const val YAHTZEE_STATS_TITLE = "Statistics"  // Wrong section
const val COUNTER_CD_HISTORY = "History"
```

### 2. Use Format Strings for Flexibility
```kotlin
// ✅ GOOD - Reusable format string
const val PLAYERS_COUNT_FORMAT = "Players (%d/%d)"
Text(PLAYERS_COUNT_FORMAT.format(selectedPlayers.size, maxPlayers))
Text(PLAYERS_COUNT_FORMAT.format(currentPlayers, totalPlayers))

// ❌ BAD - Hardcoded for one use
const val SELECTED_PLAYERS_LABEL = "Players (2/4)"
const val CURRENT_PLAYERS_LABEL = "Players (3/5)"
```

### 3. Consistent Naming Across Similar Features
```kotlin
// ✅ GOOD - Consistent pattern
const val ACTION_DELETE = "Delete"
const val ACTION_DELETE_ALL = "Delete All"
const val ACTION_CANCEL = "Cancel"
const val ACTION_SAVE = "Save"

// ❌ BAD - Inconsistent naming
const val DELETE_BTN = "Delete"
const val DELETE_ALL = "Delete All"
const val BTN_CANCEL = "Cancel"
const val SAVE_TEXT = "Save"
```

### 4. Add Explanatory Comments
```kotlin
// ✅ GOOD - Comment explains context
// Used in counter screen for the settings menu button accessibility
const val COUNTER_CD_SETTINGS_MENU = "Settings menu"

// Used in both Tarot and Yahtzee for statistics/settings button
const val CD_SETTINGS = "Settings"
```

---

## 🛠️ Common Tasks

### Adding a New Button Label
```kotlin
// 1. Add constant to appropriate section
const val ACTION_REFRESH = "Refresh"

// 2. Use in code
Button(onClick = { /* ... */ }) {
    Text(AppStrings.ACTION_REFRESH)
}
```

### Adding a Confirmation Dialog
```kotlin
// 1. Add title and message
const val DIALOG_RESET_TITLE = "Reset Settings"
const val DIALOG_RESET_MESSAGE = "Are you sure you want to reset all settings?"

// 2. Use in code
AlertDialog(
    title = { Text(AppStrings.DIALOG_RESET_TITLE) },
    text = { Text(AppStrings.DIALOG_RESET_MESSAGE) },
    // ...
)
```

### Adding an Error Message with Parameters
```kotlin
// 1. Add format string
const val ERROR_VALUE_RANGE = "Value must be between %d and %d"

// 2. Use with format()
Text(AppStrings.ERROR_VALUE_RANGE.format(min, max))
```

### Adding Accessibility Content Description
```kotlin
// 1. Add CD_* constant (if not exists)
const val CD_REFRESH = "Refresh"

// 2. Use in Icon or Button
Icon(Icons.Default.Refresh, contentDescription = AppStrings.CD_REFRESH)
```

---

## 🔄 Code Review Checklist

**When reviewing PRs, verify:**

- [ ] All new strings added to AppStrings.kt?
- [ ] No hardcoded `Text("...")` in UI files?
- [ ] Format strings use correct syntax (%s, %d, %f)?
- [ ] Naming follows convention?
- [ ] Strings placed in correct section?
- [ ] Build passes without errors?
- [ ] Imports are correct?
- [ ] Related strings are grouped together?
- [ ] Comments added for non-obvious strings?

---

## 📖 Examples

### ✅ GOOD EXAMPLE - Yahtzee Game Deletion
```kotlin
// In AppStrings.kt
const val GAME_DELETION_DIALOG_YAHTZEE_TITLE = "Delete Game"
const val GAME_DELETION_DIALOG_YAHTZEE_MESSAGE = "Are you sure you want to delete '%s'? All scores will be lost."

// In YahtzeeGameSelectionScreen.kt
AlertDialog(
    title = { Text(AppStrings.GAME_DELETION_DIALOG_YAHTZEE_TITLE) },
    text = { Text(AppStrings.GAME_DELETION_DIALOG_YAHTZEE_MESSAGE.format(gameToDelete?.name)) },
    confirmButton = {
        TextButton(onClick = { /* delete */ }) {
            Text(AppStrings.ACTION_DELETE)
        }
    }
)
```

### ✅ GOOD EXAMPLE - Counter Leader Display
```kotlin
// In AppStrings.kt
const val COUNTER_LEADER_DISPLAY_FORMAT = "%s %s"

// In CounterScreen.kt
val emoji = if (state.displayMode == CounterDisplayMode.MOST_POINTS) "📈" else "📉"
Text(
    AppStrings.COUNTER_LEADER_DISPLAY_FORMAT.format(emoji, leader.name),
    fontWeight = FontWeight.ExtraBold
)
```

### ❌ BAD EXAMPLE - Hardcoded Strings
```kotlin
// ❌ DON'T DO THIS
Text("Delete")  // Should be AppStrings.ACTION_DELETE
Text("Players (${selectedPlayers.size}/$maxPlayers)")  // Should use format string
Text("Are you sure?")  // Should be in AppStrings
```

---

## 🌍 i18n Considerations

All strings in AppStrings.kt are designed to be easily translatable:

### Prepare for Translation
```kotlin
// ✅ GOOD - Translator-friendly
const val ERROR_PLAYER_COUNT_RANGE = "Player count must be between %d and %d"
// Clear, descriptive, with proper format placeholders

// ❌ BAD - Translator nightmare
const val ERR_PC = "PC between %d-%d"
// Vague abbreviations, unclear context
```

### Translation Workflow
1. All strings in AppStrings.kt
2. Export to translation tool (future)
3. Translators work with centralized strings
4. Swap AppStrings with translated version
5. App automatically in new language

---

## 📞 Questions & Help

### What if I'm unsure about naming?
1. Look for similar existing strings
2. Follow the established pattern
3. Ask in code review if uncertain
4. Keep it consistent with related strings

### What if I need special formatting?
1. Use standard format placeholders: `%s` (string), `%d` (int), `%f` (float)
2. Use `.format(args)` method: `"Hello %s".format(name)`
3. For complex formatting, create specific format constants

### What if it's repeated across multiple screens?
1. Use a generic constant: `ACTION_DELETE` (not `COUNTER_DELETE`, `YAHTZEE_DELETE`)
2. Place in "Common Actions" section
3. Reuse across all screens

---

## 🎓 Summary

| Rule | Details |
|------|---------|
| **ALL strings in AppStrings.kt** | No hardcoded text in UI code |
| **Use naming convention** | `[SCREEN]_[TYPE]_[DESCRIPTION]` |
| **Format strings for flexibility** | Use `%s`, `%d`, with `.format()` |
| **Group related strings** | Keep related content together |
| **Test after changes** | Run build to verify |
| **Think about i18n** | Clear, descriptive, translatable text |
| **Use existing patterns** | Consistency matters |
| **Add comments** | Document non-obvious strings |

---

## 🔗 Related Files

- **String Repository:** `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/strings/AppStrings.kt`
- **This Guide:** `docs/STRING_GUIDELINES.md`
- **Current Stats:** 281 constants, 100% centralization, 382 lines

---

## 📝 Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2026-01-16 | Initial guidelines after 100% string centralization |

---

**Last Updated:** January 16, 2026  
**Status:** Complete & Enforced  
**Coverage:** 100% of user-facing strings
