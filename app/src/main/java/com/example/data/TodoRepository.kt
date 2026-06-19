package com.example.data

import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {
    val allTasks: Flow<List<TodoItem>> = todoDao.getAllTasks()

    suspend fun insertTask(task: TodoItem) {
        todoDao.insertTask(task)
    }

    suspend fun updateTask(task: TodoItem) {
        todoDao.updateTask(task)
    }

    suspend fun deleteTask(task: TodoItem) {
        todoDao.deleteTask(task)
    }

    suspend fun deleteCompletedTasks() {
        todoDao.deleteCompletedTasks()
    }

    suspend fun getTaskById(id: Int): TodoItem? {
        return todoDao.getTaskById(id)
    }
}
