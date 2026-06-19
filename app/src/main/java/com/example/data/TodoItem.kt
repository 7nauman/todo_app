package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: String = "Medium", // High, Medium, Low
    val category: String = "Personal", // Personal, Work, Shopping, Health, Other
    val dueDate: Long? = null, // timestamp
    val createdAt: Long = System.currentTimeMillis()
)
