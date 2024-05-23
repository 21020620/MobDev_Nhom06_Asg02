package com.example.mobdev2.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface Authenticator {
    fun currentUser(): FirebaseUser?
    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener)
    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener)
}