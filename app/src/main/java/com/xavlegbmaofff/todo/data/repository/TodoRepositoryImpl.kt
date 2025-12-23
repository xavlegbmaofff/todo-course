package com.xavlegbmaofff.todo.data.repository

import com.xavlegbmaofff.todo.data.model.TodoItem
import com.xavlegbmaofff.todo.data.network.ServerErrorException
import com.xavlegbmaofff.todo.data.preferences.DeviceIdProvider
import com.xavlegbmaofff.todo.data.worker.SyncScheduler
import com.xavlegbmaofff.todo.domain.datasource.LocalDataSource
import com.xavlegbmaofff.todo.domain.datasource.RemoteDataSource
import com.xavlegbmaofff.todo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.slf4j.LoggerFactory
import java.time.Instant

class TodoRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val deviceIdProvider: DeviceIdProvider,
    private val syncScheduler: SyncScheduler
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

        val updatedItem = item.copy(
            changedAt = Instant.now(),
            lastUpdatedBy = deviceIdProvider.getDeviceId()
        )

        localDataSource.saveTodo(updatedItem)

        try {
            remoteDataSource.saveTodo(updatedItem)
            logger.info("Todo saved to server successfully: uid={}", item.uid)
        } catch (e: ServerErrorException) {
            // Ошибка сервера - запускаем Worker для retry
            logger.warn("Server error when saving todo, scheduling retry worker", e)
            syncScheduler.scheduleSyncNow()
        } catch (e: Exception) {
            logger.error("Failed to save todo to remote: {}", e.message, e)
            // Планируем синхронизацию на потом
            syncScheduler.scheduleSyncNow()
        }
    }

    override suspend fun deleteTodo(uid: String) {
        logger.debug("Deleting todo: uid={}", uid)

        localDataSource.deleteTodo(uid)

        try {
            remoteDataSource.deleteTodo(uid)
            logger.info("Todo deleted from server successfully: uid={}", uid)
        } catch (e: ServerErrorException) {
            // Ошибка сервера - запускаем Worker для retry
            logger.warn("Server error when deleting todo, scheduling retry worker", e)
            syncScheduler.scheduleSyncNow()
        } catch (e: Exception) {
            logger.error("Failed to delete todo from remote: {}", e.message, e)
            // Планируем синхронизацию на потом
            syncScheduler.scheduleSyncNow()
        }
    }

    override suspend fun sync() {
        logger.info("Starting synchronization with server...")

        try {
            val localTodos = localDataSource.getTodos().first()

            logger.debug("Syncing {} local todos with server", localTodos.size)

            // Сервер вернет смердженный список ("верим серверу")
            val syncedTodos = remoteDataSource.syncTodos(localTodos)

            logger.debug("Received {} todos from server after sync", syncedTodos.size)

            localDataSource.saveTodos(syncedTodos)

            logger.info("Synchronization completed successfully. Total todos: {}", syncedTodos.size)
        } catch (e: Exception) {
            logger.error("Synchronization failed: {}", e.message, e)
            throw e
        }
    }
}
