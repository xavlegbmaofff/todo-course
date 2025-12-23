package com.xavlegbmaofff.todo.data.local.mapper

import com.xavlegbmaofff.todo.data.local.entity.SyncStatus
import com.xavlegbmaofff.todo.data.local.entity.TodoItemEntity
import com.xavlegbmaofff.todo.data.model.TodoItem

fun TodoItem.toEntity(): TodoItemEntity {
    return TodoItemEntity(
        uid = uid,
        text = text,
        importance = importance,
        color = color,
        deadline = deadline,
        isDone = isDone,
        createdAt = createdAt,
        changedAt = changedAt,
        lastUpdatedBy = lastUpdatedBy,
        syncStatus = SyncStatus.NOT_SYNCED
    )
}

fun TodoItemEntity.toDomain(): TodoItem {
    return TodoItem(
        uid = uid,
        text = text,
        importance = importance,
        color = color,
        deadline = deadline,
        isDone = isDone,
        createdAt = createdAt,
        changedAt = changedAt,
        lastUpdatedBy = lastUpdatedBy
    )
}

fun List<TodoItemEntity>.toDomain(): List<TodoItem> {
    return map { it.toDomain() }
}

fun List<TodoItem>.toEntity(): List<TodoItemEntity> {
    return map { it.toEntity() }
}