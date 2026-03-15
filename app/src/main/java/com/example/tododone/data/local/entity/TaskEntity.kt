package com.example.tododone.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["parentId"]),
        Index(value = ["dueDate"]),
        Index(value = ["status"]),
        Index(value = ["isAiSuggested"])
    ]
)
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val parentId: String?,
    val priority: String,
    val status: String,
    val dueDate: Long?,
    val dueTime: String?,
    val tagsJson: String,
    val reminderJson: String?,
    val recurrenceJson: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val completedAt: Long?,
    val isAiSuggested: Boolean,
    val aiConfidence: Float?,
    val source: String
)