# 📱 iOS Implementation Plan

> Full detailed plan available in conversation history. This document provides quick reference.

## 🎯 Overview

Adding iOS support to GameKeeper Kotlin Multiplatform project.

### Implementation Decisions

1. ✅ **Room Database** - Accept limitations and work around them
2. ✅ **Shake Detection** - Full implementation using CoreMotion
3. ✅ **Xcode Project** - Complete structure provided
4. ✅ **Locale Management** - Full multiplatform refactor

## 📋 Implementation Phases

### Phase 1: Gradle Configuration
- Add iOS targets (iosX64, iosArm64, iosSimulatorArm64)
- Configure iOS source sets
- Add KSP for iOS targets

### Phase 2: Multiplatform Locale Wrapper
- Create `MultiplatformLocale` expect/actual classes
- Refactor `LocaleManager.kt` to use wrapper
- Refactor `LocalStrings.kt` to use wrapper
- Remove `java.util.Locale` from commonMain

### Phase 3: iOS Source Structure
- Create iosMain directory structure
- Set up package hierarchy

### Phase 4: iOS Platform Files (8 files)
1. `MainViewController.kt` - iOS app entry point
2. `GameIcons.ios.kt` - Material icons mapping
3. `PlatformRepositories.ios.kt` - DI and database setup
4. `MultiplatformLocale.ios.kt` - NSLocale wrapper
5. `SystemLocale.ios.kt` - System locale detection
6. `DateUtils.ios.kt` - Date formatting
7. `TimeUtils.ios.kt` - Timestamp utilities
8. `HapticFeedback.ios.kt` - Haptic vibration
9. `ShakeDetector.ios.kt` - CoreMotion accelerometer

### Phase 5: Xcode Project
- Create iOS app structure
- GameKeeperApp.swift (SwiftUI entry)
- ContentView.swift (Compose wrapper)
- Info.plist configuration
- Build scripts

### Phase 6: Build & Test
- Build iOS framework
- Test on simulator
- Test on device
- Verify all features

## 📂 File Structure

```
GameKeeper/
├── composeApp/
│   ├── build.gradle.kts (UPDATED)
│   └── src/
│       ├── androidMain/
│       ├── commonMain/ (UPDATED: Locale refactor)
│       └── iosMain/ (NEW)
│           └── kotlin/io/github/m0nkeysan/gamekeeper/
│               ├── MainViewController.kt
│               ├── GameIcons.ios.kt
│               ├── core/model/TimeUtils.ios.kt
│               └── platform/
│                   ├── PlatformRepositories.ios.kt
│                   ├── MultiplatformLocale.ios.kt
│                   ├── SystemLocale.ios.kt
│                   ├── DateUtils.ios.kt
│                   ├── HapticFeedback.ios.kt
│                   └── ShakeDetector.ios.kt
└── iosApp/ (NEW)
    ├── iosApp.xcodeproj/
    └── iosApp/
        ├── GameKeeperApp.swift
        ├── ContentView.swift
        └── Info.plist
```

## ⏱️ Timeline

| Phase | Time Estimate |
|-------|---------------|
| Gradle Config | 45-60 min |
| Locale Refactor | 1-2 hours |
| iOS Structure | 15 min |
| iOS Files | 3-4 hours |
| Xcode Setup | 1-2 hours |
| Testing | 2-3 hours |
| **TOTAL** | **8-12 hours** |

## 🎯 Success Criteria

- [ ] iOS framework builds successfully
- [ ] Xcode project compiles
- [ ] App runs on simulator
- [ ] App runs on device
- [ ] All games playable
- [ ] Database persistence works
- [ ] Localization works (EN/FR)
- [ ] Haptic feedback works
- [ ] Shake detection works

## 🚧 Known Challenges

1. **Room Database iOS** - Experimental support, may need workarounds
2. **Locale Management** - Major refactor required (java.util.Locale → NSLocale)
3. **Shake Detection** - CoreMotion integration complex
4. **Material Icons** - Verify Compose Multiplatform compatibility

## 📚 Key Technologies

- **Kotlin Multiplatform** 2.3.0
- **Compose Multiplatform** 1.9.3
- **Room Database** 2.8.4 (multiplatform)
- **iOS Frameworks**: UIKit, Foundation, CoreMotion
- **Swift/SwiftUI** for iOS app wrapper

## 🔗 Resources

- [Kotlin Multiplatform Docs](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Room Multiplatform](https://developer.android.com/kotlin/multiplatform/room)

---

**Status:** Implementation in progress  
**Branch:** `feature/ios-support`  
**Last Updated:** January 2026
