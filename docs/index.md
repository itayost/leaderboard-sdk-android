---
layout: default
title: Leaderboard SDK for Android
---

# Leaderboard SDK for Android

A multi-module Kotlin SDK for integrating leaderboard functionality into Android games and applications. Built with Retrofit 3.0 and Kotlin coroutines.

[![](https://jitpack.io/v/itayost/leaderboard-sdk-android.svg)](https://jitpack.io/#itayost/leaderboard-sdk-android)

## Features

- **Game SDK** - For games: anonymous players, score submission, leaderboards
- **Hub SDK** - For developers: manage apps, view analytics, cross-game stats

## Architecture

```
┌─────────────────┐     ┌─────────────────┐
│   Game SDK      │     │    Hub SDK      │
│ (for games)     │     │ (for devs)      │
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

- [Game SDK Guide](game-sdk.md) - For integrating leaderboards into games
- [Hub SDK Guide](hub-sdk.md) - For developer portal apps
- [API Reference](api-reference.md) - Full API documentation

## Demo Apps

- [leaderboard-flappy-bird](https://github.com/itayost/leaderboard-flappy-bird) - Game example using the Game SDK
- [leaderboard-developer-hub](https://github.com/itayost/leaderboard-developer-hub) - Developer portal using the Hub SDK

## Requirements

- Android SDK 21+ (Android 5.0)
- Kotlin 2.0+

## License

MIT License - see [LICENSE](https://github.com/itayost/leaderboard-sdk-android/blob/main/LICENSE)
