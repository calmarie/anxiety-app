package com.example.calmy.presentation.thoughtlist

import com.example.calmy.domain.model.Thought

data class ThoughtListState(
    val thoughts: List<Thought> = emptyList(),
    val isLoading: Boolean = false,
    val isShowingCachedData: Boolean = false,
    val message: String? = null
)
