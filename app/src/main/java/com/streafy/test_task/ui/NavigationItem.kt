package com.streafy.test_task.ui

import com.streafy.test_task.navigation.Screen

sealed class NavigationItem(
    val screen: Screen,
    val name: String
) {

    data object Cameras : NavigationItem(Screen.Cameras, "Камеры")
    data object Doors : NavigationItem(Screen.Doors, "Двери")
}