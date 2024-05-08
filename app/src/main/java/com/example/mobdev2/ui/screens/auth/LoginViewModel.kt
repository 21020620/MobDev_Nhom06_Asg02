package com.example.mobdev2.ui.screens.auth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.Identity
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.R
import com.example.mobdev2.ui.screens.destinations.BookHomeScreenDestination
import com.example.mobdev2.ui.screens.destinations.SignUpScreenDestination
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

@KoinViewModel
class LoginViewModel(
    context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val navigator: DestinationsNavigator
) : ViewModel(){
    val email = savedStateHandle.getStateFlow("email", "")
    val password = savedStateHandle.getStateFlow("password", "")
    val emailError = savedStateHandle.getStateFlow("emailError", "")
    val passwordError = savedStateHandle.getStateFlow("passwordError", "")

    companion object {
        private val auth = Firebase.auth
        val signedIn
            get() = auth.currentUser != null
    }

    private val oneTapClient = Identity.getSignInClient(context)
    private val signInRequest = BeginSignInRequest
        .builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions
                .builder()
                .setSupported(true)
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .build()
        )
        .build()

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

    fun loginWithGoogle(launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) = viewModelScope.launch {
        launcher.launch(
            IntentSenderRequest
                .Builder(getSignInIntentSender() ?: return@launch)
                .build()
        )
    }

    fun signIn(intent: Intent?) {
        viewModelScope.launch {
            val token = oneTapClient.getSignInCredentialFromIntent(intent).googleIdToken
            val googleCredential = GoogleAuthProvider.getCredential(token, null)
            try {
                 auth
                    .signInWithCredential(googleCredential)
                    .await()
                navigator.navigate(BookHomeScreenDestination)

            } catch (e: Exception) {
                Log.e("Auth", "Failed to sign in with intent", e)
                if (e is CancellationException) throw e
            }
        }
    }

    fun signUp() = viewModelScope.launch {
        navigator.navigate(SignUpScreenDestination)
    }

    fun hasUser() = Firebase.auth.currentUser != null

    private suspend fun getSignInIntentSender(): IntentSender? {
        return try {
            oneTapClient
                .beginSignIn(signInRequest)
                .await()
        }
        catch (e: Exception) {
            Log.e("Auth", "Failed to get intent sender", e)
            if (e is CancellationException) throw e
            null
        }?.pendingIntent?.intentSender
    }

}