package com.streafy.test_task.data.remote.cameras

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class CamerasApi @Inject constructor(
    private val httpClient: HttpClient
) {

    suspend fun getCameras(): List<NetworkCamera> {
        val response: NetworkCameraResponse =
            httpClient.get("https://cars.cprogroup.ru/api/rubetek/cameras/").body()
        return response.data.cameras
    }
}