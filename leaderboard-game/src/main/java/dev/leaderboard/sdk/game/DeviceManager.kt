package dev.leaderboard.sdk.game

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

internal object DeviceManager {
    private const val PREFS_NAME = "leaderboard_device_prefs"
    private const val KEY_DEVICE_ID = "device_id"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getDeviceId(): String {
        var deviceId = prefs.getString(KEY_DEVICE_ID, null)
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
        }
        return deviceId
    }
}
