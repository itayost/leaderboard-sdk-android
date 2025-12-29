package dev.leaderboard.sdk.game

import android.content.Context
import dev.leaderboard.sdk.core.api.ApiClient
import dev.leaderboard.sdk.core.models.*
import retrofit2.HttpException

object LeaderboardGameSDK {
    private const val DEFAULT_URL = "https://leaderboard-api-alpha.vercel.app/"
    private const val BEARER_PREFIX = "Bearer "

    private lateinit var api: GameApi
    private lateinit var apiKey: String

    @Volatile private var currentPlayer: Player? = null
    @Volatile private var currentUser: User? = null
    @Volatile private var initialized = false

    /**
     * Initialize the SDK. Must be called before any other methods.
     *
     * @param context Application context
     * @param apiKey Your game's API key
     * @param baseUrl Optional custom API base URL
     */
    fun init(context: Context, apiKey: String, baseUrl: String = DEFAULT_URL) {
        this.apiKey = apiKey
        this.api = ApiClient.create(baseUrl, GameApi::class.java)
        DeviceManager.init(context)
        TokenManager.init(context)
        initialized = true
    }

    /** Check if the SDK has been initialized. */
    fun isInitialized(): Boolean = initialized

    /** Get the current player, or null if not registered/restored. */
    fun getCurrentPlayer(): Player? = currentPlayer

    /** Get the current logged-in user, or null if not logged in or after tryAutoRestore(). */
    fun getCurrentUser(): User? = currentUser

    /** Check if the current player is linked to a user account. */
    fun isLinkedToUser(): Boolean = currentPlayer?.isLinked == true

    /** Check if the user is logged in (has a stored token). */
    fun isLoggedIn(): Boolean = TokenManager.hasToken()

    /** Get the stored auth token, or null if not logged in. */
    fun getAuthToken(): String? = TokenManager.getToken()

    /**
     * Register an anonymous player linked to this device.
     *
     * @param username Display name for the player
     * @param avatarUrl Optional avatar URL
     * @return The created Player
     */
    suspend fun registerPlayer(username: String, avatarUrl: String? = null): Player {
        checkInitialized()
        val body = mutableMapOf<String, Any>(
            "device_id" to DeviceManager.getDeviceId(),
            "username" to username
        )
        avatarUrl?.let { body["avatar_url"] = it }

        val response = api.registerPlayer(apiKey, body)
        currentPlayer = response.player
        return response.player
    }

    /**
     * Restore an existing anonymous player by device ID.
     *
     * @return The restored Player
     * @throws HttpException with code 404 if no player exists for this device
     */
    suspend fun restorePlayer(): Player {
        checkInitialized()
        val response = api.getPlayerByDevice(apiKey, DeviceManager.getDeviceId())
        currentPlayer = response.player
        return response.player
    }

    /**
     * Link the current player to a user account.
     *
     * @param userToken JWT token from login/register
     * @return The updated Player with user_id set
     */
    suspend fun linkToUser(userToken: String): Player {
        checkInitialized()
        val player = currentPlayer ?: throw IllegalStateException("No current player. Call registerPlayer or restorePlayer first.")
        val response = api.linkPlayer(apiKey, player._id, "$BEARER_PREFIX$userToken")
        currentPlayer = response.player
        return response.player
    }

    /**
     * Register a new user account and link the current player to it.
     * If no player exists, creates an anonymous player first.
     * All anonymous scores will be preserved when linked.
     *
     * Note: If registerPlayer() succeeds but registerUser() fails, the anonymous
     * player will remain. Retry will use the existing player.
     *
     * @param email User's email address
     * @param password User's password
     * @param username Display name
     * @param avatarUrl Optional avatar URL
     * @return AuthResponse with user info and token
     */
    suspend fun register(
        email: String,
        password: String,
        username: String,
        avatarUrl: String? = null
    ): AuthResponse {
        checkInitialized()

        // Create anonymous player first if none exists
        if (currentPlayer == null) {
            registerPlayer(username, avatarUrl)
        }

        // Register user account
        val userBody = mutableMapOf(
            "email" to email,
            "password" to password,
            "username" to username
        )
        avatarUrl?.let { userBody["avatar_url"] = it }

        val authResponse = api.registerUser(userBody)

        // Save token
        TokenManager.saveToken(authResponse.token)
        currentUser = authResponse.user

        // Link current player to user (merges scores)
        linkToUser(authResponse.token)

        return authResponse
    }

    /**
     * Login with existing user account.
     * Fetches the player linked to this user if one exists.
     *
     * @param email User's email address
     * @param password User's password
     * @return AuthResponse with user info and token
     */
    suspend fun login(email: String, password: String): AuthResponse {
        checkInitialized()

        val authResponse = api.loginUser(
            mapOf(
                "email" to email,
                "password" to password
            )
        )

        // Save token
        TokenManager.saveToken(authResponse.token)
        currentUser = authResponse.user

        // Fetch player linked to this user
        try {
            val playerResponse = api.getPlayerByUser(apiKey, "$BEARER_PREFIX${authResponse.token}")
            currentPlayer = playerResponse.player
        } catch (e: HttpException) {
            if (e.code() != 404) throw e
            // User has no player in this game yet - that's okay
            // They can create one with registerPlayer()
        }

        return authResponse
    }

    /**
     * Logout current user. Clears token and reverts to anonymous state.
     */
    fun logout() {
        TokenManager.clearToken()
        currentUser = null
        currentPlayer = null
    }

    /**
     * Try to auto-restore session from saved token.
     * Call this after init() to automatically sign in returning users.
     *
     * Note: This only restores the player, not the user info. After calling this,
     * isLoggedIn() returns true but getCurrentUser() returns null. Use login()
     * if you need full user info.
     *
     * @return The player if restore succeeded, null otherwise
     */
    suspend fun tryAutoRestore(): Player? {
        if (!initialized) return null

        val token = TokenManager.getToken() ?: return null

        return try {
            val playerResponse = api.getPlayerByUser(apiKey, "$BEARER_PREFIX$token")
            currentPlayer = playerResponse.player
            playerResponse.player
        } catch (e: Exception) {
            // Token expired or invalid - clear it
            TokenManager.clearToken()
            currentUser = null
            null
        }
    }

    /**
     * Create a new leaderboard.
     *
     * @param name Leaderboard name
     * @param sortOrder "desc" for highest first, "asc" for lowest first
     */
    suspend fun createLeaderboard(name: String, sortOrder: String = "desc"): Leaderboard {
        checkInitialized()
        val response = api.createLeaderboard(
            apiKey,
            mapOf("name" to name, "sort_order" to sortOrder)
        )
        return response.leaderboard
    }

    /** Get all leaderboards for this game. */
    suspend fun getLeaderboards(): LeaderboardsResponse {
        checkInitialized()
        return api.getLeaderboards(apiKey)
    }

    /**
     * Submit a score for the current player.
     *
     * @param leaderboardId The leaderboard to submit to
     * @param score The score value
     * @param metadata Optional additional data
     */
    suspend fun submitScore(
        leaderboardId: String,
        score: Long,
        metadata: Map<String, Any>? = null
    ): Score {
        checkInitialized()
        val player = currentPlayer ?: throw IllegalStateException("No current player. Call registerPlayer or restorePlayer first.")

        val body = mutableMapOf<String, Any>(
            "leaderboard_id" to leaderboardId,
            "player_id" to player._id,
            "score" to score
        )
        metadata?.let { body["metadata"] = it }

        val response = api.submitScore(apiKey, body)
        return response.score
    }

    /**
     * Get top scores for a leaderboard.
     *
     * @param leaderboardId The leaderboard ID
     * @param limit Maximum number of scores to return (default 10)
     */
    suspend fun getTopScores(leaderboardId: String, limit: Int = 10): TopScoresResponse {
        checkInitialized()
        return api.getTopScores(apiKey, leaderboardId, limit)
    }

    /**
     * Get the current player's rank and nearby players.
     *
     * @param leaderboardId The leaderboard ID
     * @param nearby Number of players above/below to include
     */
    suspend fun getPlayerRank(leaderboardId: String, nearby: Int = 5): PlayerRankResponse {
        checkInitialized()
        val player = currentPlayer ?: throw IllegalStateException("No current player. Call registerPlayer or restorePlayer first.")
        return api.getPlayerRank(apiKey, leaderboardId, player._id, nearby)
    }

    private fun checkInitialized() {
        if (!initialized) {
            throw IllegalStateException("LeaderboardGameSDK not initialized. Call init() first.")
        }
    }

    private fun getAuthTokenOrThrow(): String {
        return TokenManager.getToken()
            ?: throw IllegalStateException("User not logged in. Call login() or register() first.")
    }
}
