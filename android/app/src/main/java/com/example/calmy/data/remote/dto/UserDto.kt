package com.example.calmy.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    @SerializedName("created_at")
    val createdAt: String
)
