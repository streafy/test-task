package com.streafy.test_task.data.local.doors

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class LocalDoor : RealmObject {
    @PrimaryKey
    var id: Int = -1
    var name: String = ""
    var room: String = ""
    var favorites: Boolean = false
    var snapshot: String = ""
}