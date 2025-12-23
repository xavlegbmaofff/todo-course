package com.xavlegbmaofff.todo.domain.repository

import com.xavlegbmaofff.todo.data.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    fun getTodos(): Flow<List<TodoItem>>

    suspend fun getTodoByUid(uid: String): TodoItem?

    suspend fun saveTodo(item: TodoItem)

    suspend fun deleteTodo(uid: String)

    suspend fun sync()
}
