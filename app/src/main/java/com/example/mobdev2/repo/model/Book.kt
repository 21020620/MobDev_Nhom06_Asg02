package com.example.mobdev2.repo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Locale

@Parcelize
data class Book (
    val id: String,
    val title: String,
    val author: String,
    val subjects: List<String>,
    val language: String,
    val imageURL: String,
    var chapters: List<Chapter>,
    var synopsis: String,
) :Parcelable {
    constructor() : this(id = "", title = "", author = "", subjects = listOf(), language = "", imageURL = "", chapters = listOf(), synopsis = "")
}

@Parcelize
data class Chapter(
    val name: String,
    val content: String,
) :Parcelable {
    constructor() : this(name = "", content = "")
}

@Parcelize
data class ReaderData(
    val bookID: String,
    val lastChapterIndex: Int,
    val lastChapterOffset: Int,
    val userID: String
) :Parcelable {
    constructor() : this(bookID = "", lastChapterIndex = 0, lastChapterOffset = 0, userID = "")

    fun getProgressPercent(totalChapters: Int) =
        String.format(Locale.US, "%.2f", ((lastChapterIndex + 1).toFloat() / totalChapters.toFloat()) * 100f)
}