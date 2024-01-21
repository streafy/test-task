package com.streafy.test_task.data

import com.streafy.test_task.data.local.cameras.LocalCamera
import com.streafy.test_task.data.local.cameras.toDomain
import com.streafy.test_task.data.local.cameras.toLocal
import com.streafy.test_task.data.remote.cameras.CamerasApi
import com.streafy.test_task.data.remote.cameras.toDomain
import com.streafy.test_task.domain.entities.Camera
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import javax.inject.Inject


class CamerasRepository @Inject constructor(
    private val realm: Realm,
    private val api: CamerasApi
) {

    suspend fun getCameras(): List<Camera> {
        val localCameras = getLocalCameras()
        if (localCameras.isNotEmpty()) {
            return localCameras
        }

        val cameras = api.getCameras().map { it.toDomain() }
        overwriteDatabase(cameras)

        return cameras
    }

    suspend fun updateCamera(camera: Camera) {
        realm.write {
            val queriedCamera = query<LocalCamera>(query = "id = $0", camera.id).first().find()
            queriedCamera?.favorites = camera.favorites
        }
    }

    suspend fun getNetworkCameras(): List<Camera> {
        val cameras = api.getCameras().map { it.toDomain() }
        overwriteDatabase(cameras)
        return cameras
    }

    fun getLocalCameras(): List<Camera> = realm.query<LocalCamera>().find().map { it.toDomain() }

    private suspend fun overwriteDatabase(cameras: List<Camera>) {
        realm.write {
            val existingCameras = query<LocalCamera>().find()
            delete(existingCameras)
        }
        cameras.forEach { camera ->
            realm.write { copyToRealm(camera.toLocal()) }
        }
    }
}