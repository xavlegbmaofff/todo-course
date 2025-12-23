package com.xavlegbmaofff.todo.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class RevisionStorage(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun getRevision(): Int {
        return prefs.getInt(KEY_REVISION, 0)
    }

    fun setRevision(revision: Int) {
        prefs.edit { putInt(KEY_REVISION, revision) }
    }

    fun clearRevision() {
        prefs.edit { remove(KEY_REVISION) }
    }

    companion object {
        private const val PREFS_NAME = "todo_prefs"
        private const val KEY_REVISION = "revision"
    }
}
