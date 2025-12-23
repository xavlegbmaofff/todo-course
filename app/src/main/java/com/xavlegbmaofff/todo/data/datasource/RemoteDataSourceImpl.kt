package com.xavlegbmaofff.todo.data.datasource

import com.xavlegbmaofff.todo.data.model.TodoItem
import com.xavlegbmaofff.todo.domain.datasource.RemoteDataSource
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory

class RemoteDataSourceImpl : RemoteDataSource {

    private val logger = LoggerFactory.getLogger(RemoteDataSourceImpl::class.java)

    override suspend fun getTodos(): List<TodoItem> {
        logger.info("Загрузка списка дел с сервера...")
        delay(500)

        logger.info("Список дел с сервера получен (пока пустой)")
        return emptyList()
    }

    override suspend fun saveTodo(item: TodoItem) {
        logger.info("Отправка дела на сервер: uid={}, text='{}'", item.uid, item.text)
        delay(300)

        logger.info("Дело успешно сохранено на сервере: uid={}", item.uid)
    }

    override suspend fun deleteTodo(uid: String) {
        logger.info("Удаление дела с сервера: uid={}", uid)
        delay(300)

        logger.info("Дело успешно удалено с сервера: uid={}", uid)
    }

    override suspend fun syncTodos(localTodos: List<TodoItem>): List<TodoItem> {
        logger.info("Начало синхронизации с сервером...")
        logger.info("Локальных дел: {}", localTodos.size)
        delay(800)

        logger.info("Синхронизация завершена. Дел после синхронизации: {}", localTodos.size)

        return localTodos
    }
}
