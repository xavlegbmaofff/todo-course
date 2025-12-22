package com.xavlegbmaofff.todo.di

import android.content.Context
import com.xavlegbmaofff.todo.data.datasource.FileStorage
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
    fun provideFileStorage(todoFile: File): FileStorage {
        val storage = FileStorage()
        storage.load(todoFile)
        return storage
    }
}