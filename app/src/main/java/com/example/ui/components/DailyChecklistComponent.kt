package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyChecklistItem
import com.example.ui.theme.AmberFocus
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Indigo900
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.RoseError

@Composable
fun DailyChecklistComponent(
    checklistItems: List<StudyChecklistItem>,
    onToggleItem: (StudyChecklistItem, Boolean) -> Unit,
    onSaveNotes: (Long, String) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onAddCustomItem: (title: String, timeSlot: String, category: String) -> Unit,
    onResetDay: () -> Unit,
    onMarkAllDone: () -> Unit,
    onStartTimerForBlockId: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var filterMode by remember { mutableStateOf("All") } // "All", "Pending", "Done"
    var showAddDialog by remember { mutableStateOf(false) }

    val completedCount = checklistItems.count { it.isCompleted }
    val totalCount = checklistItems.size
    val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
    val percentage = (progressFraction * 100).toInt()

    val filteredItems = when (filterMode) {
        "Pending" -> checklistItems.filter { !it.isCompleted }
        "Done" -> checklistItems.filter { it.isCompleted }
        else -> checklistItems
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_study_checklist_component"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✅", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Daily Routine Checklist",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Room-persisted master day tracker",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = if (percentage == 100) EmeraldSuccess else AmberFocus,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "$completedCount/$totalCount Done",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        color = Indigo900,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (percentage == 100) EmeraldSuccess else AmberFocus,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips & Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("All", "Pending", "Done").forEach { filter ->
                        val isSel = filter == filterMode
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { filterMode = filter }
                                .testTag("checklist_filter_$filter"),
                            color = if (isSel) AmberFocus else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) AmberFocus else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Text(
                                text = filter,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Indigo900 else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onResetDay,
                        modifier = Modifier.size(32.dp).testTag("checklist_reset_button")
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Reset Day",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onMarkAllDone,
                        modifier = Modifier.size(32.dp).testTag("checklist_mark_all_button")
                    ) {
                        Icon(
                            Icons.Default.TaskAlt,
                            contentDescription = "Mark All Done",
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.size(32.dp).testTag("checklist_add_custom_button")
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Goal",
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Checklist Items List
            if (filteredItems.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = if (filterMode == "Pending") "All routine items completed for today! 🎉" else "No items found.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    filteredItems.forEach { item ->
                        ChecklistItemRow(
                            item = item,
                            onToggle = { isChecked -> onToggleItem(item, isChecked) },
                            onSaveNotes = { notes -> onSaveNotes(item.id, notes) },
                            onDelete = { onDeleteItem(item.id) },
                            onStartTimer = {
                                onStartTimerForBlockId?.invoke(item.routineBlockId)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCustomGoalDialog(
            onSave = { title, time, cat ->
                onAddCustomItem(title, time, cat)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun ChecklistItemRow(
    item: StudyChecklistItem,
    onToggle: (Boolean) -> Unit,
    onSaveNotes: (String) -> Unit,
    onDelete: () -> Unit,
    onStartTimer: () -> Unit
) {
    var expandedNotes by remember { mutableStateOf(false) }
    var notesText by remember(item.notes) { mutableStateOf(item.notes) }

    val bgColor by animateColorAsState(
        targetValue = if (item.isCompleted) {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "item_row_bg"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("checklist_row_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isCompleted) EmeraldSuccess.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Checkbox Circle
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (item.isCompleted) EmeraldSuccess else Color.Transparent)
                        .border(
                            2.dp,
                            if (item.isCompleted) EmeraldSuccess else MaterialTheme.colorScheme.outline,
                            CircleShape
                        )
                        .clickable { onToggle(!item.isCompleted) }
                        .testTag("checklist_checkbox_${item.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Item info
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = item.emoji, fontSize = 16.sp, modifier = Modifier.padding(end = 4.dp))
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (item.isCompleted) {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )
                    }

                    if (item.timeSlot.isNotBlank()) {
                        Text(
                            text = item.timeSlot,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = JetBrainsMono,
                            color = if (item.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else AmberFocus
                        )
                    }
                }

                // Actions
                IconButton(
                    onClick = { expandedNotes = !expandedNotes },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Notes",
                        tint = if (item.notes.isNotBlank()) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }

                if (!item.isCompleted) {
                    IconButton(
                        onClick = onStartTimer,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Timer",
                            tint = Indigo400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (item.isCustom) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = RoseError.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Note expansion
            AnimatedVisibility(visible = expandedNotes) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        placeholder = { Text("Add daily reflection / completed topics...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                onSaveNotes(notesText)
                                expandedNotes = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Indigo900)
                        ) {
                            Text("Save Note", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddCustomGoalDialog(
    onSave: (title: String, timeSlot: String, category: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var timeSlot by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Target") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Daily Routine Goal 🎯", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Routine Item / Goal") },
                    placeholder = { Text("e.g. Solve 5 Organic Mechanisms") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = timeSlot,
                    onValueChange = { timeSlot = it },
                    label = { Text("Target Time Window (Optional)") },
                    placeholder = { Text("e.g. 03:00 PM – 04:00 PM") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, timeSlot, category)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberFocus, contentColor = Indigo900)
            ) {
                Text("Add Goal", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
