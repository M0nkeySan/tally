# GameKeeper - Project Overview

## Purpose
GameKeeper is a Kotlin Multiplatform mobile application for tracking and scoring various games.

## Tech Stack
- **Kotlin Multiplatform** targeting Android and iOS
- **Compose Multiplatform** for UI
- **Gradle** build system
- **Room** database for local data persistence

## Project Structure
```
composeApp/
├── src/
│   ├── commonMain/kotlin/io/github/m0nkeysan/gamekeeper/
│   │   ├── ui/
│   │   │   ├── screens/ - Feature screens (home, tarot, yahtzee, counter, fingerselector, etc.)
│   │   │   ├── components/ - Reusable UI components
│   │   │   └── viewmodel/ - Base ViewModel
│   │   ├── core/
│   │   │   ├── navigation/ - Navigation graph and screen definitions
│   │   │   ├── model/ - Domain models
│   │   │   ├── data/ - Data layer (database, repositories)
│   │   │   └── domain/ - Business logic (engines, use cases)
│   │   └── platform/ - Platform-specific implementations
│   ├── androidMain/ - Android-specific code
│   └── commonTest/ - Common tests
```

## Features
- **Counter**: Simple counter for board games
- **Finger Selector**: Random player selection with multi-touch
- **Yahtzee**: Complete scorecard with automatic bonuses
- **Tarot**: Score Tarot games for 3-5 players

## Code Style
- Kotlin with Compose Multiplatform
- Data classes for models
- Clean architecture with separation of concerns
- Composable functions follow naming conventions
