package com.example.mobdev2.ui.screens.book;

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.koin.android.annotation.KoinViewModel;

@KoinViewModel
class SettingsScreenViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel(){
    val isSigningOut = savedStateHandle.getStateFlow("signOutState", false)

    fun setIsLoading() {
        savedStateHandle["signOutState"] = !isSigningOut.value
    }
}
