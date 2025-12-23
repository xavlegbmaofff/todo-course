package com.xavlegbmaofff.todo.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xavlegbmaofff.todo.data.model.TodoItem
import com.xavlegbmaofff.todo.domain.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    private val logger = LoggerFactory.getLogger(TodoListViewModel::class.java)

    private val _isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<TodoListUiState> = combine(
        repository.getTodos(),
        _isRefreshing
    ) { todos, isRefreshing ->
        TodoListUiState(
            items = todos,
            isLoading = isRefreshing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodoListUiState()
    )

    fun deleteItem(item: TodoItem) {
        viewModelScope.launch {
            repository.deleteTodo(item.uid)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                _isRefreshing.value = true
                logger.info("Starting pull-to-refresh sync")
                repository.sync()
                logger.info("Pull-to-refresh sync completed")
            } catch (e: Exception) {
                logger.error("Pull-to-refresh sync failed: ${e.message}", e)
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}