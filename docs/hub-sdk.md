---
layout: default
title: Hub SDK Guide
---

# Hub SDK Guide

The Hub SDK is designed for building a Game Hub app where users can see all their scores across all games.

## Features

- User registration/login with JWT
- Cross-game stats and score aggregation
- Encrypted token storage
- Auto sign-in for returning users

## Installation

```kotlin
implementation("com.github.itayost.leaderboard-sdk-android:leaderboard-hub:1.0.0")
```

## Initialization

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LeaderboardHubSDK.init(this)
    }
}
```

## User Authentication

### Register

```kotlin
lifecycleScope.launch {
    try {
        val auth = LeaderboardHubSDK.register(
            email = "user@example.com",
            password = "password123",
            username = "Username"
        )
        Log.d("Hub", "Registered: ${auth.user.email}")
    } catch (e: HttpException) {
        when (e.code()) {
            400 -> Log.e("Hub", "Invalid input")
            409 -> Log.e("Hub", "Email already exists")
            else -> Log.e("Hub", "Error: ${e.code()}")
        }
    }
}
```

### Login

```kotlin
lifecycleScope.launch {
    try {
        val auth = LeaderboardHubSDK.login(
            email = "user@example.com",
            password = "password123"
        )
        Log.d("Hub", "Logged in: ${auth.user.email}")
    } catch (e: HttpException) {
        Log.e("Hub", "Invalid credentials")
    }
}
```

### Auto Sign-in

For returning users:

```kotlin
lifecycleScope.launch {
    val user = LeaderboardHubSDK.tryAutoRestore()
    if (user != null) {
        Log.d("Hub", "Welcome back, ${user.username}!")
    }
}
```

### Logout

```kotlin
LeaderboardHubSDK.logout()
```

### Check Auth Status

```kotlin
if (LeaderboardHubSDK.isLoggedIn()) {
    val user = LeaderboardHubSDK.getCurrentUser()
}
```

## User Profile

### Get Profile

```kotlin
lifecycleScope.launch {
    try {
        val user = LeaderboardHubSDK.getUserProfile()
        Log.d("Hub", "User: ${user.username}, Email: ${user.email}")
    } catch (e: HttpException) {
        Log.e("Hub", "Error: ${e.code()}")
    }
}
```

### Update Profile

```kotlin
lifecycleScope.launch {
    try {
        val user = LeaderboardHubSDK.updateProfile(
            username = "NewUsername",
            avatarUrl = "https://example.com/avatar.png"
        )
        Log.d("Hub", "Updated: ${user.username}")
    } catch (e: HttpException) {
        Log.e("Hub", "Error: ${e.code()}")
    }
}
```

## Cross-Game Stats

### Get All Games

Get all games the user has played:

```kotlin
lifecycleScope.launch {
    try {
        val response = LeaderboardHubSDK.getUserGames()
        response.games.forEach { game ->
            Log.d("Hub", "Game: ${game.app.name}")
            Log.d("Hub", "  Total Scores: ${game.total_scores}")
            Log.d("Hub", "  Best Score: ${game.best_score}")
        }
    } catch (e: HttpException) {
        Log.e("Hub", "Error: ${e.code()}")
    }
}
```

### Get All Scores

Get all scores across all games:

```kotlin
lifecycleScope.launch {
    try {
        val response = LeaderboardHubSDK.getUserScores(limit = 50)
        response.scores.forEach { entry ->
            Log.d("Hub", "Score: ${entry.score.score}")
            Log.d("Hub", "  Game: ${entry.app?.name}")
            Log.d("Hub", "  Leaderboard: ${entry.leaderboard?.name}")
            Log.d("Hub", "  Date: ${entry.score.created_at}")
        }
    } catch (e: HttpException) {
        Log.e("Hub", "Error: ${e.code()}")
    }
}
```

## Error Handling

```kotlin
lifecycleScope.launch {
    try {
        val user = LeaderboardHubSDK.getUserProfile()
    } catch (e: HttpException) {
        when (e.code()) {
            401 -> Log.e("Hub", "Not authenticated")
            404 -> Log.e("Hub", "User not found")
            else -> Log.e("Hub", "HTTP error: ${e.code()}")
        }
    } catch (e: IOException) {
        Log.e("Hub", "Network error: ${e.message}")
    } catch (e: IllegalStateException) {
        Log.e("Hub", "SDK error: ${e.message}")
    }
}
```

## Full API Reference

See [API Reference](api-reference.md) for complete method documentation.
