package com.streafy.test_task.data

import com.streafy.test_task.data.local.doors.LocalDoor
import com.streafy.test_task.data.local.doors.toDomain
import com.streafy.test_task.data.local.doors.toLocal
import com.streafy.test_task.data.remote.doors.DoorsApi
import com.streafy.test_task.data.remote.doors.toDomain
import com.streafy.test_task.domain.entities.Door
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

class DoorsRepository(
    private val realm: Realm,
    private val api: DoorsApi
) {

    suspend fun getDoors(): List<Door> {
        val localDoors = getLocalDoors()
        if (localDoors.isNotEmpty()) {
            return localDoors
        }
        return api.getDoors().map { it.toDomain() }.also { overwriteDatabase(it) }
    }

    suspend fun updateDoor(door: Door) {
        realm.write {
            val queriedDoor = query<LocalDoor>(query = "id = $0", door.id).first().find()
            queriedDoor?.apply {
                favorites = door.favorites
                name = door.name
            }
        }
    }

    suspend fun getNetworkDoors(): List<Door> =
        api.getDoors().map { it.toDomain() }.also { overwriteDatabase(it) }

    fun getLocalDoors(): List<Door> = realm.query<LocalDoor>().find().map { it.toDomain() }

    private suspend fun overwriteDatabase(doors: List<Door>) {
        realm.write {
            val existingDoors = query<LocalDoor>().find()
            delete(existingDoors)
        }
        doors.forEach { door ->
            realm.write { copyToRealm(door.toLocal()) }
        }
    }
}