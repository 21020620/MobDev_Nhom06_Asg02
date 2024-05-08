package com.example.mobdev2.ui.screens.book;

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.example.mobdev2.ui.theme.ThemeState
import org.koin.android.annotation.KoinViewModel;

@KoinViewModel
class SettingsScreenViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val audioManager = application.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
    var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / maxVolume
    val isSigningOut = savedStateHandle.getStateFlow("signOutState", false)

    fun toggleTheme() {
        ThemeState.darkModeState.value = !ThemeState.darkModeState.value
    }
    fun setIsLoading() {
        savedStateHandle["signOutState"] = !isSigningOut.value
    }

    fun updateVolume(volume: Float) {
        currentVolume = volume
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            (volume * maxVolume).toInt(),
            0
        )
    }
}
data class ThemeState(val isDarkTheme: Boolean)
