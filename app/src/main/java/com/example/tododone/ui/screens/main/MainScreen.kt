package com.example.tododone.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tododone.ui.components.BottomNavBar
import com.example.tododone.ui.navigation.Screen
import com.example.tododone.ui.navigation.bottomNavItems
import com.example.tododone.ui.screens.addtask.AddTaskScreen
import com.example.tododone.ui.screens.aichat.AIChatScreen
import com.example.tododone.ui.screens.calendar.CalendarScreen
import com.example.tododone.ui.screens.home.HomeScreen
import com.example.tododone.ui.screens.profile.ProfileScreen

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    // Show bottom nav only on main screens
    val showBottomNav = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onAddClick = {
                        navController.navigate(Screen.AddTask.route)
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onNavigateToAddTask = {
                            navController.navigate(Screen.AddTask.route)
                        },
                        onNavigateToTaskDetail = { taskId ->
                            navController.navigate(Screen.TaskDetail.createRoute(taskId))
                        }
                    )
                }

                composable(Screen.Calendar.route) {
                    CalendarScreen()
                }

                composable(Screen.AIChat.route) {
                    AIChatScreen()
                }

                composable(Screen.Profile.route) {
                    ProfileScreen()
                }

                composable(Screen.AddTask.route) {
                    AddTaskScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    route = Screen.TaskDetail.route,
                    arguments = listOf(
                        navArgument("taskId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                    // TODO: Create TaskDetailScreen
                    Box(modifier = Modifier.fillMaxSize())
                }

                composable(
                    route = Screen.EditTask.route,
                    arguments = listOf(
                        navArgument("taskId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                    // TODO: Create EditTaskScreen
                    Box(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
