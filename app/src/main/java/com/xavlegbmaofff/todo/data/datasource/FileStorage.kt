package com.xavlegbmaofff.todo.data.datasource

import android.os.Build
import androidx.annotation.RequiresApi
import com.xavlegbmaofff.todo.data.model.TodoItem
import com.xavlegbmaofff.todo.data.mappers.json
import com.xavlegbmaofff.todo.data.mappers.parseTodoItem
import org.json.JSONArray
import org.slf4j.LoggerFactory
import java.io.File
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
class FileStorage {
    private val logger = LoggerFactory.getLogger(FileStorage::class.java)
    private val _items = mutableListOf<TodoItem>()

    val items: List<TodoItem>
        get() = _items.toList()

    fun getItemByUid(uid: String?): TodoItem? {
        return items.find { it.uid == uid }
    }

    fun add(item: TodoItem) {
        logger.debug("Adding item: uid={}, text='{}', importance={}", item.uid, item.text, item.importance)
        pruneExpired()
        _items.add(item)
        logger.info("Item added successfully. Total items: {}", _items.size)
    }

    fun delete(uid: String) {
        logger.debug("Deleting item with uid={}", uid)
        val sizeBefore = _items.size
        _items.removeAll { it.uid == uid }
        val removed = sizeBefore - _items.size
        if (removed > 0) {
            logger.info("Deleted {} item(s) with uid={}. Remaining items: {}", removed, uid, _items.size)
        } else {
            logger.warn("No item found with uid={}", uid)
        }
    }

    fun save(file: File) {
        logger.debug("Saving items to file: {}", file.absolutePath)
        pruneExpired()
        val array = JSONArray()
        _items.forEach { array.put(it.json) }
        try {
            file.writeText(array.toString(4))
            logger.info("Successfully saved {} items to {}", _items.size, file.absolutePath)
        } catch (e: Exception) {
            logger.error("Failed to save items to {}: {}", file.absolutePath, e.message, e)
            throw e
        }
    }

    fun load(file: File) {
        logger.debug("Loading items from file: {}", file.absolutePath)
        if (!file.exists()) {
            logger.warn("File does not exist: {}. Clearing items.", file.absolutePath)
            _items.clear()
            return
        }
        try {
            val array = JSONArray(file.readText())
            _items.clear()
            var loadedCount = 0
            var failedCount = 0
            repeat(array.length()) { index ->
                val parsed = array.getJSONObject(index).parseTodoItem()
                if (parsed != null) {
                    _items.add(parsed)
                    loadedCount++
                } else {
                    failedCount++
                    logger.warn("Failed to parse item at index {}", index)
                }
            }
            logger.info("Loaded {} items from {} ({} failed to parse)", loadedCount, file.absolutePath, failedCount)
            pruneExpired()
        } catch (e: Exception) {
            logger.error("Failed to load items from {}: {}", file.absolutePath, e.message, e)
            _items.clear()
        }
    }

    private fun pruneExpired() {
        val now = Instant.now()
        val sizeBefore = _items.size
        _items.removeAll { it.deadline != null && it.deadline.isBefore(now) }
        val removed = sizeBefore - _items.size
        if (removed > 0) {
            logger.info("Pruned {} expired item(s). Remaining items: {}", removed, _items.size)
        }
    }
}