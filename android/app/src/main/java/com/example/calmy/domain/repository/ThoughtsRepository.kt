package com.example.calmy.domain.repository

import com.example.calmy.domain.model.AnxietyType
import com.example.calmy.domain.model.Statistics
import com.example.calmy.domain.model.Thought

interface ThoughtsRepository {
    suspend fun syncThought(
        anxietyLevel: Int,
        anxietyType: AnxietyType,
        description: String
    ): Result<List<Thought>>

    suspend fun saveThought(
        anxietyLevel: Int,
        anxietyType: AnxietyType,
        description: String
    ): Result<List<Thought>>

    suspend fun getThoughts(): Result<List<Thought>>
    suspend fun getCachedThoughts(): Result<List<Thought>>
    suspend fun refreshThoughts(): Result<List<Thought>>
    suspend fun deleteThought(thoughtId: String): Result<List<Thought>>
    suspend fun getStatistics(): Result<Statistics>
}
