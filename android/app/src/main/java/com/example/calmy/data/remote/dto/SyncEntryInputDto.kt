package com.example.calmy.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SyncEntryInputDto(
    @SerializedName("anxiety_level")
    val anxietyLevel: Int,
    @SerializedName("anxiety_type")
    val anxietyType: String,
    val description: String
)
