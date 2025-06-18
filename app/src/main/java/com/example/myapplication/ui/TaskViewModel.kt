package com.example.myapplication.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.TaskStorage
import com.example.myapplication.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

// State untuk UI
data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val sortMode: String = "deadline" // "deadline" atau "name"
)

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            val loadedTasks = TaskStorage.loadTasks(getApplication())
            _uiState.update { it.copy(tasks = loadedTasks) }
        }
    }

    private fun saveTasks() {
        viewModelScope.launch {
            TaskStorage.saveTasks(getApplication(), _uiState.value.tasks)
        }
    }

    fun addTask(title: String, deadline: LocalDateTime) {
        val newTask = Task(id = System.currentTimeMillis().toInt(), title = title, deadline = deadline)
        _uiState.update { currentState ->
            currentState.copy(tasks = currentState.tasks + newTask)
        }
        saveTasks()
    }

    fun deleteTask(task: Task) {
        _uiState.update { currentState ->
            currentState.copy(tasks = currentState.tasks.filterNot { it.id == task.id })
        }
        saveTasks()
    }

    fun toggleTaskDone(task: Task) {
        _uiState.update { currentState ->
            val updatedTasks = currentState.tasks.map {
                if (it.id == task.id) {
                    it.copy(isDone = !it.isDone)
                } else {
                    it
                }
            }
            currentState.copy(tasks = updatedTasks)
        }
        saveTasks()
    }

    fun changeSortMode(mode: String) {
        _uiState.update { it.copy(sortMode = mode) }
    }
}