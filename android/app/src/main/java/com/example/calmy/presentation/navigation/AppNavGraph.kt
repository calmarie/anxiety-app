package com.example.calmy.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calmy.di.AppModule
import com.example.calmy.presentation.addthought.AddThoughtScreen
import com.example.calmy.presentation.addthought.AddThoughtViewModel
import com.example.calmy.presentation.home.HomeScreen
import com.example.calmy.presentation.home.HomeViewModel
import com.example.calmy.presentation.login.LoginScreen
import com.example.calmy.presentation.login.LoginViewModel
import com.example.calmy.presentation.register.RegisterScreen
import com.example.calmy.presentation.register.RegisterViewModel
import com.example.calmy.presentation.settings.SettingsScreen
import com.example.calmy.presentation.settings.SettingsViewModel
import com.example.calmy.presentation.splash.SplashScreen
import com.example.calmy.presentation.splash.SplashViewModel
import com.example.calmy.presentation.statistics.StatisticsScreen
import com.example.calmy.presentation.statistics.StatisticsViewModel
import com.example.calmy.presentation.thoughtlist.ThoughtListScreen
import com.example.calmy.presentation.thoughtlist.ThoughtListViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext
    val dependencies = remember {
        AppModule.createDependencies(context)
    }
    val splashViewModelFactory = remember(dependencies) {
        AppModule.provideSplashViewModelFactory(dependencies)
    }
    val registerViewModelFactory = remember(dependencies) {
        AppModule.provideRegisterViewModelFactory(dependencies)
    }
    val loginViewModelFactory = remember(dependencies) {
        AppModule.provideLoginViewModelFactory(dependencies)
    }
    val homeViewModelFactory = remember(dependencies) {
        AppModule.provideHomeViewModelFactory(dependencies)
    }
    val addThoughtViewModelFactory = remember(dependencies) {
        AppModule.provideAddThoughtViewModelFactory(dependencies)
    }
    val thoughtListViewModelFactory = remember(dependencies) {
        AppModule.provideThoughtListViewModelFactory(dependencies)
    }
    val statisticsViewModelFactory = remember(dependencies) {
        AppModule.provideStatisticsViewModelFactory(dependencies)
    }
    val settingsViewModelFactory = remember(dependencies) {
        AppModule.provideSettingsViewModelFactory(dependencies)
    }

    fun openMain(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(Screen.Home.route) {
                saveState = true
            }
        }
    }

    fun openFreshMain(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(Screen.Home.route) {
                saveState = false
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            val viewModel: SplashViewModel = viewModel(factory = splashViewModelFactory)
            SplashScreen(
                state = viewModel.state,
                onOpenHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onOpenLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = viewModel(factory = loginViewModelFactory)
            LoginScreen(
                state = viewModel.state,
                onEmailChanged = viewModel::onEmailChanged,
                onPasswordChanged = viewModel::onPasswordChanged,
                onPasswordVisibilityChanged = viewModel::onPasswordVisibilityChanged,
                onLoginClick = viewModel::login,
                onOpenRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            val viewModel: RegisterViewModel = viewModel(factory = registerViewModelFactory)
            RegisterScreen(
                state = viewModel.state,
                onNameChanged = viewModel::onNameChanged,
                onEmailChanged = viewModel::onEmailChanged,
                onPasswordChanged = viewModel::onPasswordChanged,
                onPasswordVisibilityChanged = viewModel::onPasswordVisibilityChanged,
                onRegisterClick = viewModel::register,
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onOpenLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel(factory = homeViewModelFactory)
            HomeScreen(
                state = viewModel.state,
                onOpenAddThought = {
                    openMain(Screen.AddThought.route)
                },
                onOpenThoughtList = {
                    openMain(Screen.ThoughtList.route)
                },
                onCheckStatistics = viewModel::checkStatisticsAvailable,
                onOpenStatistics = {
                    openMain(Screen.Statistics.route)
                },
                onOpenSettings = {
                    openMain(Screen.Settings.route)
                },
                onStatisticsNavigationConsumed = viewModel::consumeStatisticsNavigation
            )
        }

        composable(Screen.AddThought.route) {
            val viewModel: AddThoughtViewModel = viewModel(factory = addThoughtViewModelFactory)
            AddThoughtScreen(
                state = viewModel.state,
                onDescriptionChanged = viewModel::onDescriptionChanged,
                onAnxietyLevelSelected = viewModel::onAnxietyLevelSelected,
                onAnxietyTypeSelected = viewModel::onAnxietyTypeSelected,
                onSaveClick = viewModel::saveThought,
                onSaved = {
                    openFreshMain(Screen.ThoughtList.route)
                },
                onOpenHome = {
                    openMain(Screen.Home.route)
                },
                onOpenAddThought = {
                    openMain(Screen.AddThought.route)
                },
                onOpenThoughtList = {
                    openFreshMain(Screen.ThoughtList.route)
                },
                onOpenStatistics = {
                    openMain(Screen.Statistics.route)
                },
                onOpenSettings = {
                    openMain(Screen.Settings.route)
                }
            )
        }

        composable(Screen.ThoughtList.route) {
            val viewModel: ThoughtListViewModel = viewModel(factory = thoughtListViewModelFactory)
            ThoughtListScreen(
                state = viewModel.state,
                onRefreshClick = viewModel::loadThoughts,
                onAddThoughtClick = {
                    openMain(Screen.AddThought.route)
                },
                onDeleteThoughtClick = viewModel::deleteThought,
                onOpenHome = {
                    openMain(Screen.Home.route)
                },
                onOpenAddThought = {
                    openMain(Screen.AddThought.route)
                },
                onOpenThoughtList = {
                    openMain(Screen.ThoughtList.route)
                },
                onOpenStatistics = {
                    openMain(Screen.Statistics.route)
                },
                onOpenSettings = {
                    openMain(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Statistics.route) {
            val viewModel: StatisticsViewModel = viewModel(factory = statisticsViewModelFactory)
            StatisticsScreen(
                state = viewModel.state,
                onRefreshClick = viewModel::loadStatistics,
                onOpenHome = {
                    openMain(Screen.Home.route)
                },
                onOpenAddThought = {
                    openMain(Screen.AddThought.route)
                },
                onOpenThoughtList = {
                    openMain(Screen.ThoughtList.route)
                },
                onOpenStatistics = {
                    openMain(Screen.Statistics.route)
                },
                onOpenSettings = {
                    openMain(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = viewModel(factory = settingsViewModelFactory)
            SettingsScreen(
                state = viewModel.state,
                onPresetFrequencySelected = viewModel::onPresetFrequencySelected,
                onCustomHoursChanged = viewModel::onCustomHoursChanged,
                onNotificationsEnabledChanged = viewModel::onNotificationsEnabledChanged,
                onTestFrequencyClick = viewModel::selectTestFrequency,
                onSaveClick = viewModel::saveSettings,
                onLogoutClick = viewModel::logout,
                onLogoutSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onOpenHome = {
                    openMain(Screen.Home.route)
                },
                onOpenAddThought = {
                    openMain(Screen.AddThought.route)
                },
                onOpenThoughtList = {
                    openMain(Screen.ThoughtList.route)
                },
                onOpenStatistics = {
                    openMain(Screen.Statistics.route)
                },
                onOpenSettings = {
                    openMain(Screen.Settings.route)
                }
            )
        }
    }
}
