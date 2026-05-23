package com.example.calmy.presentation.splash

data class SplashState(
    val isLoading: Boolean = true,
    val destination: SplashDestination? = null
)

enum class SplashDestination {
    Home,
    Login
}
