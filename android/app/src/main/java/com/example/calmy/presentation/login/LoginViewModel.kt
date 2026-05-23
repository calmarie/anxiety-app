package com.example.calmy.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Patterns
import com.example.calmy.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

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

    fun login() {
        val currentState = _state.value
        val validationError = validate(currentState)

        if (validationError != null) {
            _state.update { it.copy(error = validationError) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            authRepository.login(
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
                            error = throwable.message ?: "Не удалось войти",
                            isSuccess = false
                        )
                    }
                }
            )
        }
    }

    private fun validate(state: LoginState): String? {
        val email = state.email.trim()
        return when {
            email.isBlank() -> "Пожалуйста, укажи email"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Похоже, email введён не полностью"
            state.password.isBlank() -> "Пожалуйста, укажи пароль"
            else -> null
        }
    }
}
