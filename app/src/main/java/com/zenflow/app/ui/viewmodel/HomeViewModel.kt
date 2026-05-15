package com.zenflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenflow.app.data.model.*
import com.zenflow.app.data.repository.ZenFlowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ZenFlowRepository
) : ViewModel() {

    val sections = repository.getAllSections().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val todayStats = repository.getTodayStats().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    val pendingTasks = repository.getAllPendingTasks().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val completedTasks = repository.getAllCompletedTasks().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val totalPending = repository.getTotalPendingCount().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val todayCompleted = repository.getTodayCompletedCount().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val maxStreak = repository.getMaxStreak().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val todayFocusMinutes = repository.getTodayTotalFocusMinutes().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val weeklyStats = repository.getStatsFromDate(
        LocalDate.now().minusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE)
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals = repository.getActiveGoals().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun addSection(name: String, icon: String, color: String, quote: String) {
        viewModelScope.launch {
            val maxOrder = repository.getAllSections().first().size
            repository.addSection(
                Section(
                    name = name,
                    icon = icon,
                    color = color,
                    orderIndex = maxOrder,
                    motivationalQuote = quote,
                    isCustom = true
                )
            )
        }
    }

    fun deleteSection(section: Section) {
        viewModelScope.launch {
            repository.deleteSection(section)
        }
    }

    fun reorderSections(sections: List<Section>) {
        viewModelScope.launch {
            repository.reorderSections(sections)
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            repository.completeTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun addCompletedTask(sectionId: Long, title: String, durationMinutes: Int) {
        viewModelScope.launch {
            repository.addTask(
                Task(
                    sectionId = sectionId,
                    title = title,
                    isCompleted = true,
                    isAddedAsCompleted = true,
                    completedAt = System.currentTimeMillis(),
                    durationMinutes = durationMinutes
                )
            )
        }
    }

    fun calculateProductivityScore(): Int {
        val stats = todayStats.value ?: return 0
        val completed = stats.tasksCompleted
        val study = stats.studyMinutes
        val meditation = stats.meditationMinutes
        return minOf(100, (completed * 10) + (study / 10) + (meditation / 5))
    }
}