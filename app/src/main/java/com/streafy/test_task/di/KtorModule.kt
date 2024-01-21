package com.streafy.test_task.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class KtorModule {

    @Singleton
    @Provides
    fun provideHttpClient() =
        HttpClient(Android) {
            install(ContentNegotiation) {
                json()
            }
        }
}