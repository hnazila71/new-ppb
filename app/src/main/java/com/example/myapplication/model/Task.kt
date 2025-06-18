package com.example.myapplication.model // Pastikan package-nya benar

import java.time.LocalDateTime

data class Task(
    val id: Int,
    val title: String,
    val deadline: LocalDateTime,
    val isDone: Boolean = false
)