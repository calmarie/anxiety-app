package com.example.calmy.data.remote.dto

data class StatisticsResponseDto(
    val week: PeriodStatisticsDto,
    val month: PeriodStatisticsDto,
    val year: PeriodStatisticsDto
)
