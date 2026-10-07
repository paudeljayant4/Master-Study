package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.BlockCategory
import com.example.data.model.DailyBlockCompletion
import com.example.data.model.DefaultSchedule
import com.example.data.model.FlashcardItem
import com.example.data.model.MistakeEntry
import com.example.data.model.OldIsGoldSession
import com.example.data.model.StudyBlock
import com.example.data.model.StudyChecklistItem
import com.example.data.remote.FirebaseAuthManager
import com.example.data.remote.FirestoreSyncManager
import com.example.data.repository.StudyRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class TimerState(
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val targetBlock: StudyBlock? = null,
    val initialSeconds: Int = 25 * 60,
    val remainingSeconds: Int = 25 * 60,
    val questionsCount: Int = 0,
    val isCompleted: Boolean = false
)

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StudyRepository =
        StudyRepository(AppDatabase.getInstance(application))

    val authManager = FirebaseAuthManager(application)
    val syncManager = FirestoreSyncManager(application)

    val currentUser: StateFlow<FirebaseUser?> = authManager.currentUser

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault())

    private val _selectedDate = MutableStateFlow(dateFormat.format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _currentMinuteOfDay = MutableStateFlow(getCurrentMinute())
    val currentMinuteOfDay: StateFlow<Int> = _currentMinuteOfDay.asStateFlow()

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private var timerJob: Job? = null
    private var clockJob: Job? = null

    // Room Flows
    val allMistakes: StateFlow<List<MistakeEntry>> = repository.allMistakes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSessions: StateFlow<List<OldIsGoldSession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPracticeMinutes: StateFlow<Int?> = repository.totalPracticeMinutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalQuestionsSolved: StateFlow<Int?> = repository.totalQuestionsSolved
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allFlashcards: StateFlow<List<FlashcardItem>> = repository.allFlashcards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _completions = MutableStateFlow<Map<String, DailyBlockCompletion>>(emptyMap())
    val completions: StateFlow<Map<String, DailyBlockCompletion>> = _completions.asStateFlow()

    // Daily Checklist Items (Room local persistence)
    private val _checklistItems = MutableStateFlow<List<StudyChecklistItem>>(emptyList())
    val checklistItems: StateFlow<List<StudyChecklistItem>> = _checklistItems.asStateFlow()

    private val _soundVibrateEnabled = MutableStateFlow(true)
    val soundVibrateEnabled: StateFlow<Boolean> = _soundVibrateEnabled.asStateFlow()

    val masterBlocks: List<StudyBlock> = DefaultSchedule.masterBlocks

    init {
        startLiveClock()
        loadCompletionsForDate(_selectedDate.value)
        loadChecklistForDate(_selectedDate.value)
        authManager.attemptAutoSignIn(viewModelScope)
        viewModelScope.launch {
            repository.ensureDefaultFlashcardsLoaded()
        }
    }

    private fun getCurrentMinute(): Int {
        val cal = Calendar.getInstance()
        return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    }

    private fun startLiveClock() {
        clockJob?.cancel()
        clockJob = viewModelScope.launch {
            while (true) {
                _currentMinuteOfDay.value = getCurrentMinute()
                delay(10000)
            }
        }
    }

    fun setDate(dateString: String) {
        _selectedDate.value = dateString
        loadCompletionsForDate(dateString)
        loadChecklistForDate(dateString)
    }

    fun formatSelectedDateForDisplay(): String {
        return try {
            val parsed = dateFormat.parse(_selectedDate.value) ?: Date()
            displayDateFormat.format(parsed)
        } catch (e: Exception) {
            _selectedDate.value
        }
    }

    private fun loadCompletionsForDate(dateString: String) {
        viewModelScope.launch {
            repository.getCompletionsForDate(dateString).collect { list ->
                _completions.value = list.associateBy { it.blockId }
            }
        }
    }

    private fun loadChecklistForDate(dateString: String) {
        viewModelScope.launch {
            repository.ensureDailyChecklistSeeded(dateString)
            repository.getChecklistForDate(dateString).collect { list ->
                _checklistItems.value = list
                // Sync to Firestore if authenticated
                syncManager.syncChecklistSummary(dateString, list)
            }
        }
    }

    // Daily Checklist Operations (Room DB)
    fun toggleChecklistItem(item: StudyChecklistItem, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleChecklistItem(item, isCompleted)
            // Trigger Firestore sync
            val updated = _checklistItems.value.map {
                if (it.id == item.id) it.copy(isCompleted = isCompleted) else it
            }
            syncManager.syncChecklistSummary(_selectedDate.value, updated)
        }
    }

    fun updateChecklistNotes(id: Long, notes: String) {
        viewModelScope.launch {
            repository.updateChecklistItemNotes(id, notes)
        }
    }

    fun addCustomChecklistItem(title: String, timeSlot: String, category: String) {
        viewModelScope.launch {
            repository.addCustomChecklistItem(_selectedDate.value, title, timeSlot, category)
        }
    }

    fun deleteChecklistItem(id: Long) {
        viewModelScope.launch {
            repository.deleteChecklistItem(id)
        }
    }

    fun resetDayChecklist() {
        viewModelScope.launch {
            repository.resetDayChecklist(_selectedDate.value)
        }
    }

    fun markAllChecklistCompleted() {
        viewModelScope.launch {
            repository.markAllChecklistCompleted(_selectedDate.value)
        }
    }

    // Authentication & Cloud Sync
    fun signInWithGoogle(
        activity: Activity,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit,
        onCancelled: () -> Unit = {}
    ) {
        authManager.signInWithGoogle(
            activity = activity,
            scope = viewModelScope,
            onSuccess = { user ->
                viewModelScope.launch {
                    syncManager.syncUserProfile()
                    syncManager.syncChecklistSummary(_selectedDate.value, _checklistItems.value)
                }
                onSuccess(user)
            },
            onError = onError,
            onCancelled = onCancelled
        )
    }

    fun signOut(onComplete: () -> Unit = {}) {
        authManager.signOut(viewModelScope, onComplete)
    }

    fun toggleBlockDone(block: StudyBlock, isDone: Boolean, note: String = "") {
        viewModelScope.launch {
            val minutes = if (isDone) block.durationMinutes else 0
            repository.toggleBlockCompletion(
                dateString = _selectedDate.value,
                blockId = block.id,
                isCompleted = isDone,
                minutesSpent = minutes,
                note = note
            )
            // Also update matching checklist item if present
            _checklistItems.value.find { it.routineBlockId == block.id }?.let { checkItem ->
                repository.toggleChecklistItem(checkItem, isDone)
            }
        }
    }

    fun startTimerForBlock(block: StudyBlock, customMinutes: Int? = null) {
        val minutes = customMinutes ?: when (block.category) {
            BlockCategory.OLD_IS_GOLD_ENGINE -> 150
            BlockCategory.THEORY_DERIVATION -> 150
            BlockCategory.MATH_PROBLEM_SOLVING -> 165
            BlockCategory.CS_LANGUAGE -> 60
            BlockCategory.ACTIVE_RECALL_MISTAKE -> 60
            BlockCategory.WORKOUT -> 60
            BlockCategory.LEISURE_GAMING -> 60
            else -> 20
        }
        val totalSecs = minutes * 60

        timerJob?.cancel()
        _timerState.value = TimerState(
            isRunning = true,
            isPaused = false,
            targetBlock = block,
            initialSeconds = totalSecs,
            remainingSeconds = totalSecs,
            questionsCount = 0,
            isCompleted = false
        )

        timerJob = viewModelScope.launch {
            while (_timerState.value.remainingSeconds > 0 && _timerState.value.isRunning && !_timerState.value.isPaused) {
                delay(1000)
                val newRemaining = _timerState.value.remainingSeconds - 1
                if (newRemaining <= 0) {
                    _timerState.value = _timerState.value.copy(
                        remainingSeconds = 0,
                        isRunning = false,
                        isCompleted = true
                    )
                    triggerVibration()
                    toggleBlockDone(block, true, "Completed via Master Focus Timer (${minutes}m)")
                    break
                } else {
                    _timerState.value = _timerState.value.copy(remainingSeconds = newRemaining)
                }
            }
        }
    }

    fun pauseTimer() {
        _timerState.value = _timerState.value.copy(isPaused = true)
        timerJob?.cancel()
    }

    fun resumeTimer() {
        val current = _timerState.value
        if (current.remainingSeconds <= 0) return
        _timerState.value = current.copy(isRunning = true, isPaused = false)

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerState.value.remainingSeconds > 0 && _timerState.value.isRunning && !_timerState.value.isPaused) {
                delay(1000)
                val newRemaining = _timerState.value.remainingSeconds - 1
                if (newRemaining <= 0) {
                    _timerState.value = _timerState.value.copy(
                        remainingSeconds = 0,
                        isRunning = false,
                        isCompleted = true
                    )
                    triggerVibration()
                    current.targetBlock?.let {
                        toggleBlockDone(it, true, "Completed via Master Focus Timer")
                    }
                    break
                } else {
                    _timerState.value = _timerState.value.copy(remainingSeconds = newRemaining)
                }
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState()
    }

    fun incrementTimerQuestion() {
        _timerState.value = _timerState.value.copy(
            questionsCount = _timerState.value.questionsCount + 1
        )
    }

    fun toggleVibration() {
        _soundVibrateEnabled.value = !_soundVibrateEnabled.value
    }

    private fun triggerVibration() {
        if (!_soundVibrateEnabled.value) return
        try {
            val context = getApplication<Application>()
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 200, 300), -1))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(longArrayOf(0, 300, 200, 300), -1)
                }
            }
        } catch (_: Exception) {}
    }

    // Mistake operations
    fun addMistake(
        subject: String,
        topic: String,
        question: String,
        mistake: String,
        takeaway: String,
        source: String
    ) {
        viewModelScope.launch {
            repository.addMistake(
                MistakeEntry(
                    subject = subject,
                    topic = topic,
                    questionOrProblem = question,
                    mistakeMade = mistake,
                    keyTakeawayOrFormula = takeaway,
                    source = source,
                    masteryLevel = 0,
                    dateString = _selectedDate.value
                )
            )
        }
    }

    fun updateMistakeMastery(entry: MistakeEntry, newLevel: Int) {
        viewModelScope.launch {
            repository.updateMistake(entry.copy(masteryLevel = newLevel))
        }
    }

    fun deleteMistake(id: Long) {
        viewModelScope.launch {
            repository.deleteMistake(id)
        }
    }

    // Old is Gold Sessions
    fun saveOldIsGoldSession(
        subject: String,
        yearOrSet: String,
        durationMinutesPlanned: Int,
        actualMinutes: Int,
        questionsAttempted: Int,
        marksObtained: Float,
        totalPossibleMarks: Float,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addSession(
                OldIsGoldSession(
                    subject = subject,
                    yearOrSet = yearOrSet,
                    durationMinutesPlanned = durationMinutesPlanned,
                    actualMinutesElapsed = actualMinutes,
                    questionsAttempted = questionsAttempted,
                    marksObtained = marksObtained,
                    totalPossibleMarks = totalPossibleMarks,
                    reflectionNotes = notes,
                    dateString = _selectedDate.value
                )
            )
        }
    }

    fun deleteSession(session: OldIsGoldSession) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }

    // Flashcard operations
    fun toggleFlashcardMastered(card: FlashcardItem) {
        viewModelScope.launch {
            repository.updateFlashcard(
                card.copy(
                    isMastered = !card.isMastered,
                    timesReviewed = card.timesReviewed + 1
                )
            )
        }
    }

    fun addCustomFlashcard(
        subject: String,
        title: String,
        prompt: String,
        answer: String,
        formula: String
    ) {
        viewModelScope.launch {
            repository.addFlashcard(
                FlashcardItem(
                    subject = subject,
                    frontTitle = title,
                    frontPrompt = prompt,
                    backAnswer = answer,
                    formulaOrKeySteps = formula,
                    isCustom = true
                )
            )
        }
    }
}
