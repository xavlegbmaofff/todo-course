package com.xavlegbmaofff.todo.data.local

import com.xavlegbmaofff.todo.data.local.dao.TodoItemDao
import com.xavlegbmaofff.todo.data.local.mapper.toDomain
import com.xavlegbmaofff.todo.data.local.mapper.toEntity
import com.xavlegbmaofff.todo.data.model.TodoItem
import com.xavlegbmaofff.todo.domain.datasource.LocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomLocalDataSource @Inject constructor(
    private val todoItemDao: TodoItemDao
) : LocalDataSource {

    override fun getTodos(): Flow<List<TodoItem>> {
        return todoItemDao.getAllTodos().map { entities ->
            entities.toDomain()
        }
    }

    override suspend fun getTodoByUid(uid: String): TodoItem? {
        return todoItemDao.getTodoByUid(uid)?.toDomain()
    }

    override suspend fun saveTodo(item: TodoItem) {
        todoItemDao.insertTodo(item.toEntity())
    }

    override suspend fun deleteTodo(uid: String) {
        todoItemDao.deleteTodoByUid(uid)
    }

    override suspend fun saveTodos(items: List<TodoItem>) {
        todoItemDao.insertTodos(items.toEntity())
    }

    override suspend fun clear() {
        todoItemDao.deleteAllTodos()
    }
}