package com.xavlegbmaofff.todo.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xavlegbmaofff.todo.data.model.TodoItem
import com.xavlegbmaofff.todo.domain.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    val uiState: StateFlow<TodoListUiState> = repository.getTodos()
        .map { todos -> TodoListUiState(items = todos) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TodoListUiState()
        )

    fun deleteItem(item: TodoItem) {
        viewModelScope.launch {
            repository.deleteTodo(item.uid)
        }
    }

    fun syncWithServer() {
        viewModelScope.launch {
            repository.sync()
        }
    }
}