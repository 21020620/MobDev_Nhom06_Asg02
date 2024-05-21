package com.example.mobdev2.repo

import com.example.mobdev2.repo.model.Book
import com.example.mobdev2.repo.model.Chapter
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class TestAllBookRepo: AllBookRepo {
    override suspend fun getAllBooks(): List<Book> {
        return listOf(Book(
            "The Da Vinci Code",
            "The Da Vinci Code",
            "Dan Brown",
            listOf("Historical Fiction", "Mystery", "Thriller"),
            "English",
            "https://upload.wikimedia.org/wikipedia/en/thumb/6/6b/DaVinciCode.jpg/220px-DaVinciCode.jpg",
            listOf(Chapter("Chapter 01", "This is a Fake Chapter")),
            "This is a Fake Synopsis",
            listOf()
        ))
    }
}