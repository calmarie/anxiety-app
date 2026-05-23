package com.example.calmy.data.repository

import com.example.calmy.core.CalmyDateTime
import com.example.calmy.data.local.session.SessionStorage
import com.example.calmy.data.local.thoughts.ThoughtDao
import com.example.calmy.data.local.thoughts.ThoughtEntity
import com.example.calmy.data.mapper.toEntity
import com.example.calmy.data.mapper.toDomain
import com.example.calmy.data.mapper.toSyncEntryInputDto
import com.example.calmy.data.remote.api.ThoughtsApi
import com.example.calmy.data.remote.dto.SyncThoughtsRequestDto
import com.example.calmy.data.remote.dto.ThoughtDto
import com.example.calmy.domain.model.AnxietyType
import com.example.calmy.domain.model.Statistics
import com.example.calmy.domain.model.Thought
import com.example.calmy.domain.repository.ThoughtsRepository
import java.io.IOException
import java.util.UUID
import retrofit2.HttpException

class ThoughtsRepositoryImpl(
    private val thoughtsApi: ThoughtsApi,
    private val thoughtDao: ThoughtDao,
    private val sessionStorage: SessionStorage
) : ThoughtsRepository {
    override suspend fun syncThought(
        anxietyLevel: Int,
        anxietyType: AnxietyType,
        description: String
    ): Result<List<Thought>> {
        val validationError = validateThoughtInput(anxietyLevel, description)
        if (validationError != null) {
            return Result.failure(Exception(validationError))
        }
        return try {
            val response = thoughtsApi.syncThoughts(
                request = SyncThoughtsRequestDto(
                    entries = listOf(
                        toSyncEntryInputDto(
                            anxietyLevel = anxietyLevel,
                            anxietyType = anxietyType,
                            description = description
                        )
                    )
                )
            )
            cacheRemoteThoughts(response.thoughts)
            getCachedThoughts()
        } catch (exception: HttpException) {
            Result.failure(Exception("Не удалось синхронизировать мысли. Код ошибки: ${exception.code()}"))
        } catch (_: IOException) {
            Result.failure(Exception("Не удалось подключиться к серверу"))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
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
        return try {
            val response = thoughtsApi.syncThoughts(
                request = SyncThoughtsRequestDto(
                    entries = listOf(
                        toSyncEntryInputDto(
                            anxietyLevel = anxietyLevel,
                            anxietyType = anxietyType,
                            description = description
                        )
                    )
                )
            )
            cacheRemoteThoughts(response.thoughts)
            getCachedThoughts()
        } catch (exception: HttpException) {
            Result.failure(Exception("Не удалось сохранить запись. Код ошибки: ${exception.code()}"))
        } catch (_: IOException) {
            savePendingThought(
                anxietyLevel = anxietyLevel,
                anxietyType = anxietyType,
                description = description
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun getThoughts(): Result<List<Thought>> {
        return refreshThoughts().recoverCatching {
            getCachedThoughts().getOrThrow()
        }
    }

    override suspend fun getCachedThoughts(): Result<List<Thought>> {
        return try {
            val userId = currentUserId()
            if (userId == null) {
                Result.success(emptyList())
            } else {
                Result.success(thoughtDao.getThoughts(userId).map { thought -> thought.toDomain() })
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun refreshThoughts(): Result<List<Thought>> {
        return try {
            syncPendingThoughts()
            val response = thoughtsApi.getThoughts()
            replaceWithRemoteThoughts(response.thoughts)
            getCachedThoughts()
        } catch (exception: HttpException) {
            Result.failure(Exception("Не удалось получить записи. Код ошибки: ${exception.code()}"))
        } catch (_: IOException) {
            Result.failure(Exception("Нет подключения. Показываю сохранённые записи, новые записи отправятся позже."))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun deleteThought(thoughtId: String): Result<List<Thought>> {
        return try {
            val userId = currentUserId()
                ?: return Result.failure(Exception("Не удалось удалить запись без сессии"))
            thoughtDao.deleteThought(userId, thoughtId)
            getCachedThoughts()
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun getStatistics(): Result<Statistics> {
        return try {
            Result.success(thoughtsApi.getStatistics().toDomain())
        } catch (exception: HttpException) {
            Result.failure(Exception("Не удалось получить статистику. Код ошибки: ${exception.code()}"))
        } catch (_: IOException) {
            Result.failure(Exception("Статистика доступна только при подключении к интернету"))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun cacheRemoteThoughts(thoughts: List<ThoughtDto>) {
        val userId = currentUserId() ?: thoughts.firstOrNull()?.userId ?: return
        thoughtDao.replaceThoughts(
            userId = userId,
            thoughts = thoughts.map { thought -> thought.toEntity() }
        )
    }

    private suspend fun replaceWithRemoteThoughts(thoughts: List<ThoughtDto>) {
        val userId = currentUserId() ?: thoughts.firstOrNull()?.userId ?: return
        thoughtDao.replaceWithSyncedThoughts(
            userId = userId,
            thoughts = thoughts.map { thought -> thought.toEntity() }
        )
    }

    private suspend fun syncPendingThoughts() {
        val userId = currentUserId() ?: return
        val pendingThoughts = thoughtDao.getPendingThoughts(userId)
        if (pendingThoughts.isEmpty()) {
            return
        }
        val response = thoughtsApi.syncThoughts(
            request = SyncThoughtsRequestDto(
                entries = pendingThoughts.map { thought ->
                    toSyncEntryInputDto(
                        anxietyLevel = thought.anxietyLevel,
                        anxietyType = AnxietyType.fromApiValue(thought.anxietyType),
                        description = thought.description
                    )
                }
            )
        )
        thoughtDao.replaceWithSyncedThoughts(
            userId = userId,
            thoughts = response.thoughts.map { thought -> thought.toEntity() }
        )
    }

    private suspend fun currentUserId(): String? {
        return sessionStorage.getSession()?.user?.id
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

    private suspend fun savePendingThought(
        anxietyLevel: Int,
        anxietyType: AnxietyType,
        description: String
    ): Result<List<Thought>> {
        val userId = currentUserId()
            ?: return Result.failure(Exception("Не удалось сохранить запись без сессии"))
        thoughtDao.addPendingThought(
            ThoughtEntity(
                id = "local_${UUID.randomUUID()}",
                userId = userId,
                anxietyLevel = anxietyLevel,
                anxietyType = anxietyType.apiValue,
                description = description,
                createdAt = CalmyDateTime.nowIso(),
                isPendingSync = true
            )
        )
        return Result.success(thoughtDao.getThoughts(userId).map { thought -> thought.toDomain() })
    }
}
