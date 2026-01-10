# Dark Theme Implementation Plan

## Overview
Implement system preference auto-detecting dark theme with no manual toggle. Nearly black backgrounds (#0F1419) with adjusted accent colors for dark mode visibility.

## Implementation Strategy

### System Detection
- **Auto-detect**: Use `isSystemInDarkTheme()` from androidx.compose.foundation
- **No manual toggle**: Theme follows Android system preference
- **No persistence**: Always reads system setting in real-time
- **Scope**: All screens automatically support both light and dark themes via Material3 ColorScheme

---

## Phase 1: Color System Enhancement

### 1.1 Create `DarkGameColors.kt`
**Location**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/theme/DarkGameColors.kt`

**Purpose**: Define dark theme color palette

**Color Mapping for Dark Theme**:

```
NEUTRALS (Dark):
├─ Surface0 (Background): #0F1419 (nearly black, WCAG AAA compliant)
├─ Surface1 (Cards): #1A1F2E (dark charcoal, ~11% lighter)
├─ Surface2 (Accents): #2D3748 (~20% lighter for hierarchy)
├─ TextPrimary: #F8F9FA (99% white, eye-friendly)
└─ TextSecondary: #A0A8B2 (gray, for less prominent text)

SEMANTIC (Dark - Bright variants):
├─ Error: #EF4444 (bright red for visibility)
├─ Success: #10B981 (emerald green)
├─ Warning: #F59E0B (bright amber)
└─ Info: #3B82F6 (bright blue)

GAME-SPECIFIC (Dark - Brightened for visibility):
├─ TarotPurple: #D8B4FE (light lavender)
├─ YahtzeeCyan: #22D3EE (bright cyan)
└─ CounterColors: 40-50% lighter variants

PRIMARY (Indigo - Dark variants):
├─ Primary: #818CF8 (lighter indigo)
├─ PrimaryLight: #A5B4FC (even lighter)
└─ PrimaryDark: #6366F1 (base)

SECONDARY (Emerald - Dark variants):
├─ Secondary: #10B981 (keep or brighten to #34D399)
└─ SecondaryLight: #D1FAE5 (light variant)

TERTIARY (Amber - Dark variants):
├─ Tertiary: #F59E0B (bright amber)
└─ TertiaryLight: #FEF3C7 (very light)
```

**Contrast Requirements**:
- Text on dark backgrounds: minimum 7:1 ratio (WCAG AAA)
- Accent colors on dark: minimum 4.5:1 ratio (WCAG AA)
- Nearly black background (#0F1419) ensures high contrast with light text

### 1.2 GameColors.kt - No changes needed
- Existing light color palette remains unchanged
- DarkGameColors.kt provides dark variants
- Backward compatibility maintained

---

## Phase 2: Theme Infrastructure

### 2.1 Create `ThemeManager.kt`
**Location**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/theme/ThemeManager.kt`

**Purpose**: Centralize system dark mode detection

**Key Features**:
- Detect system dark mode using `isSystemInDarkTheme()` Compose function
- No persistence needed (always follows system)
- Simple utility functions for theme detection
- Works on both Android and iOS

**Implementation Strategy**:
- Use `isSystemInDarkTheme()` from androidx.compose.foundation
- Return as a simple boolean value
- Can be called from any Composable
- Single responsibility: theme detection only

### 2.2 Create `AppTheme.kt`
**Location**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/ui/theme/AppTheme.kt`

**Purpose**: Centralized Material3 theme composable with light/dark support

**Key Features**:
- Accept `isDarkTheme` parameter
- Build Material3 ColorScheme (both light and dark)
- Apply consistent styling across all screens
- Handle system bar colors compatibility

**Material3 ColorScheme Mappings**:

**Dark Theme**:
```kotlin
darkColorScheme(
    primary = #818CF8 (DarkGameColors.Primary)
    onPrimary = #F8F9FA (DarkGameColors.TextPrimary)
    primaryContainer = #A5B4FC (DarkGameColors.PrimaryLight)
    onPrimaryContainer = #F8F9FA
    
    secondary = #10B981 (DarkGameColors.Secondary)
    onSecondary = #0F1419
    secondaryContainer = #1A3A3A (dark variant)
    onSecondaryContainer = #10B981
    
    tertiary = #F59E0B (DarkGameColors.Warning)
    onTertiary = #0F1419
    tertiaryContainer = #332A1F (dark variant)
    onTertiaryContainer = #F59E0B
    
    error = #EF4444 (DarkGameColors.Error)
    onError = #0F1419
    errorContainer = #5F2C2C (dark variant)
    onErrorContainer = #EF4444
    
    background = #0F1419 (DarkGameColors.Surface0)
    onBackground = #F8F9FA (DarkGameColors.TextPrimary)
    
    surface = #1A1F2E (DarkGameColors.Surface1)
    onSurface = #F8F9FA
    surfaceVariant = #2D3748 (DarkGameColors.Surface2)
    onSurfaceVariant = #A0A8B2 (DarkGameColors.TextSecondary)
    
    outline = #3E4453 (DarkGameColors.Divider)
    outlineVariant = #49454E
    
    scrim = #000000
)
```

**Light Theme**:
```kotlin
lightColorScheme(
    primary = #6366F1 (GameColors.Primary)
    onPrimary = #FFFFFF
    primaryContainer = #E0E7FF (GameColors.PrimaryLight)
    onPrimaryContainer = #6366F1
    
    secondary = #10B981 (GameColors.Secondary)
    onSecondary = #FFFFFF
    secondaryContainer = #D1FAE5 (GameColors.SecondaryLight)
    onSecondaryContainer = #10B981
    
    tertiary = #F59E0B (GameColors.Tertiary)
    onTertiary = #FFFFFF
    tertiaryContainer = #FEF3C7 (GameColors.TertiaryLight)
    onTertiaryContainer = #F59E0B
    
    error = #DC2626 (GameColors.Error)
    onError = #FFFFFF
    errorContainer = #FDEDEB
    onErrorContainer = #DC2626
    
    background = #FFFFFF (GameColors.Surface0)
    onBackground = #111827 (GameColors.TextPrimary)
    
    surface = #FFFFFF
    onSurface = #111827
    surfaceVariant = #F3F4F6 (GameColors.Surface1)
    onSurfaceVariant = #6B7280 (GameColors.TextSecondary)
    
    outline = #E5E7EB (GameColors.Divider)
    outlineVariant = #CAC4D0
    
    scrim = #000000
)
```

---

## Phase 3: App Integration

### 3.1 Update `App.kt`
**Location**: `composeApp/src/commonMain/kotlin/io/github/m0nkeysan/gamekeeper/App.kt`

**Current Code**:
```kotlin
@Composable
fun App() {
    MaterialTheme {
        GameNavGraph()
    }
}
```

**New Code**:
```kotlin
@Composable
fun App() {
    val isDarkTheme = isSystemInDarkTheme()  // Auto-detect system preference
    
    AppTheme(isDarkTheme = isDarkTheme) {
        GameNavGraph()
    }
}
```

**Benefits**:
- Removes default MaterialTheme wrapper
- Automatically follows system dark mode preference
- Single source of truth for theme configuration
- Zero impact on navigation or screen logic

### 3.2 Update `MainActivity.kt`
**Location**: `composeApp/src/androidMain/kotlin/io/github/m0nkeysan/gamekeeper/MainActivity.kt`

**Changes Needed**:
- Detect system dark mode using `Configuration.UI_MODE_NIGHT_MASK`
- Set status bar icons appropriately:
  - **Light theme**: Dark icons (already implemented)
  - **Dark theme**: Light icons (new)
- Set navigation bar colors (if needed)

**Implementation**:
```kotlin
val isDarkMode = resources.configuration.uiMode and 
                 Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    @Suppress("DEPRECATION")
    window.decorView.systemUiVisibility = if (isDarkMode) {
        // Light icons for dark background
        window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    } else {
        // Dark icons for light background
        window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}
```

---

## Phase 4: Implementation Order

### Step 1: Create DarkGameColors.kt (5 min)
- Mirror GameColors.kt structure
- Define all dark variants with proper contrast ratios
- Include dark versions of:
  - Neutral palette (Surface0, Surface1, Surface2, Text colors)
  - Semantic colors (Error, Success, Warning, Info)
  - Primary, Secondary, Tertiary
  - Game-specific accents (Tarot, Yahtzee)
  - Player avatar colors

### Step 2: Create ThemeManager.kt (5 min)
- Simple utility file with system dark mode detection
- Reusable across entire application

### Step 3: Create AppTheme.kt (15 min)
- Build light and dark Material3 ColorSchemes
- Implement AppTheme composable
- Ensure all Material3 colors are properly mapped
- Handle shapes and typography

### Step 4: Update App.kt (3 min)
- Replace MaterialTheme with AppTheme
- Integrate system dark mode detection
- Verify imports

### Step 5: Update MainActivity.kt (5 min)
- Add dark mode aware system bar styling
- Properly handle light/dark icon colors
- Maintain backward compatibility

### Step 6: Build and verify (10 min)
- Run: `./gradlew :composeApp:assembleDebug`
- Verify all new files compile correctly
- Check for any import or syntax errors

**Total Estimated Time**: ~45 minutes implementation + compilation verification

---

## Testing Strategy

Since compilation verification is the goal:

1. **Build Debug APK**: `./gradlew :composeApp:assembleDebug`
2. **Check for compilation errors**: Verify all new files compile correctly
3. **Verify imports**: Ensure all Material3 imports work
4. **Check Material3 compatibility**: Verify ColorScheme construction is valid
5. **No runtime testing needed**: Just ensure it builds successfully

---

## File Summary

| File | Type | Size | Purpose |
|------|------|------|---------|
| DarkGameColors.kt | NEW | ~200 lines | Dark color palette definition |
| AppTheme.kt | NEW | ~150 lines | Material3 theme composable |
| ThemeManager.kt | NEW | ~30 lines | System dark mode detection utility |
| App.kt | MODIFY | ~10 lines | Use AppTheme + system detection |
| MainActivity.kt | MODIFY | ~20 lines | System bar styling per theme |

---

## Key Design Decisions

### 1. Color Palette
- **Dark Background**: Nearly black (#0F1419) for maximum contrast
- **Accent Colors**: 30-40% brighter for dark mode visibility
- **Text Colors**: 99% white (#F8F9FA) for eye-friendly reading
- **WCAG AAA Compliant**: All contrast ratios meet accessibility standards

### 2. System Detection
- **Auto-detection**: Always follows system preference
- **No toggle**: No manual theme switching UI
- **Real-time**: Changes immediately when system setting changes
- **No persistence**: No need for DataStore or SharedPreferences

### 3. Implementation Scope
- **Non-breaking**: All existing screens work without modification
- **Automatic**: Every screen supports dark mode via Material3 ColorScheme
- **Maintainable**: Color definitions in centralized files

### 4. Material3 Integration
- **Full ColorScheme**: All Material3 semantic colors properly mapped
- **Backward compatible**: Existing color references work in both themes
- **Future-proof**: Uses modern Material3 API

---

## Risks & Mitigation

| Risk | Mitigation |
|------|-----------|
| Color contrast issues in dark mode | Use WCAG AAA standards (7:1 ratio minimum for text) |
| Game-specific colors not visible | Brighten accents by 30-40% for dark mode |
| System bar color mismatch | Handle both Android 6+ and future API levels with version checks |
| Custom component colors | GameColors object provides both light/dark variants |
| Third-party color hardcoding | None found in codebase - all colors use GameColors |

---

## Success Criteria

✅ All files compile without errors
✅ App builds successfully with dark theme support
✅ Material3 ColorScheme properly configured for both themes
✅ System dark mode detection working
✅ Status bar icons adjust based on theme
✅ No breaking changes to existing screens
✅ All game-specific colors visible in both themes

---

## Future Enhancements (Out of Scope)

- Manual theme toggle UI
- System theme persistence override
- Animated theme transitions
- Per-screen theme customization
- Extended dynamic colors for Android 12+

---

## References

- [Material Design 3 - Dark Theme](https://m3.material.io/styles/color/the-color-system/color-roles)
- [Jetpack Compose Material3 ColorScheme](https://developer.android.com/reference/kotlin/androidx/compose/material3/ColorScheme)
- [WCAG Color Contrast Guidelines](https://www.w3.org/WAI/WCAG21/Understanding/contrast-minimum.html)
- [Android System Dark Theme](https://developer.android.com/develop/ui/compose/designsystems/dark)
