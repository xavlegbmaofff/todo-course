package com.xavlegbmaofff.todo.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object TodoList

@Serializable
data class TodoEdit(val todoItemUid: String? = null)
