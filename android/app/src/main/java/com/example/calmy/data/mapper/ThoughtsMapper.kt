package com.example.calmy.data.mapper

import com.example.calmy.data.local.thoughts.ThoughtEntity
import com.example.calmy.data.remote.dto.SyncEntryInputDto
import com.example.calmy.data.remote.dto.ThoughtDto
import com.example.calmy.domain.model.AnxietyType
import com.example.calmy.domain.model.Thought

fun ThoughtDto.toDomain(): Thought {
    return Thought(
        id = id,
        userId = userId,
        anxietyLevel = anxietyLevel,
        anxietyType = AnxietyType.fromApiValue(anxietyType),
        description = description,
        createdAt = createdAt,
        isPendingSync = false
    )
}

fun ThoughtDto.toEntity(): ThoughtEntity {
    return ThoughtEntity(
        id = id,
        userId = userId,
        anxietyLevel = anxietyLevel,
        anxietyType = anxietyType,
        description = description,
        createdAt = createdAt,
        isPendingSync = false
    )
}

fun ThoughtEntity.toDomain(): Thought {
    return Thought(
        id = id,
        userId = userId,
        anxietyLevel = anxietyLevel,
        anxietyType = AnxietyType.fromApiValue(anxietyType),
        description = description,
        createdAt = createdAt,
        isPendingSync = isPendingSync
    )
}

fun Thought.toEntity(): ThoughtEntity {
    return ThoughtEntity(
        id = id,
        userId = userId,
        anxietyLevel = anxietyLevel,
        anxietyType = anxietyType.apiValue,
        description = description,
        createdAt = createdAt,
        isPendingSync = isPendingSync
    )
}

fun toSyncEntryInputDto(
    anxietyLevel: Int,
    anxietyType: AnxietyType,
    description: String
): SyncEntryInputDto {
    return SyncEntryInputDto(
        anxietyLevel = anxietyLevel,
        anxietyType = anxietyType.apiValue,
        description = description
    )
}
