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

// ==================== Trophy Responses ====================

data class TrophyResponse(
    val message: String,
    val trophy: Trophy
)

data class TrophiesResponse(
    val trophies: List<Trophy>,
    val total: Int
)

data class PlayerTrophiesResponse(
    val player: Player,
    val trophies: List<TrophyWithProgress>,
    val stats: TrophyStats
)

data class TrophyTriggerResponse(
    val message: String,
    val newly_earned: List<Trophy>,
    val updated_progress: List<TrophyWithProgress>
)

data class TrophyProgressResponse(
    val trophy: Trophy,
    val progress: TrophyProgress
)

// Score response with trophies earned (updated)
data class ScoreWithTrophiesResponse(
    val message: String,
    val score: Score,
    val trophies_earned: List<Trophy>?
)

// ==================== Stats/Analytics Responses ====================

data class AppOverviewStats(
    val total_players: Int,
    val active_players_7d: Int,
    val active_players_30d: Int,
    val total_scores: Int,
    val total_leaderboards: Int,
    val average_score: Double,
    val new_players_today: Int
)

data class AppStatsResponse(
    val app_id: String,
    val app_name: String,
    val stats: AppOverviewStats,
    val generated_at: String
)

data class DailyScoreData(
    val date: String,
    val count: Int
)

data class DailyScoresResponse(
    val app_id: String,
    val period_days: Int,
    val data: List<DailyScoreData>,
    val total_scores: Int,
    val generated_at: String
)

data class ScoreDistributionBucket(
    val range: String,
    val count: Int,
    val percentage: Double
)

data class LeaderboardDistributionResponse(
    val leaderboard_id: String,
    val leaderboard_name: String,
    val distribution: List<ScoreDistributionBucket>,
    val total_scores: Int,
    val min_score: Int,
    val max_score: Int,
    val avg_score: Double,
    val generated_at: String
)

data class PlayerActivityData(
    val date: String,
    val new_players: Int,
    val active_players: Int
)

data class PlayerActivitySummary(
    val total_new_players: Int,
    val avg_daily_active: Double
)

data class PlayerActivityResponse(
    val app_id: String,
    val period_days: Int,
    val timeline: List<PlayerActivityData>,
    val summary: PlayerActivitySummary,
    val generated_at: String
)

data class LeaderboardStatsInfo(
    val total_scores: Int,
    val unique_players: Int,
    val avg_score: Double,
    val max_score: Int,
    val min_score: Int
)

data class LeaderboardRecentActivity(
    val period_days: Int,
    val scores_in_period: Int,
    val active_players: Int
)

data class LeaderboardStatsResponse(
    val leaderboard_id: String,
    val leaderboard_name: String,
    val stats: LeaderboardStatsInfo,
    val recent_activity: LeaderboardRecentActivity,
    val generated_at: String
)

// ==================== Enhanced Analytics Responses ====================

// Trophy Analytics
data class TrophyAnalytics(
    val trophy_id: String,
    val name: String,
    val description: String,
    val rarity: String,
    val points: Int,
    val times_earned: Int,
    val earn_rate_percent: Double,
    val avg_days_to_earn: Double?,
    val first_earned: String?,
    val last_earned: String?
)

data class RarityStats(
    val total: Int,
    val avg_earn_rate: Double
)

data class TrophyStatsResponse(
    val app_id: String,
    val total_trophies: Int,
    val total_awarded: Int,
    val overall_completion_rate: Double,
    val trophies: List<TrophyAnalytics>,
    val rarity_distribution: Map<String, RarityStats>,
    val generated_at: String
)

// Engagement Metrics
data class PlayFrequency(
    val daily_active: Int,
    val weekly_active: Int,
    val monthly_active: Int
)

data class PeakActivity(
    val hour_of_day: Int,
    val day_of_week: String
)

data class PlaysDistribution(
    val plays_range: String,
    val player_count: Int
)

data class EngagementStatsResponse(
    val app_id: String,
    val total_plays: Int,
    val unique_players: Int,
    val avg_plays_per_player: Double,
    val play_frequency: PlayFrequency,
    val peak_activity: PeakActivity,
    val plays_distribution: List<PlaysDistribution>,
    val generated_at: String
)

// Competition Stats
data class LeaderboardCompetition(
    val leaderboard_id: String,
    val name: String,
    val total_participants: Int,
    val avg_attempts_per_player: Double,
    val score_improvement_rate: Double,
    val top_score: Long,
    val median_score: Long,
    val competition_intensity: String,
    val recent_rank_changes: Int
)

data class CompetitionStatsResponse(
    val app_id: String,
    val leaderboards: List<LeaderboardCompetition>,
    val generated_at: String
)

// Player Segments
data class PlayerSegment(
    val count: Int,
    val criteria: String,
    val avg_plays: Double,
    val avg_trophies: Double
)

data class PlayerSegmentsResponse(
    val app_id: String,
    val segments: Map<String, PlayerSegment>,
    val generated_at: String
)
