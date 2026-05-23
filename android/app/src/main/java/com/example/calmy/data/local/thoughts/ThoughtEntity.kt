package com.example.calmy.data.local.thoughts

data class ThoughtEntity(
    val id: String,
    val userId: String,
    val anxietyLevel: Int,
    val anxietyType: String,
    val description: String,
    val createdAt: String,
    val isPendingSync: Boolean = false
)
