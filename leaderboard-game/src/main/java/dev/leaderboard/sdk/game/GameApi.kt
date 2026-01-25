package dev.leaderboard.sdk.game

import dev.leaderboard.sdk.core.models.*
import retrofit2.http.*

internal interface GameApi {
    // Auth endpoints
    @POST("users/register")
    suspend fun registerUser(
        @Body body: Map<String, String>
    ): AuthResponse

    @POST("users/login")
    suspend fun loginUser(
        @Body body: Map<String, String>
    ): AuthResponse

    @GET("players/by-user")
    suspend fun getPlayerByUser(
        @Header("X-API-Key") apiKey: String,
        @Header("Authorization") userToken: String
    ): PlayerResponse

    // Player endpoints
    @POST("players")
    suspend fun registerPlayer(
        @Header("X-API-Key") apiKey: String,
        @Body body: @JvmSuppressWildcards Map<String, Any>
    ): PlayerResponse

    @GET("players/by-device/{deviceId}")
    suspend fun getPlayerByDevice(
        @Header("X-API-Key") apiKey: String,
        @Path("deviceId") deviceId: String
    ): PlayerResponse

    @POST("leaderboards")
    suspend fun createLeaderboard(
        @Header("X-API-Key") apiKey: String,
        @Body body: Map<String, String>
    ): LeaderboardResponse

    @GET("leaderboards")
    suspend fun getLeaderboards(
        @Header("X-API-Key") apiKey: String
    ): LeaderboardsResponse

    @POST("scores")
    suspend fun submitScore(
        @Header("X-API-Key") apiKey: String,
        @Body body: @JvmSuppressWildcards Map<String, Any>
    ): ScoreWithTrophiesResponse

    @GET("scores/{leaderboardId}")
    suspend fun getTopScores(
        @Header("X-API-Key") apiKey: String,
        @Path("leaderboardId") leaderboardId: String,
        @Query("limit") limit: Int
    ): TopScoresResponse

    @GET("scores/{leaderboardId}/player/{playerId}")
    suspend fun getPlayerRank(
        @Header("X-API-Key") apiKey: String,
        @Path("leaderboardId") leaderboardId: String,
        @Path("playerId") playerId: String,
        @Query("nearby") nearby: Int
    ): PlayerRankResponse

    // ==================== Trophy Endpoints ====================

    @GET("trophies")
    suspend fun getAllTrophies(
        @Header("X-API-Key") apiKey: String
    ): TrophiesResponse

    @GET("trophies/player/{playerId}")
    suspend fun getPlayerTrophies(
        @Header("X-API-Key") apiKey: String,
        @Path("playerId") playerId: String,
        @Query("status") status: String?
    ): PlayerTrophiesResponse

    @POST("trophies/trigger")
    suspend fun triggerTrophy(
        @Header("X-API-Key") apiKey: String,
        @Body body: @JvmSuppressWildcards Map<String, Any>
    ): TrophyTriggerResponse

    @GET("trophies/{trophyId}/player/{playerId}/progress")
    suspend fun getTrophyProgress(
        @Header("X-API-Key") apiKey: String,
        @Path("trophyId") trophyId: String,
        @Path("playerId") playerId: String
    ): TrophyProgressResponse
}
