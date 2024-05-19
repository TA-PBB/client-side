package com.example.taskmaster.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("register/")
    fun register(@Body user: User): Call<User>

    @POST("login/")
    fun login(@Body user: User): Call<User>
}