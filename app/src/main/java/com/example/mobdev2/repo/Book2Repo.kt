package com.example.mobdev2.repo

interface Book2Repo {
    suspend fun addBookToLibrary(bookID: String)

    suspend fun loadHighlight(bookID: String): List<String>
}