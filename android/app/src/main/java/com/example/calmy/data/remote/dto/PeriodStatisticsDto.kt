package com.example.calmy.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PeriodStatisticsDto(
    val period: String,
    val from: String,
    val to: String,
    @SerializedName("entries_count")
    val entriesCount: Int,
    @SerializedName("average_anxiety_level")
    val averageAnxietyLevel: Double,
    @SerializedName("daily_dynamics")
    val dailyDynamics: List<DailyStatisticDto>,
    @SerializedName("anxiety_type_frequencies")
    val anxietyTypeFrequencies: List<AnxietyTypeCountDto>,
    @SerializedName("most_anxious_time")
    val mostAnxiousTime: String?
)
