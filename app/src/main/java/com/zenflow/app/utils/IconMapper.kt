package com.zenflow.app.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

object IconMapper {
    fun getIcon(name: String): ImageVector {
        return when (name.lowercase()) {
            "school", "book", "study" -> Icons.Outlined.School
            "work" -> Icons.Outlined.Work
            "self_improvement", "meditation" -> Icons.Outlined.SelfImprovement
            "fitness_center", "health" -> Icons.Outlined.FitnessCenter
            "person", "personal" -> Icons.Outlined.Person
            "code" -> Icons.Outlined.Code
            "palette" -> Icons.Outlined.Palette
            "music_note" -> Icons.Outlined.MusicNote
            "language" -> Icons.Outlined.Language
            "science" -> Icons.Outlined.Science
            "home" -> Icons.Outlined.Home
            "favorite" -> Icons.Outlined.Favorite
            "star" -> Icons.Outlined.Star
            "flag" -> Icons.Outlined.Flag
            "timer" -> Icons.Outlined.Timer
            "check_circle" -> Icons.Outlined.CheckCircle
            else -> Icons.Outlined.Bookmark
        }
    }
}