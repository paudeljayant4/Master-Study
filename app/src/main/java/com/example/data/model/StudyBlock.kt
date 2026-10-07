package com.example.data.model

enum class BlockCategory(val displayName: String) {
    MORNING_ROUTINE("Morning Routine"),
    THEORY_DERIVATION("Theory & Derivations"),
    BREAK_MEAL("Break & Nutrition"),
    MATH_PROBLEM_SOLVING("Math & Problem Solving"),
    OLD_IS_GOLD_ENGINE("Old is Gold Engine"),
    CS_LANGUAGE("CS & Language"),
    WORKOUT("Workout & Fitness"),
    LEISURE_GAMING("Guilt-Free Leisure"),
    ACTIVE_RECALL_MISTAKE("Active Recall & Mistake Log"),
    SLEEP_RECOVERY("Sleep & Recovery")
}

data class StudyBlock(
    val id: String,
    val title: String,
    val subtitle: String,
    val startTime: String,
    val endTime: String,
    val startMinuteOfDay: Int,
    val endMinuteOfDay: Int,
    val emoji: String,
    val category: BlockCategory,
    val isCoreStudy: Boolean,
    val guidance: String,
    val suggestedGoals: List<String>
) {
    val durationMinutes: Int
        get() = if (endMinuteOfDay >= startMinuteOfDay) {
            endMinuteOfDay - startMinuteOfDay
        } else {
            (24 * 60 - startMinuteOfDay) + endMinuteOfDay
        }

    fun isCurrent(currentMinuteOfDay: Int): Boolean {
        return if (startMinuteOfDay <= endMinuteOfDay) {
            currentMinuteOfDay in startMinuteOfDay until endMinuteOfDay
        } else {
            // crosses midnight (e.g., Sleep 10:30 PM to 06:30 AM)
            currentMinuteOfDay >= startMinuteOfDay || currentMinuteOfDay < endMinuteOfDay
        }
    }

    fun progressFraction(currentMinuteOfDay: Int): Float {
        if (!isCurrent(currentMinuteOfDay)) return 0f
        val elapsed = if (startMinuteOfDay <= endMinuteOfDay) {
            (currentMinuteOfDay - startMinuteOfDay).coerceAtLeast(0)
        } else {
            if (currentMinuteOfDay >= startMinuteOfDay) {
                currentMinuteOfDay - startMinuteOfDay
            } else {
                (24 * 60 - startMinuteOfDay) + currentMinuteOfDay
            }
        }
        val total = durationMinutes.coerceAtLeast(1)
        return (elapsed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    }

    fun minutesRemaining(currentMinuteOfDay: Int): Int {
        if (!isCurrent(currentMinuteOfDay)) return 0
        val remaining = if (startMinuteOfDay <= endMinuteOfDay) {
            endMinuteOfDay - currentMinuteOfDay
        } else {
            if (currentMinuteOfDay >= startMinuteOfDay) {
                (24 * 60 - currentMinuteOfDay) + endMinuteOfDay
            } else {
                endMinuteOfDay - currentMinuteOfDay
            }
        }
        return remaining.coerceAtLeast(0)
    }
}

object DefaultSchedule {
    val masterBlocks: List<StudyBlock> = listOf(
        StudyBlock(
            id = "block_morning",
            title = "Wake up, Hydrate & Morning Prep",
            subtitle = "Sunlight, 500ml water & morning mindset",
            startTime = "06:30 AM",
            endTime = "07:00 AM",
            startMinuteOfDay = 6 * 60 + 30, // 390
            endMinuteOfDay = 7 * 60,        // 420
            emoji = "🌅",
            category = BlockCategory.MORNING_ROUTINE,
            isCoreStudy = false,
            guidance = "Avoid phone screen scrolling. Drink 500ml of water, get natural sunlight exposure, and stretch lightly.",
            suggestedGoals = listOf("Drink 500ml water", "Sunlight exposure", "Review today's study goals")
        ),
        StudyBlock(
            id = "block_1",
            title = "Block 1: Heavy Theory & Derivations",
            subtitle = "Physics / Chemistry mechanisms",
            startTime = "07:00 AM",
            endTime = "09:30 AM",
            startMinuteOfDay = 7 * 60,        // 420
            endMinuteOfDay = 9 * 60 + 30,    // 570
            emoji = "🧠",
            category = BlockCategory.THEORY_DERIVATION,
            isCoreStudy = true,
            guidance = "Peak brain clarity window. Master rigorous Physics derivations without looking at notes and write out Chemistry reaction mechanisms step-by-step.",
            suggestedGoals = listOf("2 Full Physics derivations from memory", "Chemistry mechanism diagramming", "Concept map summary")
        ),
        StudyBlock(
            id = "block_breakfast",
            title = "Breakfast & Short Break",
            subtitle = "Brain fuel & mental reset",
            startTime = "09:30 AM",
            endTime = "10:15 AM",
            startMinuteOfDay = 9 * 60 + 30,   // 570
            endMinuteOfDay = 10 * 60 + 15,   // 615
            emoji = "🍳",
            category = BlockCategory.BREAK_MEAL,
            isCoreStudy = false,
            guidance = "Nutritious protein-rich meal. Step away from your study desk to let morning concepts consolidate.",
            suggestedGoals = listOf("Eat wholesome breakfast", "10-min outdoor walk", "Fill water bottle")
        ),
        StudyBlock(
            id = "block_2",
            title = "Block 2: Mathematics & Complex Problem Solving",
            subtitle = "Calculus, vectors & deep numericals",
            startTime = "10:15 AM",
            endTime = "01:00 PM",
            startMinuteOfDay = 10 * 60 + 15,  // 615
            endMinuteOfDay = 13 * 60,         // 780
            emoji = "📐",
            category = BlockCategory.MATH_PROBLEM_SOLVING,
            isCoreStudy = true,
            guidance = "2h 45m of uninterrupted computational power. Solve calculus integrals, differential equations, and challenging coordinate geometry.",
            suggestedGoals = listOf("Solve 15-20 Calculus problems", "Vector geometry proofs", "Log any stuck steps into Mistake Book")
        ),
        StudyBlock(
            id = "block_lunch",
            title = "Lunch & 20-min Power Nap / Rest",
            subtitle = "Digestive recharge & alertness reset",
            startTime = "01:00 PM",
            endTime = "02:00 PM",
            startMinuteOfDay = 13 * 60,       // 780
            endMinuteOfDay = 14 * 60,         // 840
            emoji = "🍲",
            category = BlockCategory.BREAK_MEAL,
            isCoreStudy = false,
            guidance = "Light healthy meal followed by an exact 20-minute power nap to reset adenosine levels for the afternoon engine.",
            suggestedGoals = listOf("Balanced lunch", "20-minute power nap", "Fresh cold water wash")
        ),
        StudyBlock(
            id = "block_3",
            title = "Block 3: Asmita's \"Old is Gold\" Practice Engine",
            subtitle = "Past NEB questions under exam timer",
            startTime = "02:00 PM",
            endTime = "04:30 PM",
            startMinuteOfDay = 14 * 60,       // 840
            endMinuteOfDay = 16 * 60 + 30,   // 990
            emoji = "⚡",
            category = BlockCategory.OLD_IS_GOLD_ENGINE,
            isCoreStudy = true,
            guidance = "Timed exam simulation! Open past NEB papers from Asmita's Question Bank. Write answers under strict stopwatch conditions. Score honestly.",
            suggestedGoals = listOf("Timed 1-year NEB paper set", "Physics & Chemistry past numericals", "Tag tricky questions to review log")
        ),
        StudyBlock(
            id = "block_tea",
            title = "Refreshment & Tea Break",
            subtitle = "Chai / herbal tea & breathwork",
            startTime = "04:30 PM",
            endTime = "05:00 PM",
            startMinuteOfDay = 16 * 60 + 30,  // 990
            endMinuteOfDay = 17 * 60,         // 1020
            emoji = "☕",
            category = BlockCategory.BREAK_MEAL,
            isCoreStudy = false,
            guidance = "Step out to get fresh air. Sip hot tea or water. Give your eyes a break from reading.",
            suggestedGoals = listOf("Enjoy tea/snack", "Eye 20-20-20 rest", "Hydrate")
        ),
        StudyBlock(
            id = "block_4",
            title = "Block 4: Computer Science / Language Focus",
            subtitle = "Programming syntax, DBMS & language prep",
            startTime = "05:00 PM",
            endTime = "06:00 PM",
            startMinuteOfDay = 17 * 60,       // 1020
            endMinuteOfDay = 18 * 60,         // 1080
            emoji = "💻",
            category = BlockCategory.CS_LANGUAGE,
            isCoreStudy = true,
            guidance = "Targeted 1-hour sprint. Code algorithms, revise DBMS normalization, OOP paradigms, or prepare technical English / Nepali writing formats.",
            suggestedGoals = listOf("Practice C/C++ or Python code", "Revise SQL / Database schemas", "Draft format or essay outline")
        ),
        StudyBlock(
            id = "block_workout",
            title = "Workout & Physical Exercise",
            subtitle = "Burn off mental fatigue & build stamina",
            startTime = "06:00 PM",
            endTime = "07:00 PM",
            startMinuteOfDay = 18 * 60,       // 1080
            endMinuteOfDay = 19 * 60,         // 1140
            emoji = "🏋️",
            category = BlockCategory.WORKOUT,
            isCoreStudy = false,
            guidance = "Cardio, calisthenics, or gym session. Physical exertion resets dopamine levels and primes your body for deep restorative sleep.",
            suggestedGoals = listOf("Dynamic warmup", "30-45m Strength or Cardio", "Post-workout stretching")
        ),
        StudyBlock(
            id = "block_dinner",
            title = "Dinner & Family Time",
            subtitle = "Early dinner for optimal sleep digestion",
            startTime = "07:00 PM",
            endTime = "07:45 PM",
            startMinuteOfDay = 19 * 60,       // 1140
            endMinuteOfDay = 19 * 60 + 45,   // 1185
            emoji = "🍲",
            category = BlockCategory.BREAK_MEAL,
            isCoreStudy = false,
            guidance = "Eating at 7:00 PM ensures full gastrointestinal digestion before 10:30 PM sleep, maximizing REM & Slow-wave sleep cycles.",
            suggestedGoals = listOf("Wholesome dinner", "Unplugged conversation", "No study stress at the table")
        ),
        StudyBlock(
            id = "block_shower",
            title = "Shower, Reset & Unwind",
            subtitle = "Warm shower & psychological transition",
            startTime = "07:45 PM",
            endTime = "08:30 PM",
            startMinuteOfDay = 19 * 60 + 45,  // 1185
            endMinuteOfDay = 20 * 60 + 30,   // 1230
            emoji = "🚿",
            category = BlockCategory.MORNING_ROUTINE,
            isCoreStudy = false,
            guidance = "Wash off the day. Tidy your desk so it is completely clean for tomorrow morning's Block 1.",
            suggestedGoals = listOf("Warm shower", "Clear study desk", "Prep tomorrow's materials")
        ),
        StudyBlock(
            id = "block_gaming",
            title = "Gaming & Leisure Block (Guilt-Free)",
            subtitle = "Earned 1-hour reward & recreational release",
            startTime = "08:30 PM",
            endTime = "09:30 PM",
            startMinuteOfDay = 20 * 60 + 30,  // 1230
            endMinuteOfDay = 21 * 60 + 30,   // 1290
            emoji = "🎮",
            category = BlockCategory.LEISURE_GAMING,
            isCoreStudy = false,
            guidance = "100% Guilt-Free! You completed your heavy study quotas today. Play your favorite game, watch an episode, or relax without anxiety.",
            suggestedGoals = listOf("Play favored game/hobby", "Laugh and unwind", "Wrap up by 09:30 sharp")
        ),
        StudyBlock(
            id = "block_5",
            title = "Block 5: Active Recall, Flashcards & Mistake Log Review",
            subtitle = "Lock today's knowledge into long-term memory",
            startTime = "09:30 PM",
            endTime = "10:30 PM",
            startMinuteOfDay = 21 * 60 + 30,  // 1290
            endMinuteOfDay = 22 * 60 + 30,   // 1350
            emoji = "📝",
            category = BlockCategory.ACTIVE_RECALL_MISTAKE,
            isCoreStudy = true,
            guidance = "The most critical 60 minutes for retention. Review today's mistake log, flip flashcards, and write down formulas from memory before sleeping.",
            suggestedGoals = listOf("Review today's Mistake Notebook", "Flip 20 formula flashcards", "Brain-dump 5 crucial takeaways")
        ),
        StudyBlock(
            id = "block_sleep",
            title = "8 Hours Deep Sleep (Memory Consolidation)",
            subtitle = "Non-negotiable neurological repair",
            startTime = "10:30 PM",
            endTime = "06:30 AM",
            startMinuteOfDay = 22 * 60 + 30,  // 1350
            endMinuteOfDay = 6 * 60 + 30,    // 390
            emoji = "😴",
            category = BlockCategory.SLEEP_RECOVERY,
            isCoreStudy = false,
            guidance = "During deep sleep, the hippocampus replays and consolidates all derivations and calculations learned today into neocortex memory.",
            suggestedGoals = listOf("Lights out by 10:30 PM", "Dark, cool room", "Consistent 8-hour sleep")
        )
    )
}
