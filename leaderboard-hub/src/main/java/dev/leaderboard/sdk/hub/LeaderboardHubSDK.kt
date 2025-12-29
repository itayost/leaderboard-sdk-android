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
