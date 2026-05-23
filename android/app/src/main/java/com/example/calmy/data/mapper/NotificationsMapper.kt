package com.example.calmy.data.mapper

import com.example.calmy.data.remote.dto.NotificationSettingsDto
import com.example.calmy.data.remote.dto.SupportMessageResponseDto
import com.example.calmy.domain.model.NotificationSettings
import com.example.calmy.domain.model.SupportMessage

fun NotificationSettingsDto.toDomain(enabled: Boolean = true): NotificationSettings {
    return NotificationSettings(
        userId = userId,
        frequencyMinutes = frequencyMinutes,
        updatedAt = updatedAt,
        enabled = enabled
    )
}

fun SupportMessageResponseDto.toDomain(): SupportMessage {
    return SupportMessage(
        frequencyMinutes = frequencyMinutes,
        averageDailyAnxiety = averageDailyAnxiety,
        anxietyRange = anxietyRange,
        message = message,
        generatedAt = generatedAt
    )
}
