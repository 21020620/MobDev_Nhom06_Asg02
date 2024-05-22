package com.example.mobdev2.repo

import com.example.mobdev2.repo.model.User

interface BookHomeRepo {
    suspend fun setCurrentUser()
}