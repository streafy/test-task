package com.streafy.test_task.data.remote.doors

import com.streafy.test_task.domain.entities.Door
import kotlinx.serialization.Serializable

@Serializable
data class NetworkDoorResponse(
    val data: List<NetworkDoor>,
    val success: Boolean
)

@Serializable
data class NetworkDoor(
    val favorites: Boolean,
    val id: Int,
    val name: String,
    val room: String?,
    val snapshot: String = ""
)

fun NetworkDoor.toDomain(): Door = Door(
    id = id,
    name = name,
    room = room ?: "",
    snapshot = snapshot,
    favorites = favorites
)