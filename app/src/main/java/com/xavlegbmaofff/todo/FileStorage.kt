package com.xavlegbmaofff.todo

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import org.json.JSONArray
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
class FileStorage {
    private val _items = mutableListOf<TodoItem>()

    val items: List<TodoItem>
        get() = _items.toList()

    fun add(item: TodoItem) {
        pruneExpired()
        _items.add(item)
    }

    fun delete(uid: String) {
        _items.removeAll { it.uid == uid }
    }

    fun save(file: File) {
        pruneExpired()
        val array = JSONArray()
        _items.forEach { array.put(it.json) }
        file.writeText(array.toString(4))
    }

    fun load(file: File) {
        if (!file.exists()) {
            _items.clear()
            return
        }
        val array = JSONArray(file.readText())
        _items.clear()
        repeat(array.length()) { index ->
            array.getJSONObject(index).parseTodoItem()?.let { _items.add(it) }
        }
        pruneExpired()
    }

    private fun pruneExpired() {
        val now = Instant.now()
        _items.removeAll { it.deadline != null && it.deadline.isBefore(now) }
    }
}
