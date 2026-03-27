package com.example.tododone.ui.screens.addtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tododone.domain.model.Task
import com.example.tododone.domain.model.enums.Priority
import com.example.tododone.domain.model.enums.TaskSource
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
import java.time.LocalTime
import javax.inject.Inject
import com.example.tododone.domain.model.Reminder
import com.example.tododone.domain.model.RecurrenceRule

data class AddTaskUiState(
    val title: String = "",
    val description: String = "",
    val dueDate: kotlinx.datetime.LocalDate? = null,
    val dueTime: LocalTime? = null,
    val priority: Priority = Priority.MEDIUM,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val titleError: String? = null,
    val error: String? = null
)

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()

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

        if (currentState.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val now = Clock.System.now()
            val task = Task(
                id = java.util.UUID.randomUUID().toString(),
                title = currentState.title.trim(),
                description = currentState.description.takeIf { it.isNotBlank() },
                parentId = null,
                priority = currentState.priority,
                status = TaskStatus.PENDING,
                dueDate = currentState.dueDate?.let { date ->
                    date.atStartOfDayIn(TimeZone.currentSystemDefault())
                },
                dueTime = currentState.dueTime,
                tags = emptyList(),
                reminder = null,
                recurrence = null,
                createdAt = now,
                updatedAt = now,
                completedAt = null,
                isAiSuggested = false,
                aiConfidence = null,
                source = TaskSource.MANUAL
            )

            taskRepository.createTask(task)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            error = error.message ?: "Failed to save task"
                        )
                    }
                }
        }
    }
}
