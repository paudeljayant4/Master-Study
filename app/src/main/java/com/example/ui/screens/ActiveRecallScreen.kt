package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.outlined.CheckCircleOutline
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FlashcardItem
import com.example.data.model.MistakeEntry
import com.example.ui.theme.AmberFocus
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Indigo900
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.RoseError
import com.example.ui.theme.VioletPurple
import com.example.ui.viewmodel.StudyViewModel

@Composable
fun ActiveRecallScreen(
    viewModel: StudyViewModel,
    onOpenMistakeDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val mistakes by viewModel.allMistakes.collectAsState()
    val flashcards by viewModel.allFlashcards.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("active_recall_screen")
    ) {
        // Tab selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = AmberFocus
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = "Mistake Log (${mistakes.size})",
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = "Formula Flashcards (${flashcards.size})",
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }

        if (selectedTab == 0) {
            MistakeLogTab(
                mistakes = mistakes,
                onAddMistake = onOpenMistakeDialog,
                onUpdateMastery = { entry, level -> viewModel.updateMistakeMastery(entry, level) },
                onDelete = { id -> viewModel.deleteMistake(id) }
            )
        } else {
            FlashcardsTab(
                flashcards = flashcards,
                onToggleMastered = { card -> viewModel.toggleFlashcardMastered(card) },
                onAddCustomCard = { subj, title, prompt, ans, formula ->
                    viewModel.addCustomFlashcard(subj, title, prompt, ans, formula)
                }
            )
        }
    }
}

@Composable
private fun MistakeLogTab(
    mistakes: List<MistakeEntry>,
    onAddMistake: () -> Unit,
    onUpdateMastery: (MistakeEntry, Int) -> Unit,
    onDelete: (Long) -> Unit
) {
    var subjectFilter by remember { mutableStateOf("All") }
    val subjects = listOf("All", "Physics", "Chemistry", "Mathematics", "Computer Science")

    val filtered = mistakes.filter {
        subjectFilter == "All" || it.subject.equals(subjectFilter, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, VioletPurple.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Block 5: Mistake Review Notebook 📝",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "09:30 PM – 10:30 PM nightly ritual. Reviewing errors made during derivations and numericals cements the correct synaptic pathways before sleep.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onAddMistake,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_mistake_log_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberFocus,
                            contentColor = Indigo900
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ Log New Mistake / Slip", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Subject filters
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                subjects.forEach { s ->
                    val isSel = s == subjectFilter
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { subjectFilter = s },
                        color = if (isSel) AmberFocus else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSel) AmberFocus else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(
                            text = s,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) Indigo900 else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = if (mistakes.isEmpty()) {
                            "No mistakes logged yet! As you practice in Block 1, 2, or 3, log any steps or formulas that slipped so you can review them here every night."
                        } else {
                            "No mistakes found under $subjectFilter."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(filtered, key = { it.id }) { mistake ->
                MistakeCard(
                    entry = mistake,
                    onUpdateMastery = { level -> onUpdateMastery(mistake, level) },
                    onDelete = { onDelete(mistake.id) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun MistakeCard(
    entry: MistakeEntry,
    onUpdateMastery: (Int) -> Unit,
    onDelete: () -> Unit
) {
    val isMastered = entry.masteryLevel >= 2

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMastered) MaterialTheme.colorScheme.surface.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isMastered) EmeraldSuccess.copy(alpha = 0.4f) else RoseError.copy(alpha = 0.3f)
        )
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
                    color = when (entry.subject) {
                        "Physics" -> AmberFocus.copy(alpha = 0.2f)
                        "Chemistry" -> EmeraldSuccess.copy(alpha = 0.2f)
                        "Mathematics" -> Indigo400.copy(alpha = 0.2f)
                        else -> CyanAccent.copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${entry.subject} • ${entry.topic}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (entry.subject) {
                            "Physics" -> AmberFocus
                            "Chemistry" -> EmeraldSuccess
                            "Mathematics" -> Indigo400
                            else -> CyanAccent
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                onUpdateMastery(if (isMastered) 0 else 2)
                            },
                        color = if (isMastered) EmeraldSuccess else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (isMastered) "Mastered ✓" else "Needs Review",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isMastered) Color.White else AmberFocus,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Question: ${entry.questionOrProblem}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // What went wrong (Rose background)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = RoseError.copy(alpha = 0.12f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "⚠️ What went wrong:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = RoseError
                    )
                    Text(
                        text = entry.mistakeMade,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Golden Takeaway (Emerald/Cyan background)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = EmeraldSuccess.copy(alpha = 0.12f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "💡 Correct takeaway / rule:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess
                    )
                    Text(
                        text = entry.keyTakeawayOrFormula,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Source: ${entry.source} • ${entry.dateString}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FlashcardsTab(
    flashcards: List<FlashcardItem>,
    onToggleMastered: (FlashcardItem) -> Unit,
    onAddCustomCard: (String, String, String, String, String) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedSubject by remember { mutableStateOf("All") }

    val filteredCards = flashcards.filter {
        selectedSubject == "All" || it.subject.equals(selectedSubject, ignoreCase = true)
    }

    val safeIndex = if (filteredCards.isEmpty()) 0 else currentIndex.coerceIn(0, filteredCards.size - 1)
    val currentCard = if (filteredCards.isNotEmpty()) filteredCards[safeIndex] else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Controls row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Formula & Derivation Cards",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Indigo900)
            ) {
                Text("+ New Card", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Subject selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "Physics", "Chemistry", "Mathematics", "Computer Science").forEach { s ->
                val isSel = s == selectedSubject
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            selectedSubject = s
                            currentIndex = 0
                            isFlipped = false
                        },
                    color = if (isSel) CyanAccent else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = s,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) Indigo900 else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (currentCard != null) {
            // Card Counter
            Text(
                text = "Card ${safeIndex + 1} of ${filteredCards.size} • Tap card to flip 🔄",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // The Flip Card
            val rotation by animateFloatAsState(
                targetValue = if (isFlipped) 180f else 0f,
                animationSpec = tween(durationMillis = 400),
                label = "card_flip"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    }
                    .clickable { isFlipped = !isFlipped }
                    .testTag("interactive_flashcard"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isFlipped) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    if (currentCard.isMastered) EmeraldSuccess else CyanAccent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .graphicsLayer {
                            if (rotation > 90f) {
                                rotationY = 180f
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (rotation <= 90f) {
                        // FRONT SIDE
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                color = AmberFocus.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = currentCard.subject,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberFocus,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = currentCard.frontTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentCard.frontPrompt,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // BACK SIDE
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                color = EmeraldSuccess.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "SOLUTION & KEY FORMULA",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = currentCard.formulaOrKeySteps,
                                style = MaterialTheme.typography.titleSmall,
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentCard.backAnswer,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Buttons (Prev / Next & Mastered toggle)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (safeIndex > 0) {
                            currentIndex = safeIndex - 1
                            isFlipped = false
                        }
                    },
                    enabled = safeIndex > 0
                ) {
                    Icon(Icons.Default.NavigateBefore, contentDescription = "Previous Card", modifier = Modifier.size(32.dp))
                }

                OutlinedButton(
                    onClick = { onToggleMastered(currentCard) },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = if (currentCard.isMastered) Icons.Default.CheckCircle else Icons.Outlined.CheckCircleOutline,
                        contentDescription = null,
                        tint = if (currentCard.isMastered) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentCard.isMastered) "Mastered ✓" else "Mark Mastered",
                        color = if (currentCard.isMastered) EmeraldSuccess else MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = {
                        if (safeIndex < filteredCards.size - 1) {
                            currentIndex = safeIndex + 1
                            isFlipped = false
                        }
                    },
                    enabled = safeIndex < filteredCards.size - 1
                ) {
                    Icon(Icons.Default.NavigateNext, contentDescription = "Next Card", modifier = Modifier.size(32.dp))
                }
            }
        } else {
            Text("No flashcards found for $selectedSubject.")
        }
    }

    if (showAddDialog) {
        AddFlashcardDialog(
            onSave = { subj, title, prompt, ans, formula ->
                onAddCustomCard(subj, title, prompt, ans, formula)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun AddFlashcardDialog(
    onSave: (subject: String, title: String, prompt: String, ans: String, formula: String) -> Unit,
    onDismiss: () -> Unit
) {
    var subject by remember { mutableStateOf("Physics") }
    var title by remember { mutableStateOf("") }
    var prompt by remember { mutableStateOf("") }
    var ans by remember { mutableStateOf("") }
    var formula by remember { mutableStateOf("") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Formula Flashcard 📇", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject (Physics/Chemistry/Math/CS)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Topic / Title") },
                    placeholder = { Text("e.g. Lens Maker's Formula") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    label = { Text("Front Prompt") },
                    placeholder = { Text("e.g. What is the formula for...?") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = formula,
                    onValueChange = { formula = it },
                    label = { Text("Key Formula (Back)") },
                    placeholder = { Text("e.g. 1/f = (μ-1)(1/R1 - 1/R2)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ans,
                    onValueChange = { ans = it },
                    label = { Text("Detailed Explanation (Back)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(subject, title, prompt, ans, formula)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Indigo900)
            ) {
                Text("Add Card", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
