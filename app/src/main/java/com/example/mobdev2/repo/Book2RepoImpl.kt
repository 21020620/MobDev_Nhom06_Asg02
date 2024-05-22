package com.example.mobdev2.repo

import android.util.Log
import com.example.mobdev2.CachingResults
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.koin.core.annotation.Single

@Single
class Book2RepoImpl(
    private val db: FirebaseFirestore
) : Book2Repo{
    override suspend fun addBookToLibrary(bookID: String) {
        val userID = CachingResults.currentUser.name
        val updates = hashMapOf<String, Any>(
            "library" to CachingResults.currentUser.library + bookID
        )
        db.collection("users").document(userID)
            .update(updates)
            .addOnSuccessListener {
                Log.d("UPDATE LIBRARY", "SUCCESS")
            }
            .addOnFailureListener { e ->
                Log.d("UPDATE LIBRARY", "FAIL: $e")
            }
    }

    override suspend fun loadHighlight(bookID: String): List<String> {
        val document = db.collection("users").document(CachingResults.currentUser.name).collection("books").document(bookID).get().await()
        return document.get("highlights") as List<String>
    }
}