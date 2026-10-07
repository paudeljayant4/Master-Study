package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MistakeEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MistakeDao {
    @Query("SELECT * FROM mistake_entries ORDER BY timestamp DESC")
    fun getAllMistakes(): Flow<List<MistakeEntry>>

    @Query("SELECT * FROM mistake_entries WHERE subject = :subject ORDER BY timestamp DESC")
    fun getMistakesBySubject(subject: String): Flow<List<MistakeEntry>>

    @Query("SELECT * FROM mistake_entries WHERE masteryLevel < 2 ORDER BY timestamp DESC")
    fun getUnmasteredMistakes(): Flow<List<MistakeEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistake(entry: MistakeEntry): Long

    @Update
    suspend fun updateMistake(entry: MistakeEntry)

    @Delete
    suspend fun deleteMistake(entry: MistakeEntry)

    @Query("DELETE FROM mistake_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}
