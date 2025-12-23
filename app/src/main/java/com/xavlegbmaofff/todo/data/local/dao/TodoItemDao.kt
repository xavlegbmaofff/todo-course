package com.xavlegbmaofff.todo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xavlegbmaofff.todo.data.local.entity.TodoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {
    @Query("SELECT * FROM todo_items")
    fun getAllTodos(): Flow<List<TodoItemEntity>>

    @Query("SELECT * FROM todo_items WHERE uid = :uid")
    suspend fun getTodoByUid(uid: String): TodoItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodos(todos: List<TodoItemEntity>)

    @Query("DELETE FROM todo_items WHERE uid = :uid")
    suspend fun deleteTodoByUid(uid: String)

    @Query("DELETE FROM todo_items")
    suspend fun deleteAllTodos()
}