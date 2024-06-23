package com.example.taskmaster.model

data class TaskItem(
    val id: Int,
    val task : Int,
    val id_task: Int,
    val description: String,
    val completed: Boolean,
    val created_at: String,
    val updated_at: String
)