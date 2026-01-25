package dev.leaderboard.sdk.core.models

data class Player(
    val _id: String,
    val app_id: String,
    val device_id: String?,
    val user_id: String?,
    val username: String,
    val avatar_url: String?,
    val created_at: String,
    val linked_at: String? = null
) {
    val isLinked: Boolean
        get() = user_id != null

    val isAnonymous: Boolean
        get() = user_id == null
}
