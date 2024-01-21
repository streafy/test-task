package com.streafy.test_task.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavHost(
    navHostController: NavHostController,
    camerasScreenContent: @Composable () -> Unit,
    doorsScreenContent: @Composable () -> Unit
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Cameras.route
    ) {
        composable(Screen.Cameras.route) {
            camerasScreenContent()
        }
        composable(Screen.Doors.route) {
            doorsScreenContent()
        }
    }
}