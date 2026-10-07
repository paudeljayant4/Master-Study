package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AmberFocus
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Indigo900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MistakeDialog(
    initialSubject: String = "Physics",
    initialSource: String = "Asmita's Old is Gold",
    onSave: (subject: String, topic: String, question: String, mistake: String, takeaway: String, source: String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var subject by remember { mutableStateOf(initialSubject) }
    var topic by remember { mutableStateOf("") }
    var question by remember { mutableStateOf("") }
    var mistake by remember { mutableStateOf("") }
    var takeaway by remember { mutableStateOf("") }
    var source by remember { mutableStateOf(initialSource) }

    val subjects = listOf("Physics", "Chemistry", "Mathematics", "Computer Science", "General")
    val sources = listOf("Asmita's Old is Gold", "Block 1 Theory", "Block 2 Problems", "Model Exam")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState())
                .testTag("mistake_dialog")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Log Mistake to Notebook 📝",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = "Track your conceptual errors so you never repeat them in the NEB exam.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Subject selector
            Text(
                text = "Subject",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                subjects.forEach { s ->
                    val isSelected = s == subject
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { subject = s }
                            .testTag("select_subject_$s"),
                        color = if (isSelected) AmberFocus else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) AmberFocus else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(
                            text = s,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Indigo900 else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text("Chapter / Topic") },
                placeholder = { Text("e.g. Rotational Dynamics, Calculus Definite Integrals") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mistake_input_topic"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                label = { Text("Question or Problem Statement") },
                placeholder = { Text("Brief question prompt or numerical step") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mistake_input_question"),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = mistake,
                onValueChange = { mistake = it },
                label = { Text("What Went Wrong? (The Mistake)") },
                placeholder = { Text("e.g. Forgot square root in denominator, used wrong sign in Work-Energy") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mistake_input_mistake"),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = takeaway,
                onValueChange = { takeaway = it },
                label = { Text("Correct Formula / Golden Takeaway") },
                placeholder = { Text("e.g. Recall: v_t = 2/9 * r^2(ρ - σ)g / η") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mistake_input_takeaway"),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Source selector
            Text(
                text = "Source",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                sources.forEach { src ->
                    val isSelected = src == source
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { source = src },
                        color = if (isSelected) CyanAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) CyanAccent else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(
                            text = src,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) CyanAccent else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    if (topic.isNotBlank() || question.isNotBlank() || mistake.isNotBlank()) {
                        onSave(
                            subject,
                            topic.ifBlank { "General" },
                            question.ifBlank { "Practice problem" },
                            mistake.ifBlank { "Calculation error" },
                            takeaway.ifBlank { "Review standard method" },
                            source
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_mistake_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberFocus,
                    contentColor = Indigo900
                )
            ) {
                Text(
                    text = "Save to Mistake Notebook",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
