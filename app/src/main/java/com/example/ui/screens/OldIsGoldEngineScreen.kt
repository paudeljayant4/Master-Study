package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DefaultSchedule
import com.example.data.model.OldIsGoldSession
import com.example.data.model.StudyBlock
import com.example.ui.theme.AmberFocus
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Indigo900
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.RoseError
import com.example.ui.viewmodel.StudyViewModel

@Composable
fun OldIsGoldEngineScreen(
    viewModel: StudyViewModel,
    onStartExamTimer: (StudyBlock, customMinutes: Int) -> Unit,
    onLogMistake: (subject: String, source: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.allSessions.collectAsState()
    val totalMins by viewModel.totalPracticeMinutes.collectAsState()
    val totalQuestions by viewModel.totalQuestionsSolved.collectAsState()

    var showRecordDialog by remember { mutableStateOf(false) }
    var selectedSubjectFilter by remember { mutableStateOf("All") }

    // Pre-loaded high-yield NEB Past Question prompts
    val highYieldNebPrompts = listOf(
        NebPrompt("Physics", "Rotational Dynamics", "Show relation between torque and angular acceleration τ = Iα, and calculate moment of inertia of a disc about its diameter."),
        NebPrompt("Physics", "Viscosity & Stokes' Law", "State Stokes' Law and derive the formula for terminal velocity of a falling sphere in viscous liquid."),
        NebPrompt("Chemistry", "Aldehydes & Ketones", "Outline reaction mechanism of Aldol condensation and Cannizzaro's reaction with conditions and chemical equations."),
        NebPrompt("Chemistry", "Electrochemistry", "Derive Nernst equation for single electrode potential and cell potential under non-standard conditions."),
        NebPrompt("Mathematics", "Integral Calculus", "Evaluate standard integral ∫ dx / (a² + x²) by trigonometric substitution and evaluate ∫ e^(ax) sin(bx) dx using by-parts."),
        NebPrompt("Mathematics", "Vectors & Geometry", "Prove sine formula or cosine formula in a triangle using vector cross / dot product method."),
        NebPrompt("Computer Science", "Database Systems", "Explain normalization from 1NF to 3NF with an illustrative student record database schema."),
        NebPrompt("Computer Science", "Object Oriented Programming", "Compare Inheritance and Polymorphism in C++ with virtual function code demonstrations.")
    )

    val block3 = viewModel.masterBlocks.firstOrNull { it.id == "block_3" }
        ?: DefaultSchedule.masterBlocks[5]

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("old_is_gold_engine_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = CyanAccent.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent)
                        ) {
                            Text(
                                text = "BLOCK 3 ENGINE (02:00 PM – 04:30 PM)",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "2.5h Exam Mode",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = JetBrainsMono,
                            color = AmberFocus
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Asmita's \"Old is Gold\" Practice Engine ⚡",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "The secret to excelling in NEB board examinations is daily deliberate practice under authentic timed conditions. Pick past question sets, run the timer, and log tricky steps.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Practice Quick Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Total Exam Time",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${totalMins ?: 0} mins",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent,
                                    fontFamily = JetBrainsMono
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Questions Solved",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${totalQuestions ?: 0} questions",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberFocus,
                                    fontFamily = JetBrainsMono
                                )
                            }
                        }
                    }
                }
            }
        }

        // Exam Timer Launchers
        item {
            Text(
                text = "LAUNCH TIMED NEB PRACTICE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Preset 1: Full 150m mock
                Button(
                    onClick = { onStartExamTimer(block3, 150) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("launch_full_mock_timer"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanAccent,
                        contentColor = Indigo900
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Full NEB Mock", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text("150 mins (2.5h)", style = MaterialTheme.typography.labelSmall)
                    }
                }

                // Preset 2: 60m Sprint
                OutlinedButton(
                    onClick = { onStartExamTimer(block3, 60) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("launch_60m_sprint_timer"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Subject Sprint", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text("60 mins", style = MaterialTheme.typography.labelSmall)
                    }
                }

                // Preset 3: 30m Rapid
                OutlinedButton(
                    onClick = { onStartExamTimer(block3, 30) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("launch_30m_drill_timer"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Rapid Drill", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text("30 mins", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // Action Buttons Row: Log Completed Session & Log Mistake
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showRecordDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("record_session_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberFocus,
                        contentColor = Indigo900
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Record Session Results", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { onLogMistake("Physics", "Asmita's Old is Gold") },
                    modifier = Modifier.testTag("log_mistake_from_engine"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ Log Mistake")
                }
            }
        }

        // High-Yield NEB Syllabus Prompts
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "HIGH-YIELD NEB PAST QUESTIONS BANK",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
        }

        items(highYieldNebPrompts) { prompt ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = when (prompt.subject) {
                                "Physics" -> AmberFocus.copy(alpha = 0.2f)
                                "Chemistry" -> EmeraldSuccess.copy(alpha = 0.2f)
                                "Mathematics" -> Indigo400.copy(alpha = 0.2f)
                                else -> CyanAccent.copy(alpha = 0.2f)
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = prompt.subject,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (prompt.subject) {
                                    "Physics" -> AmberFocus
                                    "Chemistry" -> EmeraldSuccess
                                    "Mathematics" -> Indigo400
                                    else -> CyanAccent
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = prompt.topic,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = prompt.question,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onLogMistake(prompt.subject, "Old is Gold: ${prompt.topic}") },
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "Stuck? Add to Mistake Log 📝",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Previous Practice Sessions Log
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "PAST COMPLETED DRILLS & PAPERS (${sessions.size})",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
        }

        if (sessions.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "No past exam sessions recorded yet. Start a timed Old is Gold mock above and log your score!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(sessions, key = { it.id }) { session ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${session.subject} • ${session.yearOrSet}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${session.actualMinutesElapsed} mins elapsed • ${session.questionsAttempted} questions • Score: ${session.marksObtained.toInt()}/${session.totalPossibleMarks.toInt()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (session.reflectionNotes.isNotBlank()) {
                                Text(
                                    text = "Notes: ${session.reflectionNotes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AmberFocus
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.deleteSession(session) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RoseError.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showRecordDialog) {
        RecordSessionDialog(
            onSave = { subj, set, mins, qCount, score, total, notes ->
                viewModel.saveOldIsGoldSession(subj, set, 150, mins, qCount, score, total, notes)
                showRecordDialog = false
            },
            onDismiss = { showRecordDialog = false }
        )
    }
}

private data class NebPrompt(
    val subject: String,
    val topic: String,
    val question: String
)

@Composable
private fun RecordSessionDialog(
    onSave: (subject: String, yearSet: String, mins: Int, questions: Int, score: Float, total: Float, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var subject by remember { mutableStateOf("Physics") }
    var yearSet by remember { mutableStateOf("2080 NEB Model Set") }
    var minsText by remember { mutableStateOf("150") }
    var questionsText by remember { mutableStateOf("12") }
    var scoreText by remember { mutableStateOf("65") }
    var totalText by remember { mutableStateOf("75") }
    var notes by remember { mutableStateOf("") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Record Old is Gold Results ⚡", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = yearSet,
                    onValueChange = { yearSet = it },
                    label = { Text("Year / Exam Set") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = minsText,
                        onValueChange = { minsText = it },
                        label = { Text("Mins Taken") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = questionsText,
                        onValueChange = { questionsText = it },
                        label = { Text("Questions") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = scoreText,
                        onValueChange = { scoreText = it },
                        label = { Text("Marks Scored") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = totalText,
                        onValueChange = { totalText = it },
                        label = { Text("Total Marks") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Reflections / Tricky Areas") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val mins = minsText.toIntOrNull() ?: 60
                    val qCount = questionsText.toIntOrNull() ?: 10
                    val sc = scoreText.toFloatOrNull() ?: 50f
                    val tot = totalText.toFloatOrNull() ?: 75f
                    onSave(subject, yearSet, mins, qCount, sc, tot, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Indigo900)
            ) {
                Text("Save Session", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
