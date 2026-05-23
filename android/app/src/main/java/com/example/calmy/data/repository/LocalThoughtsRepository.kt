package com.example.calmy.data.repository

import com.example.calmy.core.CalmyDateTime
import com.example.calmy.data.local.session.SessionStorage
import com.example.calmy.data.local.thoughts.ThoughtDao
import com.example.calmy.data.local.thoughts.ThoughtEntity
import com.example.calmy.data.mapper.toDomain
import com.example.calmy.domain.model.AnxietyType
import com.example.calmy.domain.model.AnxietyTypeCount
import com.example.calmy.domain.model.DailyStatistic
import com.example.calmy.domain.model.PeriodStatistics
import com.example.calmy.domain.model.Statistics
import com.example.calmy.domain.model.Thought
import com.example.calmy.domain.repository.ThoughtsRepository
import java.util.Date
import java.util.UUID
import kotlin.math.round

class LocalThoughtsRepository(
    private val thoughtDao: ThoughtDao,
    private val sessionStorage: SessionStorage
) : ThoughtsRepository {
    override suspend fun syncThought(
        anxietyLevel: Int,
        anxietyType: AnxietyType,
        description: String
    ): Result<List<Thought>> {
        return saveThought(
            anxietyLevel = anxietyLevel,
            anxietyType = anxietyType,
            description = description
        )
    }

    override suspend fun saveThought(
        anxietyLevel: Int,
        anxietyType: AnxietyType,
        description: String
    ): Result<List<Thought>> {
        val validationError = validateThoughtInput(anxietyLevel, description)
        if (validationError != null) {
            return Result.failure(Exception(validationError))
        }

        val userId = currentUserId()
            ?: return Result.failure(Exception("Сначала войдите в локальный аккаунт"))
        thoughtDao.addThought(
            ThoughtEntity(
                id = "local_${UUID.randomUUID()}",
                userId = userId,
                anxietyLevel = anxietyLevel,
                anxietyType = anxietyType.apiValue,
                description = description.trim(),
                createdAt = CalmyDateTime.nowIso(),
                isPendingSync = false
            )
        )
        return getCachedThoughts()
    }

    override suspend fun getThoughts(): Result<List<Thought>> {
        return getCachedThoughts()
    }

    override suspend fun getCachedThoughts(): Result<List<Thought>> {
        return runCatching {
            val userId = currentUserId() ?: return@runCatching emptyList()
            thoughtDao.getThoughts(userId).map { thought -> thought.toDomain() }
        }
    }

    override suspend fun refreshThoughts(): Result<List<Thought>> {
        return getCachedThoughts()
    }

    override suspend fun deleteThought(thoughtId: String): Result<List<Thought>> {
        return runCatching {
            val userId = currentUserId()
                ?: throw IllegalStateException("Сначала войдите в локальный аккаунт")
            thoughtDao.deleteThought(userId, thoughtId)
            getCachedThoughts().getOrThrow()
        }
    }

    override suspend fun getStatistics(): Result<Statistics> {
        return runCatching {
            val thoughts = getCachedThoughts().getOrThrow()
            val now = Date()
            Statistics(
                week = createPeriod("week", 7, thoughts, now),
                month = createPeriod("month", 30, thoughts, now),
                year = createPeriod("year", 365, thoughts, now)
            )
        }
    }

    private suspend fun currentUserId(): String? {
        return sessionStorage.getSession()?.user?.id
    }

    private fun createPeriod(
        period: String,
        days: Int,
        thoughts: List<Thought>,
        now: Date
    ): PeriodStatistics {
        val from = CalmyDateTime.daysAgoStart(days, now)
        val datedThoughts = thoughts.mapNotNull { thought ->
            val date = CalmyDateTime.parse(thought.createdAt) ?: return@mapNotNull null
            if (date.before(from) || date.after(now)) {
                null
            } else {
                DatedThought(thought = thought, date = date)
            }
        }

        val dailyGroups = datedThoughts.groupBy { item -> CalmyDateTime.dateKey(item.date) }
        val dailyDynamics = dailyGroups.map { (dateKey, items) ->
            DailyStatistic(
                date = dateKey,
                averageAnxietyLevel = average(items.sumOf { item -> item.thought.anxietyLevel }, items.size),
                entriesCount = items.size
            )
        }.sortedBy { statistic -> statistic.date }

        val typeFrequencies = datedThoughts
            .groupingBy { item -> item.thought.anxietyType }
            .eachCount()
            .map { (type, count) ->
                AnxietyTypeCount(anxietyType = type, count = count)
            }
            .sortedWith(compareByDescending<AnxietyTypeCount> { item -> item.count }.thenBy { item -> item.anxietyType.apiValue })

        return PeriodStatistics(
            period = period,
            from = CalmyDateTime.nowIso(from.time),
            to = CalmyDateTime.nowIso(now.time),
            entriesCount = datedThoughts.size,
            averageAnxietyLevel = average(datedThoughts.sumOf { item -> item.thought.anxietyLevel }, datedThoughts.size),
            dailyDynamics = dailyDynamics,
            anxietyTypeFrequencies = typeFrequencies,
            mostAnxiousTime = mostAnxiousTime(datedThoughts)
        )
    }

    private fun mostAnxiousTime(thoughts: List<DatedThought>): String? {
        if (thoughts.isEmpty()) {
            return null
        }
        val groups = thoughts.groupBy { item -> timeBucket(CalmyDateTime.hourOfDay(item.date)) }
        return TimeBucketOrder.maxByOrNull { bucket ->
            val bucketThoughts = groups[bucket].orEmpty()
            if (bucketThoughts.isEmpty()) {
                -1.0
            } else {
                bucketThoughts.sumOf { item -> item.thought.anxietyLevel }.toDouble() / bucketThoughts.size
            }
        }
    }

    private fun timeBucket(hour: Int): String {
        return when (hour) {
            in 6..11 -> "morning"
            in 12..17 -> "day"
            in 18..23 -> "evening"
            else -> "night"
        }
    }

    private fun average(sum: Int, count: Int): Double {
        if (count == 0) {
            return 0.0
        }
        return round((sum.toDouble() / count) * 100) / 100
    }

    private fun validateThoughtInput(
        anxietyLevel: Int,
        description: String
    ): String? {
        return when {
            anxietyLevel !in 1..10 -> "Выбери уровень тревоги от 1 до 10"
            description.isBlank() -> "Напиши, что сейчас в голове"
            else -> null
        }
    }

    private data class DatedThought(
        val thought: Thought,
        val date: Date
    )

    private companion object {
        val TimeBucketOrder = listOf("night", "morning", "day", "evening")
    }
}
