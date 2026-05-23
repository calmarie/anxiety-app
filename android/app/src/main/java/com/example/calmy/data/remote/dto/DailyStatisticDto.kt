package com.example.calmy.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DailyStatisticDto(
    val date: String,
    @SerializedName("average_anxiety_level")
    val averageAnxietyLevel: Double,
    @SerializedName("entries_count")
    val entriesCount: Int
)
