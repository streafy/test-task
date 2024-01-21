package com.streafy.test_task.domain.entities

data class Camera(
    val id: Int,
    val name: String,
    val snapshot: String,
    val room: String,
    val favorites: Boolean,
    val rec: Boolean
)
