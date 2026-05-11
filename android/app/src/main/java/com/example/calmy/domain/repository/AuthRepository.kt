package com.example.calmy.domain.repository

import com.example.calmy.domain.model.AuthSession

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Result<AuthSession>
}
