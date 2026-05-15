package com.zenflow.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zenflow.app.data.model.DailyStats
import com.zenflow.app.data.model.FocusSession
import com.zenflow.app.data.model.Goal
import com.zenflow.app.data.model.Section
import com.zenflow.app.data.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Section::class, Task::class, DailyStats::class, Goal::class, FocusSession::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ZenFlowDatabase : RoomDatabase() {
    abstract fun sectionDao(): SectionDao
    abstract fun taskDao(): TaskDao
    abstract fun dailyStatsDao(): DailyStatsDao
    abstract fun goalDao(): GoalDao
    abstract fun focusSessionDao(): FocusSessionDao

    companion object {
        @Volatile
        private var INSTANCE: ZenFlowDatabase? = null

        fun getDatabase(context: Context): ZenFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZenFlowDatabase::class.java,
                    "zenflow_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Pre-populate default sections
            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.sectionDao()?.insertAll(Section.DEFAULT_SECTIONS)
            }
        }
    }
}