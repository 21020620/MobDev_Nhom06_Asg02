package com.example.mobdev2.repo

import com.example.mobdev2.repo.model.Chapter
import com.example.mobdev2.repo.model.ReaderData

interface AdvancedSearchRepo {
    suspend fun getChaptersOfBook(bookID: String): List<Chapter>

    suspend fun saveReaderData(readerData: ReaderData)

    suspend fun updateReaderData(userID: String, bookID: String, updates: Map<String, Any>)

    suspend fun getReaderData(userID: String, bookID: String):ReaderData?
}