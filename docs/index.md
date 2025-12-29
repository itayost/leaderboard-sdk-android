---
layout: default
title: Leaderboard SDK for Android
---

# Leaderboard SDK for Android

A multi-module Kotlin SDK for integrating leaderboard functionality into Android games and applications. Built with Retrofit 3.0 and Kotlin coroutines.

[![](https://jitpack.io/v/itayost/leaderboard-sdk-android.svg)](https://jitpack.io/#itayost/leaderboard-sdk-android)

## Features

- **Two SDKs** for different use cases
- **Anonymous players** - No account required
- **User accounts** - Optional registration with auto sign-in
- **Real-time leaderboards** - Submit scores, get rankings
- **Cross-game stats** - Hub app shows all games

## Architecture

```
┌─────────────────┐     ┌─────────────────┐
│   Game SDK      │     │    Hub SDK      │
│ (for games)     │     │ (for hub app)   │
└────────┬────────┘     └────────┬────────┘
         │                       │
         └───────────┬───────────┘
                     ▼
            ┌─────────────────┐
            │   Core Module   │
            └─────────────────┘
```

## Quick Start

### Installation

Add JitPack to your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency to your app's `build.gradle.kts`:

```kotlin
// For games
implementation("com.github.itayost.leaderboard-sdk-android:leaderboard-game:1.0.0")

// For hub app
implementation("com.github.itayost.leaderboard-sdk-android:leaderboard-hub:1.0.0")
```

### Initialize

```kotlin
// In your Application or MainActivity
LeaderboardGameSDK.init(context, "your-api-key")
```

### Basic Usage

```kotlin
lifecycleScope.launch {
    // Create anonymous player
    val player = LeaderboardGameSDK.registerPlayer("PlayerName")

    // Submit score
    val score = LeaderboardGameSDK.submitScore("leaderboard-id", 1500L)

    // Get top scores
    val topScores = LeaderboardGameSDK.getTopScores("leaderboard-id")
}
```

## Documentation

- [Game SDK Guide](game-sdk.md) - For game developers
- [Hub SDK Guide](hub-sdk.md) - For hub app development
- [API Reference](api-reference.md) - Full API documentation

## Requirements

- Android SDK 21+ (Android 5.0)
- Kotlin 2.0+

## License

MIT License - see [LICENSE](https://github.com/itayost/leaderboard-sdk-android/blob/main/LICENSE)
