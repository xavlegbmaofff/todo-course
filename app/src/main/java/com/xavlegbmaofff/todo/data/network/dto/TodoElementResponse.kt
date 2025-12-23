package com.xavlegbmaofff.todo.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoElementResponse(
    @SerialName("status")
    val status: String,

    @SerialName("element")
    val element: TodoItemDto,

    @SerialName("revision")
    val revision: Int
)
