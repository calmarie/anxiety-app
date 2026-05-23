package com.example.calmy.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calmy.domain.repository.NotificationsRepository
import com.example.calmy.domain.repository.ThoughtsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StatisticsViewModel(
    private val thoughtsRepository: ThoughtsRepository,
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state.asStateFlow()

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    supportNotice = null
                )
            }
            thoughtsRepository.getStatistics().fold(
                onSuccess = { statistics ->
                    _state.update {
                        it.copy(
                            statistics = statistics,
                            isLoading = false,
                            error = null
                        )
                    }
                    loadSupportMessage()
                },
                onFailure = { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Статистика доступна только при подключении к интернету"
                        )
                    }
                }
            )
        }
    }

    private suspend fun loadSupportMessage() {
        notificationsRepository.getSupportMessage().fold(
            onSuccess = { supportMessage ->
                _state.update {
                    it.copy(
                        supportMessage = supportMessage,
                        supportNotice = null,
                        error = null
                    )
                }
            },
            onFailure = { throwable ->
                val message = throwable.message ?: "Данные за сегодня сейчас недоступны"
                _state.update {
                    it.copy(supportNotice = message)
                }
            }
        )
    }
}
