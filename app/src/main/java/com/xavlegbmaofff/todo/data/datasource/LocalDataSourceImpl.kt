package com.xavlegbmaofff.todo.data.datasource

import com.xavlegbmaofff.todo.data.model.TodoItem
import com.xavlegbmaofff.todo.data.mappers.json
import com.xavlegbmaofff.todo.data.mappers.parseTodoItem
import com.xavlegbmaofff.todo.domain.datasource.LocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.slf4j.LoggerFactory
import java.io.File
import java.time.Instant

class LocalDataSourceImpl(
    private val todoFile: File
) : LocalDataSource {

    private val logger = LoggerFactory.getLogger(LocalDataSourceImpl::class.java)
    private val mutex = Mutex()

    private val _todosFlow = MutableStateFlow<List<TodoItem>>(emptyList())

    init {
        loadFromFile()
    }

    override fun getTodos(): Flow<List<TodoItem>> {
        return _todosFlow.asStateFlow()
    }

    override suspend fun getTodoByUid(uid: String): TodoItem? = mutex.withLock {
        _todosFlow.value.find { it.uid == uid }
    }

    override suspend fun saveTodo(item: TodoItem) = mutex.withLock {
        logger.debug("Saving todo: uid={}, text='{}'", item.uid, item.text)

        val currentList = _todosFlow.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.uid == item.uid }

        if (existingIndex != -1) {
            currentList[existingIndex] = item
            logger.debug("Updated existing todo with uid={}", item.uid)
        } else {
            currentList.add(item)
            logger.debug("Added new todo with uid={}", item.uid)
        }

        _todosFlow.value = currentList
        saveToFile()
    }

    override suspend fun deleteTodo(uid: String) = mutex.withLock {
        logger.debug("Deleting todo with uid={}", uid)

        val currentList = _todosFlow.value.toMutableList()
        val removed = currentList.removeAll { it.uid == uid }

        if (removed) {
            _todosFlow.value = currentList
            saveToFile()
            logger.info("Deleted todo with uid={}. Remaining: {}", uid, currentList.size)
        } else {
            logger.warn("Todo with uid={} not found", uid)
        }
    }

    override suspend fun saveTodos(items: List<TodoItem>) = mutex.withLock {
        logger.debug("Saving {} todos", items.size)
        _todosFlow.value = items
        saveToFile()
    }

    override suspend fun clear() = mutex.withLock {
        logger.debug("Clearing all todos")
        _todosFlow.value = emptyList()
        saveToFile()
    }

    private fun loadFromFile() {
        try {
            logger.debug("Loading todos from file: {}", todoFile.absolutePath)

            if (!todoFile.exists()) {
                logger.warn("File does not exist: {}. Starting with empty list.", todoFile.absolutePath)
                _todosFlow.value = emptyList()
                return
            }

            val array = JSONArray(todoFile.readText())
            val items = mutableListOf<TodoItem>()
            var loadedCount = 0
            var failedCount = 0

            repeat(array.length()) { index ->
                val parsed = array.getJSONObject(index).parseTodoItem()
                if (parsed != null) {
                    items.add(parsed)
                    loadedCount++
                } else {
                    failedCount++
                    logger.warn("Failed to parse todo at index {}", index)
                }
            }

            pruneExpired(items)
            _todosFlow.value = items

            logger.info("Loaded {} todos from {} ({} failed to parse)",
                loadedCount, todoFile.absolutePath, failedCount)
        } catch (e: Exception) {
            logger.error("Failed to load todos from {}: {}", todoFile.absolutePath, e.message, e)
            _todosFlow.value = emptyList()
        }
    }

    private fun saveToFile() {
        try {
            logger.debug("Saving todos to file: {}", todoFile.absolutePath)

            val items = _todosFlow.value
            pruneExpired(items.toMutableList())

            val array = JSONArray()
            items.forEach { array.put(it.json) }

            todoFile.writeText(array.toString(4))
            logger.info("Successfully saved {} todos to {}", items.size, todoFile.absolutePath)
        } catch (e: Exception) {
            logger.error("Failed to save todos to {}: {}", todoFile.absolutePath, e.message, e)
        }
    }

    private fun pruneExpired(items: MutableList<TodoItem>) {
        val now = Instant.now()
        val sizeBefore = items.size
        items.removeAll { it.deadline != null && it.deadline.isBefore(now) }
        val removed = sizeBefore - items.size
        if (removed > 0) {
            logger.info("Pruned {} expired todo(s). Remaining: {}", removed, items.size)
        }
    }
}
