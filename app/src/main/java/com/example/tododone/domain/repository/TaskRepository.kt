package com.example.tododone.domain.repository

import com.example.tododone.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTasksByDate(date: kotlinx.datetime.LocalDate): Flow<List<Task>>
    fun getTasksByParent(parentId: String?): Flow<List<Task>>
    fun getTaskById(id: String): Flow<Task?>
    fun searchTasks(query: String): Flow<List<Task>>
    fun getTasksByStatus(status: String): Flow<List<Task>>
    fun getAiSuggestedTasks(): Flow<List<Task>>

    suspend fun createTask(task: Task): Result<Task>
    suspend fun updateTask(task: Task): Result<Task>
    suspend fun deleteTask(id: String): Result<Unit>
    suspend fun completeTask(id: String, completed: Boolean): Result<Task>
    suspend fun getSubtasks(parentId: String): List<Task>

    suspend fun sync(): Result<SyncResult>
    fun isSyncEnabled(): Boolean
}

sealed class SyncResult {
    data object Success : SyncResult()
    data object NoOp : SyncResult()
    data class Error(val message: String) : SyncResult()
}