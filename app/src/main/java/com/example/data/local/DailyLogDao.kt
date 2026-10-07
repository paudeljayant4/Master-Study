package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DailyBlockCompletion
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_block_completions WHERE dateString = :dateString")
    fun getCompletionsForDate(dateString: String): Flow<List<DailyBlockCompletion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCompletion(completion: DailyBlockCompletion): Long

    @Query("SELECT COUNT(*) FROM daily_block_completions WHERE isCompleted = 1")
    fun getTotalCompletedBlocks(): Flow<Int>

    @Query("SELECT DISTINCT dateString FROM daily_block_completions WHERE isCompleted = 1 ORDER BY dateString DESC")
    fun getActiveDays(): Flow<List<String>>

    @Query("SELECT SUM(minutesSpent) FROM daily_block_completions WHERE dateString = :dateString")
    fun getTotalMinutesForDate(dateString: String): Flow<Int?>
}
