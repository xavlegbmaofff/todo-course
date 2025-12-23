package com.xavlegbmaofff.todo.data.network

import com.xavlegbmaofff.todo.data.model.TodoItem
import com.xavlegbmaofff.todo.data.network.api.TodoApi
import com.xavlegbmaofff.todo.data.network.dto.TodoElementRequest
import com.xavlegbmaofff.todo.data.network.dto.TodoListRequest
import com.xavlegbmaofff.todo.data.network.mapper.toDomain
import com.xavlegbmaofff.todo.data.network.mapper.toDto
import com.xavlegbmaofff.todo.data.preferences.RevisionStorage
import com.xavlegbmaofff.todo.domain.datasource.RemoteDataSource
import org.slf4j.LoggerFactory
import retrofit2.HttpException
import java.io.IOException

class RemoteDataSourceImpl(
    private val api: TodoApi,
    private val revisionStorage: RevisionStorage
) : RemoteDataSource {

    private val logger = LoggerFactory.getLogger(RemoteDataSourceImpl::class.java)

    override suspend fun getTodos(): List<TodoItem> {
        logger.info("Загрузка списка дел с сервера...")
        return try {
            val response = api.getTodoList()
            revisionStorage.setRevision(response.revision)
            logger.info("Получено {} дел с сервера, revision={}", response.list.size, response.revision)
            response.list.map { it.toDomain() }
        } catch (e: HttpException) {
            handleHttpException(e, "getTodos")
        } catch (e: IOException) {
            logger.error("Сетевая ошибка при загрузке списка: {}", e.message)
            throw NetworkException("Нет подключения к интернету", e)
        } catch (e: Exception) {
            logger.error("Неизвестная ошибка при загрузке списка: {}", e.message, e)
            throw e
        }
    }

    override suspend fun saveTodo(item: TodoItem) {
        logger.info("Отправка дела на сервер: uid={}", item.uid)
        try {
            val revision = revisionStorage.getRevision()
            val request = TodoElementRequest(element = item.toDto())

            val response = try {
                api.updateTodoItem(item.uid, revision, request)
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    logger.debug("Элемент не найден на сервере, создаем новый: uid={}", item.uid)
                    api.createTodoItem(revision, request)
                } else {
                    throw e
                }
            }

            revisionStorage.setRevision(response.revision)
            logger.info("Дело сохранено на сервере: uid={}, revision={}", item.uid, response.revision)
        } catch (e: HttpException) {
            handleHttpException(e, "saveTodo")
        } catch (e: IOException) {
            logger.error("Сетевая ошибка при сохранении дела: {}", e.message)
            throw NetworkException("Нет подключения к интернету", e)
        } catch (e: Exception) {
            logger.error("Неизвестная ошибка при сохранении дела: {}", e.message, e)
            throw e
        }
    }

    override suspend fun deleteTodo(uid: String) {
        logger.info("Удаление дела с сервера: uid={}", uid)
        try {
            val revision = revisionStorage.getRevision()
            val response = api.deleteTodoItem(uid, revision)
            revisionStorage.setRevision(response.revision)
            logger.info("Дело удалено с сервера: uid={}, revision={}", uid, response.revision)
        } catch (e: HttpException) {
            if (e.code() == 404) {
                logger.info("Элемент уже удален или не существует на сервере: uid={}", uid)
                return
            }
            handleHttpException(e, "deleteTodo")
        } catch (e: IOException) {
            logger.error("Сетевая ошибка при удалении дела: {}", e.message)
            throw NetworkException("Нет подключения к интернету", e)
        } catch (e: Exception) {
            logger.error("Неизвестная ошибка при удалении дела: {}", e.message, e)
            throw e
        }
    }

    override suspend fun syncTodos(localTodos: List<TodoItem>): List<TodoItem> {
        logger.info("Начало синхронизации с сервером. Локальных дел: {}", localTodos.size)
        return try {
            val revision = revisionStorage.getRevision()
            val request = TodoListRequest(list = localTodos.map { it.toDto() })

            val response = api.syncTodoList(revision, request)
            revisionStorage.setRevision(response.revision)

            logger.info(
                "Синхронизация завершена. Получено {} дел, revision={}",
                response.list.size,
                response.revision
            )

            response.list.map { it.toDomain() }
        } catch (e: HttpException) {
            handleHttpException(e, "syncTodos")
        } catch (e: IOException) {
            logger.error("Сетевая ошибка при синхронизации: {}", e.message)
            throw NetworkException("Нет подключения к интернету", e)
        } catch (e: Exception) {
            logger.error("Неизвестная ошибка при синхронизации: {}", e.message, e)
            throw e
        }
    }

    private fun handleHttpException(e: HttpException, operation: String): Nothing {
        val code = e.code()
        val message = e.message()

        logger.error("HTTP ошибка {} при {}: {}", code, operation, message)

        when (code) {
            400 -> throw BadRequestException("Некорректный запрос: $message", e)
            401 -> throw UnauthorizedException("Ошибка авторизации: проверьте Bearer токен", e)
            404 -> throw NotFoundException("Элемент не найден на сервере", e)
            500, 502, 503, 504 -> throw ServerErrorException("Ошибка сервера ($code)", e)
            else -> throw UnknownApiException("Неизвестная ошибка API ($code): $message", e)
        }
    }
}

sealed class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause)
class BadRequestException(message: String, cause: Throwable? = null) : ApiException(message, cause)
class UnauthorizedException(message: String, cause: Throwable? = null) : ApiException(message, cause)
class NotFoundException(message: String, cause: Throwable? = null) : ApiException(message, cause)
class ServerErrorException(message: String, cause: Throwable? = null) : ApiException(message, cause)
class UnknownApiException(message: String, cause: Throwable? = null) : ApiException(message, cause)
class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)
