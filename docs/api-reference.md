---
layout: default
title: API Reference
---

# API Reference

Complete API reference for the Leaderboard SDK.

## Game SDK - LeaderboardGameSDK

### Initialization

| Method | Description |
|--------|-------------|
| `init(context, apiKey, baseUrl?)` | Initialize the SDK |
| `isInitialized()` | Check if SDK is initialized |

### Player Management

| Method | Returns | Description |
|--------|---------|-------------|
| `registerPlayer(username, avatarUrl?)` | `Player` | Create anonymous player |
| `restorePlayer()` | `Player` | Restore player by device ID |
| `getCurrentPlayer()` | `Player?` | Get current player |

### Authentication

| Method | Returns | Description |
|--------|---------|-------------|
| `register(email, password, username, avatarUrl?)` | `AuthResponse` | Register user account |
| `login(email, password)` | `AuthResponse` | Login user |
| `logout()` | `Unit` | Clear token and session |
| `tryAutoRestore()` | `Player?` | Auto sign-in returning users |
| `isLoggedIn()` | `Boolean` | Check if authenticated |
| `getAuthToken()` | `String?` | Get stored JWT token |
| `getCurrentUser()` | `User?` | Get current user |
| `isLinkedToUser()` | `Boolean` | Check if player is linked |

### Leaderboards

| Method | Returns | Description |
|--------|---------|-------------|
| `createLeaderboard(name, sortOrder)` | `Leaderboard` | Create new leaderboard |
| `getLeaderboards()` | `LeaderboardsResponse` | Get all leaderboards |
| `submitScore(leaderboardId, score, metadata?)` | `Score` | Submit a score |
| `getTopScores(leaderboardId, limit?)` | `TopScoresResponse` | Get top scores |
| `getPlayerRank(leaderboardId, nearby?)` | `PlayerRankResponse` | Get player's rank |

---

## Hub SDK - LeaderboardHubSDK

### Initialization

| Method | Description |
|--------|-------------|
| `init(context, baseUrl?)` | Initialize the SDK |
| `isInitialized()` | Check if SDK is initialized |

### Authentication

| Method | Returns | Description |
|--------|---------|-------------|
| `register(email, password, username, avatarUrl?)` | `AuthResponse` | Register new user |
| `login(email, password)` | `AuthResponse` | Login user |
| `logout()` | `Unit` | Clear token and session |
| `tryAutoRestore()` | `User?` | Auto sign-in returning users |
| `isLoggedIn()` | `Boolean` | Check if authenticated |
| `getAuthToken()` | `String?` | Get stored JWT token |
| `getCurrentUser()` | `User?` | Get current user |

### User Profile

| Method | Returns | Description |
|--------|---------|-------------|
| `getUserProfile()` | `User` | Get user profile |
| `updateProfile(username?, avatarUrl?)` | `User` | Update profile |

### Cross-Game Stats

| Method | Returns | Description |
|--------|---------|-------------|
| `getUserGames()` | `UserGamesResponse` | Get all games played |
| `getUserScores(limit?)` | `UserScoresResponse` | Get all scores |
| `getAllScores(limit?)` | `UserScoresResponse` | Alias for getUserScores |

---

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
    val isLinked: Boolean   // true if linked to user
    val isAnonymous: Boolean // true if anonymous
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

### Leaderboard

```kotlin
data class Leaderboard(
    val _id: String,
    val app_id: String,
    val name: String,
    val sort_order: String,
    val created_at: String
)
```

### AuthResponse

```kotlin
data class AuthResponse(
    val user: User,
    val token: String
)
```

### ScoreEntry

```kotlin
data class ScoreEntry(
    val rank: Int,
    val player: Player,
    val score: Score
)
```

### TopScoresResponse

```kotlin
data class TopScoresResponse(
    val leaderboard: Leaderboard,
    val scores: List<ScoreEntry>,
    val total_players: Int
)
```

### PlayerRankResponse

```kotlin
data class PlayerRankResponse(
    val player: Player,
    val best_score: Score,
    val rank: Int,
    val total_players: Int,
    val nearby: List<ScoreEntry>
)
```

---

## HTTP Error Codes

| Code | Meaning |
|------|---------|
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Invalid API key or token |
| 404 | Not Found - Resource doesn't exist |
| 409 | Conflict - Already exists |
| 500 | Server Error |

## Exception Types

| Exception | When Thrown |
|-----------|-------------|
| `HttpException` | HTTP errors (4xx, 5xx) |
| `IOException` | Network errors |
| `IllegalStateException` | SDK not initialized or missing player |
