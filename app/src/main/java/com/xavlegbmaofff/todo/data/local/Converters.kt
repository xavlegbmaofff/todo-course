package com.xavlegbmaofff.todo.data.local

import androidx.room.TypeConverter
import com.xavlegbmaofff.todo.data.local.entity.SyncStatus
import com.xavlegbmaofff.todo.data.model.Importance
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun toTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun fromImportance(importance: Importance): String {
        return importance.name
    }

    @TypeConverter
    fun toImportance(value: String): Importance {
        return Importance.valueOf(value)
    }

    @TypeConverter
    fun fromSyncStatus(syncStatus: SyncStatus): String {
        return syncStatus.name
    }

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus {
        return SyncStatus.valueOf(value)
    }
}