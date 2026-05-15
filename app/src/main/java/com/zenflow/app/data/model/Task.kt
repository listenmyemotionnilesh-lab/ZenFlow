package com.zenflow.app.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Section::class,
            parentColumns = ["id"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sectionId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sectionId: Long,
    val title: String,
    val description: String = "",
    val notes: String = "",
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val isAddedAsCompleted: Boolean = false,
    val dueDate: Long? = null,
    val dueTime: Long? = null,
    val reminderTime: Long? = null,
    val repeatType: RepeatType = RepeatType.NONE,
    val durationMinutes: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) : Parcelable

enum class Priority(val label: String, val color: String) {
    LOW("Low", "#10B981"),
    MEDIUM("Medium", "#F59E0B"),
    HIGH("High", "#EF4444"),
    URGENT("Urgent", "#DC2626")
}

enum class RepeatType(val label: String) {
    NONE("None"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly")
}