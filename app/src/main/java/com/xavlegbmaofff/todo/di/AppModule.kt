package com.xavlegbmaofff.todo.di

import android.content.Context
import com.xavlegbmaofff.todo.domain.datasource.LocalDataSource
import com.xavlegbmaofff.todo.data.datasource.LocalDataSourceImpl
import com.xavlegbmaofff.todo.domain.datasource.RemoteDataSource
import com.xavlegbmaofff.todo.data.datasource.RemoteDataSourceImpl
import com.xavlegbmaofff.todo.data.repository.TodoRepositoryImpl
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
    fun provideRemoteDataSource(): RemoteDataSource {
        return RemoteDataSourceImpl()
    }

    @Provides
    @Singleton
    fun provideTodoRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource
    ): TodoRepository {
        return TodoRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource
        )
    }
}