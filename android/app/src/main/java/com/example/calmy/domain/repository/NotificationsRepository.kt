package com.example.calmy.domain.repository

import com.example.calmy.domain.model.NotificationSettings
import com.example.calmy.domain.model.SupportMessage

interface NotificationsRepository {
    suspend fun getSettings(): Result<NotificationSettings>
    suspend fun updateSettings(frequencyMinutes: Int, enabled: Boolean): Result<NotificationSettings>
    suspend fun getSupportMessage(): Result<SupportMessage>
}
