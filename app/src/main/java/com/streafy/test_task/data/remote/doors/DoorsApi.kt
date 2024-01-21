package com.streafy.test_task.data.remote.doors

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class DoorsApi @Inject constructor(
    private val httpClient: HttpClient
) {

    suspend fun getDoors(): List<NetworkDoor> {
        val response: NetworkDoorResponse =
            httpClient.get("https://cars.cprogroup.ru/api/rubetek/doors/").body()
        return response.data
    }
}