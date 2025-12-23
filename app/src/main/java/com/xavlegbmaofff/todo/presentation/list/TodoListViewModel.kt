package com.xavlegbmaofff.todo.presentation.list

import androidx.lifecycle.ViewModel
import com.xavlegbmaofff.todo.data.datasource.FileStorage
import com.xavlegbmaofff.todo.data.model.TodoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val storage: FileStorage
) : ViewModel() {
    private val _uiState = MutableStateFlow(TodoListUiState())
    val uiState: StateFlow<TodoListUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        _uiState.update {
            it.copy(items = storage.items)
        }
    }

    fun deleteItem(item: TodoItem) {
        storage.delete(item.uid)
        loadItems()
    }
}