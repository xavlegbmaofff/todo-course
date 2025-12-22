package com.xavlegbmaofff.todo.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.xavlegbmaofff.todo.presentation.edit.TodoEditScreen
import com.xavlegbmaofff.todo.presentation.list.TodoListScreen

@Composable
fun TodoNavigationGraph(
    modifier: Modifier = Modifier
) {
    val backStack = remember {
        mutableStateListOf<Any>(TodoList)
    }

    Scaffold(modifier = modifier) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            modifier = Modifier.padding(innerPadding),
            entryProvider = { key ->
                when (key) {
                    TodoList -> NavEntry(key) {
                        TodoListScreen(
                            onItemClick = { item ->
                                backStack.add(TodoEdit(item.uid))
                            },
                            onAddClick = {
                                backStack.add(TodoEdit(null))
                            }
                        )
                    }
                    is TodoEdit -> NavEntry(key) {
                        TodoEditScreen(
                            todoItemUid = key.todoItemUid,
                            onNavigateBack = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }
                    else -> {
                        NavEntry(Unit) { Text(text = "Invalid Key: $it") }
                    }
                }
            }
        )
    }
}
