package com.zenflow.app.data.local

import androidx.room.*
import com.zenflow.app.data.model.DailyStats
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatsDao {
    @Query("SELECT * FROM daily_stats ORDER BY date DESC")
    fun getAllStats(): Flow<List<DailyStats>>

    @Query("SELECT * FROM daily_stats WHERE date = :date")
    suspend fun getStatsForDate(date: String): DailyStats?

    @Query("SELECT * FROM daily_stats WHERE date = :date")
    fun getStatsForDateFlow(date: String): Flow<DailyStats?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: DailyStats)

    @Query("SELECT * FROM daily_stats WHERE date >= :startDate ORDER BY date ASC")
    fun getStatsFromDate(startDate: String): Flow<List<DailyStats>>

    @Query("SELECT MAX(streakDay) FROM daily_stats")
    fun getMaxStreak(): Flow<Int?>

    @Query("UPDATE daily_stats SET tasksCompleted = tasksCompleted + 1 WHERE date = :date")
    suspend fun incrementTasksCompleted(date: String)

    @Query("UPDATE daily_stats SET tasksAdded = tasksAdded + 1 WHERE date = :date")
    suspend fun incrementTasksAdded(date: String)

    @Query("UPDATE daily_stats SET studyMinutes = studyMinutes + :minutes WHERE date = :date")
    suspend fun addStudyMinutes(date: String, minutes: Int)

    @Query("UPDATE daily_stats SET meditationMinutes = meditationMinutes + :minutes WHERE date = :date")
    suspend fun addMeditationMinutes(date: String, minutes: Int)

    @Query("UPDATE daily_stats SET productivityScore = :score WHERE date = :date")
    suspend fun updateProductivityScore(date: String, score: Int)
}