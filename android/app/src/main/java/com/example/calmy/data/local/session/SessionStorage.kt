package com.example.calmy.data.local.session

import com.example.calmy.domain.model.AuthSession
import kotlinx.coroutines.flow.Flow

interface SessionStorage {
    suspend fun saveSession(session: AuthSession)
    suspend fun getToken(): String?
    suspend fun getSession(): AuthSession?
    suspend fun clearSession()
    fun observeToken(): Flow<String?>
}
