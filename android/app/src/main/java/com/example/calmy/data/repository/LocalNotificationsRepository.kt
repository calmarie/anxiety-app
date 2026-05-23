package com.example.calmy.data.repository

import com.example.calmy.core.CalmyDateTime
import com.example.calmy.data.local.preferences.NotificationPreferencesStorage
import com.example.calmy.data.local.session.SessionStorage
import com.example.calmy.data.local.thoughts.ThoughtDao
import com.example.calmy.domain.model.NotificationSettings
import com.example.calmy.domain.model.SupportMessage
import com.example.calmy.domain.repository.NotificationsRepository
import java.util.Date

class LocalNotificationsRepository(
    private val preferencesStorage: NotificationPreferencesStorage,
    private val thoughtDao: ThoughtDao,
    private val sessionStorage: SessionStorage
) : NotificationsRepository {
    override suspend fun getSettings(): Result<NotificationSettings> {
        return runCatching {
            val preferences = preferencesStorage.getPreferences()
            NotificationSettings(
                userId = currentUserId().orEmpty(),
                frequencyMinutes = preferences.frequencyMinutes,
                updatedAt = null,
                enabled = preferences.enabled
            )
        }
    }

    override suspend fun updateSettings(
        frequencyMinutes: Int,
        enabled: Boolean
    ): Result<NotificationSettings> {
        if (frequencyMinutes <= 0) {
            return Result.failure(Exception("Частота должна быть больше нуля"))
        }
        preferencesStorage.savePreferences(frequencyMinutes, enabled)
        return Result.success(
            NotificationSettings(
                userId = currentUserId().orEmpty(),
                frequencyMinutes = frequencyMinutes,
                updatedAt = CalmyDateTime.nowIso(),
                enabled = enabled
            )
        )
    }

    override suspend fun getSupportMessage(): Result<SupportMessage> {
        return runCatching {
            val preferences = preferencesStorage.getPreferences()
            val todayAverage = averageTodayAnxiety()
            SupportMessage(
                frequencyMinutes = preferences.frequencyMinutes,
                averageDailyAnxiety = todayAverage,
                anxietyRange = anxietyRange(todayAverage),
                message = "",
                generatedAt = CalmyDateTime.nowIso()
            )
        }
    }

    private suspend fun averageTodayAnxiety(): Double {
        val userId = currentUserId() ?: return 0.0
        val todayStart = CalmyDateTime.daysAgoStart(1, Date())
        val todaysThoughts = thoughtDao.getThoughts(userId).filter { thought ->
            val date = CalmyDateTime.parse(thought.createdAt) ?: return@filter false
            !date.before(todayStart)
        }
        if (todaysThoughts.isEmpty()) {
            return 0.0
        }
        return todaysThoughts.sumOf { thought -> thought.anxietyLevel }.toDouble() / todaysThoughts.size
    }

    private suspend fun currentUserId(): String? {
        return sessionStorage.getSession()?.user?.id
    }

    private fun anxietyRange(value: Double): String {
        return when {
            value <= 3.0 -> "low"
            value <= 6.0 -> "medium"
            else -> "high"
        }
    }
}
