package com.example.mobdev2.ui.screens.book.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mobdev2.CachingResults
import com.example.mobdev2.repo.model.User
import com.example.mobdev2.ui.screens.NavGraphs
import com.example.mobdev2.ui.screens.destinations.LoginScreenDestination
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.tasks.await
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class BookHomeScreenViewModel(
    private val navigator: DestinationsNavigator,
    private val db: FirebaseFirestore
) : ViewModel() {
    fun navigateToLogin() {
        navigator.navigate(LoginScreenDestination) {
            popUpTo(NavGraphs.root)
        }
    }

    suspend fun setCurrentUser() {
        try {
            val email = Firebase.auth.currentUser?.email
            val userID = email?.substringBefore("@")
            if(userID.isNullOrBlank()) return
            val user = db.collection("users").document(userID).get().await()
            if(user.exists()) {
                CachingResults.currentUser = user.toObject<User>()!!
                CachingResults.currentUser = CachingResults.currentUser.copy(name = email.substringBefore("@"), email = email)
                Log.d("GET USER SUCCESS", "Current user: ${user.data}")
            }
        } catch (e: Exception) {
            Log.d("GET USER FAILED", "$e")
        }
    }
}