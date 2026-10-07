package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FlashcardItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcard_items ORDER BY id ASC")
    fun getAllFlashcards(): Flow<List<FlashcardItem>>

    @Query("SELECT * FROM flashcard_items WHERE subject = :subject ORDER BY id ASC")
    fun getFlashcardsBySubject(subject: String): Flow<List<FlashcardItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(item: FlashcardItem): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<FlashcardItem>)

    @Update
    suspend fun updateFlashcard(item: FlashcardItem)

    @Query("SELECT COUNT(*) FROM flashcard_items")
    suspend fun getCount(): Int
}
