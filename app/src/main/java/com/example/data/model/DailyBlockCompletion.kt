package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_block_completions",
    indices = [Index(value = ["dateString", "blockId"], unique = true)]
)
data class DailyBlockCompletion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateString: String,
    val blockId: String,
    val isCompleted: Boolean = false,
    val minutesSpent: Int = 0,
    val sessionNote: String = "",
    val completedAtTimestamp: Long = System.currentTimeMillis()
)
