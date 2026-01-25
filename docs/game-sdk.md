---
layout: default
title: Game SDK Guide
---

# Game SDK Guide

The Game SDK is designed for game developers to integrate leaderboard functionality into their games.

## Features

- Anonymous players (device-based, no account required)
- Optional user registration/login
- Auto sign-in for returning registered users
- Score submission and leaderboards

## Installation

```kotlin
implementation("com.github.itayost.leaderboard-sdk-android:leaderboard-game:1.0.0")
```

## Initialization

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LeaderboardGameSDK.init(this, "your-api-key")
    }
}
```

## Player Management

### Anonymous Player

Create an anonymous player linked to the device:

```kotlin
lifecycleScope.launch {
    try {
        val player = LeaderboardGameSDK.registerPlayer("PlayerName")
        Log.d("Game", "Created: ${player.username}")
    } catch (e: HttpException) {
        Log.e("Game", "Error: ${e.code()}")
    }
}
```

### Restore Player

Restore an existing player on app restart:

```kotlin
lifecycleScope.launch {
    try {
        val player = LeaderboardGameSDK.restorePlayer()
        Log.d("Game", "Restored: ${player.username}")
    } catch (e: HttpException) {
        if (e.code() == 404) {
            // No existing player, create one
        }
    }
}
```

## User Registration (Optional)

Players can optionally register with email/password:

### Register

```kotlin
lifecycleScope.launch {
    try {
        val auth = LeaderboardGameSDK.register(
            email = "player@example.com",
            password = "password123",
            username = "PlayerName"
        )
        Log.d("Game", "Registered: ${auth.user.email}")
        // On next launch, tryAutoRestore() will sign them in
    } catch (e: HttpException) {
        Log.e("Game", "Registration failed: ${e.code()}")
    }
}
```

### Login

```kotlin
lifecycleScope.launch {
    try {
        val auth = LeaderboardGameSDK.login(
            email = "player@example.com",
            password = "password123"
        )
        Log.d("Game", "Logged in: ${auth.user.email}")
    } catch (e: HttpException) {
        Log.e("Game", "Login failed")
    }
}
```

### Auto Sign-in

For returning registered users:

```kotlin
lifecycleScope.launch {
    val player = LeaderboardGameSDK.tryAutoRestore()
    if (player != null) {
        Log.d("Game", "Welcome back, ${player.username}!")
    }
}
```

### Logout

```kotlin
LeaderboardGameSDK.logout()
```

### Check Auth Status

```kotlin
if (LeaderboardGameSDK.isLoggedIn()) {
    val user = LeaderboardGameSDK.getCurrentUser()
    val player = LeaderboardGameSDK.getCurrentPlayer()
}
```

## Leaderboards

### Submit Score

```kotlin
lifecycleScope.launch {
    try {
        val score = LeaderboardGameSDK.submitScore(
            leaderboardId = "daily-highscores",
            score = 1500L,
            metadata = mapOf("level" to 5)  // Optional
        )
        Log.d("Game", "Score: ${score.score}")
    } catch (e: HttpException) {
        Log.e("Game", "Error: ${e.code()}")
    }
}
```

### Get Top Scores

```kotlin
lifecycleScope.launch {
    try {
        val response = LeaderboardGameSDK.getTopScores(
            leaderboardId = "daily-highscores",
            limit = 10
        )
        response.scores.forEach { entry ->
            Log.d("Game", "#${entry.rank} ${entry.player.username}: ${entry.score.score}")
        }
    } catch (e: HttpException) {
        Log.e("Game", "Error: ${e.code()}")
    }
}
```

### Get Player Rank

```kotlin
lifecycleScope.launch {
    try {
        val response = LeaderboardGameSDK.getPlayerRank(
            leaderboardId = "daily-highscores",
            nearby = 5  // Players above/below
        )
        Log.d("Game", "Rank: #${response.rank} of ${response.total_players}")
    } catch (e: HttpException) {
        Log.e("Game", "Error: ${e.code()}")
    }
}
```

### Create Leaderboard

```kotlin
lifecycleScope.launch {
    try {
        val leaderboard = LeaderboardGameSDK.createLeaderboard(
            name = "Daily High Scores",
            sortOrder = "desc"  // "desc" = highest first
        )
        Log.d("Game", "Created: ${leaderboard._id}")
    } catch (e: HttpException) {
        Log.e("Game", "Error: ${e.code()}")
    }
}
```

## Error Handling

The SDK uses Kotlin coroutines with exception-based error handling:

```kotlin
lifecycleScope.launch {
    try {
        val player = LeaderboardGameSDK.registerPlayer("Name")
    } catch (e: HttpException) {
        when (e.code()) {
            400 -> Log.e("Game", "Bad request")
            401 -> Log.e("Game", "Unauthorized - check API key")
            404 -> Log.e("Game", "Not found")
            409 -> Log.e("Game", "Conflict - already exists")
            else -> Log.e("Game", "HTTP error: ${e.code()}")
        }
    } catch (e: IOException) {
        Log.e("Game", "Network error: ${e.message}")
    } catch (e: IllegalStateException) {
        Log.e("Game", "SDK error: ${e.message}")
    }
}
```

## Demo App

See [leaderboard-flappy-bird](https://github.com/itayost/leaderboard-flappy-bird) for a complete game implementation using the Game SDK.

## Full API Reference

See [API Reference](api-reference.md) for complete method documentation.
