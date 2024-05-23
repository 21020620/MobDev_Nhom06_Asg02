package com.example.mobdev2.repo

import android.util.Log
import com.example.mobdev2.CachingResults
import com.example.mobdev2.repo.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import org.koin.core.annotation.Single

@Single
class BookHomeRepoImpl(
    private val db: FirebaseFirestore
): BookHomeRepo {
    override suspend fun setCurrentUser() {
        try {
            val email = Firebase.auth.currentUser?.email
            val userID = email?.substringBefore("@")
            if(userID.isNullOrBlank()) return
            val user = db.collection("users").document(userID).get().await()
            if(user.exists()) {
                CachingResults.currentUser = user.toObject<User>()!!
                CachingResults.currentUser = CachingResults.currentUser.copy(name = email.substringBefore("@"), email = email)
            }
        } catch (e: Exception) {
            Log.d("GET USER FAILED", "$e")
        }
    }
}