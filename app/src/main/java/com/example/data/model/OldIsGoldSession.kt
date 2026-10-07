package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "old_is_gold_sessions")
data class OldIsGoldSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String, // "Physics", "Chemistry", "Mathematics", "Computer Science", "Mixed Exam"
    val yearOrSet: String, // e.g. "2080 NEB Regular", "2079 Set A", "Derivation Numerical Drill"
    val durationMinutesPlanned: Int = 150,
    val actualMinutesElapsed: Int = 0,
    val questionsAttempted: Int = 0,
    val marksObtained: Float = 0f,
    val totalPossibleMarks: Float = 75f,
    val reflectionNotes: String = "",
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis()
)
