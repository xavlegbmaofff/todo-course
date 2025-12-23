package com.xavlegbmaofff.todo.di

import android.content.Context
import androidx.room.Room
import com.xavlegbmaofff.todo.data.local.RoomLocalDataSource
import com.xavlegbmaofff.todo.data.local.TodoDatabase
import com.xavlegbmaofff.todo.data.local.dao.TodoItemDao
import com.xavlegbmaofff.todo.data.local.migration.MIGRATION_1_2
import com.xavlegbmaofff.todo.domain.datasource.LocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTodoDatabase(@ApplicationContext context: Context): TodoDatabase {
        return Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todo_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideTodoItemDao(database: TodoDatabase): TodoItemDao {
        return database.todoItemDao()
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(todoItemDao: TodoItemDao): LocalDataSource {
        return RoomLocalDataSource(todoItemDao)
    }
}