package com.example.mobdev2.repo.model

data class User(
    val email: String,
    val name: String,
    val favouriteTags: List<String>,
    val library: List<String>
) {
    constructor(): this(email = "", name = "", favouriteTags = listOf(), library = listOf())
}