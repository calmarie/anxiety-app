package com.example.calmy.domain.repository

import com.example.calmy.domain.model.AuthSession
import com.example.calmy.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Result<AuthSession>
    suspend fun login(email: String, password: String): Result<AuthSession>
    suspend fun getCurrentUser(): Result<User>
    suspend fun getCurrentToken(): String?
    suspend fun logout()
    fun observeToken(): Flow<String?>
}
