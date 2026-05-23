package com.example.calmy.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calmy.data.local.preferences.CalmLevelStorage
import com.example.calmy.domain.model.CalmState
import com.example.calmy.domain.repository.AuthRepository
import com.example.calmy.domain.repository.ThoughtsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val thoughtsRepository: ThoughtsRepository,
    private val calmLevelStorage: CalmLevelStorage
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadUser()
        loadCloudLevel()
    }

    fun checkStatisticsAvailable() {
        viewModelScope.launch {
            _state.update { it.copy(isCheckingStatistics = true, message = null) }
            thoughtsRepository.getStatistics().fold(
                onSuccess = { statistics ->
                    val level = CalmState.fromAverageAnxiety(statistics.week.averageAnxietyLevel).cloudLevel
                    calmLevelStorage.saveCloudLevel(level)
                    _state.update {
                        it.copy(
                            cloudLevel = level,
                            isCheckingStatistics = false,
                            openStatistics = true,
                            message = null
                        )
                    }
                },
                onFailure = { throwable ->
                    _state.update {
                        it.copy(
                            isCheckingStatistics = false,
                            message = throwable.message ?: "Статистика доступна только при подключении к интернету"
                        )
                    }
                }
            )
        }
    }

    fun consumeStatisticsNavigation() {
        _state.update { it.copy(openStatistics = false) }
    }

    private fun loadUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser().fold(
                onSuccess = { user ->
                    _state.update { it.copy(userName = user.name) }
                },
                onFailure = {
                    _state.update { it.copy(userName = "") }
                }
            )
        }
    }

    private fun loadCloudLevel() {
        viewModelScope.launch {
            val cachedLevel = calmLevelStorage.getCloudLevel() ?: DefaultCloudLevel
            _state.update {
                it.copy(
                    cloudLevel = cachedLevel,
                    isLoadingCloud = true,
                    message = null
                )
            }
            thoughtsRepository.getStatistics().fold(
                onSuccess = { statistics ->
                    val level = CalmState.fromAverageAnxiety(statistics.week.averageAnxietyLevel).cloudLevel
                    calmLevelStorage.saveCloudLevel(level)
                    _state.update {
                        it.copy(
                            cloudLevel = level,
                            isLoadingCloud = false,
                            message = null
                        )
                    }
                },
                onFailure = {
                    _state.update {
                        it.copy(
                            isLoadingCloud = false,
                            message = null
                        )
                    }
                }
            )
        }
    }

    private companion object {
        const val DefaultCloudLevel = 2
    }
}
