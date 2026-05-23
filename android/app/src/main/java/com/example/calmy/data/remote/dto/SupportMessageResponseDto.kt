package com.example.calmy.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SupportMessageResponseDto(
    @SerializedName("frequency_minutes")
    val frequencyMinutes: Int? = null,
    @SerializedName("average_daily_anxiety")
    val averageDailyAnxiety: Double? = null,
    @SerializedName("anxiety_range")
    val anxietyRange: String? = null,
    val message: String,
    @SerializedName("generated_at")
    val generatedAt: String? = null
)
