package com.example.taskmaster.model

data class User(
    val username: String,
    val password: String,
    val token: String? = null

)