package com.example.mobdev2.ui.screens.book;

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.ui.theme.ThemeState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel;

@KoinViewModel
class SettingsScreenViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
    private val settingDataStore: UserPreferences,
    private val db: FirebaseFirestore
) : AndroidViewModel(application) {


    private val email = FirebaseAuth.getInstance().currentUser?.email
    private val username = email?.indexOf('@')?.let { email.substring(0, it) }
    private val audioManager = application.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
    var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / maxVolume
    val isSigningOut = savedStateHandle.getStateFlow("signOutState", false)

    fun toggleTheme() {
        ThemeState.darkModeState.value = !ThemeState.darkModeState.value
        viewModelScope.launch {
            settingDataStore.saveTheme(ThemeState.darkModeState.value)
        }
    }

    fun setGoal(hour: Int, minute: Int) {
        Log.d("HOUR", "$hour")
        Log.d("MINUTE", "$minute")
        val goal = hour * 60 + minute
        val userDocumentRef = db.collection("users").document(username!!)
        viewModelScope.launch {
            try {
                userDocumentRef.set(hashMapOf("goal" to goal), SetOptions.merge())
            } catch (e: Exception) {
                Log.e("UPDATE GOAL", "FAILED: $e")
            }
        }
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
