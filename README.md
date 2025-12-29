# Leaderboard SDK for Android

[![](https://jitpack.io/v/itayost/leaderboard-sdk-android.svg)](https://jitpack.io/#itayost/leaderboard-sdk-android)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A multi-module Kotlin SDK for integrating leaderboard functionality into Android games and applications. Built with Retrofit 3.0 and Kotlin coroutines.

**[Documentation](https://itayost.github.io/leaderboard-sdk-android/)** | **[API Reference](https://itayost.github.io/leaderboard-sdk-android/api-reference)**

## Architecture

This SDK consists of two separate modules:

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

### Game SDK (`leaderboard-game`)
For game developers to integrate into their games:
- Anonymous players (device-based, no account required)
- Optional user registration/login (email/password)
- Auto sign-in for returning registered users
- Score submission, leaderboards, rankings
- Link anonymous scores to user account

### Hub SDK (`leaderboard-hub`)
For the Game Hub app where users can:
- See all scores across ALL games they've played
- User registration/login with JWT
- Cross-game stats and score aggregation

## Installation

### Step 1: Add JitPack repository

Add JitPack to your project-level `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: Add the dependency

For games, add the Game SDK:

```kotlin
dependencies {
    implementation("com.github.itayost.leaderboard-sdk-android:leaderboard-game:1.0.0")
}
```

For the Hub app, add the Hub SDK:

```kotlin
dependencies {
    implementation("com.github.itayost.leaderboard-sdk-android:leaderboard-hub:1.0.0")
}
```

## Game SDK Usage

### Initialize the SDK

Initialize the SDK once in your Application class or main Activity:

```kotlin
LeaderboardGameSDK.init(
    context = applicationContext,
    apiKey = "your-api-key",
    baseUrl = "https://your-api.vercel.app/"  // Optional
)
```

### Auto Sign-in (Returning Users)

For users who previously registered, automatically restore their session:

```kotlin
lifecycleScope.launch {
    val player = LeaderboardGameSDK.tryAutoRestore()
    if (player != null) {
        // User is signed in, player restored
        Log.d("SDK", "Welcome back, ${player.username}!")
    } else {
        // No saved session, user can play anonymously or register
    }
}
```

### Anonymous Player (No Account Required)

Create an anonymous player linked to the device:

```kotlin
lifecycleScope.launch {
    try {
        val player = LeaderboardGameSDK.registerPlayer("PlayerName")
        Log.d("SDK", "Player created: ${player.username}")
    } catch (e: HttpException) {
        Log.e("SDK", "HTTP error: ${e.code()}")
    } catch (e: IOException) {
        Log.e("SDK", "Network error: ${e.message}")
    }
}
```

### Restore Existing Anonymous Player

Restore a player by device ID:

```kotlin
lifecycleScope.launch {
    try {
        val player = LeaderboardGameSDK.restorePlayer()
        Log.d("SDK", "Player restored: ${player.username}")
    } catch (e: HttpException) {
        if (e.code() == 404) {
            // No existing player, create one
        }
    }
}
```

### User Registration (Optional)

Players can optionally register with email/password. This links their anonymous player to a user account, preserving all scores:

```kotlin
lifecycleScope.launch {
    try {
        val authResponse = LeaderboardGameSDK.register(
            email = "player@example.com",
            password = "securepassword",
            username = "PlayerName"
        )
        Log.d("SDK", "Registered: ${authResponse.user.email}")
        // Player is now linked to user account
        // On next app launch, tryAutoRestore() will sign them in automatically
    } catch (e: HttpException) {
        Log.e("SDK", "Registration failed: ${e.code()}")
    }
}
```

### User Login

Sign in with existing account:

```kotlin
lifecycleScope.launch {
    try {
        val authResponse = LeaderboardGameSDK.login(
            email = "player@example.com",
            password = "securepassword"
        )
        Log.d("SDK", "Logged in: ${authResponse.user.email}")

        val player = LeaderboardGameSDK.getCurrentPlayer()
        if (player != null) {
            Log.d("SDK", "Player: ${player.username}")
        }
    } catch (e: HttpException) {
        Log.e("SDK", "Login failed: ${e.code()}")
    }
}
```

### Logout

```kotlin
LeaderboardGameSDK.logout()
// Token cleared, user can play anonymously again
```

### Check Auth Status

```kotlin
if (LeaderboardGameSDK.isLoggedIn()) {
    val user = LeaderboardGameSDK.getCurrentUser()
    val player = LeaderboardGameSDK.getCurrentPlayer()
    Log.d("SDK", "Logged in as ${user?.email}, player: ${player?.username}")
}
```

### Submit a Score

```kotlin
lifecycleScope.launch {
    try {
        val score = LeaderboardGameSDK.submitScore(
            leaderboardId = "leaderboard-id",
            score = 1500L,
            metadata = mapOf("level" to 5)  // Optional
        )
        Log.d("SDK", "Score submitted: ${score.score}")
    } catch (e: HttpException) {
        Log.e("SDK", "Error: ${e.code()}")
    } catch (e: IllegalStateException) {
        Log.e("SDK", "No player: ${e.message}")
    }
}
```

### Get Top Scores

```kotlin
lifecycleScope.launch {
    try {
        val response = LeaderboardGameSDK.getTopScores(
            leaderboardId = "leaderboard-id",
            limit = 10
        )
        response.scores.forEach { entry ->
            Log.d("SDK", "#${entry.rank} ${entry.player.username}: ${entry.score.score}")
        }
    } catch (e: HttpException) {
        Log.e("SDK", "Error: ${e.code()}")
    }
}
```

### Get Player Rank

```kotlin
lifecycleScope.launch {
    try {
        val response = LeaderboardGameSDK.getPlayerRank(
            leaderboardId = "leaderboard-id",
            nearby = 5
        )
        Log.d("SDK", "Rank: #${response.rank} of ${response.total_players}")
        Log.d("SDK", "Best Score: ${response.best_score.score}")
    } catch (e: HttpException) {
        Log.e("SDK", "Error: ${e.code()}")
    }
}
```

### Create a Leaderboard

```kotlin
lifecycleScope.launch {
    try {
        val leaderboard = LeaderboardGameSDK.createLeaderboard(
            name = "Daily High Scores",
            sortOrder = "desc"  // "desc" = highest first, "asc" = lowest first
        )
        Log.d("SDK", "Created: ${leaderboard._id}")
    } catch (e: HttpException) {
        Log.e("SDK", "Error: ${e.code()}")
    }
}
```

## Hub SDK Usage

### Initialize

```kotlin
LeaderboardHubSDK.init(
    context = applicationContext,
    baseUrl = "https://your-api.vercel.app/"  // Optional
)
```

### Register / Login

```kotlin
lifecycleScope.launch {
    try {
        val authResponse = LeaderboardHubSDK.register(
            email = "user@example.com",
            password = "securepassword",
            username = "Username"
        )
        Log.d("SDK", "Registered: ${authResponse.user.email}")
    } catch (e: HttpException) {
        Log.e("SDK", "Error: ${e.code()}")
    }
}
```

### Get All Games & Scores

```kotlin
lifecycleScope.launch {
    try {
        val gamesResponse = LeaderboardHubSDK.getMyGames()
        gamesResponse.games.forEach { game ->
            Log.d("SDK", "Game: ${game.app.name}, Scores: ${game.total_scores}")
        }

        val scoresResponse = LeaderboardHubSDK.getMyScores(limit = 50)
        scoresResponse.scores.forEach { entry ->
            Log.d("SDK", "Score: ${entry.score.score} in ${entry.app?.name}")
        }
    } catch (e: HttpException) {
        Log.e("SDK", "Error: ${e.code()}")
    }
}
```

## Data Models

### Player
```kotlin
data class Player(
    val _id: String,
    val app_id: String,
    val device_id: String?,
    val user_id: String?,
    val username: String,
    val avatar_url: String?,
    val created_at: String,
    val linked_at: String?
) {
    val isLinked: Boolean  // true if linked to user account
    val isAnonymous: Boolean  // true if anonymous (device-only)
}
```

### User
```kotlin
data class User(
    val _id: String,
    val email: String,
    val username: String,
    val avatar_url: String?,
    val created_at: String
)
```

### Score
```kotlin
data class Score(
    val _id: String,
    val leaderboard_id: String,
    val player_id: String,
    val score: Long,
    val metadata: Map<String, Any>?,
    val created_at: String
)
```

## Error Handling

The SDK uses Kotlin coroutines with exception-based error handling:

```kotlin
lifecycleScope.launch {
    try {
        val player = LeaderboardGameSDK.registerPlayer("Name")
    } catch (e: HttpException) {
        // HTTP errors (4xx, 5xx)
        when (e.code()) {
            400 -> Log.e("SDK", "Bad request")
            401 -> Log.e("SDK", "Unauthorized")
            404 -> Log.e("SDK", "Not found")
            else -> Log.e("SDK", "HTTP error: ${e.code()}")
        }
    } catch (e: IOException) {
        // Network errors
        Log.e("SDK", "Network error: ${e.message}")
    } catch (e: IllegalStateException) {
        // SDK not initialized or no current player
        Log.e("SDK", "SDK error: ${e.message}")
    }
}
```

## Requirements

- Android SDK 21+ (Android 5.0)
- Kotlin 2.0+

## License

MIT License
