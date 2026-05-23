package com.example.calmy.presentation.addthought

import com.example.calmy.domain.model.AnxietyType

data class AddThoughtState(
    val description: String = "",
    val anxietyLevel: Int = 5,
    val anxietyType: AnxietyType = AnxietyType.Health,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)
