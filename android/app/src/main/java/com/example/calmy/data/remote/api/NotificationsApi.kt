package com.example.calmy.data.remote.api

import com.example.calmy.data.remote.dto.NotificationSettingsRequestDto
import com.example.calmy.data.remote.dto.NotificationSettingsDto
import com.example.calmy.data.remote.dto.SupportMessageResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NotificationsApi {
    @POST("notifications/settings")
    suspend fun updateSettings(
        @Body request: NotificationSettingsRequestDto
    ): NotificationSettingsDto

    @GET("notifications/settings")
    suspend fun getSettings(): NotificationSettingsDto

    @GET("notifications/support-message")
    suspend fun getSupportMessage(): SupportMessageResponseDto
}
