package com.example.kanban_firebase_projmanagement

data class Column (
    val  title: String,
    val tasks: MutableList<Task>
    )