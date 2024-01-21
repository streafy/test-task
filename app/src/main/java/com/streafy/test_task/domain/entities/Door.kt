package com.streafy.test_task.domain.entities

data class Door(
    val id: Int,
    val name: String,
    val room: String,
    val snapshot: String?,
    val favorites: Boolean
)