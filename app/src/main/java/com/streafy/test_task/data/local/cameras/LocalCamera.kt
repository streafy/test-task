package com.streafy.test_task.data.local.cameras

import com.streafy.test_task.domain.entities.Camera
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class LocalCamera(
    @PrimaryKey
    var id: Int,
    var name: String,
    var snapshot: String,
    var room: String ,
    var favorites: Boolean ,
    var rec: Boolean
) : RealmObject {
    constructor(): this(-1, "", "", "", false, false)
}

fun LocalCamera.toDomain(): Camera = Camera(
    id = id,
    name = name,
    snapshot = snapshot,
    room = room,
    favorites = favorites,
    rec = rec
)

fun Camera.toLocal(): LocalCamera =
    LocalCamera(id, name, snapshot, room, favorites, rec)