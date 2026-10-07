package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_checklist_items",
    indices = [Index(value = ["dateString", "routineBlockId"], unique = true)]
)
data class StudyChecklistItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val routineBlockId: String,
    val dateString: String,
    val title: String,
    val subtitle: String = "",
    val timeSlot: String = "",
    val emoji: String = "📝",
    val category: String = "General",
    val isCompleted: Boolean = false,
    val completedAtTimestamp: Long? = null,
    val notes: String = "",
    val orderIndex: Int = 0,
    val isCustom: Boolean = false
)
