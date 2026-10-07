package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BlockCategory
import com.example.data.model.StudyBlock
import com.example.ui.theme.AmberFocus
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Indigo800
import com.example.ui.theme.Indigo900
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.OrangeGaming
import com.example.ui.theme.VioletPurple

@Composable
fun BlockTimelineItem(
    block: StudyBlock,
    isCompleted: Boolean,
    isCurrent: Boolean,
    onToggleDone: (Boolean) -> Unit,
    onStartTimer: () -> Unit,
    onAddMistakeShortcut: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // Highlight badge determination
    val specialBadge = when (block.category) {
        BlockCategory.THEORY_DERIVATION -> "Morning Prime Time 🧠"
        BlockCategory.MATH_PROBLEM_SOLVING -> "Morning Prime Time 📐"
        BlockCategory.OLD_IS_GOLD_ENGINE -> "NEB Exam Engine ⚡"
        BlockCategory.LEISURE_GAMING -> "Guilt-Free Leisure 🎮"
        BlockCategory.ACTIVE_RECALL_MISTAKE -> "Long-Term Retention 📝"
        BlockCategory.SLEEP_RECOVERY -> "Neuro-Consolidation 😴"
        BlockCategory.WORKOUT -> "Fitness & Reset 🏋️"
        else -> null
    }

    val badgeColor = when (block.category) {
        BlockCategory.THEORY_DERIVATION, BlockCategory.MATH_PROBLEM_SOLVING -> AmberFocus
        BlockCategory.OLD_IS_GOLD_ENGINE -> CyanAccent
        BlockCategory.LEISURE_GAMING -> OrangeGaming
        BlockCategory.ACTIVE_RECALL_MISTAKE -> VioletPurple
        BlockCategory.SLEEP_RECOVERY -> Indigo400
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("block_item_${block.id}")
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) {
                MaterialTheme.colorScheme.surfaceVariant
            } else if (isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isCurrent) 2.dp else 1.dp,
            color = if (isCurrent) AmberFocus else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox toggle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompleted) EmeraldSuccess else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            width = 2.dp,
                            color = if (isCompleted) EmeraldSuccess else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                        .clickable { onToggleDone(!isCompleted) }
                        .testTag("checkbox_${block.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Time Pill
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = "${block.startTime} – ${block.endTime}",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = JetBrainsMono,
                        color = if (isCurrent) AmberFocus else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Duration tag
                val durHours = block.durationMinutes / 60
                val durMins = block.durationMinutes % 60
                val durationText = when {
                    durHours > 0 && durMins > 0 -> "${durHours}h ${durMins}m"
                    durHours > 0 -> "${durHours}h"
                    else -> "${durMins}m"
                }

                Text(
                    text = durationText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.ChevronRight,
                        contentDescription = "Expand details",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & emoji
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = block.emoji,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 10.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = block.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    Text(
                        text = block.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Special badge if available
            if (specialBadge != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = badgeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = specialBadge,
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Expanded detail section
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Focus Instructions:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = block.guidance,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Checklist Objectives:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    block.suggestedGoals.forEach { goal ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(AmberFocus)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = goal,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onStartTimer() }
                                .testTag("start_timer_btn_${block.id}"),
                            color = Indigo400,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Indigo900,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Start Focus (${block.durationMinutes}m)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Indigo900
                                )
                            }
                        }

                        if (onAddMistakeShortcut != null) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onAddMistakeShortcut() }
                                    .testTag("quick_mistake_btn_${block.id}"),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "+ Log Mistake",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyanAccent,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
