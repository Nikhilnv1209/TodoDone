package com.example.tododone.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tododone.domain.model.Task
import com.example.tododone.domain.model.enums.TaskStatus
import com.example.tododone.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            taskRepository.getAllTasks().collect { tasks ->
                _uiState.update { currentState ->
                    currentState.copy(
                        tasks = tasks.sortedWith(
                            compareBy<Task> { it.status == TaskStatus.COMPLETED }
                                .thenByDescending { it.priority.ordinal }
                                .thenBy { it.dueDate }
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(taskId)
        }
    }
}
