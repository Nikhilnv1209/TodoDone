package com.example.tododone.data.mapper

import com.example.tododone.data.local.entity.TaskEntity
import com.example.tododone.domain.model.RecurrenceRule
import com.example.tododone.domain.model.Reminder
import com.example.tododone.domain.model.Task
import com.example.tododone.domain.model.enums.Priority
import com.example.tododone.domain.model.enums.TaskSource
import com.example.tododone.domain.model.enums.TaskStatus
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class TaskMapper @Inject constructor() {

    private val json = Json { ignoreUnknownKeys = true }

    fun toEntity(task: Task): TaskEntity {
        return TaskEntity(
            id = task.id,
            title = task.title,
            description = task.description,
            parentId = task.parentId,
            priority = task.priority.name,
            status = task.status.name,
            dueDate = task.dueDate?.toEpochMilliseconds(),
            dueTime = task.dueTime?.toString(),
            tagsJson = json.encodeToString(task.tags),
            reminderJson = task.reminder?.let { json.encodeToString(it) },
            recurrenceJson = task.recurrence?.let { json.encodeToString(it) },
            createdAt = task.createdAt.toEpochMilliseconds(),
            updatedAt = task.updatedAt.toEpochMilliseconds(),
            completedAt = task.completedAt?.toEpochMilliseconds(),
            isAiSuggested = task.isAiSuggested,
            aiConfidence = task.aiConfidence,
            source = task.source.name
        )
    }

    fun toDomain(entity: TaskEntity): Task {
        return Task(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            parentId = entity.parentId,
            priority = Priority.valueOf(entity.priority),
            status = TaskStatus.valueOf(entity.status),
            dueDate = entity.dueDate?.let { kotlinx.datetime.Instant.fromEpochMilliseconds(it) },
            dueTime = entity.dueTime?.let { java.time.LocalTime.parse(it) },
            tags = json.decodeFromString(entity.tagsJson),
            reminder = entity.reminderJson?.let { json.decodeFromString<Reminder>(it) },
            recurrence = entity.recurrenceJson?.let { json.decodeFromString<RecurrenceRule>(it) },
            createdAt = kotlinx.datetime.Instant.fromEpochMilliseconds(entity.createdAt),
            updatedAt = kotlinx.datetime.Instant.fromEpochMilliseconds(entity.updatedAt),
            completedAt = entity.completedAt?.let { kotlinx.datetime.Instant.fromEpochMilliseconds(it) },
            isAiSuggested = entity.isAiSuggested,
            aiConfidence = entity.aiConfidence,
            source = TaskSource.valueOf(entity.source)
        )
    }
}