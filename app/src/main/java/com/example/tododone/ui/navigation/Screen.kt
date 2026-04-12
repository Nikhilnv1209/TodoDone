package com.example.tododone.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val icon: ImageVector? = null,
    val label: String = "",
    val isFab: Boolean = false
) {
    // Bottom Navigation Items
    object Home : Screen("home", Icons.Default.Home, "Home")
    object Calendar : Screen("calendar", Icons.Default.CalendarToday, "Calendar")
    object Add : Screen("add", Icons.Default.Add, "Add", isFab = true)
    object AIChat : Screen("aichat", Icons.Default.ChatBubble, "AI")
    object Profile : Screen("profile", Icons.Default.Person, "Profile")

    // Other Screens
    object AddTask : Screen("add_task")
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: String) = "task_detail/$taskId"
    }
    object EditTask : Screen("edit_task/{taskId}") {
        fun createRoute(taskId: String) = "edit_task/$taskId"
    }
}

// Bottom navigation items list
val bottomNavItems = listOf(Screen.Home, Screen.Calendar, Screen.AIChat, Screen.Profile)
