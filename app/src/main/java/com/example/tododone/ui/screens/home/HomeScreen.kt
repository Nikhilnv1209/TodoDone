package com.example.tododone.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tododone.domain.model.Task
import com.example.tododone.domain.model.enums.Priority
import com.example.tododone.domain.model.enums.TaskStatus
import com.example.tododone.ui.theme.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddTask: () -> Unit,
    onNavigateToTaskDetail: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val greeting = when (currentDate.hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..21 -> "Good evening"
        else -> "Good night"
    }

    Scaffold(
        containerColor = BackgroundPrimary,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTask,
                containerColor = PrimaryCream,
                contentColor = OnPrimaryDark,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Section
            item {
                HeaderSection(
                    greeting = greeting,
                      taskCount = uiState.tasks.count { it.status != TaskStatus.COMPLETED },
                      completedCount = uiState.tasks.count { it.status == TaskStatus.COMPLETED }
                )
            }

            // Category Cards
            item {
                CategoryCardsSection(
                    totalTasks = uiState.tasks.size,
                      completedTasks = uiState.tasks.count { it.status == TaskStatus.COMPLETED }
                )
            }

            // Ongoing Tasks Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ongoing",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    TextButton(onClick = { }) {
                        Text(
                            text = "See All",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Task List
            if (uiState.tasks.isEmpty()) {
                item {
                    EmptyState()
                }
            } else {
                items(
                      items = uiState.tasks.filter { it.status != TaskStatus.COMPLETED },
                    key = { it.id }
                ) { task ->
                    PremiumTaskCard(
                        task = task,
                        onClick = { onNavigateToTaskDetail(task.id) },
                        onToggleComplete = { viewModel.toggleTaskCompletion(task.id) }
                    )
                }
            }

              // Completed Tasks Section
              val completedTasks = uiState.tasks.filter { it.status == TaskStatus.COMPLETED }
            if (completedTasks.isNotEmpty()) {
                item {
                    Text(
                        text = "Completed (${completedTasks.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                items(
                    items = completedTasks,
                    key = { it.id }
                ) { task ->
                    PremiumTaskCard(
                        task = task,
                        onClick = { onNavigateToTaskDetail(task.id) },
                        onToggleComplete = { viewModel.toggleTaskCompletion(task.id) }
                    )
                }
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryCream)
            }
        }
    }
}

@Composable
private fun HeaderSection(
    greeting: String,
    taskCount: Int,
    completedCount: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$greeting ",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "You've got $taskCount tasks to do",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextSecondary
            )
        )

        // Progress Bar
        if (taskCount + completedCount > 0) {
            Spacer(modifier = Modifier.height(16.dp))
            val progress = if (taskCount + completedCount > 0) {
                completedCount.toFloat() / (taskCount + completedCount)
            } else 0f

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = PrimaryCream,
                trackColor = BackgroundElevated,
            )
        }
    }
}

@Composable
private fun CategoryCardsSection(
    totalTasks: Int,
    completedTasks: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // All Tasks Card
        CategoryCard(
            title = "All Tasks",
            count = totalTasks,
            gradient = Brush.linearGradient(
                colors = listOf(AccentLavender, AccentSky)
            ),
            modifier = Modifier.weight(1f)
        )

        // Completed Card
        CategoryCard(
            title = "Completed",
            count = completedTasks,
            gradient = Brush.linearGradient(
                colors = listOf(AccentMint, AccentCream)
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CategoryCard(
    title: String,
    count: Int,
    gradient: Brush,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = BackgroundPrimary
                )
            )
            Text(
                text = "$count",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = BackgroundPrimary
                )
            )
        }
    }
}

@Composable
private fun PremiumTaskCard(
    task: Task,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit
) {
      val priorityColor = when (task.priority) {
          Priority.HIGH -> PriorityHigh
          Priority.MEDIUM -> PriorityMedium
          Priority.LOW -> PriorityLow
          Priority.NONE -> TextTertiary
      }

    val isTaskCompleted = task.status == TaskStatus.COMPLETED
    val cardBackground = if (isTaskCompleted) {
        BackgroundSecondary.copy(alpha = 0.5f)
    } else {
        BackgroundSecondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority Indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(priorityColor)
            )

            Spacer(modifier = Modifier.width(16.dp))

              // Task Content
              Column(modifier = Modifier.weight(1f)) {
                  Text(
                      text = task.title,
                      style = MaterialTheme.typography.titleMedium.copy(
                          fontWeight = FontWeight.SemiBold,
                          color = if (isTaskCompleted) TextTertiary else TextPrimary
                      )
                  )
                  if (!task.description.isNullOrBlank()) {
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                          text = task.description,
                          style = MaterialTheme.typography.bodyMedium.copy(
                              color = TextSecondary
                          ),
                          maxLines = 1
                      )
                  }
              }

            // Completion Checkbox
            IconButton(onClick = onToggleComplete) {
                Icon(
                    imageVector = if (isTaskCompleted) {
                        Icons.Filled.CheckCircle
                    } else {
                        Icons.Outlined.CheckCircle
                    },
                    contentDescription = if (isTaskCompleted) "Mark incomplete" else "Mark complete",
                    tint = if (isTaskCompleted) SuccessGreen else TextTertiary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(BackgroundElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No tasks yet",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap + to add your first task",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextTertiary
                )
            )
        }
    }
}
