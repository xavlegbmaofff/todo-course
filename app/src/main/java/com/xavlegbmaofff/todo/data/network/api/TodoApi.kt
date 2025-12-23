package com.xavlegbmaofff.todo.data.network.api

import com.xavlegbmaofff.todo.data.network.dto.TodoElementRequest
import com.xavlegbmaofff.todo.data.network.dto.TodoElementResponse
import com.xavlegbmaofff.todo.data.network.dto.TodoListRequest
import com.xavlegbmaofff.todo.data.network.dto.TodoListResponse
import retrofit2.http.*

interface TodoApi {

    @GET("list")
    suspend fun getTodoList(): TodoListResponse

    @PATCH("list")
    suspend fun syncTodoList(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: TodoListRequest
    ): TodoListResponse

    @GET("list/{id}")
    suspend fun getTodoItem(
        @Path("id") id: String
    ): TodoElementResponse

    @POST("list")
    suspend fun createTodoItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: TodoElementRequest
    ): TodoElementResponse

    @PUT("list/{id}")
    suspend fun updateTodoItem(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: TodoElementRequest
    ): TodoElementResponse

    @DELETE("list/{id}")
    suspend fun deleteTodoItem(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int
    ): TodoElementResponse
}
