package com.xavlegbmaofff.todo.data.repository

import com.xavlegbmaofff.todo.domain.datasource.LocalDataSource
import com.xavlegbmaofff.todo.domain.datasource.RemoteDataSource
import com.xavlegbmaofff.todo.data.model.TodoItem
import com.xavlegbmaofff.todo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import org.slf4j.LoggerFactory

class TodoRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : TodoRepository {

    private val logger = LoggerFactory.getLogger(TodoRepositoryImpl::class.java)

    override fun getTodos(): Flow<List<TodoItem>> {
        logger.debug("Getting todos flow from local data source")
        return localDataSource.getTodos()
    }

    override suspend fun getTodoByUid(uid: String): TodoItem? {
        logger.debug("Getting todo by uid={}", uid)
        return localDataSource.getTodoByUid(uid)
    }

    override suspend fun saveTodo(item: TodoItem) {
        logger.debug("Saving todo: uid={}", item.uid)

        localDataSource.saveTodo(item)

        try {
            remoteDataSource.saveTodo(item)
        } catch (e: Exception) {
            logger.error("Failed to save todo to remote: {}", e.message, e)
        }
    }

    override suspend fun deleteTodo(uid: String) {
        logger.debug("Deleting todo: uid={}", uid)
        localDataSource.deleteTodo(uid)

        try {
            remoteDataSource.deleteTodo(uid)
        } catch (e: Exception) {
            logger.error("Failed to delete todo from remote: {}", e.message, e)
        }
    }

    override suspend fun sync() {
        logger.info("Starting synchronization...")

        try {
            var localTodos: List<TodoItem> = emptyList()
            localDataSource.getTodos().collect { todos ->
                localTodos = todos
                return@collect
            }
            val syncedTodos = remoteDataSource.syncTodos(localTodos)

            localDataSource.saveTodos(syncedTodos)

            logger.info("Synchronization completed successfully")
        } catch (e: Exception) {
            logger.error("Synchronization failed: {}", e.message, e)
        }
    }
}
