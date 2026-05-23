package com.example.calmy.data.repository

import com.example.calmy.core.CalmyDateTime
import com.example.calmy.data.local.session.SessionStorage
import com.example.calmy.domain.model.AuthSession
import com.example.calmy.domain.model.User
import com.example.calmy.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class LocalAuthRepository(
    private val sessionStorage: SessionStorage
) : AuthRepository {
    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<AuthSession> {
        val validationError = validateInput(name, email, password)
        if (validationError != null) {
            return Result.failure(Exception(validationError))
        }
        return saveLocalSession(
            name = name.trim(),
            email = email.trim()
        )
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthSession> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Введите почту и пароль"))
        }
        return saveLocalSession(
            name = email.substringBefore("@").ifBlank { "Локальный аккаунт" },
            email = email.trim()
        )
    }

    override suspend fun getCurrentUser(): Result<User> {
        val user = sessionStorage.getSession()?.user
            ?: return Result.failure(Exception("Локальная сессия не найдена"))
        return Result.success(user)
    }

    override suspend fun getCurrentToken(): String? {
        return sessionStorage.getToken()
    }

    override suspend fun logout() {
        sessionStorage.clearSession()
    }

    override fun observeToken(): Flow<String?> {
        return sessionStorage.observeToken()
    }

    private suspend fun saveLocalSession(name: String, email: String): Result<AuthSession> {
        val createdAt = CalmyDateTime.nowIso()
        val user = User(
            id = "local_${email.lowercase().hashCode()}",
            email = email,
            name = name,
            createdAt = createdAt
        )
        val session = AuthSession(
            token = "local-token-${user.id}",
            expiresAt = createdAt,
            user = user
        )
        sessionStorage.saveSession(session)
        return Result.success(session)
    }

    private fun validateInput(name: String, email: String, password: String): String? {
        return when {
            name.isBlank() -> "Введите имя"
            email.isBlank() -> "Введите почту"
            password.isBlank() -> "Введите пароль"
            else -> null
        }
    }
}
