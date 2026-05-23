package com.example.calmy.domain.model

data class NotificationSettings(
    val userId: String,
    val frequencyMinutes: Int,
    val updatedAt: String?,
    val enabled: Boolean = true
)
