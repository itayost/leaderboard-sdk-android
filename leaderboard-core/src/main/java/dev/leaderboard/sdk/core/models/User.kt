package dev.leaderboard.sdk.core.models

data class User(
    val _id: String,
    val email: String,
    val username: String,
    val avatar_url: String?,
    val created_at: String,
    val updated_at: String? = null
)
