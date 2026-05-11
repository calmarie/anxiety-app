package com.example.calmy.data.mapper

import com.example.calmy.data.remote.dto.AuthResponseDto
import com.example.calmy.data.remote.dto.UserDto
import com.example.calmy.domain.model.AuthSession
import com.example.calmy.domain.model.User

fun UserDto.toDomain(): User {
    return User(
        id = id,
        email = email,
        name = name,
        createdAt = createdAt
    )
}

fun AuthResponseDto.toDomain(): AuthSession {
    return AuthSession(
        token = token,
        expiresAt = expiresAt,
        user = user.toDomain()
    )
}
