package com.example.myapplication.model

import java.time.LocalDateTime // Pastikan import ini benar

data class Task(
    val id: Int,
    val title: String,
    val deadline: LocalDateTime, // Pastikan tipe datanya LocalDateTime
    val isDone: Boolean = false
)