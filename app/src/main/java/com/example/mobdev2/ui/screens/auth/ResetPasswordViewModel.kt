package com.example.mobdev2.ui.screens.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ResetPasswordViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    val email = savedStateHandle.getStateFlow("email", "")
    fun setEmail(email: String) {
        savedStateHandle["email"] = email
    }
}