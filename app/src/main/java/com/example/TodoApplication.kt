package com.example

import android.app.Application
import com.example.data.TodoDatabase
import com.example.data.TodoRepository

class TodoApplication : Application() {
    val database by lazy { TodoDatabase.getDatabase(this) }
    val repository by lazy { TodoRepository(database.todoDao) }
}
