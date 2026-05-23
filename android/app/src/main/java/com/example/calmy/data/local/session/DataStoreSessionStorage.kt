package com.example.calmy.data.local.session

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.calmy.domain.model.AuthSession
import com.example.calmy.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(name = "calmy_session")

class DataStoreSessionStorage(
    context: Context
) : SessionStorage {
    private val dataStore = context.applicationContext.sessionDataStore

    override suspend fun saveSession(session: AuthSession) {
        dataStore.edit { preferences ->
            preferences[Keys.Token] = session.token
            preferences[Keys.ExpiresAt] = session.expiresAt
            preferences[Keys.UserId] = session.user.id
            preferences[Keys.UserEmail] = session.user.email
            preferences[Keys.UserName] = session.user.name
            preferences[Keys.UserCreatedAt] = session.user.createdAt
        }
    }

    override suspend fun getToken(): String? {
        return dataStore.data.first()[Keys.Token]
    }

    override suspend fun getSession(): AuthSession? {
        return dataStore.data.first().toSession()
    }

    override suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    override fun observeToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[Keys.Token]
        }
    }

    private fun Preferences.toSession(): AuthSession? {
        val token = this[Keys.Token] ?: return null
        val expiresAt = this[Keys.ExpiresAt] ?: return null
        val userId = this[Keys.UserId] ?: return null
        val userEmail = this[Keys.UserEmail] ?: return null
        val userName = this[Keys.UserName] ?: return null
        val userCreatedAt = this[Keys.UserCreatedAt] ?: return null

        return AuthSession(
            token = token,
            expiresAt = expiresAt,
            user = User(
                id = userId,
                email = userEmail,
                name = userName,
                createdAt = userCreatedAt
            )
        )
    }

    private object Keys {
        val Token = stringPreferencesKey("token")
        val ExpiresAt = stringPreferencesKey("expires_at")
        val UserId = stringPreferencesKey("user_id")
        val UserEmail = stringPreferencesKey("user_email")
        val UserName = stringPreferencesKey("user_name")
        val UserCreatedAt = stringPreferencesKey("user_created_at")
    }
}
