package com.example.mobdev2.ui.screens.book

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.ramcosta.composedestinations.annotation.Destination
@BookNavGraph(start = true)
@Destination
@Composable
fun AllBookScreen() {
    Text(text = "All Book Screen")
}