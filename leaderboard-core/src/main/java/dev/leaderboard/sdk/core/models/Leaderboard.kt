package dev.leaderboard.sdk.core.models

data class Leaderboard(
    val _id: String,
    val app_id: String,
    val name: String,
    val sort_order: String,
    val created_at: String
)
