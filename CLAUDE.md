# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build all modules
./gradlew build

# Build individual modules
./gradlew :leaderboard-core:build
./gradlew :leaderboard-game:build
./gradlew :leaderboard-hub:build
./gradlew :sample-game:build
./gradlew :sample-hub:build

# Assemble release
./gradlew assembleRelease

# Run tests
./gradlew test

# Clean build
./gradlew clean build
```

## Architecture

This is a **multi-module Android SDK** for game leaderboards using **Retrofit 3.0** with native Kotlin coroutines.

### Module Structure

```
┌─────────────────┐     ┌─────────────────┐
│  sample-game    │     │   sample-hub    │
└────────┬────────┘     └────────┬────────┘
         │                       │
         ▼                       ▼
┌─────────────────┐     ┌─────────────────┐
│ leaderboard-game│     │ leaderboard-hub │
└────────┬────────┘     └────────┬────────┘
         │                       │
         └───────────┬───────────┘
                     ▼
            ┌─────────────────┐
            │ leaderboard-core│
            └─────────────────┘
```

### Two SDKs

1. **Game SDK** (`leaderboard-game`) - For game developers:
   - Anonymous players (device-based, no account required)
   - Optional user registration/login (email/password)
   - Auto sign-in for returning registered users
   - Score submission, leaderboards, rankings
   - Link anonymous scores to user account when registering

2. **Hub SDK** (`leaderboard-hub`) - For Game Hub app:
   - User registration/login with JWT
   - Cross-game stats and score aggregation
   - Encrypted token storage via EncryptedSharedPreferences

### Core Module (`leaderboard-core`)

Shared code:
- `models/` - Data classes (Player, Score, Leaderboard, User, Responses)
- `api/ApiClient.kt` - Retrofit factory

## Game SDK API

### Initialization & Auth

```kotlin
// Initialize
LeaderboardGameSDK.init(context, apiKey, baseUrl)

// Auto sign-in returning users
suspend fun tryAutoRestore(): Player?

// Anonymous player
suspend fun registerPlayer(username: String, avatarUrl: String?): Player
suspend fun restorePlayer(): Player

// User registration/login
suspend fun register(email: String, password: String, username: String): AuthResponse
suspend fun login(email: String, password: String): AuthResponse
fun logout()

// Auth status
fun isLoggedIn(): Boolean
fun getAuthToken(): String?
fun getCurrentUser(): User?
fun getCurrentPlayer(): Player?
fun isLinkedToUser(): Boolean
```

### Leaderboard Operations

```kotlin
suspend fun submitScore(leaderboardId: String, score: Long, metadata: Map<String, Any>?): Score
suspend fun getTopScores(leaderboardId: String, limit: Int): TopScoresResponse
suspend fun getPlayerRank(leaderboardId: String, nearby: Int): PlayerRankResponse
suspend fun createLeaderboard(name: String, sortOrder: String): Leaderboard
suspend fun getLeaderboards(): LeaderboardsResponse
```

### Retrofit 3.0 Pattern

All API methods are **suspend functions** that return types directly (no `Call<T>`):

```kotlin
interface GameApi {
    @POST("players")
    suspend fun registerPlayer(
        @Header("X-API-Key") apiKey: String,
        @Body body: Map<String, Any?>
    ): PlayerResponse

    @POST("users/register")
    suspend fun registerUser(
        @Body body: Map<String, String>
    ): AuthResponse

    @GET("players/by-user")
    suspend fun getPlayerByUser(
        @Header("X-API-Key") apiKey: String,
        @Header("Authorization") userToken: String
    ): PlayerResponse
}
```

Error handling uses **exceptions** (not callbacks):
- `HttpException` for HTTP errors (4xx, 5xx)
- `IOException` for network errors

## Key Technical Details

- **Min SDK**: 21 (Android 5.0)
- **Kotlin**: 2.0+
- **Retrofit**: 3.0.0 (native coroutines)
- **JWT Storage**: EncryptedSharedPreferences (both SDKs)
- **Device ID**: UUID stored in SharedPreferences (Game SDK)

## Key Files

### Game SDK
- `LeaderboardGameSDK.kt` - Main public API (singleton)
- `GameApi.kt` - Retrofit interface for API calls
- `TokenManager.kt` - Secure JWT storage
- `DeviceManager.kt` - Device UUID management

### Hub SDK
- `LeaderboardHubSDK.kt` - Main public API (singleton)
- `HubApi.kt` - Retrofit interface
- `TokenManager.kt` - Secure JWT storage

### Core
- `models/Player.kt` - Player data class with `isLinked`/`isAnonymous`
- `models/User.kt` - User data class
- `models/Responses.kt` - All API response wrappers
- `api/ApiClient.kt` - Retrofit factory

## Backend API

The SDK connects to a Flask + MongoDB Atlas backend.

**Game SDK endpoints**:
- `POST /players` - Create anonymous player
- `GET /players/by-device/{deviceId}` - Get player by device
- `GET /players/by-user` - Get player by user token
- `POST /players/{id}/link` - Link player to user
- `POST /users/register` - Register user account
- `POST /users/login` - Login user
- `POST /scores` - Submit score
- `GET /scores/{leaderboardId}` - Get top scores
- `GET /scores/{leaderboardId}/player/{playerId}` - Get player rank

**Hub SDK endpoints**:
- `/users/register`, `/users/login`, `/users/me`
- `/users/me/games` - Get all games user played
- `/users/me/scores` - Get all scores across games

## User Flow

```
First Launch:
1. SDK.init() → initializes TokenManager
2. SDK.tryAutoRestore() → checks for saved token
   - Token found → fetch player by user → auto signed in
   - No token → play anonymously

Anonymous Play:
1. SDK.registerPlayer("name") → creates device-linked player
2. SDK.submitScore(...) → scores linked to anonymous player

Optional Registration:
1. SDK.register(email, password, username) → creates user + links player
2. Token saved → next launch auto-restores
3. All anonymous scores preserved (merged)

Next Launch:
1. SDK.tryAutoRestore() → finds token → fetches player → signed in
```
