package com.xavlegbmaofff.todo.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Добавляем новую колонку syncStatus со значением по умолчанию NOT_SYNCED
        db.execSQL(
            """
            ALTER TABLE todo_items
            ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'NOT_SYNCED'
            """.trimIndent()
        )

        db.execSQL(
            """
            UPDATE todo_items
            SET syncStatus = 'SYNCED'
            """.trimIndent()
        )
    }
}