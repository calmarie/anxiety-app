package com.example.calmy.presentation.navigation

sealed class Screen(val route: String) {
    data object Register : Screen("register")
    data object Home : Screen("home")
    // TODO: Add Login screen route when authentication flow is expanded.
}
