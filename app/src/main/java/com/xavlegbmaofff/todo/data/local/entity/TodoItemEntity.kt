package com.xavlegbmaofff.todo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xavlegbmaofff.todo.data.model.Importance
import java.time.Instant

@Entity(tableName = "todo_items")
data class TodoItemEntity(
    @PrimaryKey
    val uid: String,
    val text: String,
    val importance: Importance,
    val color: Int,
    val deadline: Instant?,
    val isDone: Boolean,
    val createdAt: Instant,
    val changedAt: Instant,
    val lastUpdatedBy: String,
    val syncStatus: SyncStatus = SyncStatus.NOT_SYNCED
)