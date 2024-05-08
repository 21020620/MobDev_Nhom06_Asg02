package com.example.mobdev2.ui.screens.auth

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.ui.screens.destinations.LoginScreenDestination
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.android.annotation.KoinViewModel
import java.util.concurrent.CancellationException

@KoinViewModel
class ResetPasswordViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val navigator: DestinationsNavigator
): ViewModel() {
    val email = savedStateHandle.getStateFlow("email", "")
    fun setEmail(email: String) {
        savedStateHandle["email"] = email
    }
    companion object {
        private val auth = Firebase.auth
    }

    fun resetPassword(email: String) {
        viewModelScope.launch { resetPasswordS(email) }
    }

    private suspend fun resetPasswordS(email: String) {
        try {
            auth.sendPasswordResetEmail(email)
                .await()
            navigator.navigate(LoginScreenDestination)
        } catch (e: Exception) {
            Log.e("Auth", "Failed to reset password", e)
            if(e is CancellationException) throw e
        }
    }
}