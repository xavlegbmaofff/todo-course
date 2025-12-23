package com.xavlegbmaofff.todo.data.model

import android.graphics.Color
import java.time.Instant
import java.util.UUID

enum class Importance(val value: String) {
    LOW("Неважная"),
    NORMAL("Обычная"),
    HIGH("Важная");

    companion object {
        fun fromValue(value: String): Importance =
            values().find { it.value == value } ?: NORMAL
    }
}

data class TodoItem(
    val uid: String = UUID.randomUUID().toString(),
    val text: String,
    val importance: Importance = Importance.NORMAL,
    val color: Int = Color.WHITE,
    val deadline: Instant? = null,
    val isDone: Boolean = false
)