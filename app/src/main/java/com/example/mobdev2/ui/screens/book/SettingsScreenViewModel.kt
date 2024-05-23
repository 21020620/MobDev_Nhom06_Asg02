package com.example.mobdev2.ui.screens.book;

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.media.AudioManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.ui.screens.book.notification.NotificationBroadcastReceiver
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

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun scheduleNotification(context: Context, hour: Int, minute: Int){
//        NotificationBroadcastReceiver.showNotificationNow(context)
//        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
//
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)
//        val calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            set(Calendar.HOUR_OF_DAY, hour)
//            set(Calendar.MINUTE, minute)
//            set(Calendar.SECOND, 0)
//        }
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
//        } else {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
//        }
//
//        Log.d("NotificationScheduler", "Notification scheduled for $hour:$minute")
//    }

    fun setGoal(hour: Int, minute: Int) {
        val goal = hour * 60 + minute
        val userDocumentRef = db.collection("users").document(username!!)
        viewModelScope.launch {
            try {
                userDocumentRef.get().addOnSuccessListener {document ->
                    // as? for safe cast
                    val session = document.get("session") as? Long ?: 0L
                    if (session != 0L) {
                        userDocumentRef.set(hashMapOf("goal" to goal), SetOptions.merge())
                    } else {
                        userDocumentRef.set(hashMapOf("goal" to goal, "session" to 0), SetOptions.merge())
                    }
                }
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
