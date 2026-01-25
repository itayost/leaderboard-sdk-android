package dev.leaderboard.sdk.game

import android.content.Context
import dev.leaderboard.sdk.core.storage.TokenManager as CoreTokenManager

/**
 * Game SDK token manager - delegates to core TokenManager.
 */
internal object TokenManager {
    private const val PREFS_NAME = "leaderboard_game_secure_prefs"

    private var manager: CoreTokenManager? = null

    val isInitialized: Boolean
        get() = manager != null

    fun init(context: Context) {
        manager = CoreTokenManager(context, PREFS_NAME)
    }

    fun saveToken(token: String) {
        manager?.saveToken(token)
            ?: throw IllegalStateException("TokenManager not initialized")
    }

    fun getToken(): String? {
        return manager?.getToken()
    }

    fun clearToken() {
        manager?.clearToken()
    }

    fun hasToken(): Boolean {
        return manager?.hasToken() ?: false
    }
}
