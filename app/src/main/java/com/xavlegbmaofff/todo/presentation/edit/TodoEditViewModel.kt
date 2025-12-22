package com.xavlegbmaofff.todo.presentation.edit

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xavlegbmaofff.todo.data.model.Importance
import com.xavlegbmaofff.todo.data.model.TodoItem
import com.xavlegbmaofff.todo.domain.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TodoEditViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TodoEditUiState())
    val uiState: StateFlow<TodoEditUiState> = _uiState.asStateFlow()

    fun initializeFromTodoItem(todoItemUid: String?) {
        viewModelScope.launch {
            val todoItem = if (todoItemUid != null) {
                repository.getTodoByUid(todoItemUid)
            } else {
                null
            } ?: TodoItem(text = "")

            _uiState.update { _ ->
                TodoEditUiState(
                    text = todoItem.text,
                    importance = todoItem.importance,
                    isDone = todoItem.isDone,
                    deadline = todoItem.deadline,
                    selectedColor = Color(todoItem.color),
                    customColor = null
                )
            }
        }
    }

    fun updateText(text: String) {
        _uiState.update { it.copy(text = text) }
    }

    fun updateImportance(importance: Importance) {
        _uiState.update { it.copy(importance = importance) }
    }

    fun updateIsDone(isDone: Boolean) {
        _uiState.update { it.copy(isDone = isDone) }
    }

    fun updateDeadline(deadline: Instant?) {
        _uiState.update { it.copy(deadline = deadline) }
    }

    fun updateSelectedColor(color: Color) {
        _uiState.update { it.copy(selectedColor = color) }
    }

    fun updateCustomColor(color: Color?) {
        _uiState.update { it.copy(customColor = color) }
    }

    fun createTodoItem(existingUid: String? = null): TodoItem {
        val state = _uiState.value
        return TodoItem(
            uid = existingUid ?: UUID.randomUUID().toString(),
            text = state.text,
            importance = state.importance,
            color = state.selectedColor.toArgb(),
            deadline = state.deadline,
            isDone = state.isDone
        )
    }

    fun saveTodoItem(existingUid: String? = null) {
        viewModelScope.launch {
            val todoItem = createTodoItem(existingUid)
            repository.saveTodo(todoItem)
        }
    }
}