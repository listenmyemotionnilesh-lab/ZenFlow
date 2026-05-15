package com.zenflow.app.data.repository

import com.zenflow.app.data.local.*
import com.zenflow.app.data.model.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ZenFlowRepository @Inject constructor(
    private val sectionDao: SectionDao,
    private val taskDao: TaskDao,
    private val dailyStatsDao: DailyStatsDao,
    private val goalDao: GoalDao,
    private val focusSessionDao: FocusSessionDao
) {
    // ========== SECTIONS ==========
    fun getAllSections() = sectionDao.getAllSections()
    suspend fun getSectionById(id: Long) = sectionDao.getSectionById(id)
    suspend fun addSection(section: Section) = sectionDao.insertSection(section)
    suspend fun updateSection(section: Section) = sectionDao.updateSection(section)
    suspend fun deleteSection(section: Section) = sectionDao.deleteSection(section)
    suspend fun reorderSections(sections: List<Section>) {
        sections.forEachIndexed { index, section ->
            sectionDao.updateOrderIndex(section.id, index)
        }
    }
    fun getPendingTaskCount(sectionId: Long) = sectionDao.getPendingTaskCount(sectionId)
    fun getCompletedTaskCount(sectionId: Long) = sectionDao.getCompletedTaskCount(sectionId)

    // ========== TASKS ==========
    fun getTasksBySection(sectionId: Long) = taskDao.getTasksBySection(sectionId)
    fun getAllPendingTasks() = taskDao.getAllPendingTasks()
    fun getAllCompletedTasks() = taskDao.getAllCompletedTasks()
    fun searchTasks(query: String) = taskDao.searchTasks(query)
    suspend fun getTaskById(id: Long) = taskDao.getTaskById(id)
    suspend fun addTask(task: Task): Long {
        val id = taskDao.insertTask(task)
        incrementTasksAdded()
        return id
    }
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    suspend fun completeTask(task: Task) {
        taskDao.updateCompletionStatus(task.id, true, System.currentTimeMillis())
        incrementTasksCompleted()
    }
    suspend fun uncompleteTask(task: Task) {
        taskDao.updateCompletionStatus(task.id, false, null)
    }
    fun getTotalPendingCount() = taskDao.getTotalPendingCount()
    fun getTodayCompletedCount() = taskDao.getTodayCompletedCount(getStartOfDay())

    // ========== STATS ==========
    fun getAllStats() = dailyStatsDao.getAllStats()
    fun getTodayStats() = dailyStatsDao.getStatsForDateFlow(getTodayDate())
    fun getStatsFromDate(startDate: String) = dailyStatsDao.getStatsFromDate(startDate)
    fun getMaxStreak() = dailyStatsDao.getMaxStreak()

    suspend fun ensureTodayStats() {
        val today = getTodayDate()
        val existing = dailyStatsDao.getStatsForDate(today)
        if (existing == null) {
            val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
            val yesterdayStats = dailyStatsDao.getStatsForDate(yesterday)
            val streak = if (yesterdayStats != null && yesterdayStats.tasksCompleted > 0) {
                yesterdayStats.streakDay + 1
            } else 0
            dailyStatsDao.insertOrUpdate(DailyStats(date = today, streakDay = streak))
        }
    }

    private suspend fun incrementTasksCompleted() {
        ensureTodayStats()
        dailyStatsDao.incrementTasksCompleted(getTodayDate())
    }

    private suspend fun incrementTasksAdded() {
        ensureTodayStats()
        dailyStatsDao.incrementTasksAdded(getTodayDate())
    }

    suspend fun addStudyMinutes(minutes: Int) {
        ensureTodayStats()
        dailyStatsDao.addStudyMinutes(getTodayDate(), minutes)
    }

    suspend fun addMeditationMinutes(minutes: Int) {
        ensureTodayStats()
        dailyStatsDao.addMeditationMinutes(getTodayDate(), minutes)
    }

    suspend fun updateProductivityScore(score: Int) {
        ensureTodayStats()
        dailyStatsDao.updateProductivityScore(getTodayDate(), score)
    }

    // ========== GOALS ==========
    fun getAllGoals() = goalDao.getAllGoals()
    fun getActiveGoals() = goalDao.getActiveGoals()
    suspend fun addGoal(goal: Goal) = goalDao.insertGoal(goal)
    suspend fun updateGoal(goal: Goal) = goalDao.updateGoal(goal)
    suspend fun deleteGoal(goal: Goal) = goalDao.deleteGoal(goal)
    suspend fun updateGoalProgress(goalId: Long, progress: Int) = goalDao.updateProgress(goalId, progress)

    // ========== FOCUS SESSIONS ==========
    fun getAllSessions() = focusSessionDao.getAllSessions()
    fun getTodaySessions() = focusSessionDao.getTodaySessions(getStartOfDay())
    suspend fun addSession(session: FocusSession) = focusSessionDao.insertSession(session)
    suspend fun updateSession(session: FocusSession) = focusSessionDao.updateSession(session)
    fun getTodayTotalFocusMinutes() = focusSessionDao.getTodayTotalMinutes(getStartOfDay())

    // ========== UTILS ==========
    private fun getTodayDate(): String {
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    private fun getStartOfDay(): Long {
        return LocalDate.now().atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC) * 1000
    }
}