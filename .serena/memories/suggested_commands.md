# GameKeeper - Suggested Commands

## Build Commands

### Android
```shell
# Build debug APK
./gradlew :composeApp:assembleDebug

# Install on device
./gradlew :composeApp:installDebug
```

### iOS
Open iosApp directory in Xcode and run from there.

## Testing
```shell
# Run common tests
./gradlew :composeApp:testDebugUnitTest

# Run all tests
./gradlew test
```

## Clean Build
```shell
./gradlew clean build
```
