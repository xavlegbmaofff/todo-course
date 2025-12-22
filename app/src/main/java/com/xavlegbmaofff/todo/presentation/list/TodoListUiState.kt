package com.xavlegbmaofff.todo.presentation.list

import com.xavlegbmaofff.todo.data.model.TodoItem

data class TodoListUiState(
    val items: List<TodoItem> = emptyList(),
    val isLoading: Boolean = false
)