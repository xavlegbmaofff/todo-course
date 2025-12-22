package com.xavlegbmaofff.todo.presentation.edit

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import com.xavlegbmaofff.todo.data.model.Importance
import com.xavlegbmaofff.todo.data.model.TodoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.util.UUID

class TodoEditViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TodoEditUiState())
    val uiState: StateFlow<TodoEditUiState> = _uiState.asStateFlow()

    fun initializeFromTodoItem(todoItem: TodoItem?) {
        todoItem?.let {
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
}