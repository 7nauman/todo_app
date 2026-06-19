package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.TodoItem
import com.example.data.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter {
    ALL, ACTIVE, COMPLETED
}

enum class SortOption {
    CREATION_DATE, PRIORITY, DUE_DATE
}

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow(TaskFilter.ALL)
    val selectedCategory = MutableStateFlow<String?>(null)
    val selectedSort = MutableStateFlow(SortOption.PRIORITY)

    // Reactive task stream from Repository combined with sorting, filtering, searching in the ViewModel
    val uiState: StateFlow<TodoUiState> = combine(
        repository.allTasks,
        searchQuery,
        selectedFilter,
        selectedCategory,
        selectedSort
    ) { tasks, query, filter, category, sort ->
        var filteredList = tasks.filter { task ->
            // Search filter
            val matchesQuery = task.title.contains(query, ignoreCase = true) || 
                               task.description.contains(query, ignoreCase = true)
            
            // Completeness filter
            val matchesFilter = when (filter) {
                TaskFilter.ALL -> true
                TaskFilter.ACTIVE -> !task.isCompleted
                TaskFilter.COMPLETED -> task.isCompleted
            }

            // Category filter
            val matchesCategory = if (category == null) true else task.category == category

            matchesQuery && matchesFilter && matchesCategory
        }

        // Custom in-memory sort on top of the default DAO ordering (for flexibility)
        filteredList = when (sort) {
            SortOption.CREATION_DATE -> {
                filteredList.sortedWith(
                    compareBy<TodoItem> { it.isCompleted } // Incomplete first
                        .thenByDescending { it.createdAt }
                )
            }
            SortOption.PRIORITY -> {
                filteredList.sortedWith(
                    compareBy<TodoItem> { it.isCompleted } // Incomplete first
                        .thenBy { 
                            when (it.priority) {
                                "High" -> 1
                                "Medium" -> 2
                                "Low" -> 3
                                else -> 4
                            }
                        }
                        .thenByDescending { it.createdAt }
                )
            }
            SortOption.DUE_DATE -> {
                filteredList.sortedWith(
                    compareBy<TodoItem> { it.isCompleted } // Incomplete first
                        .thenBy { it.dueDate ?: Long.MAX_VALUE } // No due date at bottom
                        .thenByDescending { it.createdAt }
                )
            }
        }

        TodoUiState.Success(
            tasks = filteredList,
            categories = tasks.map { it.category }.distinct().sorted(),
            totalCount = tasks.size,
            completedCount = tasks.count { it.isCompleted },
            activeCount = tasks.count { !it.isCompleted }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodoUiState.Loading
    )

    fun addTask(
        title: String,
        description: String,
        priority: String,
        category: String,
        dueDate: Long?
    ) {
        viewModelScope.launch {
            if (title.isNotBlank()) {
                val task = TodoItem(
                    title = title.trim(),
                    description = description.trim(),
                    priority = priority,
                    category = category.trim(),
                    dueDate = dueDate
                )
                repository.insertTask(task)
            }
        }
    }

    fun toggleTaskCompleteness(task: TodoItem) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun updateTask(task: TodoItem) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: TodoItem) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            repository.deleteCompletedTasks()
        }
    }
}

sealed interface TodoUiState {
    object Loading : TodoUiState
    data class Success(
        val tasks: List<TodoItem>,
        val categories: List<String>,
        val totalCount: Int,
        val completedCount: Int,
        val activeCount: Int
    ) : TodoUiState
}

class TodoViewModelFactory(private val repository: TodoRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            return TodoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
