package com.example.tododone.domain.model

import com.example.tododone.domain.model.enums.Priority
import com.example.tododone.domain.model.enums.TaskSource
import com.example.tododone.domain.model.enums.TaskStatus
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.time.LocalTime

data class Task(
    val id: String,
    val title: String,
    val description: String?,
    val parentId: String?,
    val priority: Priority,
    val status: TaskStatus,
    val dueDate: Instant?,
    val dueTime: LocalTime?,
    val tags: List<String>,
    val reminder: Reminder?,
    val recurrence: RecurrenceRule?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val completedAt: Instant?,
    val isAiSuggested: Boolean,
    val aiConfidence: Float?,
    val source: TaskSource
) {
    fun isOverdue(): Boolean =
        status == TaskStatus.PENDING && dueDate?.let { it < Clock.System.now() } == true

    fun calculateProgress(subtasks: List<Task>): Float =
        if (subtasks.isEmpty()) {
            if (status == TaskStatus.COMPLETED) 1f else 0f
        } else {
            subtasks.count { it.status == TaskStatus.COMPLETED }.toFloat() / subtasks.size
        }
}