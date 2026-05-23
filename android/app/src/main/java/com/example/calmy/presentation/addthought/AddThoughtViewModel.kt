package com.example.calmy.presentation.addthought

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calmy.domain.model.AnxietyType
import com.example.calmy.domain.repository.ThoughtsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddThoughtViewModel(
    private val thoughtsRepository: ThoughtsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AddThoughtState())
    val state: StateFlow<AddThoughtState> = _state.asStateFlow()

    fun onDescriptionChanged(value: String) {
        _state.update {
            it.copy(
                description = value,
                error = null,
                isSaved = false
            )
        }
    }

    fun onAnxietyLevelSelected(value: Int) {
        _state.update {
            it.copy(
                anxietyLevel = value.coerceIn(1, 10),
                error = null,
                isSaved = false
            )
        }
    }

    fun onAnxietyTypeSelected(value: AnxietyType) {
        _state.update {
            it.copy(
                anxietyType = value,
                error = null,
                isSaved = false
            )
        }
    }

    fun saveThought() {
        val currentState = _state.value
        val validationError = validate(currentState)
        if (validationError != null) {
            _state.update { it.copy(error = validationError) }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSaving = true,
                    error = null,
                    isSaved = false
                )
            }
            thoughtsRepository.saveThought(
                anxietyLevel = currentState.anxietyLevel,
                anxietyType = currentState.anxietyType,
                description = currentState.description.trim()
            ).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            description = "",
                            anxietyLevel = 5,
                            anxietyType = AnxietyType.Health,
                            isSaving = false,
                            isSaved = true,
                            error = null
                        )
                    }
                },
                onFailure = { throwable ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            error = throwable.message ?: "Не удалось сохранить запись"
                        )
                    }
                }
            )
        }
    }

    private fun validate(state: AddThoughtState): String? {
        return when {
            state.anxietyLevel !in 1..10 -> "Выбери уровень тревоги от 1 до 10"
            state.anxietyType.apiValue.isBlank() -> "Выбери сценарий тревоги"
            state.description.isBlank() -> "Напиши, что сейчас в голове"
            else -> null
        }
    }
}
