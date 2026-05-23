package com.example.calmy.data.repository

import com.example.calmy.data.mapper.toDomain
import com.example.calmy.data.local.preferences.NotificationPreferencesStorage
import com.example.calmy.data.remote.api.NotificationsApi
import com.example.calmy.data.remote.dto.NotificationSettingsRequestDto
import com.example.calmy.domain.model.NotificationSettings
import com.example.calmy.domain.model.SupportMessage
import com.example.calmy.domain.repository.NotificationsRepository
import java.io.IOException
import retrofit2.HttpException

class NotificationsRepositoryImpl(
    private val notificationsApi: NotificationsApi,
    private val preferencesStorage: NotificationPreferencesStorage
) : NotificationsRepository {
    override suspend fun getSettings(): Result<NotificationSettings> {
        val localPreferences = preferencesStorage.getPreferences()
        if (!localPreferences.enabled) {
            return Result.success(
                DefaultSettings.copy(
                    frequencyMinutes = localPreferences.frequencyMinutes,
                    enabled = false
                )
            )
        }

        return try {
            val settings = notificationsApi.getSettings().toDomain(enabled = localPreferences.enabled)
            preferencesStorage.savePreferences(settings.frequencyMinutes, localPreferences.enabled)
            Result.success(settings)
        } catch (exception: HttpException) {
            if (exception.code() == 404) {
                Result.success(
                    DefaultSettings.copy(
                        frequencyMinutes = localPreferences.frequencyMinutes,
                        enabled = localPreferences.enabled
                    )
                )
            } else {
                Result.failure(Exception("Не удалось получить настройки. Код ошибки: ${exception.code()}"))
            }
        } catch (_: IOException) {
            Result.success(
                DefaultSettings.copy(
                    frequencyMinutes = localPreferences.frequencyMinutes,
                    enabled = localPreferences.enabled
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun updateSettings(frequencyMinutes: Int, enabled: Boolean): Result<NotificationSettings> {
        preferencesStorage.savePreferences(frequencyMinutes, enabled)
        if (!enabled) {
            return Result.success(
                DefaultSettings.copy(
                    frequencyMinutes = frequencyMinutes,
                    enabled = false
                )
            )
        }

        return try {
            val response = notificationsApi.updateSettings(
                request = NotificationSettingsRequestDto(
                    frequencyMinutes = frequencyMinutes
                )
            )
            Result.success(response.toDomain(enabled = true))
        } catch (exception: HttpException) {
            Result.failure(Exception("Не удалось сохранить настройки. Код ошибки: ${exception.code()}"))
        } catch (_: IOException) {
            Result.success(
                DefaultSettings.copy(
                    frequencyMinutes = frequencyMinutes,
                    enabled = true
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun getSupportMessage(): Result<SupportMessage> {
        return try {
            Result.success(notificationsApi.getSupportMessage().toDomain())
        } catch (exception: HttpException) {
            if (exception.code() == 404) {
                Result.failure(Exception("Данные за сегодня появятся после настройки уведомлений"))
            } else {
                Result.failure(Exception("Не удалось получить данные за сегодня. Код ошибки: ${exception.code()}"))
            }
        } catch (_: IOException) {
            Result.failure(Exception("Данные за сегодня сейчас недоступны при проблемах с подключением"))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private companion object {
        val DefaultSettings = NotificationSettings(
            userId = "",
            frequencyMinutes = 180,
            updatedAt = null,
            enabled = true
        )
    }
}
