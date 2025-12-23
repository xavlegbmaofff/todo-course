package com.xavlegbmaofff.todo.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoListResponse(
    @SerialName("status")
    val status: String,

    @SerialName("list")
    val list: List<TodoItemDto>,

    @SerialName("revision")
    val revision: Int
)
