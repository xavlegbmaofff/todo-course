package com.xavlegbmaofff.todo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xavlegbmaofff.todo.data.local.dao.TodoItemDao
import com.xavlegbmaofff.todo.data.local.entity.TodoItemEntity

@Database(
    entities = [TodoItemEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoItemDao(): TodoItemDao
}