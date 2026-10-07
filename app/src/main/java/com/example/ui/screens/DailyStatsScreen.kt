package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberFocus
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Indigo800
import com.example.ui.theme.Indigo900
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.OrangeGaming
import com.example.ui.theme.VioletPurple
import com.example.ui.viewmodel.StudyViewModel

@Composable
fun DailyStatsScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val completions by viewModel.completions.collectAsState()
    val allMistakes by viewModel.allMistakes.collectAsState()
    val totalPracticeMins by viewModel.totalPracticeMinutes.collectAsState()
    val totalQuestions by viewModel.totalQuestionsSolved.collectAsState()

    val completedBlocksCount = completions.values.count { it.isCompleted }
    val totalMinutesCompleted = completions.values.filter { it.isCompleted }.sumOf { it.minutesSpent }
    val masteredMistakesCount = allMistakes.count { it.masteryLevel >= 2 }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("daily_stats_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Strategy Architecture Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberFocus.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            color = AmberFocus,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "MASTER STRATEGY METRICS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Indigo900,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "9h 45m Peak Study",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Why This Layout Outperforms Normal Study",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "A scientifically balanced self-study routine: maximum cognitive load occurs during biological morning alertness, followed by active past exam drilling, physical exercise to burn mental stress, guilt-free recreation, and synaptic memory consolidation before 8 hours of sleep.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 4 Grid KPI Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricKpiCard(
                    title = "Blocks Completed",
                    value = "$completedBlocksCount / 14",
                    sub = "${(completedBlocksCount * 100 / 14)}% of full routine",
                    color = AmberFocus,
                    modifier = Modifier.weight(1f)
                )
                MetricKpiCard(
                    title = "Logged Focus",
                    value = "${totalMinutesCompleted / 60}h ${totalMinutesCompleted % 60}m",
                    sub = "Target: 9h 45m study",
                    color = CyanAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricKpiCard(
                    title = "Questions Solved",
                    value = "${totalQuestions ?: 0}",
                    sub = "Old is Gold Engine",
                    color = EmeraldSuccess,
                    modifier = Modifier.weight(1f)
                )
                MetricKpiCard(
                    title = "Mistakes Mastered",
                    value = "$masteredMistakesCount / ${allMistakes.size}",
                    sub = "Review Notebook",
                    color = VioletPurple,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section: The 4 Key Pillars of this Daily Routine
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "THE 4 PILLARS OF YOUR SCHEDULE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
        }

        item {
            PillarCard(
                emoji = "🌅",
                title = "1. Morning Prime Time (07:00 AM – 01:00 PM)",
                subtitle = "5+ hours top-tier focus before lunch",
                body = "Your prefrontal cortex and cognitive processing bandwidth are at their maximum between 7 AM and 1 PM. Placing Physics derivations, Organic Chemistry mechanisms, and Calculus problem solving here guarantees you tackle the hardest topics when your willpower is untouched.",
                accentColor = AmberFocus
            )
        }

        item {
            PillarCard(
                emoji = "⚡",
                title = "2. Afternoon \"Old is Gold\" Engine (02:00 PM – 04:30 PM)",
                subtitle = "Asmita's past questions under timed conditions",
                body = "After a restorative lunch and exact 20-minute power nap, you dive straight into authentic past NEB board papers. Practicing under strict stopwatch conditions builds exam stamina and pattern recognition for the actual questions that appear year after year.",
                accentColor = CyanAccent
            )
        }

        item {
            PillarCard(
                emoji = "🏋️",
                title = "3. Evening Workout & Early Dinner (06:00 PM – 07:45 PM)",
                subtitle = "Burn off day's fatigue & optimal sleep digestion",
                body = "Working out right before dinner releases endorphins and clears mental brain fog. Eating dinner early at 7:00 PM gives your gastrointestinal system 3.5 hours to complete digestion before sleep, avoiding blood sugar spikes and sleep fragmentation.",
                accentColor = EmeraldSuccess
            )
        }

        item {
            PillarCard(
                emoji = "🎮",
                title = "4. Guilt-Free Gaming (08:30 PM – 09:30 PM)",
                subtitle = "Earned 1-hour recreation & psychological release",
                body = "True discipline requires sustainable rest. Because you already completed 8+ hours of intensive studying and your workout, this 1-hour gaming session is 100% earned and guilt-free. You enjoy your game without background anxiety because your work for the day is done.",
                accentColor = OrangeGaming
            )
        }

        item {
            PillarCard(
                emoji = "📝",
                title = "5. Nightly Active Recall & Mistake Log (09:30 PM – 10:30 PM)",
                subtitle = "Lock knowledge in memory before 8h sleep",
                body = "The final 60 minutes before lights-out. Flipping formula flashcards and re-reading your Mistake Log triggers overnight synaptic consolidation in the hippocampus during your 8 hours of deep sleep (10:30 PM – 06:30 AM).",
                accentColor = VioletPurple
            )
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun MetricKpiCard(
    title: String,
    value: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = JetBrainsMono,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = sub,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PillarCard(
    emoji: String,
    title: String,
    subtitle: String,
    body: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = emoji, fontSize = 24.sp, modifier = Modifier.padding(end = 10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}
