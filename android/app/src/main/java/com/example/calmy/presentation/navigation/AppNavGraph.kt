package com.example.calmy.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calmy.presentation.home.HomeScreen
import com.example.calmy.presentation.register.RegisterScreen
import com.example.calmy.presentation.register.RegisterViewModel

@Composable
fun AppNavGraph(
    registerViewModelFactory: ViewModelProvider.Factory
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Register.route
    ) {
        composable(Screen.Register.route) {
            val viewModel: RegisterViewModel = viewModel(factory = registerViewModelFactory)
            RegisterScreen(
                state = viewModel.state,
                onNameChanged = viewModel::onNameChanged,
                onEmailChanged = viewModel::onEmailChanged,
                onPasswordChanged = viewModel::onPasswordChanged,
                onRegisterClick = viewModel::register,
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}
