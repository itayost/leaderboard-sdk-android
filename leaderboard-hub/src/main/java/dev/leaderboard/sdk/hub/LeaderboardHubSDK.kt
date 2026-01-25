package dev.leaderboard.sdk.hub

import android.content.Context
import dev.leaderboard.sdk.core.api.ApiClient
import dev.leaderboard.sdk.core.models.*

object LeaderboardHubSDK {
    private const val DEFAULT_URL = "https://leaderboard-api-alpha.vercel.app/"
    private const val BEARER_PREFIX = "Bearer "

    private lateinit var api: HubApi
    @Volatile private var currentUser: User? = null
    @Volatile private var initialized = false

    fun init(context: Context, baseUrl: String = DEFAULT_URL) {
        this.api = ApiClient.create(baseUrl, HubApi::class.java)
        TokenManager.init(context)
        initialized = true
    }

    fun isInitialized(): Boolean = initialized

    fun isLoggedIn(): Boolean = TokenManager.hasToken()

    fun getAuthToken(): String? = TokenManager.getToken()

    fun getCurrentUser(): User? = currentUser

    suspend fun register(
        email: String,
        password: String,
        username: String,
        avatarUrl: String? = null
    ): AuthResponse {
        checkInitialized()
        val response = api.register(
            mapOf(
                "email" to email,
                "password" to password,
                "username" to username,
                "avatar_url" to avatarUrl
            )
        )
        TokenManager.saveToken(response.token)
        currentUser = response.user
        return response
    }

    suspend fun login(email: String, password: String): AuthResponse {
        checkInitialized()
        val response = api.login(
            mapOf(
                "email" to email,
                "password" to password
            )
        )
        TokenManager.saveToken(response.token)
        currentUser = response.user
        return response
    }

    fun logout() {
        TokenManager.clearToken()
        currentUser = null
    }

    /**
     * Try to restore a previously logged-in user session.
     * Returns the User if a valid token exists and can fetch profile,
     * null otherwise.
     */
    suspend fun tryAutoRestore(): User? {
        if (!initialized) return null
        val token = TokenManager.getToken() ?: return null

        return try {
            val response = api.getProfile("$BEARER_PREFIX$token")
            currentUser = response.user
            response.user
        } catch (e: Exception) {
            // Token might be expired or invalid
            TokenManager.clearToken()
            null
        }
    }

    suspend fun getUserProfile(): User {
        checkInitialized()
        val token = getAuthTokenOrThrow()
        val response = api.getProfile("$BEARER_PREFIX$token")
        currentUser = response.user
        return response.user
    }

    suspend fun updateProfile(username: String? = null, avatarUrl: String? = null): User {
        checkInitialized()
        val token = getAuthTokenOrThrow()
        val body = mutableMapOf<String, String?>()
        username?.let { body["username"] = it }
        avatarUrl?.let { body["avatar_url"] = it }

        val response = api.updateProfile("$BEARER_PREFIX$token", body)
        currentUser = response.user
        return response.user
    }

    suspend fun getUserGames(): UserGamesResponse {
        checkInitialized()
        val token = getAuthTokenOrThrow()
        return api.getUserGames("$BEARER_PREFIX$token")
    }

    suspend fun getAllScores(limit: Int = 50): UserScoresResponse {
        checkInitialized()
        val token = getAuthTokenOrThrow()
        return api.getUserScores("$BEARER_PREFIX$token", limit)
    }

    /**
     * Alias for getAllScores for naming consistency.
     */
    suspend fun getUserScores(limit: Int = 50): UserScoresResponse = getAllScores(limit)

    // ==================== App Management (Developer Portal) ====================

    /**
     * Create a new app/game and get its API key.
     * Requires user to be logged in.
     */
    suspend fun createApp(name: String, description: String? = null): AppCreationResponse {
        checkInitialized()
        val token = getAuthTokenOrThrow()
        return api.createApp(
            "$BEARER_PREFIX$token",
            mapOf(
                "name" to name,
                "description" to description
            )
        )
    }

    /**
     * Get all apps owned by the current user.
     * Requires user to be logged in.
     */
    suspend fun getMyApps(): MyAppsResponse {
        checkInitialized()
        val token = getAuthTokenOrThrow()
        return api.getMyApps("$BEARER_PREFIX$token")
    }

    /**
     * Delete an app and all its related data (leaderboards, scores, players, trophies).
     * Only the app owner can delete it.
     * Requires user to be logged in.
     */
    suspend fun deleteApp(appId: String) {
        checkInitialized()
        val token = getAuthTokenOrThrow()
        api.deleteApp("$BEARER_PREFIX$token", appId)
    }

    // ==================== Leaderboard Management ====================

    /**
     * Create a new leaderboard for an app.
     * @param apiKey The app's API key
     * @param name Leaderboard name
     * @param sortOrder "desc" for highest first, "asc" for lowest first
     */
    suspend fun createLeaderboard(
        apiKey: String,
        name: String,
        sortOrder: String = "desc"
    ): Leaderboard {
        checkInitialized()
        val response = api.createLeaderboard(
            apiKey,
            mapOf(
                "name" to name,
                "sort_order" to sortOrder
            )
        )
        return response.leaderboard
    }

    /**
     * Get all leaderboards for an app.
     */
    suspend fun getLeaderboards(apiKey: String): LeaderboardsResponse {
        checkInitialized()
        return api.getLeaderboards(apiKey)
    }

    /**
     * Delete a leaderboard and all its scores.
     */
    suspend fun deleteLeaderboard(apiKey: String, leaderboardId: String) {
        checkInitialized()
        api.deleteLeaderboard(apiKey, leaderboardId)
    }

    // ==================== Trophy Management ====================

    /**
     * Create a new trophy/achievement.
     * @param apiKey The app's API key
     * @param name Trophy name
     * @param description Trophy description
     * @param trophyType "point_based", "count_based", or "manual"
     * @param trigger Trigger configuration (threshold, count_target, or event_key)
     * @param rarity "common", "rare", "epic", or "legendary"
     * @param points Points awarded for earning
     * @param iconUrl Optional icon URL
     */
    suspend fun createTrophy(
        apiKey: String,
        name: String,
        description: String,
        trophyType: String,
        trigger: Map<String, Any>,
        rarity: String = "common",
        points: Int = 10,
        iconUrl: String? = null
    ): Trophy {
        checkInitialized()
        val body = mutableMapOf<String, Any?>(
            "name" to name,
            "description" to description,
            "trophy_type" to trophyType,
            "trigger" to trigger,
            "rarity" to rarity,
            "points" to points,
            "icon_url" to iconUrl
        )
        val response = api.createTrophy(apiKey, body)
        return response.trophy
    }

    /**
     * Get all trophies for an app.
     */
    suspend fun getTrophies(apiKey: String): TrophiesResponse {
        checkInitialized()
        return api.getTrophies(apiKey)
    }

    /**
     * Update a trophy.
     */
    suspend fun updateTrophy(
        apiKey: String,
        trophyId: String,
        name: String? = null,
        description: String? = null,
        points: Int? = null,
        iconUrl: String? = null
    ): Trophy {
        checkInitialized()
        val body = mutableMapOf<String, Any?>()
        name?.let { body["name"] = it }
        description?.let { body["description"] = it }
        points?.let { body["points"] = it }
        iconUrl?.let { body["icon_url"] = it }

        val response = api.updateTrophy(apiKey, trophyId, body)
        return response.trophy
    }

    /**
     * Delete a trophy and all player progress.
     */
    suspend fun deleteTrophy(apiKey: String, trophyId: String) {
        checkInitialized()
        api.deleteTrophy(apiKey, trophyId)
    }

    // ==================== Stats & Analytics ====================

    /**
     * Get overview statistics for an app.
     */
    suspend fun getAppStats(apiKey: String): AppStatsResponse {
        checkInitialized()
        return api.getAppStats(apiKey)
    }

    /**
     * Get daily score counts.
     * @param days Number of days (1-90)
     */
    suspend fun getDailyScores(apiKey: String, days: Int = 30): DailyScoresResponse {
        checkInitialized()
        return api.getDailyScores(apiKey, days.coerceIn(1, 90))
    }

    /**
     * Get score distribution for a leaderboard.
     */
    suspend fun getLeaderboardDistribution(
        apiKey: String,
        leaderboardId: String
    ): LeaderboardDistributionResponse {
        checkInitialized()
        return api.getLeaderboardDistribution(apiKey, leaderboardId)
    }

    /**
     * Get player activity timeline.
     * @param days Number of days (1-90)
     */
    suspend fun getPlayerActivity(apiKey: String, days: Int = 30): PlayerActivityResponse {
        checkInitialized()
        return api.getPlayerActivity(apiKey, days.coerceIn(1, 90))
    }

    /**
     * Get statistics for a specific leaderboard.
     * @param days Number of days for recent activity
     */
    suspend fun getLeaderboardStats(
        apiKey: String,
        leaderboardId: String,
        days: Int = 30
    ): LeaderboardStatsResponse {
        checkInitialized()
        return api.getLeaderboardStats(apiKey, leaderboardId, days.coerceIn(1, 90))
    }

    // ==================== Enhanced Analytics ====================

    /**
     * Get detailed trophy analytics including earn rates, popularity, and rarity distribution.
     */
    suspend fun getTrophyStats(apiKey: String): TrophyStatsResponse {
        checkInitialized()
        return api.getTrophyStats(apiKey)
    }

    /**
     * Get engagement metrics including play counts, frequency, and peak activity times.
     */
    suspend fun getEngagementStats(apiKey: String): EngagementStatsResponse {
        checkInitialized()
        return api.getEngagementStats(apiKey)
    }

    /**
     * Get leaderboard competition stats including improvement rates and competition intensity.
     */
    suspend fun getCompetitionStats(apiKey: String): CompetitionStatsResponse {
        checkInitialized()
        return api.getCompetitionStats(apiKey)
    }

    /**
     * Get player segmentation analytics (casual, regular, hardcore, top players, etc.)
     */
    suspend fun getPlayerSegments(apiKey: String): PlayerSegmentsResponse {
        checkInitialized()
        return api.getPlayerSegments(apiKey)
    }

    private fun checkInitialized() {
        if (!initialized) {
            throw IllegalStateException("LeaderboardHubSDK not initialized. Call init() first.")
        }
    }

    private fun getAuthTokenOrThrow(): String {
        return TokenManager.getToken()
            ?: throw IllegalStateException("User not logged in. Call login() or register() first.")
    }
}
