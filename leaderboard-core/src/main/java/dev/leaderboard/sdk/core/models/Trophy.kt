package dev.leaderboard.sdk.core.models

/**
 * Trophy/Achievement definition
 */
data class Trophy(
    val _id: String,
    val app_id: String,
    val name: String,
    val description: String,
    val icon_url: String?,
    val trophy_type: String,  // "point_based", "count_based", "manual"
    val trigger: TrophyTrigger,
    val rarity: String,  // "common", "rare", "epic", "legendary"
    val points: Int,
    val created_at: String,
    val updated_at: String
)

/**
 * Trophy trigger configuration
 */
data class TrophyTrigger(
    val leaderboard_id: String? = null,  // For score_threshold - specific leaderboard
    val threshold: Long? = null,  // For point_based trophies
    val count_target: Int? = null,  // For count_based trophies
    val event_key: String? = null  // For manual triggers
)

/**
 * Player's progress on a trophy
 */
data class PlayerTrophy(
    val _id: String,
    val player_id: String,
    val trophy_id: String,
    val app_id: String,
    val status: String,  // "in_progress", "earned"
    val current_progress: Long,
    val target_progress: Long,
    val earned_at: String?,
    val trigger_data: Map<String, Any>?,
    val created_at: String,
    val updated_at: String
)

/**
 * Trophy with player's progress information
 */
data class TrophyWithProgress(
    val trophy: Trophy,
    val player_trophy: PlayerTrophy?,
    val progress_percentage: Float
)

/**
 * Trophy statistics for a player
 */
data class TrophyStats(
    val total_earned: Int,
    val total_available: Int,
    val completion_percentage: Float,
    val total_points: Int
)

/**
 * Trophy progress details
 */
data class TrophyProgress(
    val status: String,
    val current: Long,
    val target: Long,
    val percentage: Float,
    val remaining: Long
)
