package com.example.tododone.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tododone.domain.model.Task
import com.example.tododone.domain.model.enums.Priority
import com.example.tododone.domain.model.enums.TaskStatus
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (task.status == TaskStatus.COMPLETED) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleComplete) {
                Icon(
                    imageVector = if (task.status == TaskStatus.COMPLETED) {
                        Icons.Filled.CheckCircle
                    } else {
                        Icons.Outlined.Circle
                    },
                    contentDescription = if (task.status == TaskStatus.COMPLETED) {
                        "Mark incomplete"
                    } else {
                        "Mark complete"
                    },
                    tint = if (task.status == TaskStatus.COMPLETED) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.status == TaskStatus.COMPLETED) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                  task.dueDate?.let { dueDate ->
                      val localDate = dueDate.toLocalDateTime(TimeZone.currentSystemDefault())
                      val dateStr = "${localDate.month.name.take(3)} ${localDate.dayOfMonth}, ${localDate.year}"
                      Text(
                          text = dateStr,
                          style = MaterialTheme.typography.bodySmall,
                          color = if (task.isOverdue()) {
                              MaterialTheme.colorScheme.error
                          } else {
                              MaterialTheme.colorScheme.onSurfaceVariant
                          }
                      )
                  }
            }

            if (task.priority.ordinal >= 2) {
                PriorityIndicator(priority = task.priority)
            }
        }
    }
}

@Composable
private fun PriorityIndicator(priority: Priority) {
    val color = when (priority) {
        Priority.HIGH -> MaterialTheme.colorScheme.error
        Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
        Priority.LOW -> MaterialTheme.colorScheme.outline
        Priority.NONE -> MaterialTheme.colorScheme.outline
    }
    
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = priority.name,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
