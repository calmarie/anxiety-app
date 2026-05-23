package com.example.calmy.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NotificationSettingsDto(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("frequency_minutes")
    val frequencyMinutes: Int,
    @SerializedName("updated_at")
    val updatedAt: String
)
