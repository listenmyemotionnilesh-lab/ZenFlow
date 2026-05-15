package com.zenflow.app.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "sections")
data class Section(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String = "book",
    val color: String = "#6366F1",
    val orderIndex: Int = 0,
    val motivationalQuote: String = "",
    val isCustom: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable {
    companion object {
        val DEFAULT_SECTIONS = listOf(
            Section(1, "Study", "school", "#6366F1", 0, "Every expert was once a beginner. Keep going!", false),
            Section(2, "Work", "work", "#10B981", 1, "Small steps every day lead to big results.", false),
            Section(3, "Meditation", "self_improvement", "#8B5CF6", 2, "Peace begins with a single breath.", false),
            Section(4, "Health", "fitness_center", "#F59E0B", 3, "A healthy mind lives in a healthy body.", false),
            Section(5, "Personal", "person", "#EC4899", 4, "Invest in yourself. It pays the best interest.", false)
        )
    }
}