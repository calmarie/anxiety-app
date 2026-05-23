package com.example.calmy.domain.model

data class Thought(
    val id: String,
    val userId: String,
    val anxietyLevel: Int,
    val anxietyType: AnxietyType,
    val description: String,
    val createdAt: String,
    val isPendingSync: Boolean = false
)
