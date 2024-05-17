package com.example.mobdev2.repo

class AuthRepositoryImpl {
    fun checkInputEmailPassword(email: String, password: String, confirmedPassword: String): Boolean {
        if(email.isEmpty() || password.isEmpty()) return false
        if(!email.contains('@')) return false
        if(password != confirmedPassword) return false
        return true
    }
}