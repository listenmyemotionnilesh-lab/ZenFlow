package com.zenflow.app.data.local

import androidx.room.*
import com.zenflow.app.data.model.Section
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDao {
    @Query("SELECT * FROM sections ORDER BY orderIndex ASC")
    fun getAllSections(): Flow<List<Section>>

    @Query("SELECT * FROM sections WHERE id = :id")
    suspend fun getSectionById(id: Long): Section?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(section: Section): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sections: List<Section>)

    @Update
    suspend fun updateSection(section: Section)

    @Delete
    suspend fun deleteSection(section: Section)

    @Query("SELECT MAX(orderIndex) FROM sections")
    suspend fun getMaxOrderIndex(): Int?

    @Query("UPDATE sections SET orderIndex = :newIndex WHERE id = :sectionId")
    suspend fun updateOrderIndex(sectionId: Long, newIndex: Int)

    @Query("SELECT COUNT(*) FROM tasks WHERE sectionId = :sectionId AND isCompleted = 0")
    fun getPendingTaskCount(sectionId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE sectionId = :sectionId AND isCompleted = 1")
    fun getCompletedTaskCount(sectionId: Long): Flow<Int>
}