package com.zenflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenflow.app.data.model.*
import com.zenflow.app.data.repository.ZenFlowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: ZenFlowRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val searchResults = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) flowOf(emptyList())
            else repository.searchTasks(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.addTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            repository.completeTask(task)
        }
    }

    fun uncompleteTask(task: Task) {
        viewModelScope.launch {
            repository.uncompleteTask(task)
        }
    }

    fun addCompletedTask(sectionId: Long, title: String, notes: String = "", durationMinutes: Int = 0) {
        viewModelScope.launch {
            repository.addTask(
                Task(
                    sectionId = sectionId,
                    title = title,
                    notes = notes,
                    isCompleted = true,
                    isAddedAsCompleted = true,
                    completedAt = System.currentTimeMillis(),
                    durationMinutes = durationMinutes
                )
            )
        }
    }
}