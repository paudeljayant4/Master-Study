package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.DailyBlockCompletion
import com.example.data.model.DefaultFlashcards
import com.example.data.model.FlashcardItem
import com.example.data.model.MistakeEntry
import com.example.data.model.OldIsGoldSession
import com.example.data.model.StudyChecklistItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MistakeEntry::class,
        OldIsGoldSession::class,
        DailyBlockCompletion::class,
        FlashcardItem::class,
        StudyChecklistItem::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mistakeDao(): MistakeDao
    abstract fun oldIsGoldDao(): OldIsGoldDao
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun checklistDao(): ChecklistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "master_study_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed default NEB formula flashcards
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).flashcardDao().insertAll(DefaultFlashcards.items)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
