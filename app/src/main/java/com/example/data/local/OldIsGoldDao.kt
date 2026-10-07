package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.OldIsGoldSession
import kotlinx.coroutines.flow.Flow

@Dao
interface OldIsGoldDao {
    @Query("SELECT * FROM old_is_gold_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<OldIsGoldSession>>

    @Query("SELECT * FROM old_is_gold_sessions WHERE subject = :subject ORDER BY timestamp DESC")
    fun getSessionsBySubject(subject: String): Flow<List<OldIsGoldSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: OldIsGoldSession): Long

    @Delete
    suspend fun deleteSession(session: OldIsGoldSession)

    @Query("SELECT SUM(actualMinutesElapsed) FROM old_is_gold_sessions")
    fun getTotalPracticeMinutes(): Flow<Int?>

    @Query("SELECT SUM(questionsAttempted) FROM old_is_gold_sessions")
    fun getTotalQuestionsSolved(): Flow<Int?>
}
