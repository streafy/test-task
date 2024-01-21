package com.streafy.test_task.data.local.doors

import com.streafy.test_task.domain.entities.Door
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class LocalDoor(
    @PrimaryKey
    var id: Int,
    var name: String,
    var room: String,
    var favorites: Boolean,
    var snapshot: String,
) : RealmObject {
    constructor(): this(-1, "", "", false, "")
}

fun LocalDoor.toDomain(): Door = Door(
    id = id,
    name = name,
    room = room,
    favorites = favorites,
    snapshot = snapshot
)

fun Door.toLocal(): LocalDoor = LocalDoor(id, name, room, favorites, snapshot)
