package com.example.mobdev2.repo

import com.example.mobdev2.repo.model.Book

interface BookRepo {
    suspend fun getBookByID(bookID: String): Book?

    suspend fun getSimilarBooks(bookID: String): List<Book>?
}