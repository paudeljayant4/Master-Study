package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mistake_entries")
data class MistakeEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String, // "Physics", "Chemistry", "Mathematics", "Computer Science", "General"
    val topic: String,
    val questionOrProblem: String,
    val mistakeMade: String,
    val keyTakeawayOrFormula: String,
    val source: String, // "Old is Gold", "Block 1 Theory", "Block 2 Problem Solving", "Practice Paper"
    val masteryLevel: Int = 0, // 0 = Needs Review, 1 = Reviewing, 2 = Mastered
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis()
)
