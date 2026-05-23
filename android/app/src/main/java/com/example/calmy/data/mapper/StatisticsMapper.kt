package com.example.calmy.data.mapper

import com.example.calmy.data.remote.dto.AnxietyTypeCountDto
import com.example.calmy.data.remote.dto.DailyStatisticDto
import com.example.calmy.data.remote.dto.PeriodStatisticsDto
import com.example.calmy.data.remote.dto.StatisticsResponseDto
import com.example.calmy.domain.model.AnxietyType
import com.example.calmy.domain.model.AnxietyTypeCount
import com.example.calmy.domain.model.DailyStatistic
import com.example.calmy.domain.model.PeriodStatistics
import com.example.calmy.domain.model.Statistics

fun StatisticsResponseDto.toDomain(): Statistics {
    return Statistics(
        week = week.toDomain(),
        month = month.toDomain(),
        year = year.toDomain()
    )
}

fun PeriodStatisticsDto.toDomain(): PeriodStatistics {
    return PeriodStatistics(
        period = period,
        from = from,
        to = to,
        entriesCount = entriesCount,
        averageAnxietyLevel = averageAnxietyLevel,
        dailyDynamics = dailyDynamics.map { value -> value.toDomain() },
        anxietyTypeFrequencies = anxietyTypeFrequencies.map { value -> value.toDomain() },
        mostAnxiousTime = mostAnxiousTime
    )
}

fun DailyStatisticDto.toDomain(): DailyStatistic {
    return DailyStatistic(
        date = date,
        averageAnxietyLevel = averageAnxietyLevel,
        entriesCount = entriesCount
    )
}

fun AnxietyTypeCountDto.toDomain(): AnxietyTypeCount {
    return AnxietyTypeCount(
        anxietyType = AnxietyType.fromApiValue(anxietyType),
        count = count
    )
}
