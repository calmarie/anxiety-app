package com.example.calmy.domain.model

data class SupportMessage(
    val frequencyMinutes: Int?,
    val averageDailyAnxiety: Double?,
    val anxietyRange: String?,
    val message: String,
    val generatedAt: String?
)
