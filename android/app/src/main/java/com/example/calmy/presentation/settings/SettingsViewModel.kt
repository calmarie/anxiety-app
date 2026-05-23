package com.example.calmy.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calmy.domain.repository.AuthRepository
import com.example.calmy.domain.repository.NotificationsRepository
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val notificationsRepository: NotificationsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadSettings()
    }

    fun onPresetFrequencySelected(minutes: Int) {
        _state.update {
            it.copy(
                selectedFrequencyMinutes = minutes,
                customHoursText = "",
                message = null,
                isError = false
            )
        }
    }

    fun onCustomHoursChanged(value: String) {
        val cleanValue = value.filter { char -> char.isDigit() || char == '.' || char == ',' }
        val minutes = cleanValue.toFrequencyMinutesOrNull()
        _state.update {
            it.copy(
                customHoursText = cleanValue,
                selectedFrequencyMinutes = minutes ?: it.selectedFrequencyMinutes,
                message = null,
                isError = false
            )
        }
    }

    fun onNotificationsEnabledChanged(enabled: Boolean) {
        _state.update {
            it.copy(
                notificationsEnabled = enabled,
                message = null,
                isError = false
            )
        }
    }

    fun selectTestFrequency() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedFrequencyMinutes = TestFrequency,
                    customHoursText = "",
                    notificationsEnabled = true,
                    isSaving = true,
                    message = null,
                    isError = false
                )
            }
            notificationsRepository.updateSettings(
                frequencyMinutes = TestFrequency,
                enabled = true
            ).fold(
                onSuccess = { settings ->
                    _state.update {
                        it.copy(
                            selectedFrequencyMinutes = settings.frequencyMinutes,
                            customHoursText = "",
                            notificationsEnabled = settings.enabled,
                            isSaving = false,
                            message = "Тестовая частота сохранена: раз в минуту",
                            isError = false
                        )
                    }
                },
                onFailure = { throwable ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            message = throwable.message ?: "Не удалось поставить тестовую частоту",
                            isError = true
                        )
                    }
                }
            )
        }
    }

    fun loadSettings() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, message = null, isError = false) }
            notificationsRepository.getSettings().fold(
                onSuccess = { settings ->
                    _state.update {
                        it.copy(
                            selectedFrequencyMinutes = settings.frequencyMinutes,
                            customHoursText = "",
                            notificationsEnabled = settings.enabled,
                            isLoading = false,
                            message = null,
                            isError = false
                        )
                    }
                },
                onFailure = { throwable ->
                    _state.update {
                        it.copy(
                            selectedFrequencyMinutes = DefaultFrequency,
                            isLoading = false,
                            message = throwable.message ?: "Не удалось загрузить настройки. Можно сохранить значение по умолчанию.",
                            isError = true
                        )
                    }
                }
            )
        }
    }

    fun saveSettings() {
        val currentState = _state.value
        val customFrequency = currentState.customHoursText.toFrequencyMinutesOrNull()
        if (currentState.notificationsEnabled && currentState.customHoursText.isNotBlank() && customFrequency == null) {
            _state.update {
                it.copy(
                    message = "В своём времени нужно указать число часов больше нуля",
                    isError = true
                )
            }
            return
        }
        val frequency = if (currentState.notificationsEnabled) {
            customFrequency ?: currentState.selectedFrequencyMinutes
        } else {
            currentState.selectedFrequencyMinutes
        }
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSaving = true,
                    message = null,
                    isError = false
                )
            }
            notificationsRepository.updateSettings(
                frequencyMinutes = frequency,
                enabled = currentState.notificationsEnabled
            ).fold(
                onSuccess = { settings ->
                    _state.update {
                        it.copy(
                            selectedFrequencyMinutes = settings.frequencyMinutes,
                            customHoursText = "",
                            notificationsEnabled = settings.enabled,
                            isSaving = false,
                            message = if (settings.enabled) "Настройки сохранены" else "Уведомления выключены",
                            isError = false
                        )
                    }
                },
                onFailure = { throwable ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            message = throwable.message ?: "Не удалось сохранить настройки",
                            isError = true
                        )
                    }
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoggingOut = true,
                    message = null,
                    isError = false
                )
            }
            runCatching {
                authRepository.logout()
            }.fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoggingOut = false,
                            isLoggedOut = true
                        )
                    }
                },
                onFailure = { throwable ->
                    _state.update {
                        it.copy(
                            isLoggingOut = false,
                            message = throwable.message ?: "Не удалось выйти из аккаунта",
                            isError = true
                        )
                    }
                }
            )
        }
    }

    private fun String.toFrequencyMinutesOrNull(): Int? {
        if (isBlank()) {
            return null
        }
        val hours = replace(',', '.').toDoubleOrNull() ?: return null
        if (hours <= 0.0) {
            return null
        }
        return (hours * MinutesInHour).roundToInt().coerceAtLeast(1)
    }

    private companion object {
        const val DefaultFrequency = 180
        const val TestFrequency = 1
        const val MinutesInHour = 60
    }
}
