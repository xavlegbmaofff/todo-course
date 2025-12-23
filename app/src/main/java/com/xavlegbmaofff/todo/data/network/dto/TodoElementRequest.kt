package com.xavlegbmaofff.todo.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoElementRequest(
    @SerialName("element")
    val element: TodoItemDto
)
