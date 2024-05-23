package com.example.mobdev2.repo

import android.util.Log
import com.example.mobdev2.CachingResults
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.annotation.Single

@Single
class LibraryRepoImpl(
    private val db: FirebaseFirestore
): LibraryRepo {
    override fun removeBookFromLibrary(bookID: String) {
        val userID = CachingResults.currentUser.name
        val updates = hashMapOf<String, Any>(
            "library" to CachingResults.currentUser.library.filterNot {
                it == bookID
            }
        )

        db.collection("users").document(userID)
            .update(updates)
            .addOnSuccessListener {
                Log.d("UPDATE LIBRARY", "SUCCESS REMOVED")
            }
            .addOnFailureListener { e ->
                Log.d("UPDATE LIBRARY", "REMOVED FAIL: $e")
            }
    }
}