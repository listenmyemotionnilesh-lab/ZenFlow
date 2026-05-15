package com.zenflow.app.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val targetDate: Long? = null,
    val isCompleted: Boolean = false,
    val progress: Int = 0, // 0-100
    val color: String = "#6366F1",
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable