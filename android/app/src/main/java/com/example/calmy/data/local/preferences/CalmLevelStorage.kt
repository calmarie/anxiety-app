package com.example.calmy.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.calmLevelDataStore by preferencesDataStore(name = "calmy_calm_level")

class CalmLevelStorage(
    context: Context
) {
    private val dataStore = context.applicationContext.calmLevelDataStore

    suspend fun saveCloudLevel(level: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.CloudLevel] = level.coerceIn(1, 5)
        }
    }

    suspend fun getCloudLevel(): Int? {
        return dataStore.data.map { preferences ->
            preferences[Keys.CloudLevel]?.coerceIn(1, 5)
        }.first()
    }

    private object Keys {
        val CloudLevel = intPreferencesKey("cloud_level")
    }
}
