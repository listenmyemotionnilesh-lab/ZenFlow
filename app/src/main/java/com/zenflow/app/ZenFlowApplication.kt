package com.zenflow.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Configuration
import com.zenflow.app.data.local.ZenFlowDatabase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class ZenFlowApplication : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()

        // Initialize database with default data
        CoroutineScope(Dispatchers.IO).launch {
            ZenFlowDatabase.getDatabase(this@ZenFlowApplication)
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    "task_reminders",
                    "Task Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Reminders for your tasks and deadlines"
                },
                NotificationChannel(
                    "focus_timer",
                    "Focus Timer",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Focus session notifications"
                },
                NotificationChannel(
                    "daily_summary",
                    "Daily Summary",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Daily productivity summary"
                }
            )

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(channels)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}