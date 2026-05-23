package com.example.calmy.presentation.thoughtlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calmy.domain.repository.ThoughtsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ThoughtListViewModel(
    private val thoughtsRepository: ThoughtsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ThoughtListState())
    val state: StateFlow<ThoughtListState> = _state.asStateFlow()

    init {
        loadThoughts()
    }

    fun loadThoughts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, message = null) }
            val cachedThoughts = thoughtsRepository.getCachedThoughts().getOrDefault(emptyList())
            if (cachedThoughts.isNotEmpty()) {
                _state.update {
                    it.copy(
                        thoughts = cachedThoughts,
                        isShowingCachedData = true
                    )
                }
            }
            thoughtsRepository.refreshThoughts().fold(
                onSuccess = { thoughts ->
                    _state.update {
                        it.copy(
                            thoughts = thoughts,
                            isLoading = false,
                            isShowingCachedData = false,
                            message = "Записи обновлены"
                        )
                    }
                },
                onFailure = { throwable ->
                    val fallback = thoughtsRepository.getCachedThoughts().getOrDefault(cachedThoughts)
                    _state.update {
                        it.copy(
                            thoughts = fallback,
                            isLoading = false,
                            isShowingCachedData = fallback.isNotEmpty(),
                            message = if (fallback.isNotEmpty()) {
                                "Отображаются сохранённые данные. Обновление будет доступно при подключении к интернету."
                            } else {
                                throwable.message ?: "Не удалось загрузить записи"
                            }
                        )
                    }
                }
            )
        }
    }

    fun deleteThought(thoughtId: String) {
        viewModelScope.launch {
            thoughtsRepository.deleteThought(thoughtId).fold(
                onSuccess = { thoughts ->
                    _state.update {
                        it.copy(
                            thoughts = thoughts,
                            message = "Запись удалена",
                            isShowingCachedData = false
                        )
                    }
                },
                onFailure = { throwable ->
                    _state.update {
                        it.copy(
                            message = throwable.message ?: "Не удалось удалить запись"
                        )
                    }
                }
            )
        }
    }
}
