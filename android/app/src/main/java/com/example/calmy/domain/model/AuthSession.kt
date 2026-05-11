package com.example.calmy.domain.model

data class AuthSession(
    val token: String,
    val expiresAt: String,
    val user: User
)
