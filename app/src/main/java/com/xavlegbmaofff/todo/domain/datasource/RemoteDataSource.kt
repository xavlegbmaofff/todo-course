package com.xavlegbmaofff.todo.domain.datasource

import com.xavlegbmaofff.todo.data.model.TodoItem

interface RemoteDataSource {
    suspend fun getTodos(): List<TodoItem>

    suspend fun saveTodo(item: TodoItem)

    suspend fun deleteTodo(uid: String)

    suspend fun syncTodos(localTodos: List<TodoItem>): List<TodoItem>
}