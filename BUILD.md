# Zen Flow - Build Instructions

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34
- Kotlin 1.9.22

## Setup

1. Open the project in Android Studio
2. Sync Gradle files (File -> Sync Project with Gradle Files)
3. Add fonts to `app/src/main/res/font/` (see font/README.txt)
4. Create `local.properties` with your SDK path (see local.properties.template)

## Build Debug APK

```bash
./gradlew assembleDebug
```

APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

## Build Release APK

1. Create a signing keystore:
```bash
keytool -genkey -v -keystore zenflow.keystore -alias zenflow -keyalg RSA -keysize 2048 -validity 10000
```

2. Create `keystore.properties`:
```properties
storeFile=zenflow.keystore
storePassword=YOUR_PASSWORD
keyPassword=YOUR_PASSWORD
keyAlias=zenflow
```

3. Build release:
```bash
./gradlew assembleRelease
```

APK will be at: `app/build/outputs/apk/release/app-release.apk`

## Features

- Offline-first with Room Database
- AMOLED-friendly dark mode
- SD card install support
- Export/import backups
- Focus timer with Pomodoro
- Meditation timer
- Task management with sections
- Progress analytics
- Home screen widget
- Local notifications

## Architecture

- Kotlin + Jetpack Compose
- Material Design 3
- MVVM architecture
- Room Database
- Hilt DI (optional)
- Coroutines + Flow