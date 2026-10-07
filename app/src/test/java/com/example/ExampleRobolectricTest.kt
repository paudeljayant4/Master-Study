package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.DefaultSchedule
import com.example.data.model.StudyChecklistItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Master Study", appName)
    }

    @Test
    fun `master schedule contains all 14 blocks`() {
        val blocks = DefaultSchedule.masterBlocks
        assertEquals(14, blocks.size)

        // Verify key blocks
        val block1 = blocks.find { it.id == "block_1" }
        assertNotNull(block1)
        assertEquals("07:00 AM", block1?.startTime)
        assertEquals("09:30 AM", block1?.endTime)

        val block3 = blocks.find { it.id == "block_3" }
        assertNotNull(block3)
        assertTrue(block3?.title?.contains("Old is Gold") == true)
        assertEquals("02:00 PM", block3?.startTime)
        assertEquals("04:30 PM", block3?.endTime)

        val gamingBlock = blocks.find { it.id == "block_gaming" }
        assertNotNull(gamingBlock)
        assertEquals("08:30 PM", gamingBlock?.startTime)
        assertEquals("09:30 PM", gamingBlock?.endTime)

        val block5 = blocks.find { it.id == "block_5" }
        assertNotNull(block5)
        assertEquals("09:30 PM", block5?.startTime)
        assertEquals("10:30 PM", block5?.endTime)
    }

    @Test
    fun `daily checklist dao stores and toggles items locally in room`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getInstance(context)
        val checklistDao = db.checklistDao()

        val item = StudyChecklistItem(
            routineBlockId = "test_block_1",
            dateString = "2026-10-07",
            title = "Block 1: Heavy Theory & Derivations",
            subtitle = "Physics / Chemistry",
            timeSlot = "07:00 AM – 09:30 AM",
            emoji = "🧠",
            category = "Theory & Derivations",
            isCompleted = false
        )

        val id = checklistDao.insertOrUpdate(item)
        assertTrue(id > 0)

        val items = checklistDao.getChecklistForDate("2026-10-07").first()
        val saved = items.find { it.routineBlockId == "test_block_1" }
        assertNotNull(saved)
        assertEquals(false, saved?.isCompleted)

        // Toggle to true
        checklistDao.updateCompletion(saved!!.id, true, System.currentTimeMillis())

        val updated = checklistDao.getChecklistForDate("2026-10-07").first()
        val toggled = updated.find { it.routineBlockId == "test_block_1" }
        assertEquals(true, toggled?.isCompleted)
    }
}
