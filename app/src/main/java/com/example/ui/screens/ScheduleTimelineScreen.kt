package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyBlock
import com.example.ui.components.BlockTimelineItem
import com.example.ui.components.CurrentBlockHeroCard
import com.example.ui.components.DailyChecklistComponent
import com.example.ui.theme.AmberFocus
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Indigo900
import com.example.ui.theme.JetBrainsMono
import com.example.ui.viewmodel.StudyViewModel

@Composable
fun ScheduleTimelineScreen(
    viewModel: StudyViewModel,
    onStartTimerForBlock: (StudyBlock) -> Unit,
    onLogMistake: (subject: String, source: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentMinuteOfDay by viewModel.currentMinuteOfDay.collectAsState()
    val completions by viewModel.completions.collectAsState()
    val checklistItems by viewModel.checklistItems.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val soundVibrate by viewModel.soundVibrateEnabled.collectAsState()
    val formattedDate = viewModel.formatSelectedDateForDisplay()

    // Determine current block
    val currentBlock = viewModel.masterBlocks.firstOrNull { it.isCurrent(currentMinuteOfDay) }

    val completedCount = checklistItems.count { it.isCompleted }
    val totalBlocks = checklistItems.size
    val completionPercent = if (totalBlocks > 0) (completedCount * 100) / totalBlocks else 0

    var showStrategyCard by remember { mutableStateOf(false) }
    var authErrorMsg by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("schedule_timeline_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // App Header Bar with Cloud / Google Sign-In Status
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Master Self-Study",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Google Auth / Cloud Sync Status Chip
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                if (currentUser == null && context is Activity) {
                                    viewModel.signInWithGoogle(
                                        activity = context,
                                        onSuccess = { authErrorMsg = null },
                                        onError = { authErrorMsg = it }
                                    )
                                } else {
                                    viewModel.signOut()
                                }
                            }
                            .testTag("google_signin_chip"),
                        color = if (currentUser != null) EmeraldSuccess.copy(alpha = 0.2f) else CyanAccent.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (currentUser != null) EmeraldSuccess else CyanAccent
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (currentUser != null) Icons.Default.CloudDone else Icons.Default.AccountCircle,
                                contentDescription = "Auth Status",
                                tint = if (currentUser != null) EmeraldSuccess else CyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (currentUser != null) "Cloud Synced ✓" else "Sign in (Google)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (currentUser != null) EmeraldSuccess else CyanAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { viewModel.toggleVibration() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (soundVibrate) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = "Haptics toggle",
                            tint = if (soundVibrate) AmberFocus else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (authErrorMsg != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    Text(
                        text = authErrorMsg ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // Live Hero Card
        item {
            val isCurrentCompleted = currentBlock?.let { completions[it.id]?.isCompleted == true } ?: false
            CurrentBlockHeroCard(
                currentBlock = currentBlock,
                currentMinuteOfDay = currentMinuteOfDay,
                isCompleted = isCurrentCompleted,
                onStartTimer = onStartTimerForBlock,
                onToggleDone = { block, done -> viewModel.toggleBlockDone(block, done) }
            )
        }

        // FEATURED: Daily Study Checklist (Room Local Persistence + Firestore Sync)
        item {
            DailyChecklistComponent(
                checklistItems = checklistItems,
                onToggleItem = { item, isChecked ->
                    viewModel.toggleChecklistItem(item, isChecked)
                },
                onSaveNotes = { id, notes ->
                    viewModel.updateChecklistNotes(id, notes)
                },
                onDeleteItem = { id ->
                    viewModel.deleteChecklistItem(id)
                },
                onAddCustomItem = { title, timeSlot, cat ->
                    viewModel.addCustomChecklistItem(title, timeSlot, cat)
                },
                onResetDay = {
                    viewModel.resetDayChecklist()
                },
                onMarkAllDone = {
                    viewModel.markAllChecklistCompleted()
                },
                onStartTimerForBlockId = { blockId ->
                    viewModel.masterBlocks.find { it.id == blockId }?.let { block ->
                        onStartTimerForBlock(block)
                    }
                }
            )
        }

        // Daily Progress KPI Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_progress_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Routine Completion",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$completedCount of $totalBlocks Routine Items Done ($completionPercent%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        color = if (completionPercent >= 80) EmeraldSuccess else Indigo400,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "$completionPercent%",
                            style = MaterialTheme.typography.labelLarge,
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            color = Indigo900,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Strategy Breakdown Banner (Toggleable)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { showStrategyCard = !showStrategyCard },
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💡", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Why This Full-Day Schedule Works",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                        }
                        Text(
                            text = if (showStrategyCard) "Hide ▲" else "Read Strategy ▼",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AnimatedVisibility(visible = showStrategyCard) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Text(
                                text = "1. Morning Prime Time (07:00 AM – 01:00 PM):\n5+ hours of top-tier focus before lunch for your hardest subjects (Physics derivations, Organic Chemistry mechanisms, and Math calculus).\n\n" +
                                        "2. Afternoon \"Old is Gold\" Engine (02:00 PM – 04:30 PM):\nDedicated 2.5 hours every single day to solve past NEB questions under timed conditions.\n\n" +
                                        "3. Evening Workout & Early Dinner (06:00 PM – 07:45 PM):\nBurns mental fatigue; 7:00 PM dinner lets digestion settle early for deep sleep.\n\n" +
                                        "4. Guilt-Free Gaming (08:30 PM – 09:30 PM):\nEarned 1-hour gaming session tucked safely before light recall review.\n\n" +
                                        "5. Block 5 Active Recall (09:30 PM – 10:30 PM):\nReview today's Mistake Notebook and flashcards right before sleep to lock in synaptic retention.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Daily Master Timeline
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MASTER TIMELINE (14 BLOCKS)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Tap block for details",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Timeline items
        items(viewModel.masterBlocks, key = { it.id }) { block ->
            val isDone = completions[block.id]?.isCompleted == true
            val isCur = block.isCurrent(currentMinuteOfDay)

            BlockTimelineItem(
                block = block,
                isCompleted = isDone,
                isCurrent = isCur,
                onToggleDone = { done -> viewModel.toggleBlockDone(block, done) },
                onStartTimer = { onStartTimerForBlock(block) },
                onAddMistakeShortcut = {
                    val defaultSubject = when {
                        block.title.contains("Physics", ignoreCase = true) -> "Physics"
                        block.title.contains("Chemistry", ignoreCase = true) -> "Chemistry"
                        block.title.contains("Math", ignoreCase = true) -> "Mathematics"
                        block.title.contains("Computer", ignoreCase = true) -> "Computer Science"
                        else -> "Physics"
                    }
                    onLogMistake(defaultSubject, block.title)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
