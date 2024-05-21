package com.example.mobdev2.repo

import com.example.mobdev2.repo.model.Book

interface AllBookRepo {
    suspend fun getAllBooks(): List<Book>
}