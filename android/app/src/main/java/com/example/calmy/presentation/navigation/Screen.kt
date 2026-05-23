package com.example.calmy.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object AddThought : Screen("add_thought")
    data object ThoughtList : Screen("thought_list")
    data object Statistics : Screen("statistics")
    data object Settings : Screen("settings")
}
