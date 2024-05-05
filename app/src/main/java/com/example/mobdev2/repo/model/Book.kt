package com.example.mobdev2.repo.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Book(
    @SerializedName("title")
    val title: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("subjects")
    val subjects: List<String>,
    @SerializedName("language")
    val language: String,
    @SerializedName("imageURL")
    val imageURL: String,
    @SerializedName("chapters")
    var chapters: List<Chapter>,
    @SerializedName("synopsis")
    var synopsis: String,
) :Serializable {
    constructor() : this(title = "", author = "", subjects = listOf(), language = "", imageURL = "", chapters = listOf(), synopsis = "")
}

data class BookSet(
    val count: Int,
    val next: String?,
    val previous: String?,
    val books: List<Book>
)

data class Chapter(
    @SerializedName("name")
    val name: String,
    @SerializedName("content")
    val content: String,
) :Serializable {
    constructor() : this(name = "", content = "")
}