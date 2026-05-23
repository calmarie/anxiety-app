package com.example.calmy.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NotificationSettingsRequestDto(
    @SerializedName("frequency_minutes")
    val frequencyMinutes: Int
)
