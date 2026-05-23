package com.example.calmy.domain.model

data class Statistics(
    val week: PeriodStatistics,
    val month: PeriodStatistics,
    val year: PeriodStatistics
)

data class PeriodStatistics(
    val period: String,
    val from: String,
    val to: String,
    val entriesCount: Int,
    val averageAnxietyLevel: Double,
    val dailyDynamics: List<DailyStatistic>,
    val anxietyTypeFrequencies: List<AnxietyTypeCount>,
    val mostAnxiousTime: String?
)

data class DailyStatistic(
    val date: String,
    val averageAnxietyLevel: Double,
    val entriesCount: Int
)

data class AnxietyTypeCount(
    val anxietyType: AnxietyType,
    val count: Int
)
