package com.xavlegbmaofff.todo.data.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import java.util.UUID
import androidx.core.content.edit

class DeviceIdProvider(private val context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        val savedId = prefs.getString(KEY_DEVICE_ID, null)
        if (savedId != null) {
            return savedId
        }

        val androidId = try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            null
        }

        val deviceId = if (!androidId.isNullOrBlank() && androidId != "9774d56d682e549c") {
            androidId
        } else {
            UUID.randomUUID().toString()
        }

        prefs.edit { putString(KEY_DEVICE_ID, deviceId) }

        return deviceId
    }

    companion object {
        private const val PREFS_NAME = "todo_prefs"
        private const val KEY_DEVICE_ID = "device_id"
    }
}
