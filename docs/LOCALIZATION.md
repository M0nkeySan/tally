# 🌍 Tally Localization Guide

> Comprehensive guide to internationalization (i18n) implementation in Tally

---

## 📖 Table of Contents

1. [Overview](#-overview)
2. [Architecture](#-architecture)
3. [File Organization](#-file-organization)
4. [Naming Conventions](#-naming-conventions)
5. [Adding New Translations](#-adding-new-translations)
6. [Technical Implementation](#-technical-implementation)
7. [Current Status](#-current-status)
8. [Examples](#-examples)
9. [Troubleshooting](#-troubleshooting)

---

## 🌍 Overview

Tally implements a comprehensive internationalization (i18n) system that allows users to switch
between languages at runtime without restarting the app.

### Supported Languages

- 🇬🇧 **English** (`en`) - Default
- 🇫🇷 **French** (`fr`) - Français

### Key Features

- ✅ **Runtime language switching** - Change language without app restart
- ✅ **System locale detection** - Automatically detects device language on first launch
- ✅ **Compose Resources integration** - XML-based resource system
- ✅ **Persistent preference** - Language choice saved across sessions
- ✅ **Complete coverage** - 321+ localized strings, 389+ usages across codebase

### Technology Stack

- **Compose Multiplatform Resources** - XML-based string resources
- **Kotlin StateFlow** - Reactive locale state management
- **Java Locale API** - System-level locale updates
- **SQLDelight Database** - Persistent user preferences

---

## 🏗️ Architecture

### Core Components

#### **1. LocaleManager** (Singleton)

Located: `ui/strings/LocaleManager.kt`

Manages the application's current locale and persists user language preferences.

```kotlin
LocaleManager.instance.setLocale("fr")  // Switch to French
val currentLocale = LocaleManager.instance.currentLocale.collectAsState()
```

**Responsibilities:**

- Exposes `currentLocale: StateFlow<String>` for UI observation
- Updates `Locale.setDefault()` to affect `stringResource()`
- Saves language preference via `UserPreferencesRepository`
- Detects system locale on first launch

#### **2. AppLocale** (Enum)

Located: `core/domain/model/AppLocale.kt`

Defines supported languages and their metadata.

```kotlin
enum class AppLocale(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    FRENCH("fr", "Français")
}
```

#### **3. Compose Resources**

Located: `composeApp/src/commonMain/composeResources/`

Standard Android-style XML resource files organized by locale:

- `values/` - English (default)
- `values-fr/` - French translations

#### **4. UserPreferencesRepository**

Persists the user's language choice using SQLDelight database, ensuring the preference survives app
restarts.

On Web, the database is stored in the Origin Private File System (OPFS) for persistence.

### Language Switching Flow

```
┌─────────────────────────────────────────────────────────────┐
│ User selects language in Settings                           │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│ LocaleManager.setLocale("fr")                               │
│   1. applySystemLocale("fr") → Locale.setDefault(French)    │
│   2. _currentLocale.value = "fr"                            │
│   3. userPreferencesRepository.saveLocale(FRENCH)           │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│ StateFlow emits new locale → UI observes                    │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│ key(currentLocale) forces full recomposition                │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│ stringResource() reads from values-fr/ instead of values/   │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 File Organization

### Directory Structure

```
composeApp/src/commonMain/composeResources/
├── drawable/
│   ├── compose-multiplatform.xml
│   ├── tarot.xml
│   └── yahtzee.xml
├── values/                          # 🇬🇧 English (default)
│   ├── strings.xml                  # Common/shared strings
│   ├── strings_counter.xml          # Counter game
│   ├── strings_dice.xml             # Dice roller
│   ├── strings_errors.xml           # Error messages
│   ├── strings_finger.xml           # Finger selector
│   ├── strings_game.xml             # Game selection
│   ├── strings_home.xml             # Home screen
│   ├── strings_player.xml           # Player management
│   ├── strings_results.xml          # Results screen
│   ├── strings_settings.xml         # Settings
│   ├── strings_tarot.xml            # Tarot game
│   └── strings_yahtzee.xml          # Yahtzee game
└── values-fr/                       # 🇫🇷 French
    ├── strings.xml
    ├── strings_counter.xml
    ├── strings_dice.xml
    ├── strings_errors.xml
    ├── strings_finger.xml
    ├── strings_game.xml
    ├── strings_home.xml
    ├── strings_player.xml
    ├── strings_results.xml
    ├── strings_settings.xml
    ├── strings_tarot.xml
    └── strings_yahtzee.xml
```

### File Separation Strategy

#### **Why Split Into Multiple Files?**

Originally, all strings were in a single `strings.xml` file. This was refactored into 12
component-specific files for:

1. **Better Organization** - Each feature has its own string file
2. **Easier Maintenance** - Find strings quickly by feature name
3. **Parallel Development** - Multiple developers can work on different features without merge
   conflicts
4. **Logical Grouping** - Related strings stay together

#### **File Naming Convention**

```
strings_[feature_name].xml
```

- Use **underscore** separator (not hyphen or camel case)
- Use **lowercase** only
- Match the **feature or screen name**

**Examples:**

- ✅ `strings_yahtzee.xml`
- ✅ `strings_player.xml`
- ✅ `strings_settings.xml`
- ❌ `strings-yahtzee.xml` (wrong separator)
- ❌ `stringsYahtzee.xml` (camel case)
- ❌ `Strings_Yahtzee.xml` (capitals)

### File Descriptions

| File                     | Strings | Purpose                                                                                                                                      |
|--------------------------|---------|----------------------------------------------------------------------------------------------------------------------------------------------|
| **strings.xml**          | ~45     | Common actions (`action_save`, `action_cancel`), states (`state_loading`), accessibility labels (`cd_back`, `cd_settings`), dialogs, plurals |
| **strings_yahtzee.xml**  | ~100    | Yahtzee game - scoring, statistics, categories, labels (largest file)                                                                        |
| **strings_tarot.xml**    | ~54     | Tarot game - scoring, statistics, bids, rounds, announces                                                                                    |
| **strings_counter.xml**  | ~42     | Counter game - screens, dialogs, labels                                                                                                      |
| **strings_player.xml**   | ~27     | Player management - dialogs, fields, validation                                                                                              |
| **strings_settings.xml** | ~12     | Settings screen - theme, language, sections                                                                                                  |
| **strings_home.xml**     | ~8      | Home screen - titles, descriptions                                                                                                           |
| **strings_game.xml**     | ~6      | Game selection - game names, descriptions                                                                                                    |
| **strings_results.xml**  | ~6      | Results screen - winner, ranking                                                                                                             |
| **strings_dice.xml**     | ~5      | Dice roller - labels, buttons                                                                                                                |
| **strings_finger.xml**   | ~4      | Finger selector - instructions, labels                                                                                                       |
| **strings_errors.xml**   | ~3      | Error messages - validation, generic errors                                                                                                  |

---

## 🏷️ Naming Conventions

### String Resource Naming Pattern

```
[prefix]_[category]_[specific_name]
```

**Example:** `yahtzee_stats_average_score`

- **Prefix:** `yahtzee` (feature)
- **Category:** `stats` (statistics screen)
- **Name:** `average_score` (what it represents)

### Prefix Guidelines

| Prefix      | Usage                                | Examples                                                   |
|-------------|--------------------------------------|------------------------------------------------------------|
| `action_`   | User actions (buttons, menu items)   | `action_save`, `action_cancel`, `action_delete`            |
| `state_`    | Loading/status states                | `state_loading`, `state_loading_games`                     |
| `cd_`       | Content descriptions (accessibility) | `cd_back`, `cd_settings`, `cd_add_player`                  |
| `dialog_`   | Dialog titles and messages           | `dialog_delete_all_title`, `dialog_delete_counter_message` |
| `yahtzee_`  | Yahtzee game strings                 | `yahtzee_scoring_total_format`, `yahtzee_category_aces`    |
| `tarot_`    | Tarot game strings                   | `tarot_stats_label_win_rate`, `tarot_round_label_taker`    |
| `counter_`  | Counter game strings                 | `counter_screen_title`, `counter_label_value`              |
| `dice_`     | Dice roller strings                  | `dice_title`, `dice_label_sides`                           |
| `finger_`   | Finger selector strings              | `finger_title`, `finger_instruction`                       |
| `home_`     | Home screen strings                  | `home_title`, `home_desc_finger_selector`                  |
| `player_`   | Player management strings            | `player_dialog_title_edit`, `player_field_name`            |
| `settings_` | Settings screen strings              | `settings_title`, `settings_label_theme`                   |
| `game_`     | Game selection strings               | `game_tarot`, `game_yahtzee`                               |
| `results_`  | Results screen strings               | `results_title`, `results_winner`                          |
| `error_`    | Error messages                       | `error_empty_player_name`, `error_generic`                 |

### Category Suffixes

| Suffix         | Meaning                             | Examples                                          |
|----------------|-------------------------------------|---------------------------------------------------|
| `_title`       | Screen/section titles               | `yahtzee_stats_title`, `settings_title`           |
| `_label`       | Field/option labels                 | `tarot_round_label_taker`, `settings_label_theme` |
| `_desc`        | Descriptions                        | `home_desc_finger_selector`, `desc_yahtzee`       |
| `_cd`          | Content description (accessibility) | `yahtzee_scoring_cd_next`, `cd_back`              |
| `_section`     | Section headers                     | `yahtzee_section_upper`, `yahtzee_section_lower`  |
| `_format`      | Format strings with placeholders    | `yahtzee_scoring_total_format`                    |
| `_placeholder` | Input field placeholders            | `yahtzee_placeholder_dice_sum`                    |
| `_empty`       | Empty state messages                | `tarot_scoring_empty_rounds`                      |
| `_error`       | Error messages                      | `yahtzee_error_score_too_high`                    |
| `_action`      | Action buttons                      | `tarot_round_action_save`                         |

### Format String Placeholders

Use standard Java format specifiers for dynamic content:

| Placeholder    | Type                | Example Usage                              |
|----------------|---------------------|--------------------------------------------|
| `%d`           | Integer             | `"Total: %d"` → `"Total: 150"`             |
| `%s`           | String              | `"Hello, %s"` → `"Hello, John"`            |
| `%1$d`, `%2$d` | Positional integer  | `"%1$d/%2$d as taker"` → `"3/5 as taker"`  |
| `%1$s`, `%2$s` | Positional string   | `"%1$s beats %2$s"` → `"Alice beats Bob"`  |
| `%.1f`         | Float (1 decimal)   | `"Win Rate: %.1f%%"` → `"Win Rate: 75.5%"` |
| `%.0f`         | Float (no decimals) | `"Score: %.0f"` → `"Score: 123"`           |

**Important:** When translating format strings, keep placeholders in the same order or use
positional arguments!

### Complete Naming Examples

```xml
<!-- Actions -->
<string name="action_save">Save</string><string name="action_cancel">Cancel</string><string
name="action_delete">Delete
</string>

    <!-- States -->
<string name="state_loading">Loading...</string><string name="state_loading_games">Loading
games...
</string>

    <!-- Content Descriptions -->
<string name="cd_back">Back</string><string name="cd_settings">Settings</string><string
name="cd_add_player">Add Player
</string>

    <!-- Yahtzee Feature -->
<string name="yahtzee_stats_title">Statistics</string><string name="yahtzee_stats_average_score">
Average Score
</string><string name="yahtzee_scoring_total_format">Total: %d</string><string
name="yahtzee_category_full_house">Full House
</string>

    <!-- Tarot Feature -->
<string name="tarot_stats_label_win_rate">Win Rate</string><string name="tarot_round_label_taker">
TAKER
</string><string name="tarot_stats_format_as_taker">%1$d/%2$d as taker</string>

    <!-- Settings -->
<string name="settings_title">Settings</string><string name="settings_label_theme">Theme
</string><string name="settings_option_theme_light">Light</string>
```

---

## ➕ Adding New Translations

### Adding a New String (Step-by-Step)

#### **Step 1: Decide Which File**

Choose the appropriate string file based on the feature:

- Common actions/states? → `strings.xml`
- Yahtzee-specific? → `strings_yahtzee.xml`
- Tarot-specific? → `strings_tarot.xml`
- New feature? → Create `strings_newfeature.xml`

#### **Step 2: Add to English File**

```xml
<!-- In composeResources/values/strings_yahtzee.xml -->
<resources>
    <!-- Add your new string -->
    <string name="yahtzee_new_feature_title">New Feature</string>
    <string name="yahtzee_new_feature_description">This is a new feature description</string>
</resources>
```

#### **Step 3: Add French Translation**

```xml
<!-- In composeResources/values-fr/strings_yahtzee.xml -->
<resources>
    <!-- Translate the string -->
    <string name="yahtzee_new_feature_title">Nouvelle fonctionnalité</string>
    <string name="yahtzee_new_feature_description">Ceci est une description de la nouvelle
        fonctionnalité
    </string>
</resources>
```

⚠️ **Important:** Keep the resource **name** identical in both files! Only translate the **text
content**.

#### **Step 4: Import in Kotlin File**

```kotlin
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.yahtzee_new_feature_title
import io.github.m0nkeysan.tally.generated.resources.yahtzee_new_feature_description
import org.jetbrains.compose.resources.stringResource
```

⚠️ **Critical:** Each string resource requires its **own individual import line**!

```kotlin
// ✅ CORRECT - Individual imports
import io.github.m0nkeysan.tally.generated.resources.yahtzee_new_feature_title
import io.github.m0nkeysan.tally.generated.resources.yahtzee_new_feature_description

// ❌ WRONG - Cannot import multiple at once
import io.github.m0nkeysan.tally.generated.resources.{ yahtzee_new_feature_title, yahtzee_new_feature_description }
```

#### **Step 5: Use in Composable**

```kotlin
@Composable
fun NewFeatureScreen() {
    Column {
        Text(
            text = stringResource(Res.string.yahtzee_new_feature_title),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = stringResource(Res.string.yahtzee_new_feature_description),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
```

#### **Step 6: Build and Test**

```bash
# Rebuild to generate resource classes
./gradlew generateComposeResClass

# Install and test
./gradlew installDebug
```

Test both languages:

1. Open app → Go to Settings
2. Change language to French
3. Navigate to your new feature
4. Verify text appears in French

### Adding a New Language

#### **Step 1: Create Language Directory**

```bash
# For Spanish (es)
mkdir composeApp/src/commonMain/composeResources/values-es

# For German (de)
mkdir composeApp/src/commonMain/composeResources/values-de
```

The directory name follows Android conventions: `values-[language-code]`

**Common language codes:**

- `es` - Spanish (Español)
- `de` - German (Deutsch)
- `it` - Italian (Italiano)
- `pt` - Portuguese (Português)
- `ja` - Japanese (日本語)
- `zh` - Chinese (中文)

#### **Step 2: Copy All String Files**

```bash
# Copy all English files to the new language directory
cp composeApp/src/commonMain/composeResources/values/strings*.xml \
   composeApp/src/commonMain/composeResources/values-es/
```

#### **Step 3: Translate All Strings**

Open each file in `values-es/` and translate:

```xml
<!-- Before (English) -->
<string name="action_save">Save</string><string name="yahtzee_stats_title">Statistics</string>

    <!-- After (Spanish) -->
<string name="action_save">Guardar</string><string name="yahtzee_stats_title">Estadísticas</string>
```

⚠️ **Important Rules:**

1. Keep resource **names** unchanged (e.g., `action_save` stays `action_save`)
2. Only translate the **text content**
3. Preserve **format placeholders** (e.g., `%d`, `%s`)
4. Keep **XML comments** in English for consistency

**Format string example:**

```xml
<!-- English -->
<string name="tarot_stats_format_as_taker">%1$d/%2$d as taker</string>

    <!-- Spanish - Keep placeholders! -->
<string name="tarot_stats_format_as_taker">%1$d/%2$d como tomador</string>
```

#### **Step 4: Update AppLocale Enum**

```kotlin
// In core/domain/model/AppLocale.kt
enum class AppLocale(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    FRENCH("fr", "Français"),
    SPANISH("es", "Español")  // Add new language
}
```

#### **Step 5: Update LocaleManager**

```kotlin
// In ui/strings/LocaleManager.kt
private fun getDefaultLocale(): String {
    val supportedLanguages = listOf("en", "fr", "es")  // Add language code
    val systemLang = getSystemLocaleCode()
    return if (systemLang in supportedLanguages) systemLang else "en"
}
```

#### **Step 6: Update Settings Screen**

If your settings screen has hardcoded language options, update it:

```kotlin
// In SettingsScreen.kt or wherever language selection is implemented
val languages = listOf(
    AppLocale.ENGLISH,
    AppLocale.FRENCH,
    AppLocale.SPANISH  // Add new language
)
```

#### **Step 7: Build and Test**

```bash
./gradlew clean
./gradlew installDebug
```

Test the new language:

1. Set device system language to Spanish (or your new language)
2. Launch app for first time → should auto-detect Spanish
3. Or manually change language in Settings

---

## 🔧 Technical Implementation

### How Language Switching Works

#### **1. User Changes Language**

In the Settings screen, user selects a new language:

```kotlin
// SettingsScreen.kt
LanguageSelector(
    currentLanguage = currentLanguage,
    onLanguageChange = { newLanguage ->
        LocaleManager.instance.setLocale(newLanguage.code)
    }
)
```

#### **2. LocaleManager Updates System Locale**

```kotlin
// LocaleManager.kt
fun setLocale(languageCode: String) {
    applySystemLocale(languageCode)  // Updates Java Locale.setDefault()
    _currentLocale.value = languageCode  // Updates StateFlow
    scope.launch {
        userPreferencesRepository.saveLocale(AppLocale.fromCode(languageCode))  // Persists choice
    }
}

private fun applySystemLocale(languageCode: String) {
    try {
        val parts = languageCode.split("_", "-")
        val localeBuilder = Locale.Builder().setLanguage(parts[0])
        
        if (parts.size >= 2 && parts[1].isNotEmpty()) {
            localeBuilder.setRegion(parts[1])
        }
        if (parts.size >= 3 && parts[2].isNotEmpty()) {
            localeBuilder.setVariant(parts[2])
        }
        
        val locale = localeBuilder.build()
        Locale.setDefault(locale)  // Critical: Updates JVM default locale
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
```

**Why `Locale.Builder`?**

- Modern API (replaces deprecated `Locale` constructors)
- More explicit and type-safe than constructor overloading
- Allows setting language, region, and variant separately
- Recommended approach in Java 7+ for BCP 47 compliance

**Why `Locale.setDefault()`?**

- Compose Resources' `stringResource()` checks `Locale.getDefault()`
- This determines which `values-[locale]/` directory to read from
- Without this, language switching won't work

#### **3. UI Observes Locale Change**

```kotlin
// In App.kt or root composable
@Composable
fun TallyApp() {
    val currentLocale by LocaleManager.instance.currentLocale.collectAsState()

    // Force full recomposition when locale changes
    key(currentLocale) {
        NavigationHost()
    }
}
```

**Why `key(currentLocale)`?**

- Forces Compose to treat the entire UI tree as "new" when locale changes
- Without this, some cached strings might not update
- Acts as a "reboot" of the composable hierarchy

#### **4. stringResource() Picks Correct File**

```kotlin
Text(stringResource(Res.string.yahtzee_stats_title))
```

**Under the hood:**

1. `stringResource()` checks `Locale.getDefault()`
2. If locale is `"fr"` → reads from `values-fr/strings_yahtzee.xml`
3. If locale is `"en"` (or unsupported) → reads from `values/strings_yahtzee.xml`
4. Returns the localized string

### Localizing Enums

For enums that need localized display names (like `YahtzeeCategory`), use **extension functions**
instead of hardcoded strings in the enum.

#### **❌ Bad Approach (Hardcoded)**

```kotlin
enum class YahtzeeCategory(val displayName: String) {
    ACES("Ones"),           // ❌ Not localized
    TWOS("Twos"),
    FULL_HOUSE("Full House")
}

// Usage
Text(YahtzeeCategory.ACES.displayName)  // Always "Ones" regardless of language
```

**Problem:** `displayName` is hardcoded and can't change based on language.

#### **✅ Good Approach (Localized)**

**Step 1: Remove hardcoded displayName**

```kotlin
enum class YahtzeeCategory {
    ACES,
    TWOS,
    FULL_HOUSE
    // No displayName property!
}
```

**Step 2: Add string resources**

```xml
<!-- values/strings_yahtzee.xml -->
<string name="yahtzee_category_aces">Ones</string><string name="yahtzee_category_twos">Twos
</string><string name="yahtzee_category_full_house">Full House</string>

    <!-- values-fr/strings_yahtzee.xml -->
<string name="yahtzee_category_aces">Un (1)</string><string name="yahtzee_category_twos">Deux (2)
</string><string name="yahtzee_category_full_house">Full</string>
```

**Step 3: Create @Composable extension function**

```kotlin
// In YahtzeeScore.kt
@Composable
fun YahtzeeCategory.getLocalizedName(): String {
    return stringResource(
        when (this) {
            YahtzeeCategory.ACES -> Res.string.yahtzee_category_aces
            YahtzeeCategory.TWOS -> Res.string.yahtzee_category_twos
            YahtzeeCategory.FULL_HOUSE -> Res.string.yahtzee_category_full_house
            // ... all other categories
        }
    )
}
```

**Step 4: Use in UI**

```kotlin
@Composable
fun CategoryCard(category: YahtzeeCategory) {
    Text(category.getLocalizedName())  // ✅ Returns localized string!
}
```

**Why this works:**

- Extension function is `@Composable` → can call `stringResource()`
- `stringResource()` respects current locale
- When language changes and UI recomposes, `getLocalizedName()` returns new translation

### Format Strings

Use format strings for dynamic content with placeholders.

#### **Simple Placeholder**

```xml

<string name="yahtzee_scoring_total_format">Total: %d</string>
```

```kotlin
val score = 150
Text(stringResource(Res.string.yahtzee_scoring_total_format, score))
// Output: "Total: 150"
```

#### **Multiple Placeholders (Positional)**

```xml

<string name="tarot_stats_format_as_taker">%1$d/%2$d as taker</string>
```

```kotlin
val wins = 3
val total = 5
Text(stringResource(Res.string.tarot_stats_format_as_taker, wins, total))
// Output: "3/5 as taker"
```

**Why positional?**

- In some languages, word order changes
- Positional arguments allow translators to reorder placeholders

**Example (English vs French):**

```xml
<!-- English -->
<string name="game_summary">%1$s won with %2$d points
</string><!-- Output: "Alice won with 150 points" -->

    <!-- French (reversed order) -->
<string name="game_summary">%2$d points pour la victoire de %1$s
</string><!-- Output: "150 points pour la victoire de Alice" -->
```

#### **Using .format() Method (Alternative)**

```kotlin
// Instead of passing args to stringResource()
val roundNumber = 5
Text(stringResource(Res.string.tarot_stats_label_round, roundNumber))
```

Both approaches work, but passing arguments to `stringResource()` is more common.

### Plurals

Compose Resources supports plurals (different strings for singular/plural).

```xml
<!-- values/strings.xml -->
<plurals name="player_count">
    <item quantity="one">%d player</item>
    <item quantity="other">%d players</item>
</plurals>

    <!-- values-fr/strings.xml -->
<plurals name="player_count">
<item quantity="one">%d joueur</item>
<item quantity="other">%d joueurs</item>
</plurals>
```

```kotlin
val count = 3
Text(pluralStringResource(Res.plurals.player_count, count, count))
// English: "3 players"
// French: "3 joueurs"
```

---

## 📊 Current Status

### Implementation Statistics

- ✅ **Total String Resources:** 321+
- ✅ **String Files:** 12 component-specific files
- ✅ **Translations:** English (100%), French (100%)
- ✅ **stringResource() Usages:** 389+ across codebase
- ✅ **Resource Lines:** 426 lines of XML

### Localized Features

All major features are fully localized:

| Feature              | Status     | File                   | Strings |
|----------------------|------------|------------------------|---------|
| 🏠 Home Screen       | ✅ Complete | `strings_home.xml`     | ~8      |
| ⚙️ Settings          | ✅ Complete | `strings_settings.xml` | ~12     |
| 👥 Player Management | ✅ Complete | `strings_player.xml`   | ~27     |
| 🎲 Yahtzee Game      | ✅ Complete | `strings_yahtzee.xml`  | ~100    |
| 🃏 Tarot Game        | ✅ Complete | `strings_tarot.xml`    | ~54     |
| 🔢 Counter           | ✅ Complete | `strings_counter.xml`  | ~42     |
| 🎲 Dice Roller       | ✅ Complete | `strings_dice.xml`     | ~5      |
| ☝️ Finger Selector   | ✅ Complete | `strings_finger.xml`   | ~4      |
| 🏆 Results Screen    | ✅ Complete | `strings_results.xml`  | ~6      |
| ❌ Error Messages     | ✅ Complete | `strings_errors.xml`   | ~3      |
| 🎮 Game Selection    | ✅ Complete | `strings_game.xml`     | ~6      |
| 🌐 Common Strings    | ✅ Complete | `strings.xml`          | ~45     |

### Translation Coverage

| Language     | Coverage | Notes                  |
|--------------|----------|------------------------|
| 🇬🇧 English | 100%     | Default language       |
| 🇫🇷 French  | 100%     | All strings translated |

### String File Statistics

| File                   | Approx. Lines | Approx. Strings | Complexity                        |
|------------------------|---------------|-----------------|-----------------------------------|
| `strings_yahtzee.xml`  | ~160          | ~100            | High (categories, stats, scoring) |
| `strings_tarot.xml`    | ~70           | ~54             | Medium (bids, scoring, stats)     |
| `strings_counter.xml`  | ~60           | ~42             | Medium (screens, dialogs)         |
| `strings.xml`          | ~56           | ~45             | Medium (common strings, plurals)  |
| `strings_player.xml`   | ~40           | ~27             | Low (dialogs, fields)             |
| `strings_settings.xml` | ~18           | ~12             | Low (options, labels)             |
| `strings_home.xml`     | ~13           | ~8              | Low (titles, descriptions)        |
| `strings_game.xml`     | ~11           | ~6              | Low (game names)                  |
| `strings_results.xml`  | ~10           | ~6              | Low (winner, ranking)             |
| `strings_dice.xml`     | ~9            | ~5              | Low (labels, buttons)             |
| `strings_finger.xml`   | ~8            | ~4              | Low (instructions)                |
| `strings_errors.xml`   | ~6            | ~3              | Low (error messages)              |

---

## 💡 Examples

### Example 1: Simple Text Translation

#### Before (Hardcoded)

```kotlin
Text("Round Breakdown")
```

#### After (Localized)

**Step 1: Add strings**

```xml
<!-- values/strings_tarot.xml -->
<string name="tarot_stats_section_round_breakdown">Round Breakdown</string>

    <!-- values-fr/strings_tarot.xml -->
<string name="tarot_stats_section_round_breakdown">Détail des manches</string>
```

**Step 2: Import and use**

```kotlin
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_section_round_breakdown
import org.jetbrains.compose.resources.stringResource

@Composable
fun TarotStatsScreen() {
    Text(stringResource(Res.string.tarot_stats_section_round_breakdown))
}
```

**Result:**

- English: "Round Breakdown"
- French: "Détail des manches"

---

### Example 2: Format String with Single Parameter

#### Before

```kotlin
Text("Round ${round.roundNumber}")
```

#### After

**Step 1: Add strings**

```xml
<!-- values/strings_tarot.xml -->
<string name="tarot_stats_label_round">Round %d</string>

    <!-- values-fr/strings_tarot.xml -->
<string name="tarot_stats_label_round">Manche %d</string>
```

**Step 2: Import and use**

```kotlin
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_label_round

@Composable
fun RoundHeader(round: TarotRound) {
    Text(stringResource(Res.string.tarot_stats_label_round, round.roundNumber))
}
```

**Result:**

- English: "Round 3"
- French: "Manche 3"

---

### Example 3: Format String with Multiple Parameters

#### Before

```kotlin
Text("${ranking.roundsWonAsTaker}/${ranking.roundsPlayedAsTaker} as taker")
```

#### After

**Step 1: Add strings**

```xml
<!-- values/strings_tarot.xml -->
<string name="tarot_stats_format_as_taker">%1$d/%2$d as taker</string>

    <!-- values-fr/strings_tarot.xml -->
<string name="tarot_stats_format_as_taker">%1$d/%2$d en preneur</string>
```

**Step 2: Import and use**

```kotlin
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_format_as_taker

@Composable
fun PlayerRanking(ranking: PlayerRanking) {
    Text(
        stringResource(
            Res.string.tarot_stats_format_as_taker,
            ranking.roundsWonAsTaker,
            ranking.roundsPlayedAsTaker
        )
    )
}
```

**Result:**

- English: "3/5 as taker"
- French: "3/5 en preneur"

---

### Example 4: Conditional Text

#### Before

```kotlin
Text(if (round.contractWon) "✓ Won" else "✗ Lost")
```

#### After

**Step 1: Add strings**

```xml
<!-- values/strings_tarot.xml -->
<string name="tarot_stats_contract_won">✓ Won</string><string name="tarot_stats_contract_lost">✗
Lost
</string>

    <!-- values-fr/strings_tarot.xml -->
<string name="tarot_stats_contract_won">✓ Gagné</string><string name="tarot_stats_contract_lost">✗
Perdu
</string>
```

**Step 2: Import and use**

```kotlin
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_contract_won
import io.github.m0nkeysan.tally.generated.resources.tarot_stats_contract_lost

@Composable
fun RoundResult(round: TarotRound) {
    Text(
        text = if (round.contractWon)
            stringResource(Res.string.tarot_stats_contract_won)
        else
            stringResource(Res.string.tarot_stats_contract_lost),
        color = if (round.contractWon)
            MaterialTheme.colorScheme.secondary
        else
            MaterialTheme.colorScheme.error
    )
}
```

**Result:**

- English: "✓ Won" or "✗ Lost"
- French: "✓ Gagné" or "✗ Perdu"

---

### Example 5: Localizing Enum Display Names

This is a complete example of how Yahtzee categories were localized.

#### Step 1: Remove Hardcoded displayName

**Before:**

```kotlin
enum class YahtzeeCategory(val displayName: String) {
    ACES("Ones"),
    TWOS("Twos"),
    THREES("Threes"),
    FOURS("Fours"),
    FIVES("Fives"),
    SIXES("Sixes"),
    CHANCE("Chance"),
    THREE_OF_KIND("Three of a Kind"),
    FOUR_OF_KIND("Four of a Kind"),
    FULL_HOUSE("Full House"),
    SMALL_STRAIGHT("Small Straight"),
    LARGE_STRAIGHT("Large Straight"),
    YAHTZEE("Yahtzee")
}
```

**After:**

```kotlin
enum class YahtzeeCategory {
    ACES,
    TWOS,
    THREES,
    FOURS,
    FIVES,
    SIXES,
    CHANCE,
    THREE_OF_KIND,
    FOUR_OF_KIND,
    FULL_HOUSE,
    SMALL_STRAIGHT,
    LARGE_STRAIGHT,
    YAHTZEE
}
```

#### Step 2: Add String Resources

```xml
<!-- values/strings_yahtzee.xml -->
<string name="yahtzee_category_aces">Ones</string><string name="yahtzee_category_twos">Twos
</string><string name="yahtzee_category_threes">Threes</string><string
name="yahtzee_category_fours">Fours
</string><string name="yahtzee_category_fives">Fives</string><string name="yahtzee_category_sixes">
Sixes
</string><string name="yahtzee_category_chance">Chance (total of 5 dices)</string><string
name="yahtzee_category_three_of_kind">Three of a Kind
</string><string name="yahtzee_category_four_of_kind">Four of a Kind</string><string
name="yahtzee_category_full_house">Full House
</string><string name="yahtzee_category_small_straight">Small Straight</string><string
name="yahtzee_category_large_straight">Large Straight
</string><string name="yahtzee_category_yahtzee">Yahtzee</string>

    <!-- values-fr/strings_yahtzee.xml -->
<string name="yahtzee_category_aces">Un (1)</string><string name="yahtzee_category_twos">Deux (2)
</string><string name="yahtzee_category_threes">Trois (3)</string><string
name="yahtzee_category_fours">Quatre (4)
</string><string name="yahtzee_category_fives">Cinq (5)</string><string
name="yahtzee_category_sixes">Six (6)
</string><string name="yahtzee_category_chance">Chance (total des 5 dés)</string><string
name="yahtzee_category_three_of_kind">Brelan
</string><string name="yahtzee_category_four_of_kind">Carré</string><string
name="yahtzee_category_full_house">Full
</string><string name="yahtzee_category_small_straight">Petite suite</string><string
name="yahtzee_category_large_straight">Grande suite
</string><string name="yahtzee_category_yahtzee">Yahtzee</string>
```

#### Step 3: Create Extension Function

```kotlin
// In YahtzeeScore.kt
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.yahtzee_category_aces
import io.github.m0nkeysan.tally.generated.resources.yahtzee_category_twos
// ... import all category strings

@Composable
fun YahtzeeCategory.getLocalizedName(): String {
    return stringResource(
        when (this) {
            YahtzeeCategory.ACES -> Res.string.yahtzee_category_aces
            YahtzeeCategory.TWOS -> Res.string.yahtzee_category_twos
            YahtzeeCategory.THREES -> Res.string.yahtzee_category_threes
            YahtzeeCategory.FOURS -> Res.string.yahtzee_category_fours
            YahtzeeCategory.FIVES -> Res.string.yahtzee_category_fives
            YahtzeeCategory.SIXES -> Res.string.yahtzee_category_sixes
            YahtzeeCategory.CHANCE -> Res.string.yahtzee_category_chance
            YahtzeeCategory.THREE_OF_KIND -> Res.string.yahtzee_category_three_of_kind
            YahtzeeCategory.FOUR_OF_KIND -> Res.string.yahtzee_category_four_of_kind
            YahtzeeCategory.FULL_HOUSE -> Res.string.yahtzee_category_full_house
            YahtzeeCategory.SMALL_STRAIGHT -> Res.string.yahtzee_category_small_straight
            YahtzeeCategory.LARGE_STRAIGHT -> Res.string.yahtzee_category_large_straight
            YahtzeeCategory.YAHTZEE -> Res.string.yahtzee_category_yahtzee
        }
    )
}
```

#### Step 4: Update UI Code

**Before:**

```kotlin
Text(category.displayName)  // Hardcoded
```

**After:**

```kotlin
Text(category.getLocalizedName())  // Localized!
```

**Result:**

- English: "Full House", "Three of a Kind"
- French: "Full", "Brelan"

---

### Example 6: Content Descriptions for Accessibility

```xml
<!-- values/strings.xml -->
<string name="cd_back">Back</string><string name="cd_settings">Settings</string><string
name="cd_add_player">Add Player
</string>

    <!-- values-fr/strings.xml -->
<string name="cd_back">Retour</string><string name="cd_settings">Paramètres</string><string
name="cd_add_player">Ajouter un joueur
</string>
```

```kotlin
import io.github.m0nkeysan.tally.generated.resources.cd_back
import io.github.m0nkeysan.tally.generated.resources.cd_settings

@Composable
fun TopBar(onBackClick: () -> Unit, onSettingsClick: () -> Unit) {
    TopAppBar(
        title = { Text("Game") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(Res.string.cd_back)
                )
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(Res.string.cd_settings)
                )
            }
        }
    )
}
```

---

## ❓ Troubleshooting

### Problem: Language Not Switching

**Symptoms:** Changed language in settings but UI still shows old language

**Possible Causes & Solutions:**

#### **1. Missing `key(currentLocale)`**

The UI needs to recompose when locale changes.

**Check:**

```kotlin
// In App.kt or root composable
@Composable
fun TallyApp() {
    val currentLocale by LocaleManager.instance.currentLocale.collectAsState()

    // ✅ This should exist
    key(currentLocale) {
        YourContent()
    }
}
```

**If missing, add it:**

```kotlin
key(currentLocale) {
    NavigationHost()
}
```

#### **2. Locale.setDefault() Not Called**

Verify `LocaleManager.applySystemLocale()` is working.

**Add debug logging:**

```kotlin
private fun applySystemLocale(languageCode: String) {
    try {
        val parts = languageCode.split("_", "-")
        val locale = when (parts.size) {
            1 -> Locale(parts[0])
            2 -> Locale(parts[0], parts[1])
            else -> Locale(parts[0], parts[1], parts[2])
        }
        Locale.setDefault(locale)
        println("✅ Locale set to: ${Locale.getDefault()}")  // Add this
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
```

#### **3. Build Cache Issue**

Sometimes Compose Resources don't update properly.

**Solution:**

```bash
./gradlew clean
./gradlew generateComposeResClass
./gradlew installDebug
```

---

### Problem: Missing Translation / Fallback to English

**Symptoms:** Some text appears in English even when French is selected

**Possible Causes & Solutions:**

#### **1. Missing String in values-fr/**

**Check:** Open `values-fr/strings_[feature].xml` and verify the string exists.

**Solution:** Add the missing translation:

```xml
<!-- values-fr/strings_yahtzee.xml -->
<string name="yahtzee_new_feature">Nouvelle fonctionnalité</string>
```

#### **2. Typo in Resource Name**

Resource names must match **exactly** between English and French files.

**Example of error:**

```xml
<!-- values/strings.xml -->
<string name="action_save">Save</string>

    <!-- values-fr/strings.xml -->
<string name="action_saev">Enregistrer</string>  ❌ Typo!
```

**Fix:**

```xml
<!-- values-fr/strings.xml -->
<string name="action_save">Enregistrer</string>  ✅ Correct
```

#### **3. String in Wrong File**

If you added a string to `values/strings_yahtzee.xml` but the French translation is in
`values-fr/strings_tarot.xml`, it won't work.

**Solution:** Ensure both files have the same name and structure.

---

### Problem: Compile Error - "Unresolved reference"

**Symptoms:**

```
Unresolved reference: yahtzee_stats_title
```

**Possible Causes & Solutions:**

#### **1. Missing Import**

**Solution:** Add the import:

```kotlin
import io.github.m0nkeysan.tally.generated.resources.yahtzee_stats_title
```

Remember: **Each string resource needs its own import line!**

#### **2. Resource Doesn't Exist in XML**

**Check:** Open the XML file and verify:

```xml

<string name="yahtzee_stats_title">Statistics</string>
```

**If missing, add it.**

#### **3. Build Hasn't Generated Resource Class**

**Solution:** Rebuild:

```bash
./gradlew generateComposeResClass
```

Or in Android Studio: **Build → Rebuild Project**

#### **4. Typo in Resource Name**

**Example:**

```xml

<string name="yahtzee_stats_title">Statistics</string>
```

```kotlin
// ❌ Wrong
stringResource(Res.string.yahtzee_stat_title)  // Missing 's'

// ✅ Correct
stringResource(Res.string.yahtzee_stats_title)
```

---

### Problem: Format String Crashes

**Symptoms:** App crashes with `java.util.MissingFormatArgumentException`

**Cause:** Mismatch between placeholders in string and arguments passed.

#### **Example 1: Missing Arguments**

**XML:**

```xml

<string name="score_format">Score: %1$d / %2$d</string>
```

**Code:**

```kotlin
stringResource(Res.string.score_format, 100)  // ❌ Missing second argument
```

**Fix:**

```kotlin
stringResource(Res.string.score_format, 100, 200)  // ✅ Both arguments
```

#### **Example 2: Wrong Argument Type**

**XML:**

```xml

<string name="score_format">Score: %d</string>
```

**Code:**

```kotlin
stringResource(Res.string.score_format, "100")  // ❌ String instead of Int
```

**Fix:**

```kotlin
stringResource(Res.string.score_format, 100)  // ✅ Int
```

#### **Example 3: Positional Mismatch**

**XML:**

```xml

<string name="player_score">%1$s scored %2$d points</string>
```

**Code:**

```kotlin
stringResource(Res.string.player_score, 150, "Alice")  // ❌ Wrong order
```

**Fix:**

```kotlin
stringResource(Res.string.player_score, "Alice", 150)  // ✅ Correct order
```

---

### Problem: String Truncated / Ellipsis

**Symptoms:** Text cut off with "..." especially in French (longer translations)

**Solutions:**

#### **1. Increase maxLines**

```kotlin
Text(
    text = stringResource(Res.string.long_description),
    maxLines = 3  // Increase from 2
)
```

#### **2. Use Smaller Font**

```kotlin
Text(
    text = stringResource(Res.string.long_description),
    style = MaterialTheme.typography.labelSmall  // Smaller text
)
```

#### **3. Shorten Translation**

Edit the French string to be more concise:

```xml
<!-- Before -->
<string name="home_desc_finger_selector">Sélectionnez aléatoirement un joueur de départ avec
    plusieurs doigts
</string>

    <!-- After (shorter) -->
<string name="home_desc_finger_selector">Sélectionnez un joueur au hasard</string>
```

#### **4. Make Card Taller**

If text is in a card with fixed aspect ratio:

```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(0.8f)  // Change from 1f (square) to 0.8f (taller)
) {
    // Content
}
```

---

### Problem: Plurals Not Working

**Symptoms:** Always shows "1 players" instead of "1 player"

**Cause:** Using wrong function or passing wrong quantity.

**Wrong:**

```kotlin
Text(stringResource(Res.string.player_count, count))  // ❌ Wrong function
```

**Correct:**

```kotlin
Text(pluralStringResource(Res.plurals.player_count, count, count))  // ✅
//                                                      ^      ^
//                                                   quantity  format arg
```

**XML:**

```xml

<plurals name="player_count">
    <item quantity="one">%d player</item>
    <item quantity="other">%d players</item>
</plurals>
```

---

### Problem: Special Characters Breaking XML

**Symptoms:** Build error when adding strings with quotes, ampersands, etc.

**Cause:** XML special characters need escaping.

**Examples:**

| Character | XML Escape | Example                                             |
|-----------|------------|-----------------------------------------------------|
| `"`       | `\"`       | `<string name="quote">He said \"Hello\"</string>`   |
| `'`       | `\'`       | `<string name="apostrophe">Player\'s turn</string>` |
| `&`       | `&amp;`    | `<string name="and">Rock &amp; Roll</string>`       |
| `<`       | `&lt;`     | `<string name="less">Score &lt; 100</string>`       |
| `>`       | `&gt;`     | `<string name="greater">Score &gt; 200</string>`    |

**Example:**

```xml
<!-- ❌ Wrong - will break XML -->
<string name="message">Player's score: 100 & won</string>

    <!-- ✅ Correct - escaped -->
<string name="message">Player\'s score: 100 &amp; won</string>
```

---

## 🎓 Summary

You now have a complete understanding of Tally's localization system:

### ✅ Key Takeaways

1. **12 component-specific string files** organized by feature
2. **Runtime language switching** without app restart
3. **LocaleManager + key(currentLocale)** pattern for reactive updates
4. **Consistent naming conventions** with prefixes and suffixes
5. **Extension functions for enum localization** (no hardcoded displayName)
6. **Format strings with positional arguments** for flexible translations
7. **Each string resource requires individual import** (project convention)

### 📚 Quick Reference

**Add new string:**

1. Add to `values/strings_[feature].xml`
2. Add to `values-fr/strings_[feature].xml`
3. Import in Kotlin: `import ...generated.resources.string_name`
4. Use: `stringResource(Res.string.string_name)`

**Add new language:**

1. Create `values-[code]/` directory
2. Copy all string files
3. Translate content (keep names!)
4. Update `AppLocale` enum
5. Update `LocaleManager` supported languages

**Localize enum:**

1. Remove hardcoded displayName from enum
2. Add string resources for each value
3. Create `@Composable` extension function with `when` expression
4. Use `enum.getLocalizedName()` in UI

### 🚀 Next Steps

- ✅ Current: English + French fully implemented
- 🔮 Future: Add Spanish, German, or other languages using the guide above
- 🔮 Future: Consider adding region-specific variants (en-US, en-GB, fr-CA)

---

**Last Updated:** January 2026  
**Project:** Tally - Kotlin Multiplatform Compose  
**Supported Languages:** English, French
