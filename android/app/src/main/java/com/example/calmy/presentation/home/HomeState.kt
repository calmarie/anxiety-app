package com.example.calmy.presentation.home

data class HomeState(
    val userName: String = "",
    val cloudLevel: Int = 2,
    val isLoadingCloud: Boolean = false,
    val isCheckingStatistics: Boolean = false,
    val openStatistics: Boolean = false,
    val message: String? = null
)
