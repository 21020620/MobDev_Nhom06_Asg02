package com.example.mobdev2.repo

import com.example.mobdev2.CachingResults
import com.example.mobdev2.repo.BookHomeRepo
import com.example.mobdev2.repo.model.User

class TestBookHomeRepo: BookHomeRepo {
    override suspend fun setCurrentUser() {
        CachingResults.currentUser = User("ngominh@gmail.com", "ngominh", listOf("Horror", "Historical Fiction"), listOf())
    }
}