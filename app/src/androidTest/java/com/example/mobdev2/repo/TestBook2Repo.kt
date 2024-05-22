package com.example.mobdev2.repo

import com.example.mobdev2.CachingResults
import org.koin.core.annotation.Single

@Single
class TestBook2Repo: Book2Repo {
    override suspend fun addBookToLibrary(bookID: String) {
    }

    override suspend fun loadHighlight(bookID: String): List<String> {
        return listOf()
    }
}