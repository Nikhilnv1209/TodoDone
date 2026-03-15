package com.example.tododone.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.tododone.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE parentId IS NULL ORDER BY createdAt DESC")
    fun getRootTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE parentId = :parentId ORDER BY createdAt DESC")
    fun getSubtasks(parentId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskByIdFlow(id: String): Flow<TaskEntity?>

    @Query("""
        SELECT * FROM tasks
        WHERE dueDate >= :startOfDay AND dueDate < :endOfDay AND parentId IS NULL
        ORDER BY priority DESC, createdAt DESC
    """)
    fun getTasksForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE status = :status
        ORDER BY createdAt DESC
    """)
    fun getTasksByStatus(status: String): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
        AND parentId IS NULL
        ORDER BY createdAt DESC
    """)
    fun searchTasks(query: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isAiSuggested = 1 ORDER BY createdAt DESC")
    fun getAiSuggestedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE recurrenceJson IS NOT NULL")
    fun getRecurringTasks(): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE parentId IS NULL
        ORDER BY
            CASE priority
                WHEN 'HIGH' THEN 3
                WHEN 'MEDIUM' THEN 2
                WHEN 'LOW' THEN 1
                ELSE 0
            END DESC,
            createdAt DESC
    """)
    fun getTasksOrderedByPriority(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)

    @Query("DELETE FROM tasks WHERE parentId = :parentId")
    suspend fun deleteSubtasks(parentId: String)

    @Transaction
    suspend fun deleteTaskAndSubtasks(taskId: String) {
        deleteSubtasks(taskId)
        deleteTaskById(taskId)
    }

    @Query("SELECT COUNT(*) FROM tasks WHERE parentId = :parentId AND status = 'COMPLETED'")
    suspend fun getCompletedSubtaskCount(parentId: String): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE parentId = :parentId")
    suspend fun getTotalSubtaskCount(parentId: String): Int

    @Query("UPDATE tasks SET status = :status, completedAt = :completedAt, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: String, status: String, completedAt: Long?, updatedAt: Long)

    @Query("SELECT * FROM tasks WHERE parentId = :parentId")
    suspend fun getSubtasksSync(parentId: String): List<TaskEntity>
}