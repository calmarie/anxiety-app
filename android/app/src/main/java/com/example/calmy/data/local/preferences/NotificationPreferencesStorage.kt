package com.example.calmy.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.notificationPreferencesDataStore by preferencesDataStore(name = "calmy_notification_preferences")

class NotificationPreferencesStorage(
    context: Context
) {
    private val dataStore = context.applicationContext.notificationPreferencesDataStore

    suspend fun getPreferences(): NotificationPreferences {
        val preferences = dataStore.data.first()
        return NotificationPreferences(
            frequencyMinutes = preferences[Keys.FrequencyMinutes] ?: DefaultFrequencyMinutes,
            enabled = preferences[Keys.Enabled] ?: true
        )
    }

    suspend fun savePreferences(frequencyMinutes: Int, enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.FrequencyMinutes] = frequencyMinutes
            preferences[Keys.Enabled] = enabled
        }
    }

    private object Keys {
        val FrequencyMinutes = intPreferencesKey("frequency_minutes")
        val Enabled = booleanPreferencesKey("enabled")
    }

    private companion object {
        const val DefaultFrequencyMinutes = 180
    }
}

data class NotificationPreferences(
    val frequencyMinutes: Int,
    val enabled: Boolean
)
