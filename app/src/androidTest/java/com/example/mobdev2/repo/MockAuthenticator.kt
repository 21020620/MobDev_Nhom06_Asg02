package com.example.mobdev2.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.mockk
import org.koin.core.annotation.Single

@Single
class MockAuthenticator : Authenticator {
    private var user: FirebaseUser? = mockk(relaxed = true)
    private val listeners = mutableListOf<FirebaseAuth.AuthStateListener>()

    override fun currentUser() = user
    override fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        listeners.add(listener)
    }
    override fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        listeners.remove(listener)
    }
}