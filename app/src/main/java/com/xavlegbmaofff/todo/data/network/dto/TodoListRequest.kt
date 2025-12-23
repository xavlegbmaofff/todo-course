package com.xavlegbmaofff.todo.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoListRequest(
    @SerialName("list")
    val list: List<TodoItemDto>
)
