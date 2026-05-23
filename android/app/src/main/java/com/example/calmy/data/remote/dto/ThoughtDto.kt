package com.example.calmy.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ThoughtDto(
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("anxiety_level")
    val anxietyLevel: Int,
    @SerializedName("anxiety_type")
    val anxietyType: String,
    val description: String,
    @SerializedName("created_at")
    val createdAt: String
)
