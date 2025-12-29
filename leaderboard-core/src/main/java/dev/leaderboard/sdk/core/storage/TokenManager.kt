package dev.leaderboard.sdk.core.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Secure token storage using EncryptedSharedPreferences.
 * Each SDK should create its own instance with a unique prefs name.
 *
 * @param context Application context
 * @param prefsName Unique name for the encrypted preferences file
 */
class TokenManager(context: Context, prefsName: String) {
    private val prefs: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context,
            prefsName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Save authentication token securely.
     */
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    /**
     * Get stored authentication token.
     * @return Token string or null if not stored
     */
    fun getToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Clear stored authentication token.
     */
    fun clearToken() {
        prefs.edit().remove(KEY_AUTH_TOKEN).apply()
    }

    /**
     * Check if a token is stored.
     */
    fun hasToken(): Boolean {
        return getToken() != null
    }

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}
