package com.zenflow.app.utils

import android.content.Context
import android.os.Environment
import com.zenflow.app.data.local.ZenFlowDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object BackupManager {

    suspend fun exportBackup(context: Context): Result<String> = withContext(Dispatchers.IO) {
        try {
            val db = ZenFlowDatabase.getDatabase(context)
            val json = JSONObject()

            // Export sections
            val sectionsArray = JSONArray()
            db.sectionDao().getAllSections().collect { sections ->
                sections.forEach { section ->
                    sectionsArray.put(JSONObject().apply {
                        put("id", section.id)
                        put("name", section.name)
                        put("icon", section.icon)
                        put("color", section.color)
                        put("orderIndex", section.orderIndex)
                        put("motivationalQuote", section.motivationalQuote)
                        put("isCustom", section.isCustom)
                    })
                }
            }
            json.put("sections", sectionsArray)

            // Export tasks
            val tasksArray = JSONArray()
            db.taskDao().getAllPendingTasks().collect { tasks ->
                tasks.forEach { task ->
                    tasksArray.put(JSONObject().apply {
                        put("id", task.id)
                        put("sectionId", task.sectionId)
                        put("title", task.title)
                        put("description", task.description)
                        put("notes", task.notes)
                        put("priority", task.priority.name)
                        put("isCompleted", task.isCompleted)
                        put("isAddedAsCompleted", task.isAddedAsCompleted)
                        put("dueDate", task.dueDate)
                        put("repeatType", task.repeatType.name)
                        put("durationMinutes", task.durationMinutes)
                    })
                }
            }
            json.put("tasks", tasksArray)

            // Save to Downloads
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val filename = "zenflow_backup_$timestamp.json"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, filename)
            file.writeText(json.toString(2))

            Result.success(file.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importBackup(context: Context, filePath: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            val json = JSONObject(file.readText())
            val db = ZenFlowDatabase.getDatabase(context)

            // Import sections
            val sectionsArray = json.getJSONArray("sections")
            for (i in 0 until sectionsArray.length()) {
                val obj = sectionsArray.getJSONObject(i)
                // Insert logic here
            }

            // Import tasks
            val tasksArray = json.getJSONArray("tasks")
            for (i in 0 until tasksArray.length()) {
                val obj = tasksArray.getJSONObject(i)
                // Insert logic here
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}