package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.StudyBlock
import com.example.ui.components.MistakeDialog
import com.example.ui.components.StudyTimerSheet
import com.example.ui.theme.AmberFocus
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Indigo800
import com.example.ui.theme.Indigo900
import com.example.ui.viewmodel.StudyViewModel

sealed class StudyNavTab(val index: Int, val title: String) {
    object Timeline : StudyNavTab(0, "Schedule")
    object OldIsGold : StudyNavTab(1, "Old is Gold")
    object ActiveRecall : StudyNavTab(2, "Active Recall")
    object Strategy : StudyNavTab(3, "Strategy")
}

@Composable
fun MainStudyScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showTimerSheet by remember { mutableStateOf(false) }
    var showMistakeDialog by remember { mutableStateOf(false) }
    var initialMistakeSubject by remember { mutableStateOf("Physics") }
    var initialMistakeSource by remember { mutableStateOf("Asmita's Old is Gold") }

    val timerState by viewModel.timerState.collectAsState()
    val soundVibrate by viewModel.soundVibrateEnabled.collectAsState()

    // Handle back button: if on sub-tab, return to timeline (tab 0)
    BackHandler(enabled = selectedTab != 0) {
        selectedTab = 0
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                tonalElevation = 8.dp
            ) {
                // Tab 0: Timeline
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Filled.Schedule else Icons.Outlined.Schedule,
                            contentDescription = "Schedule"
                        )
                    },
                    label = { Text("Schedule", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Indigo900,
                        selectedTextColor = AmberFocus,
                        indicatorColor = AmberFocus
                    ),
                    modifier = Modifier.testTag("nav_tab_schedule")
                )

                // Tab 1: Old is Gold Engine
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Filled.Bolt else Icons.Outlined.Bolt,
                            contentDescription = "Old is Gold"
                        )
                    },
                    label = { Text("Old is Gold", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Indigo900,
                        selectedTextColor = CyanAccent,
                        indicatorColor = CyanAccent
                    ),
                    modifier = Modifier.testTag("nav_tab_old_is_gold")
                )

                // Tab 2: Active Recall & Mistake Log
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 2) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                            contentDescription = "Recall & Mistakes"
                        )
                    },
                    label = { Text("Recall", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Indigo900,
                        selectedTextColor = Indigo400,
                        indicatorColor = Indigo400
                    ),
                    modifier = Modifier.testTag("nav_tab_active_recall")
                )

                // Tab 3: Strategy & Stats
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 3) Icons.Filled.Equalizer else Icons.Outlined.Equalizer,
                            contentDescription = "Strategy"
                        )
                    },
                    label = { Text("Strategy", fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Indigo900,
                        selectedTextColor = AmberFocus,
                        indicatorColor = AmberFocus
                    ),
                    modifier = Modifier.testTag("nav_tab_strategy")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> ScheduleTimelineScreen(
                    viewModel = viewModel,
                    onStartTimerForBlock = { block ->
                        viewModel.startTimerForBlock(block)
                        showTimerSheet = true
                    },
                    onLogMistake = { subj, src ->
                        initialMistakeSubject = subj
                        initialMistakeSource = src
                        showMistakeDialog = true
                    }
                )
                1 -> OldIsGoldEngineScreen(
                    viewModel = viewModel,
                    onStartExamTimer = { block, mins ->
                        viewModel.startTimerForBlock(block, mins)
                        showTimerSheet = true
                    },
                    onLogMistake = { subj, src ->
                        initialMistakeSubject = subj
                        initialMistakeSource = src
                        showMistakeDialog = true
                    }
                )
                2 -> ActiveRecallScreen(
                    viewModel = viewModel,
                    onOpenMistakeDialog = {
                        initialMistakeSubject = "Physics"
                        initialMistakeSource = "Active Recall Review"
                        showMistakeDialog = true
                    }
                )
                3 -> DailyStatsScreen(
                    viewModel = viewModel
                )
            }
        }
    }

    // Active Timer Modal Sheet
    if (showTimerSheet && (timerState.isRunning || timerState.isPaused || timerState.isCompleted)) {
        StudyTimerSheet(
            timerState = timerState,
            soundVibrateEnabled = soundVibrate,
            onPause = { viewModel.pauseTimer() },
            onResume = { viewModel.resumeTimer() },
            onStop = {
                viewModel.stopTimer()
                showTimerSheet = false
            },
            onIncrementQuestion = { viewModel.incrementTimerQuestion() },
            onToggleVibrate = { viewModel.toggleVibration() },
            onOpenMistakeLogger = {
                val blockTitle = timerState.targetBlock?.title ?: "Focus Session"
                val subj = when {
                    blockTitle.contains("Physics", true) -> "Physics"
                    blockTitle.contains("Chemistry", true) -> "Chemistry"
                    blockTitle.contains("Math", true) -> "Mathematics"
                    blockTitle.contains("Computer", true) -> "Computer Science"
                    else -> "Physics"
                }
                initialMistakeSubject = subj
                initialMistakeSource = blockTitle
                showMistakeDialog = true
            },
            onDismiss = { showTimerSheet = false }
        )
    }

    // Mistake Dialog Modal
    if (showMistakeDialog) {
        MistakeDialog(
            initialSubject = initialMistakeSubject,
            initialSource = initialMistakeSource,
            onSave = { subj, topic, q, mist, take, src ->
                viewModel.addMistake(subj, topic, q, mist, take, src)
                showMistakeDialog = false
            },
            onDismiss = { showMistakeDialog = false }
        )
    }
}
