package com.example.calmy.presentation.navigation

sealed class Screen(val route: String) {
    data object Register : Screen("register")
    data object Home : Screen("home")
}
