package com.example.taskmaster.api

import com.example.taskmaster.model.Task
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

interface TaskService {
    @GET("tasks/")
    fun getTasks(): Call<List<Task>>

    @POST("tasks/")
    fun createTask(@Body task: Task): Call<Task>
}
