package com.zenflow.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_stats")
data class DailyStats(
    @PrimaryKey
    val date: String, // yyyy-MM-dd format
    val tasksCompleted: Int = 0,
    val tasksAdded: Int = 0,
    val studyMinutes: Int = 0,
    val meditationMinutes: Int = 0,
    val workMinutes: Int = 0,
    val healthMinutes: Int = 0,
    val productivityScore: Int = 0,
    val streakDay: Int = 0,
    val lastActiveDate: Long = System.currentTimeMillis()
)