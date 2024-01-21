package com.streafy.test_task.navigation

sealed class Screen(
    val route: String
) {

    data object Cameras : Screen(ROUTE_CAMERAS)
    data object Doors : Screen(ROUTE_DOORS)

    companion object {

        const val ROUTE_CAMERAS = "cameras"
        const val ROUTE_DOORS = "doors"
    }
}