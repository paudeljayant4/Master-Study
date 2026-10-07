package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.StudyChecklistItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM study_checklist_items WHERE dateString = :dateString ORDER BY orderIndex ASC")
    fun getChecklistForDate(dateString: String): Flow<List<StudyChecklistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: StudyChecklistItem): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<StudyChecklistItem>)

    @Query("UPDATE study_checklist_items SET isCompleted = :isCompleted, completedAtTimestamp = :timestamp WHERE id = :id")
    suspend fun updateCompletion(id: Long, isCompleted: Boolean, timestamp: Long?)

    @Query("UPDATE study_checklist_items SET notes = :notes WHERE id = :id")
    suspend fun updateNotes(id: Long, notes: String)

    @Query("DELETE FROM study_checklist_items WHERE id = :id")
    suspend fun deleteItem(id: Long)

    @Query("UPDATE study_checklist_items SET isCompleted = 0, completedAtTimestamp = NULL WHERE dateString = :dateString")
    suspend fun resetDay(dateString: String)

    @Query("UPDATE study_checklist_items SET isCompleted = 1, completedAtTimestamp = :timestamp WHERE dateString = :dateString")
    suspend fun markAllCompleted(dateString: String, timestamp: Long)

    @Query("SELECT COUNT(*) FROM study_checklist_items WHERE dateString = :dateString AND isCompleted = 1")
    fun getCompletedCount(dateString: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM study_checklist_items WHERE dateString = :dateString")
    fun getTotalCount(dateString: String): Flow<Int>
}
