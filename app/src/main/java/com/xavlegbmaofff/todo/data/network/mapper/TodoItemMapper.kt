package com.xavlegbmaofff.todo.data.network.mapper

import android.graphics.Color
import com.xavlegbmaofff.todo.data.model.Importance
import com.xavlegbmaofff.todo.data.model.TodoItem
import com.xavlegbmaofff.todo.data.network.dto.TodoItemDto
import java.time.Instant
import androidx.core.graphics.toColorInt

fun TodoItem.toDto(): TodoItemDto {
    return TodoItemDto(
        id = uid,
        text = text,
        importance = importance.toApiString(),
        deadline = deadline?.epochSecond,
        done = isDone,
        color = if (color == Color.WHITE) null else String.format("#%06X", 0xFFFFFF and color),
        createdAt = createdAt.epochSecond,
        changedAt = changedAt.epochSecond,
        lastUpdatedBy = lastUpdatedBy
    )
}

fun TodoItemDto.toDomain(): TodoItem {
    return TodoItem(
        uid = id,
        text = text,
        importance = importance.toImportance(),
        deadline = deadline?.let { Instant.ofEpochSecond(it) },
        isDone = done,
        color = color?.let { parseColor(it) } ?: Color.WHITE,
        createdAt = Instant.ofEpochSecond(createdAt),
        changedAt = Instant.ofEpochSecond(changedAt),
        lastUpdatedBy = lastUpdatedBy
    )
}

private fun Importance.toApiString(): String = when (this) {
    Importance.LOW -> "low"
    Importance.NORMAL -> "basic"
    Importance.HIGH -> "important"
}

private fun String.toImportance(): Importance = when (this) {
    "low" -> Importance.LOW
    "basic" -> Importance.NORMAL
    "important" -> Importance.HIGH
    else -> Importance.NORMAL
}

private fun parseColor(hexColor: String): Int {
    return try {
        hexColor.toColorInt()
    } catch (e: IllegalArgumentException) {
        Color.WHITE
    }
}
