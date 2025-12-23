package com.xavlegbmaofff.todo.di

import android.content.Context
import com.xavlegbmaofff.todo.data.datasource.LocalDataSourceImpl
import com.xavlegbmaofff.todo.data.network.RemoteDataSourceImpl
import com.xavlegbmaofff.todo.data.network.api.TodoApi
import com.xavlegbmaofff.todo.data.preferences.DeviceIdProvider
import com.xavlegbmaofff.todo.data.preferences.RevisionStorage
import com.xavlegbmaofff.todo.data.repository.TodoRepositoryImpl
import com.xavlegbmaofff.todo.data.worker.SyncScheduler
import com.xavlegbmaofff.todo.domain.datasource.LocalDataSource
import com.xavlegbmaofff.todo.domain.datasource.RemoteDataSource
import com.xavlegbmaofff.todo.domain.repository.TodoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTodoFile(@ApplicationContext context: Context): File {
        return File(context.filesDir, "todos.json")
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(todoFile: File): LocalDataSource {
        return LocalDataSourceImpl(todoFile)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        api: TodoApi,
        revisionStorage: RevisionStorage
    ): RemoteDataSource {
        return RemoteDataSourceImpl(api, revisionStorage)
    }

    @Provides
    @Singleton
    fun provideSyncScheduler(@ApplicationContext context: Context): SyncScheduler {
        return SyncScheduler(context)
    }

    @Provides
    @Singleton
    fun provideTodoRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource,
        deviceIdProvider: DeviceIdProvider,
        syncScheduler: SyncScheduler
    ): TodoRepository {
        return TodoRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            deviceIdProvider = deviceIdProvider,
            syncScheduler = syncScheduler
        )
    }
}