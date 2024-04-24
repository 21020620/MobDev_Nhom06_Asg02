package com.example.mobdev2.ui.screens.auth

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.ui.screens.destinations.BookHomeScreenDestination
import com.example.mobdev2.ui.screens.destinations.PickBookGenresScreenDestination
import com.example.mobdev2.ui.screens.destinations.SignUpScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import kotlinx.coroutines.tasks.await

@KoinViewModel
class LoginViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val navigator: DestinationsNavigator
) : ViewModel(){
    val email = savedStateHandle.getStateFlow("email", "")
    val password = savedStateHandle.getStateFlow("password", "")
    val emailError = savedStateHandle.getStateFlow("emailError", "")
    val passwordError = savedStateHandle.getStateFlow("passwordError", "")

    fun setEmail(email: String) {
        savedStateHandle["email"] = email
    }

    fun setPassword(password: String) {
        savedStateHandle["password"] = password
    }

    fun loginWithEmailPassword() = viewModelScope.launch {
        try {
            Firebase.auth.signInWithEmailAndPassword(email.value, password.value).await()
            navigator.navigate(BookHomeScreenDestination)
        } catch (e: Exception) {
            Log.d("AUTHENTICATION FAILURE", "Login failed: $e")
        }
    }

    fun loginWithGoogle() = viewModelScope.launch {

        navigator.navigate(PickBookGenresScreenDestination)
    }

    fun signUp() = viewModelScope.launch {
        navigator.navigate(SignUpScreenDestination)
    }
}