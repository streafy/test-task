package com.streafy.test_task.di

import com.streafy.test_task.data.CamerasRepository
import com.streafy.test_task.data.DoorsRepository
import com.streafy.test_task.data.remote.cameras.CamerasApi
import com.streafy.test_task.data.remote.doors.DoorsApi
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

    @Provides
    fun provideDoorsApi(httpClient: HttpClient) = DoorsApi(httpClient)

    @Provides
    fun provideDoorsRepository(realm: Realm, api: DoorsApi) = DoorsRepository(realm, api)
}