# iOS Xcode Project Setup Guide

This guide will help you set up the Xcode project for GameKeeper iOS.

## Prerequisites

- macOS with Xcode 15.0 or later installed
- Kotlin Multiplatform Mobile (KMM) plugin for Android Studio (optional, for development)

## Automatic Setup (Recommended)

The easiest way to create the Xcode project is to let Kotlin Multiplatform generate it for you:

### Step 1: Build the iOS Framework

From the project root, run:

```bash
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

This will:
- Compile the Kotlin code for iOS
- Generate the `ComposeApp.framework` in `composeApp/build/bin/iosSimulatorArm64/debugFramework/`

### Step 2: Create Xcode Project

1. Open Xcode
2. Create a new project: **File → New → Project**
3. Select **iOS → App**
4. Configure:
   - Product Name: `iosApp`
   - Organization Identifier: `io.github.m0nkeysan.gamekeeper`
   - Interface: **SwiftUI**
   - Language: **Swift**
   - Storage: **None**
5. Save to: `GameKeeper/iosApp/`

### Step 3: Add Framework to Xcode

1. In Xcode, select the project in the navigator
2. Select the **iosApp** target
3. Go to **General → Frameworks, Libraries, and Embedded Content**
4. Click **+** and then **Add Other → Add Files...**
5. Navigate to: `composeApp/build/bin/iosSimulatorArm64/debugFramework/ComposeApp.framework`
6. Select **"Copy items if needed"** and **"Create groups"**
7. Set **Embed** to **"Embed & Sign"**

### Step 4: Configure Framework Search Paths

1. Select the **iosApp** target
2. Go to **Build Settings**
3. Search for **Framework Search Paths**
4. Add (for Debug configuration):
   ```
   $(PROJECT_DIR)/../composeApp/build/bin/iosSimulatorArm64/debugFramework
   ```
5. Add (for Release configuration):
   ```
   $(PROJECT_DIR)/../composeApp/build/bin/iosSimulatorArm64/releaseFramework
   ```

### Step 5: Replace Generated Files

Replace the auto-generated SwiftUI files with the ones provided:

1. Delete `ContentView.swift` and `GameKeeperApp.swift` from Xcode (Move to Trash)
2. Drag the following files from `iosApp/iosApp/` into Xcode:
   - `GameKeeperApp.swift`
   - `ContentView.swift`
3. Select **"Copy items if needed"** and **"Create groups"**

### Step 6: Update Info.plist

1. In Xcode, open `Info.plist`
2. Add the motion usage description:
   - Key: `NSMotionUsageDescription`
   - Value: `GameKeeper uses motion detection to enable shake-to-roll dice functionality in Yahtzee game.`

(Or replace the entire Info.plist with the one in `iosApp/iosApp/Info.plist`)

### Step 7: Configure Deployment Target

1. Select the **iosApp** target
2. Go to **General → Deployment Info**
3. Set **iOS Deployment Target** to **14.0** or higher

### Step 8: Build Script Phase (Optional but Recommended)

Add a build phase to automatically rebuild the framework:

1. Select the **iosApp** target
2. Go to **Build Phases**
3. Click **+** → **New Run Script Phase**
4. Name it: `Build Kotlin Framework`
5. Add script:
   ```bash
   cd "$SRCROOT/.."
   ./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
   ```
6. Drag this phase **before** "Compile Sources"

## Build and Run

1. Select a simulator (iPhone 15 Pro recommended)
2. Click **Run** (⌘R)
3. The app should build and launch in the simulator

## For Physical Devices

To run on a physical iOS device:

1. Build the ARM64 framework:
   ```bash
   ./gradlew :composeApp:linkDebugFrameworkIosArm64
   ```

2. Update Framework Search Path in Xcode:
   ```
   $(PROJECT_DIR)/../composeApp/build/bin/iosArm64/debugFramework
   ```

3. Connect your device and select it in Xcode
4. Configure signing in **Signing & Capabilities**
5. Run the app

## Troubleshooting

### "Module 'ComposeApp' not found"

- Ensure the framework was built: `./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64`
- Check Framework Search Paths in Build Settings
- Clean build folder: **Product → Clean Build Folder** (⇧⌘K)

### "Undefined symbols for architecture arm64"

- Make sure you're building for the correct architecture (simulator vs device)
- Verify the framework in Build Phases is set to "Embed & Sign"

### Shake detection not working

- Ensure `NSMotionUsageDescription` is in Info.plist
- Shake detection only works on physical devices (not simulators)

### Database errors

- The database is stored in the app's Documents directory
- To reset: Delete the app from simulator/device and reinstall

## Next Steps

Once the app is running:

1. Test all three games (Yahtzee, Tarot, Counter)
2. Test language switching (English ↔ French)
3. Test haptic feedback (requires device)
4. Test shake detection (requires device)
5. Verify database persistence

## Manual Xcode Project Configuration (Alternative)

If you prefer to create the Xcode project manually without using Xcode's wizard, you can use the CocoaPods integration or create a workspace. However, the method above is simpler and recommended for getting started quickly.
