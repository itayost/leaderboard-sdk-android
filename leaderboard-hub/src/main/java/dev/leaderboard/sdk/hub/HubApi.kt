package dev.leaderboard.sdk.hub

import dev.leaderboard.sdk.core.models.*
import retrofit2.http.*

internal interface HubApi {
    // ==================== User Auth ====================

    @POST("users/register")
    suspend fun register(
        @Body body: Map<String, String?>
    ): AuthResponse

    @POST("users/login")
    suspend fun login(
        @Body body: Map<String, String>
    ): AuthResponse

    @GET("users/me")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): UserResponse

    @PUT("users/me")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body body: Map<String, String?>
    ): UserUpdateResponse

    @GET("users/me/games")
    suspend fun getUserGames(
        @Header("Authorization") token: String
    ): UserGamesResponse

    @GET("users/me/scores")
    suspend fun getUserScores(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int
    ): UserScoresResponse

    // ==================== App Management (Developer Portal) ====================

    @POST("apps")
    suspend fun createApp(
        @Header("Authorization") token: String,
        @Body body: Map<String, String?>
    ): AppCreationResponse

    @GET("apps/mine")
    suspend fun getMyApps(
        @Header("Authorization") token: String
    ): MyAppsResponse

    @DELETE("apps/{appId}")
    suspend fun deleteApp(
        @Header("Authorization") token: String,
        @Path("appId") appId: String
    ): MessageResponse

    // ==================== Leaderboard Management ====================

    @POST("leaderboards")
    suspend fun createLeaderboard(
        @Header("X-API-Key") apiKey: String,
        @Body body: Map<String, String>
    ): LeaderboardResponse

    @GET("leaderboards")
    suspend fun getLeaderboards(
        @Header("X-API-Key") apiKey: String
    ): LeaderboardsResponse

    @GET("leaderboards/{leaderboardId}")
    suspend fun getLeaderboard(
        @Header("X-API-Key") apiKey: String,
        @Path("leaderboardId") leaderboardId: String
    ): LeaderboardResponse

    @DELETE("leaderboards/{leaderboardId}")
    suspend fun deleteLeaderboard(
        @Header("X-API-Key") apiKey: String,
        @Path("leaderboardId") leaderboardId: String
    ): MessageResponse

    // ==================== Trophy Management ====================

    @POST("trophies")
    suspend fun createTrophy(
        @Header("X-API-Key") apiKey: String,
        @Body body: @JvmSuppressWildcards Map<String, Any?>
    ): TrophyResponse

    @GET("trophies")
    suspend fun getTrophies(
        @Header("X-API-Key") apiKey: String
    ): TrophiesResponse

    @GET("trophies/{trophyId}")
    suspend fun getTrophy(
        @Header("X-API-Key") apiKey: String,
        @Path("trophyId") trophyId: String
    ): TrophyResponse

    @PUT("trophies/{trophyId}")
    suspend fun updateTrophy(
        @Header("X-API-Key") apiKey: String,
        @Path("trophyId") trophyId: String,
        @Body body: @JvmSuppressWildcards Map<String, Any?>
    ): TrophyResponse

    @DELETE("trophies/{trophyId}")
    suspend fun deleteTrophy(
        @Header("X-API-Key") apiKey: String,
        @Path("trophyId") trophyId: String
    ): MessageResponse

    // ==================== Stats & Analytics ====================

    @GET("stats/overview")
    suspend fun getAppStats(
        @Header("X-API-Key") apiKey: String
    ): AppStatsResponse

    @GET("stats/scores/daily")
    suspend fun getDailyScores(
        @Header("X-API-Key") apiKey: String,
        @Query("days") days: Int
    ): DailyScoresResponse

    @GET("stats/leaderboards/{leaderboardId}/distribution")
    suspend fun getLeaderboardDistribution(
        @Header("X-API-Key") apiKey: String,
        @Path("leaderboardId") leaderboardId: String
    ): LeaderboardDistributionResponse

    @GET("stats/players/activity")
    suspend fun getPlayerActivity(
        @Header("X-API-Key") apiKey: String,
        @Query("days") days: Int
    ): PlayerActivityResponse

    @GET("stats/leaderboards/{leaderboardId}")
    suspend fun getLeaderboardStats(
        @Header("X-API-Key") apiKey: String,
        @Path("leaderboardId") leaderboardId: String,
        @Query("days") days: Int
    ): LeaderboardStatsResponse

    // ==================== Enhanced Analytics ====================

    @GET("stats/trophies")
    suspend fun getTrophyStats(
        @Header("X-API-Key") apiKey: String
    ): TrophyStatsResponse

    @GET("stats/engagement")
    suspend fun getEngagementStats(
        @Header("X-API-Key") apiKey: String
    ): EngagementStatsResponse

    @GET("stats/competition")
    suspend fun getCompetitionStats(
        @Header("X-API-Key") apiKey: String
    ): CompetitionStatsResponse

    @GET("stats/segments")
    suspend fun getPlayerSegments(
        @Header("X-API-Key") apiKey: String
    ): PlayerSegmentsResponse
}

// Additional response types for Hub SDK
data class AppCreationResponse(
    val message: String,
    val app_id: String,
    val api_key: String
)

data class MyAppsResponse(
    val apps: List<AppDetails>,
    val total: Int
)

data class AppDetails(
    val _id: String,
    val name: String,
    val api_key: String,
    val created_at: String
)

data class MessageResponse(
    val message: String
)
