package com.zenflow.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: SessionType = SessionType.POMODORO,
    val durationMinutes: Int = 25,
    val sectionId: Long? = null,
    val taskId: Long? = null,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val isCompleted: Boolean = false
)

enum class SessionType {
    POMODORO, MEDITATION, DEEP_WORK, CUSTOM
}