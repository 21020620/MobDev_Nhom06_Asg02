package com.example.mobdev2

import com.example.mobdev2.repo.model.Book
import com.example.mobdev2.repo.model.User

object CachingResults {
    var bookList: List<Book> = listOf()
    var currentUser: User = User()
    var highlights: List<List<Int>> = listOf(listOf())
}