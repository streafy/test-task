package com.streafy.test_task.ui

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.rememberNavController
import com.streafy.test_task.navigation.AppNavHost
import com.streafy.test_task.ui.screens.cameras.CamerasScreen
import com.streafy.test_task.ui.screens.doors.DoorsScreen

@Composable
fun MainScreen() {
    var state by remember {
        mutableIntStateOf(0)
    }

    val navController = rememberNavController()

    Scaffold(
        topBar = {
            val navBackStackEntry = navController.currentBackStackEntry

            val navigationItems = listOf(NavigationItem.Cameras, NavigationItem.Doors)

            TabRow(selectedTabIndex = state) {
                navigationItems.forEachIndexed { index, item ->
                    val selected = navBackStackEntry?.destination?.hierarchy?.any {
                        it.route == item.screen.route
                    } ?: false

                    Tab(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                state = index
                                navController.navigate(item.screen.route)
                            }
                        },
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text(text = item.name)
                    }
                }
            }
        }
    ) {
        AppNavHost(
            navHostController = navController,
            camerasScreenContent = { CamerasScreen(padding = it) },
            doorsScreenContent = { DoorsScreen(padding = it) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}