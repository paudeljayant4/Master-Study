package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.StudyChecklistItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreSyncManager(private val context: Context) {
    private val db: FirebaseFirestore by lazy {
        val dbId = context.getString(R.string.firestore_database_id)
        FirebaseFirestore.getInstance(dbId)
    }

    private val auth = Firebase.auth

    suspend fun syncUserProfile() {
        val user = auth.currentUser ?: return
        try {
            val userDoc = db.collection("users").document(user.uid)
            val data = mapOf(
                "userId" to user.uid,
                "displayName" to (user.displayName ?: "Student"),
                "email" to (user.email ?: ""),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            userDoc.set(data, com.google.firebase.firestore.SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.w("FirestoreSyncManager", "Profile sync failed: ${e.message}")
        }
    }

    suspend fun syncChecklistSummary(
        dateString: String,
        items: List<StudyChecklistItem>
    ) {
        val user = auth.currentUser ?: return
        try {
            val completedItems = items.filter { it.isCompleted }
            val completedCount = completedItems.size
            val totalCount = items.size
            val percentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0
            val completedBlockIds = completedItems.map { it.routineBlockId }

            val docRef = db.collection("users")
                .document(user.uid)
                .collection("checklists")
                .document(dateString)

            val payload = mapOf(
                "userId" to user.uid,
                "dateString" to dateString,
                "completedCount" to completedCount,
                "totalCount" to totalCount,
                "completionPercentage" to percentage,
                "completedBlockIds" to completedBlockIds,
                "updatedAt" to FieldValue.serverTimestamp()
            )

            docRef.set(payload, com.google.firebase.firestore.SetOptions.merge()).await()
            Log.d("FirestoreSyncManager", "Synced checklist for $dateString: $completedCount/$totalCount")
        } catch (e: Exception) {
            Log.w("FirestoreSyncManager", "Checklist cloud sync error: ${e.message}")
        }
    }
}
