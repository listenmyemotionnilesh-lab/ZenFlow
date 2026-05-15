package com.zenflow.app.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object DateUtils {
    fun formatDate(timestamp: Long?): String {
        if (timestamp == null) return ""
        val date = Date(timestamp)
        val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
        return date.toInstant().atZone(ZoneId.systemDefault()).format(formatter)
    }

    fun formatTime(timestamp: Long?): String {
        if (timestamp == null) return ""
        val date = Date(timestamp)
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        return date.toInstant().atZone(ZoneId.systemDefault()).format(formatter)
    }

    fun getStartOfDay(): Long {
        return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun getTodayDate(): String {
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    fun isToday(timestamp: Long): Boolean {
        val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
        return date == LocalDate.now()
    }
}