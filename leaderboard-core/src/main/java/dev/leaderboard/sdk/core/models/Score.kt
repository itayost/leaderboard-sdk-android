package dev.leaderboard.sdk.core.models

data class Score(
    val _id: String,
    val leaderboard_id: String,
    val player_id: String,
    val score: Long,
    val metadata: Map<String, Any>?,
    val created_at: String
)
