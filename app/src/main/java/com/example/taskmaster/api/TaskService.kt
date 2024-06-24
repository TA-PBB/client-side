package com.example.taskmaster.api

import com.example.taskmaster.model.Task
import com.example.taskmaster.model.TaskItem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskService {

    @POST("tasks/")
    fun createTask(@Body task: Task): Call<Task>

    @GET("tasks/")
    fun getTasks(): Call<List<Task>>

    @GET("tasks/search/")
    fun searchTasks(@Query("search") query: String): Call<List<Task>>

    @GET("tasks/{id}/")
    fun getTask(@Path("id") id: Int): Call<Task>

    @PUT("tasks/{id}/")
    fun updateTask(@Path("id") id: Int, @Body task: Task): Call<Task>

    @DELETE("tasks/{id}/")
    fun deleteTask(@Path("id") id: Int): Call<Void>

    @GET("tasks/{task_id}/items/")
    fun getTaskItems(@Path("task_id") taskId: Int): Call<List<TaskItem>>

    @POST("tasks/{task_id}/items/")
    fun createTaskItem(@Path("task_id") taskId: Int, @Body taskItem: TaskItem): Call<TaskItem>

    @GET("tasks/{task_id}/items/{id}/")
    fun getTaskItem(@Path("task_id") taskId: Int, @Path("id") id: Int): Call<TaskItem>

    @PUT("tasks/{task_id}/items/{id}/")
    fun updateTaskItem(@Path("task_id") taskId: Int, @Path("id") id: Int, @Body taskItem: TaskItem): Call<TaskItem>

    @DELETE("tasks/{task_id}/items/{id}/")
    fun deleteTaskItem(@Path("task_id") taskId: Int, @Path("id") id: Int): Call<Void>

    @GET("tasks/user/{user_id}/")
    fun getTasksByUser(@Query("user_id") userId: Int): Call<List<Task>>

}
