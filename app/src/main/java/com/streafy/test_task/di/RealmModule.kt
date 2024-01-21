package com.streafy.test_task.di

import com.streafy.test_task.data.local.cameras.LocalCamera
import com.streafy.test_task.data.local.doors.LocalDoor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

@Module
@InstallIn(SingletonComponent::class)
class RealmModule {

    @Provides
    fun provideRealm(): Realm {
        val config = RealmConfiguration.create(schema = setOf(LocalCamera::class, LocalDoor::class))
        return Realm.open(config)
    }
}