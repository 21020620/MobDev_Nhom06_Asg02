package com.example.mobdev2.ui.screens.book

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.ramcosta.composedestinations.annotation.Destination

@BookNavGraph
@Destination
@Composable
fun BookForumScreen() {
    Text(text = "Book Forum Screen")
}