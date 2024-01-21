package com.streafy.test_task.di

import com.streafy.test_task.data.CamerasRepository
import com.streafy.test_task.data.remote.cameras.CamerasApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.realm.kotlin.Realm

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    fun provideCamerasApi(httpClient: HttpClient) = CamerasApi(httpClient)

    @Provides
    fun provideCamerasRepository(realm: Realm, api: CamerasApi) = CamerasRepository(realm, api)
}