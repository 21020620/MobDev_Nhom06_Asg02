package com.example.mobdev2.repo

import com.google.firebase.auth.FirebaseAuth
class AuthenticatorImpl(
    private val firebaseAuth: FirebaseAuth
): Authenticator {
    override fun currentUser() = firebaseAuth.currentUser
    override fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        firebaseAuth.addAuthStateListener(listener)
    }
    override fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        firebaseAuth.removeAuthStateListener(listener)
    }
}