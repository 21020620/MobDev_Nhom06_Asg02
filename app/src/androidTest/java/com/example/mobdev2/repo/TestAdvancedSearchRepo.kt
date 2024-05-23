package com.example.mobdev2.repo

import android.util.Log
import com.example.mobdev2.CachingResults
import com.example.mobdev2.repo.model.Chapter
import com.example.mobdev2.repo.model.ReaderData
import org.koin.core.annotation.Single

@Single
class TestAdvancedSearchRepo: AdvancedSearchRepo {
    override suspend fun getChaptersOfBook(bookID: String): List<Chapter> {
        val book = CachingResults.bookList
            .first { it.id == bookID }
        Log.d("Perform", book.id)
        return book.chapters
    }

    override suspend fun saveReaderData(readerData: ReaderData) {
        return
    }

    override suspend fun updateReaderData(userID: String, bookID: String, updates: Map<String, Any>) {
        return
    }

    override suspend fun getReaderData(userID: String, bookID: String): ReaderData? {
        return ReaderData("The Da Vinci Code", 2, 200, "ngominh")
    }
}