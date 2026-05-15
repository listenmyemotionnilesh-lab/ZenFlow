package com.zenflow.app.data.local

import androidx.room.*
import com.zenflow.app.data.model.FocusSession
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<FocusSession>>

    @Query("SELECT * FROM focus_sessions WHERE startTime >= :startOfDay ORDER BY startTime DESC")
    fun getTodaySessions(startOfDay: Long): Flow<List<FocusSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSession): Long

    @Update
    suspend fun updateSession(session: FocusSession)

    @Query("SELECT SUM(durationMinutes) FROM focus_sessions WHERE startTime >= :startOfDay AND isCompleted = 1")
    fun getTodayTotalMinutes(startOfDay: Long): Flow<Int?>
}