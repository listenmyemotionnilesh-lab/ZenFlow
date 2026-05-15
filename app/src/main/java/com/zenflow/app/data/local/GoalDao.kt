package com.zenflow.app.data.local

import androidx.room.*
import com.zenflow.app.data.model.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getActiveGoals(): Flow<List<Goal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal): Long

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Query("UPDATE goals SET progress = :progress WHERE id = :goalId")
    suspend fun updateProgress(goalId: Long, progress: Int)

    @Query("UPDATE goals SET isCompleted = 1 WHERE id = :goalId")
    suspend fun markCompleted(goalId: Long)
}