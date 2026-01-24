# Screenshots Guide

This folder contains screenshots of the Tally app for use in the main README and promotional materials.

## 📐 Recommended Specifications

### Image Dimensions
- **Mobile Screenshots:** 1080x2400 (9:19.5 aspect ratio)
- **Feature Highlights:** 1920x1080 (16:9 for wider displays)
- **Format:** PNG (preferred) or JPG
- **File Size:** Keep under 500KB per image

### Theme Variants
Capture screenshots in both light and dark modes to showcase theme support.

## 📸 Required Screenshots

### Essential (Priority 1)
1. **home-screen.png** - Main game selection screen showing all available games
2. **yahtzee-scoring.png** - Active Yahtzee scorecard with scores filled in
3. **tarot-scoring.png** - Tarot game in progress showing round tracking
4. **player-management.png** - Player list and creation interface

### Additional (Priority 2)
5. **dice-roller.png** - Dice roller screen showing rolled dice
6. **counter.png** - Counter screen with history
7. **finger-selector.png** - Finger selector in action
8. **dark-mode-example.png** - Any screen showcasing dark mode

### Optional
- **statistics.png** - Game statistics view (Yahtzee or Tarot)
- **settings.png** - Settings/preferences screen
- **game-creation.png** - New game creation flow

## 📋 Naming Conventions

- Use lowercase with hyphens: `feature-name.png`
- For variants, append theme: `home-screen-dark.png`
- For device types: `home-screen-tablet.png` (if needed)
- Keep names descriptive and consistent

## 🎨 Capture Guidelines

1. **Clean State** - Remove personal/test data, use demo names like "Alice", "Bob", "Charlie"
2. **Realistic Data** - Show the app in actual use with believable scores/states
3. **Good Timing** - Capture screens at visually appealing states (not mid-animation)
4. **No Debug Info** - Ensure no developer overlays or debug information is visible
5. **Consistent Device** - Use the same device/emulator dimensions for all screenshots

## 🔧 Capture Tools

### Android
- **Android Studio:** Run app → Device Explorer → Screenshot icon
- **ADB:** `adb shell screencap -p /sdcard/screenshot.png`
- **Emulator:** Click camera icon in emulator toolbar

### iOS
- **Xcode Simulator:** Cmd+S or File → New Screenshot
- **Physical Device:** Volume Up + Side Button

## 📝 Post-Processing

- Crop status bar if needed (or use device frames)
- Compress images: [TinyPNG](https://tinypng.com/) or `pngquant`
- Add device frames (optional): [Mockuphone](https://mockuphone.com/)

## ✅ Checklist Before Adding

- [ ] Image is clear and properly focused
- [ ] No personal/sensitive information visible
- [ ] File size is reasonable (<500KB)
- [ ] Named according to conventions
- [ ] Shows app in a positive, functional state
- [ ] Follows Material Design 3 aesthetic

---

Once screenshots are captured, update the main README.md to reference them:

```markdown
![Home Screen](screenshots/home-screen.png)
```
