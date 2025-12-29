package dev.leaderboard.sdk.hub

import dev.leaderboard.sdk.core.models.*
import retrofit2.http.*

internal interface HubApi {
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
}
