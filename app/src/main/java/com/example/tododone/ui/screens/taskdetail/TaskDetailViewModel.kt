package com.example.tododone.ui.screens.taskdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tododone.domain.model.Task
import com.example.tododone.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskDetailUiState(
    val task: Task? = null,
    val isLoading: Boolean = true,
    val isDeleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    private var currentTaskId: String? = null

    fun loadTask(taskId: String) {
        currentTaskId = taskId
        viewModelScope.launch {
            taskRepository.getTaskById(taskId).collect { task ->
                _uiState.update {
                    it.copy(task = task, isLoading = false)
                }
            }
        }
    }

    fun toggleComplete() {
        val task = _uiState.value.task ?: return
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(task.id)
        }
    }

    fun deleteTask() {
        val task = _uiState.value.task ?: return
        viewModelScope.launch {
            taskRepository.deleteTask(task.id)
                .onSuccess {
                    _uiState.update { it.copy(isDeleted = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }
}
