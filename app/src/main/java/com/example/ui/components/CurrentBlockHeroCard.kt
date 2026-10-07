package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.StudyBlock
import com.example.ui.theme.AmberFocus
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Indigo800
import com.example.ui.theme.Indigo900
import com.example.ui.theme.JetBrainsMono

@Composable
fun CurrentBlockHeroCard(
    currentBlock: StudyBlock?,
    currentMinuteOfDay: Int,
    isCompleted: Boolean,
    onStartTimer: (StudyBlock) -> Unit,
    onToggleDone: (StudyBlock, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showGuidance by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("current_block_hero_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Hero banner image header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_study_hero_1791383671403),
                    contentDescription = "Study desk banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        )
                )

                // Top Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = if (currentBlock != null) AmberFocus else Indigo800,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (currentBlock != null) Indigo900 else CyanAccent)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (currentBlock != null) "LIVE ACTIVE BLOCK" else "BETWEEN BLOCKS",
                                color = if (currentBlock != null) Indigo900 else Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (currentBlock != null) {
                        Surface(
                            color = Indigo900.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                        ) {
                            Text(
                                text = "${currentBlock.startTime} – ${currentBlock.endTime}",
                                color = CyanAccent,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = JetBrainsMono,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Block Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (currentBlock != null) {
                    val progress = currentBlock.progressFraction(currentMinuteOfDay)
                    val remainingMins = currentBlock.minutesRemaining(currentMinuteOfDay)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = currentBlock.emoji,
                            fontSize = 32.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentBlock.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentBlock.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${(progress * 100).toInt()}% elapsed",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (remainingMins >= 60) {
                                "${remainingMins / 60}h ${remainingMins % 60}m left"
                            } else {
                                "${remainingMins}m left"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            color = AmberFocus
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = AmberFocus,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onStartTimer(currentBlock) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hero_start_timer_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Indigo400,
                                contentColor = Indigo900
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Start Timer",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Focus Timer",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        OutlinedButton(
                            onClick = { onToggleDone(currentBlock, !isCompleted) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hero_toggle_done_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isCompleted) EmeraldSuccess else MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(
                                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                contentDescription = "Toggle completion",
                                tint = if (isCompleted) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isCompleted) "Done ✓" else "Mark Done",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier
                                .clickable { showGuidance = !showGuidance }
                                .testTag("hero_info_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Tips",
                                    tint = CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = showGuidance,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Indigo900.copy(alpha = 0.6f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Block Strategy & Focus Tips:",
                                style = MaterialTheme.typography.labelMedium,
                                color = AmberFocus,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentBlock.guidance,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            currentBlock.suggestedGoals.forEach { goal ->
                                Text(
                                    text = "• $goal",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyanAccent
                                )
                            }
                        }
                    }

                } else {
                    // Between scheduled blocks or general summary
                    Text(
                        text = "Master Study Routine Active",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Stay consistent with your daily 14-block rhythm. Tap any block below to launch its dedicated focus timer or log progress.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
