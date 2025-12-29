package dev.leaderboard.sdk.core.models

// Player responses
data class PlayerResponse(
    val message: String,
    val player: Player
)

// Score responses
data class ScoreResponse(
    val message: String,
    val score: Score
)

data class ScoreEntry(
    val rank: Int,
    val score: Score,
    val player: Player
)

data class TopScoresResponse(
    val leaderboard: Leaderboard,
    val scores: List<ScoreEntry>,
    val total_scores: Int
)

data class PlayerRankResponse(
    val player: Player,
    val best_score: Score,
    val rank: Int,
    val total_players: Int,
    val nearby: List<ScoreEntry>
)

// Leaderboard responses
data class LeaderboardResponse(
    val message: String,
    val leaderboard: Leaderboard
)

data class LeaderboardsResponse(
    val leaderboards: List<Leaderboard>,
    val total: Int
)

// Auth responses (Hub SDK)
data class AuthResponse(
    val message: String,
    val user: User,
    val token: String
)

// User profile responses
data class UserResponse(
    val user: User
)

data class UserUpdateResponse(
    val message: String,
    val user: User
)

// Hub SDK responses
data class App(
    val _id: String,
    val name: String
)

data class LeaderboardInfo(
    val _id: String,
    val name: String
)

data class ScoreInfo(
    val _id: String,
    val score: Long,
    val created_at: String
)

data class UserGamePlayer(
    val _id: String,
    val username: String
)

data class BestScore(
    val leaderboard: LeaderboardInfo,
    val score: Long
)

data class UserGame(
    val app: App,
    val player: UserGamePlayer,
    val total_scores: Int,
    val best_scores: List<BestScore>
)

data class UserGamesResponse(
    val user: User,
    val games: List<UserGame>,
    val total_games: Int
)

data class UserScoreEntry(
    val score: ScoreInfo,
    val player: UserGamePlayer,
    val leaderboard: LeaderboardInfo?,
    val app: App?
)

data class UserScoresResponse(
    val user: User,
    val scores: List<UserScoreEntry>,
    val total_scores: Int
)
