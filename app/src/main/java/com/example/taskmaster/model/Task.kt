package com.example.taskmaster.model

data class Task(
    val id: Int,
    val user: Int,
    val title: String,
    val created_at: String,
    val updated_at: String
)