package com.example.calmy.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthResponseDto(
    val token: String,
    @SerializedName("expires_at")
    val expiresAt: String,
    val user: UserDto
)
