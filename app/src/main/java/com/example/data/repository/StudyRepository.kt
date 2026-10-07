package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.DailyBlockCompletion
import com.example.data.model.DefaultFlashcards
import com.example.data.model.DefaultSchedule
import com.example.data.model.FlashcardItem
import com.example.data.model.MistakeEntry
import com.example.data.model.OldIsGoldSession
import com.example.data.model.StudyChecklistItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class StudyRepository(private val database: AppDatabase) {
    private val mistakeDao = database.mistakeDao()
    private val oldIsGoldDao = database.oldIsGoldDao()
    private val dailyLogDao = database.dailyLogDao()
    private val flashcardDao = database.flashcardDao()
    private val checklistDao = database.checklistDao()

    // Mistakes
    val allMistakes: Flow<List<MistakeEntry>> = mistakeDao.getAllMistakes()
    val unmasteredMistakes: Flow<List<MistakeEntry>> = mistakeDao.getUnmasteredMistakes()
    fun getMistakesBySubject(subject: String): Flow<List<MistakeEntry>> =
        mistakeDao.getMistakesBySubject(subject)

    suspend fun addMistake(entry: MistakeEntry): Long = mistakeDao.insertMistake(entry)
    suspend fun updateMistake(entry: MistakeEntry) = mistakeDao.updateMistake(entry)
    suspend fun deleteMistake(id: Long) = mistakeDao.deleteById(id)

    // Old is Gold Sessions
    val allSessions: Flow<List<OldIsGoldSession>> = oldIsGoldDao.getAllSessions()
    val totalPracticeMinutes: Flow<Int?> = oldIsGoldDao.getTotalPracticeMinutes()
    val totalQuestionsSolved: Flow<Int?> = oldIsGoldDao.getTotalQuestionsSolved()
    fun getSessionsBySubject(subject: String): Flow<List<OldIsGoldSession>> =
        oldIsGoldDao.getSessionsBySubject(subject)

    suspend fun addSession(session: OldIsGoldSession): Long =
        oldIsGoldDao.insertSession(session)

    suspend fun deleteSession(session: OldIsGoldSession) =
        oldIsGoldDao.deleteSession(session)

    // Daily Completions
    fun getCompletionsForDate(dateString: String): Flow<List<DailyBlockCompletion>> =
        dailyLogDao.getCompletionsForDate(dateString)

    val totalCompletedBlocks: Flow<Int> = dailyLogDao.getTotalCompletedBlocks()
    val activeDays: Flow<List<String>> = dailyLogDao.getActiveDays()

    suspend fun toggleBlockCompletion(
        dateString: String,
        blockId: String,
        isCompleted: Boolean,
        minutesSpent: Int,
        note: String
    ) {
        dailyLogDao.insertOrUpdateCompletion(
            DailyBlockCompletion(
                dateString = dateString,
                blockId = blockId,
                isCompleted = isCompleted,
                minutesSpent = minutesSpent,
                sessionNote = note
            )
        )
    }

    // Daily Study Checklist (Room local persistence)
    fun getChecklistForDate(dateString: String): Flow<List<StudyChecklistItem>> =
        checklistDao.getChecklistForDate(dateString)

    fun getCompletedChecklistCount(dateString: String): Flow<Int> =
        checklistDao.getCompletedCount(dateString)

    fun getTotalChecklistCount(dateString: String): Flow<Int> =
        checklistDao.getTotalCount(dateString)

    suspend fun ensureDailyChecklistSeeded(dateString: String) {
        val existing = checklistDao.getChecklistForDate(dateString).firstOrNull()
        if (existing.isNullOrEmpty()) {
            val initialItems = DefaultSchedule.masterBlocks.mapIndexed { index, block ->
                StudyChecklistItem(
                    routineBlockId = block.id,
                    dateString = dateString,
                    title = block.title,
                    subtitle = block.subtitle,
                    timeSlot = "${block.startTime} – ${block.endTime}",
                    emoji = block.emoji,
                    category = block.category.displayName,
                    isCompleted = false,
                    notes = "",
                    orderIndex = index,
                    isCustom = false
                )
            }
            checklistDao.insertAll(initialItems)
        }
    }

    suspend fun toggleChecklistItem(item: StudyChecklistItem, isCompleted: Boolean) {
        val timestamp = if (isCompleted) System.currentTimeMillis() else null
        checklistDao.updateCompletion(item.id, isCompleted, timestamp)
        // Also keep DailyBlockCompletion synced for schedule timeline
        toggleBlockCompletion(
            dateString = item.dateString,
            blockId = item.routineBlockId,
            isCompleted = isCompleted,
            minutesSpent = if (isCompleted) 60 else 0,
            note = item.notes
        )
    }

    suspend fun updateChecklistItemNotes(id: Long, notes: String) {
        checklistDao.updateNotes(id, notes)
    }

    suspend fun addCustomChecklistItem(
        dateString: String,
        title: String,
        timeSlot: String,
        category: String
    ): Long {
        val customItem = StudyChecklistItem(
            routineBlockId = "custom_${System.currentTimeMillis()}",
            dateString = dateString,
            title = title,
            subtitle = "Custom Study Goal",
            timeSlot = timeSlot,
            emoji = "🎯",
            category = category,
            isCompleted = false,
            orderIndex = 100,
            isCustom = true
        )
        return checklistDao.insertOrUpdate(customItem)
    }

    suspend fun deleteChecklistItem(id: Long) {
        checklistDao.deleteItem(id)
    }

    suspend fun resetDayChecklist(dateString: String) {
        checklistDao.resetDay(dateString)
    }

    suspend fun markAllChecklistCompleted(dateString: String) {
        checklistDao.markAllCompleted(dateString, System.currentTimeMillis())
    }

    // Flashcards
    val allFlashcards: Flow<List<FlashcardItem>> = flashcardDao.getAllFlashcards()
    fun getFlashcardsBySubject(subject: String): Flow<List<FlashcardItem>> =
        flashcardDao.getFlashcardsBySubject(subject)

    suspend fun addFlashcard(item: FlashcardItem): Long =
        flashcardDao.insertFlashcard(item)

    suspend fun updateFlashcard(item: FlashcardItem) =
        flashcardDao.updateFlashcard(item)

    suspend fun ensureDefaultFlashcardsLoaded() {
        val count = flashcardDao.getCount()
        if (count == 0) {
            flashcardDao.insertAll(DefaultFlashcards.items)
        }
    }
}
