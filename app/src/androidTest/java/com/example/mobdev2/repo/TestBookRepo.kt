package com.example.mobdev2.repo

import com.example.mobdev2.repo.model.Book
import com.example.mobdev2.repo.model.Chapter
import org.koin.core.annotation.Single

@Single
class TestBookRepo: BookRepo {
    override suspend fun getBookByID(bookID: String): Book? {
        return Book(
            "The Da Vinci Code",
            "The Da Vinci Code",
            "Dan Brown",
            listOf("Historical Fiction", "Mystery", "Thriller"),
            "English",
            "https://upload.wikimedia.org/wikipedia/en/thumb/6/6b/DaVinciCode.jpg/220px-DaVinciCode.jpg",
            listOf(Chapter("Chapter 01", "This is a Fake Chapter")),
            "This is a Fake Synopsis",
            listOf()
        )
    }

    override suspend fun getSimilarBooks(bookID: String): List<Book>? {
        return listOf(
            Book(
                "The Green Beret",
                "The Green Beret",
                "Tom Purdom",
                listOf("Historical Fiction", "Mystery", "Thriller"),
                "English",
                "https://www.gutenberg.org/cache/epub/24278/pg24278.cover.medium.jpg",
                listOf(Chapter("Chapter 01", "This is a Fake Chapter for The Green Beret 01")),
                "This is a Fake Synopsis. Only for Testing",
                listOf()
            )
        )
    }
}