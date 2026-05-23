package com.example.calmy.presentation.statistics

import com.example.calmy.domain.model.Statistics
import com.example.calmy.domain.model.SupportMessage

data class StatisticsState(
    val statistics: Statistics? = null,
    val supportMessage: SupportMessage? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val supportNotice: String? = null
)
