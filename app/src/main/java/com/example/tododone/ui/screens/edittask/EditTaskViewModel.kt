package com.example.tododone.ui.screens.edittask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tododone.domain.model.Task
import com.example.tododone.domain.model.enums.Priority
import com.example.tododone.domain.model.enums.TaskStatus
import com.example.tododone.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import java.time.LocalTime
import javax.inject.Inject

data class EditTaskUiState(
    val taskId: String = "",
    val title: String = "",
    val description: String = "",
    val dueDate: kotlinx.datetime.LocalDate? = null,
    val dueTime: LocalTime? = null,
    val priority: Priority = Priority.MEDIUM,
    val status: TaskStatus = TaskStatus.PENDING,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val titleError: String? = null,
    val error: String? = null
)

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditTaskUiState())
    val uiState: StateFlow<EditTaskUiState> = _uiState.asStateFlow()

    private var originalTask: Task? = null

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.getTaskById(taskId).collect { task ->
                task?.let {
                    originalTask = it
                    _uiState.update { state ->
                        state.copy(
                            taskId = it.id,
                            title = it.title,
                            description = it.description ?: "",
                            dueDate = it.dueDate?.toLocalDateTime(TimeZone.currentSystemDefault())?.date,
                            dueTime = it.dueTime,
                            priority = it.priority,
                            status = it.status,
                            isLoading = false
                        )
                    }
                } ?: run {
                    _uiState.update { it.copy(isLoading = false, error = "Task not found") }
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateDueDate(date: kotlinx.datetime.LocalDate?) {
        _uiState.update { it.copy(dueDate = date) }
    }

    fun updateDueTime(time: LocalTime?) {
        _uiState.update { it.copy(dueTime = time) }
    }

    fun updatePriority(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun saveTask() {
        val currentState = _uiState.value
        val original = originalTask ?: return

        if (currentState.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val updatedTask = original.copy(
                title = currentState.title.trim(),
                description = currentState.description.takeIf { it.isNotBlank() },
                priority = currentState.priority,
                dueDate = currentState.dueDate?.atStartOfDayIn(TimeZone.currentSystemDefault()),
                dueTime = currentState.dueTime,
                updatedAt = Clock.System.now()
            )

            taskRepository.updateTask(updatedTask)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            error = error.message ?: "Failed to update task"
                        )
                    }
                }
        }
    }
}
