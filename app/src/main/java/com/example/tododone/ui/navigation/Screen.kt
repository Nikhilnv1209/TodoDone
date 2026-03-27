package com.example.tododone.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddTask : Screen("add_task")
    data object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: String) = "task_detail/$taskId"
    }
    data object EditTask : Screen("edit_task/{taskId}") {
        fun createRoute(taskId: String) = "edit_task/$taskId"
    }
}
