package com.streafy.test_task.data.remote.cameras

import com.streafy.test_task.domain.entities.Camera
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCameraResponse(
    val data: Data,
    val success: Boolean
)

@Serializable
data class Data(
    val cameras: List<NetworkCamera>,
    val room: List<Room?>
)

@Serializable
enum class Room {
    FIRST, SECOND
}

@Serializable
data class NetworkCamera(
    val favorites: Boolean,
    val id: Int,
    val name: String,
    val rec: Boolean,
    val room: Room?,
    val snapshot: String
)

fun NetworkCamera.toDomain(): Camera = Camera(
    id = id,
    name = name,
    snapshot = snapshot,
    room = room?.name ?: "",
    favorites = favorites,
    rec = rec
)