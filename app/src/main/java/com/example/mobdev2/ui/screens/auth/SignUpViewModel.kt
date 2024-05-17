package com.example.mobdev2.ui.screens.auth

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.repo.AuthRepositoryImpl
import com.example.mobdev2.ui.screens.destinations.LoginScreenDestination
import com.example.mobdev2.ui.screens.destinations.PickBookGenresScreenDestination
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SignUpViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val navigator: DestinationsNavigator
): ViewModel() {
    val email = savedStateHandle.getStateFlow("email", "")
    val password = savedStateHandle.getStateFlow("password", "")
    val confirmPassword = savedStateHandle.getStateFlow("confirmPassword", "")
    val emailError = savedStateHandle.getStateFlow("emailError", "")
    val passwordError = savedStateHandle.getStateFlow("passwordError", "")
    val confirmPasswordError = savedStateHandle.getStateFlow("confirmPasswordError", "")

    fun setEmail(email: String) {
        savedStateHandle["email"] = email
    }

    fun setPassword(password: String) {
        savedStateHandle["password"] = password
    }

    fun setConfirmPassword(confirmPassword: String) {
        savedStateHandle["confirmPassword"] = confirmPassword
    }

    fun signUp() = viewModelScope.launch {
        try {
            val inputCheck = AuthRepositoryImpl()
            if(!inputCheck.checkInputEmailPassword(email.value, password.value, confirmPassword.value))
                throw Exception("INVALID INPUT FOR EMAIL AND PASSWORD")
            Firebase.auth.createUserWithEmailAndPassword(email.value, password.value).await()
            navigator.navigate(PickBookGenresScreenDestination(email = email.value))
        } catch (e: Exception) {
            Log.d("AUTHENTICATION FAILURE", "$e")
        }
    }

    fun login() = viewModelScope.launch {
        navigator.navigate(LoginScreenDestination)
    }
}