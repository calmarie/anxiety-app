package com.example.calmy.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Patterns
import com.example.calmy.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onNameChanged(value: String) {
        _state.update { current ->
            current.copy(name = value, error = null, isSuccess = false)
        }
    }

    fun onEmailChanged(value: String) {
        _state.update { current ->
            current.copy(email = value, error = null, isSuccess = false)
        }
    }

    fun onPasswordChanged(value: String) {
        _state.update { current ->
            current.copy(password = value, error = null, isSuccess = false)
        }
    }

    fun onPasswordVisibilityChanged() {
        _state.update { current ->
            current.copy(isPasswordVisible = !current.isPasswordVisible)
        }
    }

    fun register() {
        val currentState = _state.value
        val validationError = validate(currentState)

        if (validationError != null) {
            _state.update { it.copy(error = validationError) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            authRepository.register(
                name = currentState.name.trim(),
                email = currentState.email.trim(),
                password = currentState.password
            ).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            isSuccess = true
                        )
                    }
                },
                onFailure = { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Не удалось зарегистрироваться",
                            isSuccess = false
                        )
                    }
                }
            )
        }
    }

    private fun validate(state: RegisterState): String? {
        val email = state.email.trim()
        val name = state.name.trim()
        return when {
            name.length < 2 -> "Имя должно быть не короче 2 символов"
            email.isBlank() -> "Пожалуйста, укажи email"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Похоже, email введён не полностью"
            state.password.length < 8 -> "Пароль должен содержать минимум 8 символов"
            else -> null
        }
    }
}
