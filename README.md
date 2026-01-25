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
│ (for games)     │     │ (for devs)      │
└────────┬────────┘     └────────┬────────┘
         │                       │
         └───────────┬───────────┘
                     ▼
            ┌─────────────────┐
            │   Core Module   │
            └─────────────────┘
```

### Game SDK (`leaderboard-game`)
For integrating leaderboards into games:
- Anonymous players (device-based, no login required)
- Score submission and retrieval
- Leaderboard rankings
- Simple API - just a few lines of code

### Hub SDK (`leaderboard-hub`)
For developers to manage their apps and view analytics:
- User authentication (email/password)
- View all registered apps and their statistics
- Player management and analytics
- Cross-game score aggregation

## Demo Apps

See the SDK in action with these demo applications:

| Demo | SDK Used | Description |
|------|----------|-------------|
| [Flappy Bird](https://github.com/itayost/leaderboard-flappy-bird) | Game SDK | Classic game demonstrating leaderboard integration |
| [Developer Hub](https://github.com/itayost/leaderboard-developer-hub) | Hub SDK | Developer portal for managing apps and viewing analytics |

Both demos are ready to clone and run - see their READMEs for setup instructions.

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

### 1. Initialize

```kotlin
LeaderboardGameSDK.init(
    context = applicationContext,
    apiKey = "your-api-key",  // Get from Developer Hub
    baseUrl = "https://leaderboard-api-alpha.vercel.app/"
)
```

### 2. Register Player

Create an anonymous player (no login required):

```kotlin
lifecycleScope.launch {
    val player = LeaderboardGameSDK.registerPlayer("PlayerName")
    // Player is now ready to submit scores
}
```

### 3. Submit a Score

```kotlin
lifecycleScope.launch {
    val score = LeaderboardGameSDK.submitScore(
        leaderboardId = "leaderboard-id",
        score = 1500L
    )
}
```

### 4. Get Leaderboard

```kotlin
lifecycleScope.launch {
    val response = LeaderboardGameSDK.getTopScores(
        leaderboardId = "leaderboard-id",
        limit = 10
    )
    response.scores.forEach { entry ->
        Log.d("SDK", "#${entry.rank} ${entry.player.username}: ${entry.score.score}")
    }
}
```

## Hub SDK Usage

### Initialize

```kotlin
LeaderboardHubSDK.init(
    context = applicationContext,
    baseUrl = "https://leaderboard-api-alpha.vercel.app/"
)
```

### Developer Authentication

```kotlin
lifecycleScope.launch {
    try {
        // Login as developer
        val authResponse = LeaderboardHubSDK.login(
            email = "developer@example.com",
            password = "securepassword"
        )
        Log.d("SDK", "Logged in: ${authResponse.user.email}")
    } catch (e: HttpException) {
        Log.e("SDK", "Error: ${e.code()}")
    }
}
```

### View Apps & Analytics

```kotlin
lifecycleScope.launch {
    try {
        // Get all apps registered by this developer
        val gamesResponse = LeaderboardHubSDK.getMyGames()
        gamesResponse.games.forEach { game ->
            Log.d("SDK", "App: ${game.app.name}, Total Scores: ${game.total_scores}")
        }

        // Get score data across all apps
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

## Related Repositories

| Repository | Description |
|------------|-------------|
| [leaderboard-sdk-android](https://github.com/itayost/leaderboard-sdk-android) | Android SDK (Game SDK + Hub SDK) |
| [leaderboard-api](https://github.com/itayost/leaderboard-api) | Flask REST API backend |
| [leaderboard-flappy-bird](https://github.com/itayost/leaderboard-flappy-bird) | Flappy Bird demo (Game SDK) |
| [leaderboard-developer-hub](https://github.com/itayost/leaderboard-developer-hub) | Developer Hub demo (Hub SDK) |

## License

MIT License
