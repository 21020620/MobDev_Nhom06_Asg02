package com.example.mobdev2.model

data class Book(
    val title: String,
    val author: String,
    val subjects: List<String>,
    val language: String,
    val imageURL: String
) {
    constructor() : this("", "", listOf(""), "", "")
}