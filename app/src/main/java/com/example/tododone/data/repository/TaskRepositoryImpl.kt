package com.example.tododone.data.repository

import com.example.tododone.data.local.dao.TaskDao
import com.example.tododone.data.mapper.TaskMapper
import com.example.tododone.domain.model.Task
import com.example.tododone.domain.model.enums.TaskStatus
import com.example.tododone.domain.repository.SyncResult
import com.example.tododone.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.plus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskMapper: TaskMapper
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> =
        taskDao.getRootTasks()
            .map { entities -> entities.map { taskMapper.toDomain(it) } }

    override fun getTasksByDate(date: kotlinx.datetime.LocalDate): Flow<List<Task>> {
        val startOfDay = date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        val endOfDay = date.plus(kotlinx.datetime.DatePeriod(days = 1))
            .atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        return taskDao.getTasksForDateRange(startOfDay, endOfDay)
            .map { entities -> entities.map { taskMapper.toDomain(it) } }
    }

    override fun getTasksByParent(parentId: String?): Flow<List<Task>> {
        return if (parentId == null) {
            getAllTasks()
        } else {
            taskDao.getSubtasks(parentId)
                .map { entities -> entities.map { taskMapper.toDomain(it) } }
        }
    }

    override fun getTaskById(id: String): Flow<Task?> =
        taskDao.getTaskByIdFlow(id)
            .map { it?.let { taskMapper.toDomain(it) } }

    override fun searchTasks(query: String): Flow<List<Task>> =
        taskDao.searchTasks(query)
            .map { entities -> entities.map { taskMapper.toDomain(it) } }

    override fun getTasksByStatus(status: String): Flow<List<Task>> =
        taskDao.getTasksByStatus(status)
            .map { entities -> entities.map { taskMapper.toDomain(it) } }

    override fun getAiSuggestedTasks(): Flow<List<Task>> =
        taskDao.getAiSuggestedTasks()
            .map { entities -> entities.map { taskMapper.toDomain(it) } }

    override suspend fun createTask(task: Task): Result<Task> = runCatching {
        taskDao.insertTask(taskMapper.toEntity(task))
        task
    }

    override suspend fun updateTask(task: Task): Result<Task> = runCatching {
        val updatedTask = task.copy(updatedAt = Clock.System.now())
        taskDao.updateTask(taskMapper.toEntity(updatedTask))
        updatedTask
    }

    override suspend fun deleteTask(id: String): Result<Unit> = runCatching {
        taskDao.deleteTaskAndSubtasks(id)
    }

    override suspend fun completeTask(id: String, completed: Boolean): Result<Task> = runCatching {
        val now = Clock.System.now()
        val entity = taskDao.getTaskById(id)
            ?: throw IllegalArgumentException("Task not found: $id")

        val updatedEntity = entity.copy(
            status = if (completed) TaskStatus.COMPLETED.name else TaskStatus.PENDING.name,
            completedAt = if (completed) now.toEpochMilliseconds() else null,
            updatedAt = now.toEpochMilliseconds()
        )
        taskDao.updateTask(updatedEntity)
        taskMapper.toDomain(updatedEntity)
    }

    override suspend fun getSubtasks(parentId: String): List<Task> =
        taskDao.getSubtasksSync(parentId).map { taskMapper.toDomain(it) }

    override suspend fun sync(): Result<SyncResult> {
        return Result.success(SyncResult.NoOp)
    }

    override fun isSyncEnabled(): Boolean = false
}