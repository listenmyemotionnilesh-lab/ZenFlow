package com.zenflow.app.data.local

import androidx.room.*
import com.zenflow.app.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE sectionId = :sectionId ORDER BY priority DESC, createdAt DESC")
    fun getTasksBySection(sectionId: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY priority DESC, dueDate ASC")
    fun getAllPendingTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getAllCompletedTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate = :date AND isCompleted = 0")
    fun getTasksForDate(date: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchTasks(query: String): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("UPDATE tasks SET isCompleted = :completed, completedAt = :completedAt WHERE id = :taskId")
    suspend fun updateCompletionStatus(taskId: Long, completed: Boolean, completedAt: Long?)

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun getTotalPendingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1 AND completedAt >= :startOfDay")
    fun getTodayCompletedCount(startOfDay: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE sectionId = :sectionId")
    fun getTaskCountForSection(sectionId: Long): Flow<Int>
}